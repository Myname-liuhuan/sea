package com.example.sea.code.service.impl;

import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.code.dao.CodegenDataSourceMapper;
import com.example.sea.code.entity.CodegenDataSourcePO;
import com.example.sea.code.api.dto.CodeGenDataSourceDTO;
import com.example.sea.code.api.vo.CodegenDataSourceVO;
import com.example.sea.code.api.vo.TableColumnsVO;
import com.example.sea.code.converter.CodegenDataSourceConverter;
import com.example.sea.code.common.utils.ColumnUtil;
import com.example.sea.code.common.utils.JdbcUrlBuilder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CodegenDataSourceServiceImpl extends ServiceImpl<CodegenDataSourceMapper, CodegenDataSourcePO> implements ICodegenDataSourceService {

    private final CodegenDataSourceConverter cDataSourceConverter;

    public CodegenDataSourceServiceImpl(CodegenDataSourceConverter cDataSourceConverter) {
        this.cDataSourceConverter = cDataSourceConverter;
    }

    @Override
    public CommonResult<Boolean> checkDataSource(CodeGenDataSourceDTO codeGenDataSourceDTO) {
        try {
            String jdbcUrl = JdbcUrlBuilder.build(
                    codeGenDataSourceDTO.getDbType(), codeGenDataSourceDTO.getHost(), codeGenDataSourceDTO.getPort());

            try (Connection connection = DriverManager.getConnection(
                    jdbcUrl, codeGenDataSourceDTO.getUsername(), codeGenDataSourceDTO.getPassword())) {
                return connection.isValid(5) ?
                        CommonResult.success(true, "数据库连接成功") :
                        CommonResult.failed("数据库连接无效");
            }
        } catch (IllegalArgumentException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("数据库连接失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Boolean> saveDataSource(CodeGenDataSourceDTO codeGenDataSourceDTO) {
        if (Objects.isNull(codeGenDataSourceDTO.getName())) {
            return CommonResult.failed("数据源参数不完整");
        }

        if (lambdaQuery()
                .eq(CodegenDataSourcePO::getName, codeGenDataSourceDTO.getName())
                .exists()) {
            return CommonResult.failed("数据源名称已存在");
        }

        CodegenDataSourcePO dataSource = cDataSourceConverter.dtoToEntity(codeGenDataSourceDTO);

        return save(dataSource) ?
                CommonResult.success(true, "数据源保存成功") :
                CommonResult.failed("数据源保存失败");
    }

    @Override
    public CommonResult<List<CodegenDataSourceVO>> listDataSource() {
        return CommonResult.success(cDataSourceConverter.entityToVo(list()));
    }

    @Override
    public CommonResult<List<String>> listDataBase(Long dataSourceId) {
        CodegenDataSourcePO dataSource = getById(dataSourceId);
        if (dataSource == null) {
            return CommonResult.failed("数据源不存在");
        }

        try (Connection connection = getConnection(dataSource, null)) {
            List<String> databases = new ArrayList<>();
            String querySql = switch (dataSource.getDbType().toLowerCase()) {
                case "mysql" -> "SHOW DATABASES";
                case "postgresql" -> "SELECT datname FROM pg_database";
                case "oracle" -> "SELECT username FROM all_users";
                default -> throw new IllegalArgumentException("不支持的数据库类型");
            };

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(querySql)) {
                while (rs.next()) {
                    databases.add(rs.getString(1));
                }
            }

            return CommonResult.success(databases);
        } catch (IllegalArgumentException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取数据库列表失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<List<String>> listTable(Long dataSourceId, String database) {
        CodegenDataSourcePO dataSource = getById(dataSourceId);
        if (dataSource == null) {
            return CommonResult.failed("数据源不存在");
        }

        try (Connection connection = getConnection(dataSource, database)) {
            List<String> tables = new ArrayList<>();
            String querySql = switch (dataSource.getDbType().toLowerCase()) {
                case "mysql" -> "SHOW TABLES";
                case "postgresql" -> "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
                case "oracle" -> "SELECT table_name FROM user_tables";
                default -> throw new IllegalArgumentException("不支持的数据库类型");
            };

            try (PreparedStatement stmt = connection.prepareStatement(querySql)) {
                if (dataSource.getDbType().equalsIgnoreCase("postgresql")) {
                    stmt.setString(1, "public");
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        tables.add(rs.getString(1));
                    }
                }
            }

            return CommonResult.success(tables);
        } catch (IllegalArgumentException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取表列表失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<List<TableColumnsVO>> listColumns(Long dataSourceId, String database, String tableName) {
        CodegenDataSourcePO dataSource = getById(dataSourceId);
        if (dataSource == null) {
            return CommonResult.failed("数据源不存在");
        }

        try (Connection connection = getConnection(dataSource, database)) {
            List<TableColumnsVO> columnList = new ArrayList<>();
            String dbType = dataSource.getDbType().toLowerCase();

            if (dbType.equals("mysql")) {
                String querySql = "SELECT column_name, column_type, column_comment " +
                        "FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";
                try (PreparedStatement stmt = connection.prepareStatement(querySql)) {
                    stmt.setString(1, database);
                    stmt.setString(2, tableName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            columnList.add(new TableColumnsVO()
                                    .setColumnName(rs.getString("column_name"))
                                    .setColumnType(rs.getString("column_type"))
                                    .setEntityType(ColumnUtil.getEntityTypeByColumnType(rs.getString("column_type")))
                                    .setColumnComment(rs.getString("column_comment")));
                        }
                    }
                }
            } else if (dbType.equals("postgresql")) {
                String querySql = "SELECT column_name, data_type, coalesce(description, '') " +
                        "FROM information_schema.columns " +
                        "LEFT JOIN pg_description ON " +
                        "pg_description.objsubid = information_schema.columns.ordinal_position AND " +
                        "pg_description.objoid = (SELECT oid FROM pg_class WHERE relname = ?) " +
                        "WHERE table_name = ?";
                try (PreparedStatement stmt = connection.prepareStatement(querySql)) {
                    stmt.setString(1, tableName);
                    stmt.setString(2, tableName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            columnList.add(new TableColumnsVO()
                                    .setColumnName(rs.getString(1))
                                    .setColumnType(rs.getString(2))
                                    .setColumnComment(rs.getString(3)));
                        }
                    }
                }
            } else if (dbType.equals("oracle")) {
                String querySql = "SELECT column_name, data_type, " +
                        "NVL((SELECT comments FROM user_col_comments " +
                        "WHERE table_name = ? AND column_name = t.column_name), '') " +
                        "FROM user_tab_columns t WHERE table_name = ?";
                try (PreparedStatement stmt = connection.prepareStatement(querySql)) {
                    stmt.setString(1, tableName);
                    stmt.setString(2, tableName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            columnList.add(new TableColumnsVO()
                                    .setColumnName(rs.getString(1))
                                    .setColumnType(rs.getString(2))
                                    .setColumnComment(rs.getString(3)));
                        }
                    }
                }
            }

            return CommonResult.success(columnList);
        } catch (IllegalArgumentException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("获取字段列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据库连接
     * @param dataSource 数据源配置
     * @param dbName 数据库名，为 null 时连接服务器（用于列出数据库）
     */
    private Connection getConnection(CodegenDataSourcePO dataSource, String dbName) throws Exception {
        String jdbcUrl = (dbName != null)
                ? JdbcUrlBuilder.build(dataSource.getDbType(), dataSource.getHost(), dataSource.getPort(), dbName)
                : JdbcUrlBuilder.build(dataSource.getDbType(), dataSource.getHost(), dataSource.getPort());
        return DriverManager.getConnection(jdbcUrl, dataSource.getUsername(), dataSource.getPassword());
    }
}
