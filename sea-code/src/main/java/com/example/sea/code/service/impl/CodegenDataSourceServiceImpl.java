package com.example.sea.code.service.impl;

import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.code.mapper.CodegenDataSourceMapper;
import com.example.sea.code.common.utils.ColumnUtil;
import com.example.sea.code.converter.CodegenDataSourceConverter;
import com.example.sea.code.entity.CodegenDataSource;
import com.example.sea.code.entity.dto.CodeGenDataSourceDTO;
import com.example.sea.code.entity.vo.CodegenDataSourceVO;
import com.example.sea.code.entity.vo.TableColumnsVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 代码生成 - 数据源信息表服务实现类
 * @author liuhuan
 * @date 2025-03-31
 * @description 代码生成 - 数据源信息表服务实现类
 */
@Service
public class CodegenDataSourceServiceImpl extends ServiceImpl<CodegenDataSourceMapper, CodegenDataSource> implements ICodegenDataSourceService {


    private final CodegenDataSourceConverter cDataSourceConverter;

    @Autowired
    public CodegenDataSourceServiceImpl(CodegenDataSourceConverter cDataSourceConverter) {
        this.cDataSourceConverter = cDataSourceConverter;
    }


    @Override
    public CommonResult<Boolean> checkDataSource(CodeGenDataSourceDTO codeGenDataSourceDTO) {
        // 构建JDBC URL
        String jdbcUrl;
        switch (codeGenDataSourceDTO.getDbType().toLowerCase()) {
            case "mysql":
                jdbcUrl = String.format("jdbc:mysql://%s:%d/?useSSL=false&serverTimezone=UTC",
                    codeGenDataSourceDTO.getHost(), codeGenDataSourceDTO.getPort());
                break;
            case "postgresql":
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/postgres",
                    codeGenDataSourceDTO.getHost(), codeGenDataSourceDTO.getPort());
                break;
            case "oracle":
                jdbcUrl = String.format("jdbc:oracle:thin:@%s:%d:orcl",
                    codeGenDataSourceDTO.getHost(), codeGenDataSourceDTO.getPort());
                break;
            default:
                return CommonResult.failed("不支持的数据库类型");
        }

        // 测试数据库连接
        try (Connection connection = DriverManager.getConnection(
            jdbcUrl,
            codeGenDataSourceDTO.getUsername(),
            codeGenDataSourceDTO.getPassword())) {
            
            return connection.isValid(5) ? 
                CommonResult.success(true, "数据库连接成功") : 
                CommonResult.failed("数据库连接无效");
        } catch (Exception e) {
            return CommonResult.failed("数据库连接失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Boolean> saveDataSource(CodeGenDataSourceDTO codeGenDataSourceDTO) {
        // 参数校验
        if (Objects.isNull(codeGenDataSourceDTO.getName())) {
            return CommonResult.failed("数据源参数不完整");
        }

        // 检查数据源是否已存在
        if (lambdaQuery()
            .eq(CodegenDataSource::getName, codeGenDataSourceDTO.getName())
            .exists()) {
            return CommonResult.failed("数据源名称已存在");
        }

        // 转换为实体并保存
        CodegenDataSource dataSource = new CodegenDataSource()
            .setName(codeGenDataSourceDTO.getName())
            .setDbType(codeGenDataSourceDTO.getDbType())
            .setHost(codeGenDataSourceDTO.getHost())
            .setPort(codeGenDataSourceDTO.getPort())
            .setUsername(codeGenDataSourceDTO.getUsername())
            .setPassword(codeGenDataSourceDTO.getPassword());
        
        return save(dataSource) ? 
            CommonResult.success(true, "数据源保存成功") :
            CommonResult.failed("数据源保存失败");
    }

    @Override
    public CommonResult<List<CodegenDataSourceVO>> listDataSource() {
        return CommonResult.success(
            cDataSourceConverter.entityToVo(list())
        );
    }


    @Override
    public CommonResult<List<String>> listDataBase(Long dataSourceId) {
        // 获取数据源信息
        CodegenDataSource dataSource = getById(dataSourceId);
        if (dataSource == null) {
            return CommonResult.failed("数据源不存在");
        }

        // 构建JDBC URL
        String jdbcUrl;
        switch (dataSource.getDbType().toLowerCase()) {
            case "mysql":
                jdbcUrl = String.format("jdbc:mysql://%s:%d/?useSSL=false&serverTimezone=UTC",
                    dataSource.getHost(), dataSource.getPort());
                break;
            case "postgresql":
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/postgres",
                    dataSource.getHost(), dataSource.getPort());
                break;
            case "oracle":
                jdbcUrl = String.format("jdbc:oracle:thin:@%s:%d:orcl",
                    dataSource.getHost(), dataSource.getPort());
                break;
            default:
                return CommonResult.failed("不支持的数据库类型");
        }

        // 查询数据库列表
        try (Connection connection = DriverManager.getConnection(
            jdbcUrl,
            dataSource.getUsername(),
            dataSource.getPassword())) {
            
            List<String> databases = new ArrayList<>();
            String querySql;
            switch (dataSource.getDbType().toLowerCase()) {
                case "mysql":
                    querySql = "SHOW DATABASES";
                    break;
                case "postgresql":
                    querySql = "SELECT datname FROM pg_database";
                    break;
                case "oracle":
                    querySql = "SELECT username FROM all_users";
                    break;
                default:
                    return CommonResult.failed("不支持的数据库类型");
            }

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(querySql)) {
                while (rs.next()) {
                    databases.add(rs.getString(1));
                }
            }
            
            return CommonResult.success(databases);
        } catch (Exception e) {
            return CommonResult.failed("获取数据库列表失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<List<String>> listTable(Long dataSourceId, String database) {
        // 获取数据源信息
        CodegenDataSource dataSource = getById(dataSourceId);
        if (dataSource == null) {
            return CommonResult.failed("数据源不存在");
        }

        // 构建JDBC URL
        String jdbcUrl;
        switch (dataSource.getDbType().toLowerCase()) {
            case "mysql":
                jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
                    dataSource.getHost(), dataSource.getPort(), database);
                break;
            case "postgresql":
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                    dataSource.getHost(), dataSource.getPort(), database);
                break;
            case "oracle":
                jdbcUrl = String.format("jdbc:oracle:thin:@%s:%d:orcl",
                    dataSource.getHost(), dataSource.getPort());
                break;
            default:
                return CommonResult.failed("不支持的数据库类型");
        }

