package com.example.sea.code.controller;

import com.example.sea.code.entity.dto.CodeGenerateDTO;
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

/**
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
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"generated-code.zip\"")
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
        byte[] zipBytes = codeGenerationService.generateCode(codeGenerateDTO);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"generated-code.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }

}
