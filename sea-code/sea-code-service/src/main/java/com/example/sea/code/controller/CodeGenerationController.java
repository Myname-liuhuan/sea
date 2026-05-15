package com.example.sea.code.controller;

import com.example.sea.code.api.constants.CodePermissionConstants;
import com.example.sea.code.api.dto.CodeGenerateDTO;
import com.example.sea.code.service.ICodeGenerationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ContentDisposition;

@RestController
@RequestMapping("/codegen")
@RequiredArgsConstructor
@Tag(name = "代码生成", description = "代码生成相关操作接口")
public class CodeGenerationController {

    private final ICodeGenerationService codeGenerationService;

    @GetMapping("/generate")
    @Operation(summary = "默认生成代码", description = "根据表名和包名生成MyBatis-Plus CRUD代码")
    @PreAuthorize("hasAuthority('" + CodePermissionConstants.CODE_GENERATE + "')")
    public ResponseEntity<byte[]> generateCode(@Validated CodeGenerateDTO codeGenerateDTO) throws IOException {
        byte[] zipBytes = codeGenerationService.generateCode(codeGenerateDTO);

        ContentDisposition disposition = ContentDisposition.builder("attachment")
                .filename("generated-code.zip", StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }

    @PostMapping("/generateCodeByConfig")
    @Operation(summary = "自定义配置生成代码", description = "根据自定义列配置生成代码")
    @PreAuthorize("hasAuthority('" + CodePermissionConstants.CODE_GENERATE + "')")
    public ResponseEntity<byte[]> generateCodeByConfig(@Validated @RequestBody CodeGenerateDTO codeGenerateDTO) throws IOException {
        byte[] zipBytes = codeGenerationService.generateCodeByConfig(codeGenerateDTO);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String filename = "generated-code" + LocalDateTime.now().format(formatter) + ".zip";

        ContentDisposition disposition = ContentDisposition.builder("attachment")
                .filename(filename, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }

}
