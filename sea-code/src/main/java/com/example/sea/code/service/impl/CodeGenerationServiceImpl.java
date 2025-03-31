package com.example.sea.code.service.impl;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.example.sea.code.entity.dto.CodeGenerateDTO;
import com.example.sea.code.service.ICodeGenerationService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 代码生成服务实现类
 * @author liuhuan
 * @date 2025-03-24
 */
@Service
public class CodeGenerationServiceImpl implements ICodeGenerationService {

    @Value("${codegen.jdbc.url}")
    private String jdbcUrl;
    
    @Value("${codegen.jdbc.username}")
    private String username;
    
    @Value("${codegen.jdbc.password}")
    private String password;
    
    @Value("${codegen.output-dir}")
    private String outputDir;
    

    @Override
    public byte[] generateCode(CodeGenerateDTO codeGenerateDTO) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            
            // 创建临时目录
            Path tempDir = Files.createTempDirectory("codegen");
            
            // 生成代码到临时目录
            FastAutoGenerator.create(jdbcUrl, username, password)
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
}
