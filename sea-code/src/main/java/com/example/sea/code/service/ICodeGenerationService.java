package com.example.sea.code.service;

import java.io.IOException;

import com.example.sea.code.entity.dto.CodeGenerateDTO;

/**
 * 代码生成服务
 * @author liuhuan
 * @date 2025-03-24
 */
public interface ICodeGenerationService {

    /**
     * 根据表名默认生成
     * @param tableName
     * @return 压缩包字节
     * @throws IOException
     */
    byte[] generateCode(CodeGenerateDTO codeGenerateDTO) throws IOException;

    /**
     * 根据表名和自定义配置生成代码
     * @param codeGenerateDTO 代码生成参数
     * @return 压缩包字节
     * @throws IOException IO异常
     */
    byte[] generateCodeByConfig(CodeGenerateDTO codeGenerateDTO) throws IOException;
}
