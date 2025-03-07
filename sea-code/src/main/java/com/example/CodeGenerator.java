package com.example;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Collections;

/**
 * 代码生成器
 * @author your_name
 * @since 1.0.0
 * @version 1.0.0
 */
public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/test", "root", "lh123456")
                .globalConfig(builder -> {
                    builder.author("huan") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("D://file//codeAuto//src"); // 指定输出目录
                })
//                .dataSourceConfig(builder -> builder
//                        .dbType(DbType.MYSQL) // 设置数据库类型
//                        .url("jdbc:mysql://localhost:3306/test") // 数据库连接 URL
//                        .driverName("com.mysql.cj.jdbc.Driver") // 数据库驱动类名
//                        .username("root") // 数据库用户名
//                        .password("lh123456") // 数据库密码
//                )
                .packageConfig(builder -> builder
                        .parent("com.example") // 设置父包名
                        .moduleName("sea.media") // 设置父包模块名
                        .pathInfo(Collections.singletonMap(OutputFile.xml, "D://file//codeAuto//mapper")) // 设置 mapper XML 生成路径
                )
                .strategyConfig(builder -> builder
                        .addInclude("sys_user") // 设置需要生成的表名
                        .addTablePrefix("prefix_") // 设置过滤表前缀
                        .entityBuilder()
                        .enableLombok() // 开启 Lombok 模型
                        .naming(NamingStrategy.underline_to_camel) // 设置命名策略
                        .columnNaming(NamingStrategy.underline_to_camel) // 设置列命名策略
                        .controllerBuilder()
                        .enableRestStyle() // 开启 RESTful 风格控制器
                )
                .templateConfig(builder -> builder
                    .entity("/templates/entity.java.vm")
                    .controller("/templates/controller.java.vm")
                    .service("/templates/service.java.vm")
                    .serviceImpl("/templates/serviceImpl.java.vm")
                    .mapper("/templates/mapper.java.vm")
                )
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}
