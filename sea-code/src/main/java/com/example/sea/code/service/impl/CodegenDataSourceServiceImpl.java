package com.example.sea.code.service.impl;

import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.common.result.CommonResult;
import com.example.sea.code.mapper.CodegenDataSourceMapper;
import com.example.sea.code.entity.CodegenDataSource;
import com.example.sea.code.entity.dto.CodeGenDataSourceDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

/**
 * 代码生成 - 数据源信息表服务实现类
 * @author liuhuan
 * @date 2025-03-31
 * @description 代码生成 - 数据源信息表服务实现类
 */
@Service
public class CodegenDataSourceServiceImpl extends ServiceImpl<CodegenDataSourceMapper, CodegenDataSource> implements ICodegenDataSourceService {

    @Override
    public CommonResult<Boolean> checkDataSource(CodeGenDataSourceDTO codeGenDataSourceDTO) {
        // 参数校验
        if (Objects.isNull(codeGenDataSourceDTO.getDbType()) || 
            Objects.isNull(codeGenDataSourceDTO.getHost()) ||
            Objects.isNull(codeGenDataSourceDTO.getPort()) ||
            Objects.isNull(codeGenDataSourceDTO.getUsername()) ||
            Objects.isNull(codeGenDataSourceDTO.getPassword())) {
            return CommonResult.failed("数据源参数不完整");
        }

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
        if (Objects.isNull(codeGenDataSourceDTO.getDbType()) || 
            Objects.isNull(codeGenDataSourceDTO.getHost()) ||
            Objects.isNull(codeGenDataSourceDTO.getPort()) ||
            Objects.isNull(codeGenDataSourceDTO.getUsername()) ||
            Objects.isNull(codeGenDataSourceDTO.getPassword()) ||
            Objects.isNull(codeGenDataSourceDTO.getName())) {
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
}
