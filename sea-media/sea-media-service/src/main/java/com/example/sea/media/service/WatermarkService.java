package com.example.sea.media.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

/**
 * 水印服务接口
 * @author liuhuan
 * @date 2025/11/4
 */
public interface WatermarkService {
    
    /**
     * 给视频添加文字水印
     * 
     * @param videoFile 视频文件
     * @param watermarkText 水印文字内容
     * @return 添加水印后的视频文件
     * @throws Exception 处理异常
     */
    File addTextWatermarkToVideo(MultipartFile videoFile, String watermarkText) throws Exception;
    
}
