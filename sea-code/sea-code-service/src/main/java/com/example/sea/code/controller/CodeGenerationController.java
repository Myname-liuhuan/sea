package com.example.sea.code.controller;

import com.example.sea.code.api.dto.CodeGenerateDTO;
import com.example.sea.code.service.ICodeGenerationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

/**
 * 代码生成控制器
 * @author liuhuan
 * @date 2025-02-28
 */
@RestController
@RequestMapping("/codegen")
public class CodeGenerationController {

    private final ICodeGenerationService codeGenerationService;

    @Autowired
    public CodeGenerationController(ICodeGenerationService codeGenerationService) {
        this.codeGenerationService = codeGenerationService;
    }

    /**
     * 生成代码
     *
     * @param codeGenerateDTO 代码生成参数
     * @return 生成的代码文件
     * @throws IOException IO异常
     */
    @GetMapping("/generate")
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

    /**
     * 生成代码-自定义实体类字段
     * @param codeGenerateDTO
     * @return
     * @throws IOException
     */
    @PostMapping("/generateCodeByConfig")
    public ResponseEntity<byte[]> generateCodeByConfig(@Validated @RequestBody CodeGenerateDTO codeGenerateDTO) throws IOException {
        byte[] zipBytes = codeGenerationService.generateCodeByConfig(codeGenerateDTO);
        //当前时间yyyyMMddHHmmss字符串
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
