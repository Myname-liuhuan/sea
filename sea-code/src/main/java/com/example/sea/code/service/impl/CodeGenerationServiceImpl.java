package com.example.sea.code.service.impl;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.example.sea.code.entity.CodegenDataSource;
import com.example.sea.code.entity.dto.CodeGenerateDTO;
import com.example.sea.code.mapper.CodegenDataSourceMapper;
import com.example.sea.code.service.ICodeGenerationService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 代码生成服务实现类
 * @author liuhuan
 * @date 2025-03-24
 */
@Service
public class CodeGenerationServiceImpl implements ICodeGenerationService {

    private final CodegenDataSourceMapper codegenDataSourceMapper;

    @Autowired
    public CodeGenerationServiceImpl(CodegenDataSourceMapper codegenDataSourceMapper) {
        this.codegenDataSourceMapper = codegenDataSourceMapper;
    }
    

    @Override
    public byte[] generateCode(CodeGenerateDTO codeGenerateDTO) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            
            // 创建临时目录
            Path tempDir = Files.createTempDirectory("codegen");
            
            // 查询数据源信息
            CodegenDataSource dataSource = codegenDataSourceMapper.selectById(codeGenerateDTO.getDataSourceId());
            if (Objects.isNull(dataSource)) {
                throw new IllegalArgumentException("数据源不存在");
            }

            // 生成代码到临时目录
            FastAutoGenerator.create(this.constructJdbcUrl(dataSource.getDbType(), 
                                                            dataSource.getHost(), 
                                                            dataSource.getPort(),
                                                            codeGenerateDTO.getDbName()),
                                                    dataSource.getUsername(), 
                                                    dataSource.getPassword())
                .globalConfig(builder -> {  
                    builder.author("System")
                           .enableSwagger()
                           .outputDir(tempDir.toString());
                })
                .packageConfig(builder -> builder
                    .parent(codeGenerateDTO.getPackageName())
                    .pathInfo(Collections.singletonMap(OutputFile.xml, tempDir + "/mapper"))
                )
                .strategyConfig(builder -> builder
                    .addInclude(codeGenerateDTO.getTableName())
                    .entityBuilder()
                    .enableLombok()
                    .naming(NamingStrategy.underline_to_camel)
                    .columnNaming(NamingStrategy.underline_to_camel)
                    .controllerBuilder()
                    .enableRestStyle()
                )
                .templateEngine(new VelocityTemplateEngine())
                .execute();
            
            // 将生成的代码打包到zip
            zipDirectory(tempDir.toFile(), zipOut);
            
            // 删除临时目录
            FileUtils.deleteDirectory(tempDir.toFile());
            
            return byteArrayOutputStream.toByteArray();
        }
    }
    
    private void zipDirectory(File directory, ZipOutputStream zipOut) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(file, zipOut);
                continue;
            }
            
            String fullPath = file.getPath().replace('\\', '/');
            int comIndex = fullPath.indexOf("com/");
            String relativePath = comIndex >= 0 ? fullPath.substring(comIndex) : fullPath;
            zipOut.putNextEntry(new ZipEntry(relativePath.replace('\\', '/')));
            zipOut.write(Files.readAllBytes(file.toPath()));
            zipOut.closeEntry();
        }
    }


    /**
     * 构建JDBC URL
     * @param dbType 数据库类型
     * @param host 主机地址
     * @param port 端口号
     * @param databaseName 数据库名称
     * @return JDBC URL
     */
    private String constructJdbcUrl(String dbType, String host, int port, String databaseName) {
        switch (dbType.toLowerCase()) {
            case "mysql":
                return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC", host, port, databaseName);
            case "postgresql":
                return String.format("jdbc:postgresql://%s:%d/%s", host, port, databaseName);
            case "oracle":
                return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, databaseName);
            default:
                throw new IllegalArgumentException("不支持的数据库类型");
        }
    }
}
