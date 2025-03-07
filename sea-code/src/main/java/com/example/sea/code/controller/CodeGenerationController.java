package com.example.sea.code.controller;

import com.example.sea.code.service.CodeGenerationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/codegen")
public class CodeGenerationController {

    private final CodeGenerationService codeGenerationService;

    public CodeGenerationController(CodeGenerationService codeGenerationService) {
        this.codeGenerationService = codeGenerationService;
    }

    @PostMapping("/generate")
    public String generateCode(@RequestParam String tableName) {
        codeGenerationService.generateCode(tableName);
        return "Code generated successfully for table: " + tableName;
    }
}
