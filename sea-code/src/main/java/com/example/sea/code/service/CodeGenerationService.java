package com.example.sea.code.service;

import java.io.IOException;

/**
 * 代码生成服务
 * @author liuhuan
 * @date 2025-03-24
 */
public interface CodeGenerationService {

    /**
     * 根据表名默认生成
     * @param tableName
     * @return
     * @throws IOException
     */
    byte[] generateCode(String tableName) throws IOException;
}
