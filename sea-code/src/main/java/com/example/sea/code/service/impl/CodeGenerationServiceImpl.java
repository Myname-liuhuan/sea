package com.example.sea.code.service.impl;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.example.sea.code.entity.CodegenDataSource;
import com.example.sea.code.entity.dto.CodeGenColumnSettingDTO;
import com.example.sea.code.entity.dto.CodeGenerateDTO;
import com.example.sea.code.exception.BusinessException;
import com.example.sea.code.mapper.CodegenDataSourceMapper;
import com.example.sea.code.service.ICodeGenerationService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
                throw new BusinessException("数据源不存在");
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

    /**
     * 生成代码-自定义实体类字段
     * @param codeGenerateDTO 代码生成参数
     * @return 生成的代码文件
     */
    @Override
    public byte[] generateCodeByConfig(CodeGenerateDTO codeGenerateDTO) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            
            // 创建临时目录
            Path tempDir = Files.createTempDirectory("codegen");
            
            // 查询数据源信息
            CodegenDataSource dataSource = codegenDataSourceMapper.selectById(codeGenerateDTO.getDataSourceId());
            if (Objects.isNull(dataSource)) {
                throw new BusinessException("数据源不存在");
            }

            List<CodeGenColumnSettingDTO> settingList = codeGenerateDTO.getColumnSettingList();
            if (CollectionUtils.isEmpty(settingList)) {
                throw new BusinessException("自定义字段设置不能为空");
            }
            //提取出需要忽略的列
            List<String> ignoreColumns = settingList.stream()
                                                    .filter(setting -> !setting.getIsEntityField())
                                                    .map(CodeGenColumnSettingDTO::getColumnName)
                                                    .collect(Collectors.toList());

            //将自定义字段转为map
            Map<String, Object> customMap = new HashMap<>();
            Map<String, String> typeOverride = settingList.stream()
                .collect(Collectors.toMap(CodeGenColumnSettingDTO::getColumnName, CodeGenColumnSettingDTO::getEntityType, 
                                          (existing, replacement) -> existing));
            
            customMap.put("typeOverride", typeOverride);
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
                    .addIgnoreColumns(ignoreColumns)
                    .enableLombok()
                    .naming(NamingStrategy.underline_to_camel)
                    .columnNaming(NamingStrategy.underline_to_camel)
                    .controllerBuilder()
                    .enableRestStyle()
                )
                //自定义字段类型映射
                .injectionConfig(consumer -> consumer.customMap(customMap))
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
            //解决c:空文件夹问题
            if (comIndex < 0) {
                continue;
            }
            String relativePath = fullPath.substring(comIndex);
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
