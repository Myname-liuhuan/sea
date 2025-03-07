package com.example.sea.code.service;

import java.io.IOException;

public interface CodeGenerationService {
    byte[] generateCode(String tableName) throws IOException;
}
