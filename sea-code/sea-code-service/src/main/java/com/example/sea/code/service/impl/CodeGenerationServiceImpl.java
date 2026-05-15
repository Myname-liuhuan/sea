package com.example.sea.code.service.impl;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.ColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.example.sea.code.common.utils.JdbcUrlBuilder;
import com.example.sea.code.entity.CodegenDataSourcePO;
import com.example.sea.code.api.dto.CodeGenColumnSettingDTO;
import com.example.sea.code.api.dto.CodeGenerateDTO;
import com.example.sea.code.dao.CodegenDataSourceMapper;
import com.example.sea.code.service.ICodeGenerationService;
import com.example.sea.common.core.exception.BusinessException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CodeGenerationServiceImpl implements ICodeGenerationService {

    private final CodegenDataSourceMapper codegenDataSourceMapper;

    private static final String TEMP_DIR_PREFIX = "codegen";

    public CodeGenerationServiceImpl(CodegenDataSourceMapper codegenDataSourceMapper) {
        this.codegenDataSourceMapper = codegenDataSourceMapper;
    }

    @Override
    public byte[] generateCode(CodeGenerateDTO codeGenerateDTO) throws IOException {
        return generateToZip(codeGenerateDTO, null);
    }

    @Override
    public byte[] generateCodeByConfig(CodeGenerateDTO codeGenerateDTO) throws IOException {
        List<CodeGenColumnSettingDTO> settingList = codeGenerateDTO.getColumnSettingList();
        if (CollectionUtils.isEmpty(settingList)) {
            throw new BusinessException("自定义字段设置不能为空");
        }

        List<String> ignoreColumns = settingList.stream()
                .filter(setting -> !setting.getIsEntityField())
                .map(CodeGenColumnSettingDTO::getColumnName)
                .collect(Collectors.toList());

        Map<String, String> typeOverride = settingList.stream()
                .filter(CodeGenColumnSettingDTO::getIsEntityField)
                .collect(Collectors.toMap(
                        CodeGenColumnSettingDTO::getColumnName,
                        CodeGenColumnSettingDTO::getEntityType,
                        (existing, replacement) -> existing));

        return generateToZip(codeGenerateDTO, builder -> {
            builder.entityBuilder()
                    .addIgnoreColumns(ignoreColumns)
                    .columnTypeMapping((columnName, defaultType) -> {
                        String override = typeOverride.get(columnName);
                        if (override != null) {
                            return ColumnType.valueOf(override.toUpperCase());
                        }
                        return defaultType;
                    });
        });
    }

    private byte[] generateToZip(CodeGenerateDTO dto, Consumer<StrategyConfig.Builder> extraStrategyConfig) throws IOException {
        CodegenDataSourcePO dataSource = codegenDataSourceMapper.selectById(dto.getDataSourceId());
        if (Objects.isNull(dataSource)) {
            throw new BusinessException("数据源不存在");
        }

        Path tempDir = Files.createTempDirectory(TEMP_DIR_PREFIX);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {

            String jdbcUrl = JdbcUrlBuilder.build(
                    dataSource.getDbType(), dataSource.getHost(), dataSource.getPort(), dto.getDbName());

            FastAutoGenerator.create(jdbcUrl, dataSource.getUsername(), dataSource.getPassword())
                    .globalConfig(builder -> builder
                            .author("admin")
                            .commentDate("yyyy-MM-dd")
                            .outputDir(tempDir.toString()))
                    .packageConfig(builder -> builder
                            .parent(dto.getPackageName())
                            .mapper("dao")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, tempDir + "/mappers")))
                    .strategyConfig(builder -> {
                        builder.addInclude(dto.getTableName())
                                .entityBuilder()
                                .enableLombok()
                                .naming(NamingStrategy.underline_to_camel)
                                .columnNaming(NamingStrategy.underline_to_camel)
                                .controllerBuilder()
                                .enableRestStyle();

                        if (extraStrategyConfig != null) {
                            extraStrategyConfig.accept(builder);
                        }
                    })
                    .templateEngine(new VelocityTemplateEngine())
                    .execute();

            zipDirectory(tempDir.toFile(), zipOut);

            return byteArrayOutputStream.toByteArray();
        } finally {
            FileUtils.deleteDirectory(tempDir.toFile());
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
            String filePathInZip = fullPath.substring(fullPath.indexOf(TEMP_DIR_PREFIX));

            zipOut.putNextEntry(new ZipEntry(filePathInZip));
            zipOut.write(Files.readAllBytes(file.toPath()));
            zipOut.closeEntry();
        }
    }
}
