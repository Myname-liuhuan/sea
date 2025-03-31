package com.example.sea.code.controller;

import com.example.sea.code.entity.dto.CodeGenerateDTO;
import com.example.sea.code.service.ICodeGenerationService;
import com.example.sea.common.result.CommonResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final ICodeGenerationService codeGenerationService;

    @Autowired
    public CodeGenerationController(ICodeGenerationService codeGenerationService) {
        this.codeGenerationService = codeGenerationService;
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateCode(CodeGenerateDTO codeGenerateDTO) throws IOException {
        byte[] zipBytes = codeGenerationService.generateCode(codeGenerateDTO);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"generated-code.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }

    public CommonResult<String> getDataSourceList(){
        return null;
    }
}
