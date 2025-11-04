package com.example.sea.media.interfaces.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * 水印参数接收类
 * @author liuhuan
 * @date 2025/11/4
 */
@Data
public class WatermarkDTO {
    
    /** 水印内容 */
    private String watermarkText;

    /** 文件 */
    private MultipartFile file;


}
