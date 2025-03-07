package com.example.sea.code.service.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.example.sea.code.service.CodeGenerationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CodeGenerationServiceImpl implements CodeGenerationService {

    @Value("${codegen.jdbc.url}")
    private String jdbcUrl;
    
    @Value("${codegen.jdbc.username}")
    private String username;
    
    @Value("${codegen.jdbc.password}")
    private String password;
    
    @Value("${codegen.output-dir}")
    private String outputDir;
    
    @Value("${codegen.base-package}")
    private String basePackage;

    @Override
    public void generateCode(String tableName) {
        FastAutoGenerator.create(jdbcUrl, username, password)
            .globalConfig(builder -> {
                builder.author("System")
                       .enableSwagger()
                       .outputDir(outputDir);
            })
            .packageConfig(builder -> builder
                .parent(basePackage)
                .pathInfo(Collections.singletonMap(OutputFile.xml, outputDir + "/mapper"))
            )
            .strategyConfig(builder -> builder
                .addInclude(tableName)
                .entityBuilder()
                .enableLombok()
                .naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                .controllerBuilder()
                .enableRestStyle()
            )
            .templateEngine(new VelocityTemplateEngine())
            .execute();
    }
}
