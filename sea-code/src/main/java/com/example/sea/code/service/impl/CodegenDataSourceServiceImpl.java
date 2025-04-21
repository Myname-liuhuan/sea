package com.example.sea.code.service.impl;

import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.common.result.CommonResult;
import com.example.sea.code.mapper.CodegenDataSourceMapper;
import com.example.sea.code.converter.CodegenDataSourceConverter;
import com.example.sea.code.entity.CodegenDataSource;
import com.example.sea.code.entity.dto.CodeGenDataSourceDTO;
import com.example.sea.code.entity.vo.CodegenDataSourceVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.DriverManager;
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
}