        // 查询表列表
        try (Connection connection = DriverManager.getConnection(
            jdbcUrl,
            dataSource.getUsername(),
            dataSource.getPassword())) {
            
            List<String> tables = new ArrayList<>();
            String querySql;
            switch (dataSource.getDbType().toLowerCase()) {
                case "mysql":
                    querySql = "SHOW TABLES";
                    break;
                case "postgresql":
                    querySql = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
                    break;
                case "oracle":
                    querySql = "SELECT table_name FROM user_tables";
                    break;
                default:
                    return CommonResult.failed("不支持的数据库类型");
            }

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
        } catch (Exception e) {
            return CommonResult.failed("获取表列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<List<TableColumnsVO>> listColumns(Long dataSourceId, String database, String tableName) {
        // 获取数据源信息
        CodegenDataSource dataSource = getById(dataSourceId);
        if (dataSource == null) {
            return CommonResult.failed("数据源不存在");
        }

        // 构建JDBC URL
        String jdbcUrl;
        switch (dataSource.getDbType().toLowerCase()) {
            case "mysql":
                jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
                    dataSource.getHost(), dataSource.getPort(), database);
                break;
            case "postgresql":
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                    dataSource.getHost(), dataSource.getPort(), database);
                break;
            case "oracle":
                jdbcUrl = String.format("jdbc:oracle:thin:@%s:%d:orcl",
                    dataSource.getHost(), dataSource.getPort());
                break;
            default:
                return CommonResult.failed("不支持的数据库类型");
        }

        // 查询字段列表
        try (Connection connection = DriverManager.getConnection(
            jdbcUrl,
            dataSource.getUsername(),
            dataSource.getPassword())) {
            
            List<TableColumnsVO> columnList = new ArrayList<>();
            String querySql;
            switch (dataSource.getDbType().toLowerCase()) {
                case "mysql":
                    querySql = "SHOW FULL COLUMNS FROM " + tableName;
                    break;
                case "postgresql":
                    querySql = "SELECT column_name, data_type, coalesce(description, '') " +
                               "FROM information_schema.columns " +
                               "LEFT JOIN pg_description ON " +
                               "pg_description.objsubid = information_schema.columns.ordinal_position AND " +
                               "pg_description.objoid = (SELECT oid FROM pg_class WHERE relname = ?) " +
                               "WHERE table_name = ?";
                    break;
                case "oracle":
                    querySql = "SELECT column_name, data_type, " +
                              "NVL((SELECT comments FROM user_col_comments " +
                              "WHERE table_name = ? AND column_name = t.column_name), '') " +
                              "FROM user_tab_columns t WHERE table_name = ?";
                    break;
                default:
                    return CommonResult.failed("不支持的数据库类型");
            }

            try (PreparedStatement stmt = connection.prepareStatement(querySql)) {
                if (dataSource.getDbType().equalsIgnoreCase("postgresql")) {
                    stmt.setString(1, tableName);
                    stmt.setString(2, tableName);
                } else if (dataSource.getDbType().equalsIgnoreCase("oracle")) {
                    stmt.setString(1, tableName);
                    stmt.setString(2, tableName);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        TableColumnsVO column = new TableColumnsVO();
                        
                        if (dataSource.getDbType().equalsIgnoreCase("mysql")) {
                            // MySQL SHOW FULL COLUMNS 结果格式:
                            // Field, Type, Collation, Null, Key, Default, Extra, Privileges, Comment
                            column.setColumnName(rs.getString("Field"))
                                  .setColumnType(rs.getString("Type"))
                                  .setEntityType(ColumnUtil.getEntityTypeByColumnType(rs.getString("Type")))
                                  .setColumnComment(rs.getString("Comment"));
                        } else {
                            // PostgreSQL/Oracle 使用位置索引获取
                            column.setColumnName(rs.getString(1))
                                  .setColumnType(rs.getString(2))
                                  .setColumnComment(rs.getString(3));
                        }
                        
                        columnList.add(column);
                    }
                }
            }
            
            return CommonResult.success(columnList);
        } catch (Exception e) {
            return CommonResult.failed("获取字段列表失败: " + e.getMessage());
        }
    }

}
