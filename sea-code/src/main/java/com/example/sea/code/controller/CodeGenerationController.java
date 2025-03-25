package com.example.sea.code.controller;

import com.example.sea.code.service.CodeGenerationService;

import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/codegen")
public class CodeGenerationController {

    private final CodeGenerationService codeGenerationService;

    @Autowired
    public CodeGenerationController(CodeGenerationService codeGenerationService) {
        this.codeGenerationService = codeGenerationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateCode(@RequestBody @NotBlank String tableName) throws IOException {
        byte[] zipBytes = codeGenerationService.generateCode(tableName);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"generated-code.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }
}
