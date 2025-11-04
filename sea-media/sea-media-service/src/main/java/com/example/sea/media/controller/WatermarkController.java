package com.example.sea.media.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.sea.media.service.WatermarkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 水印接口
 * @author liuhuan
 * @date 2025/11/4
 */
@Slf4j
@RestController
@RequestMapping("/watermark")
@Tag(name = "水印管理", description = "视频水印相关接口")
public class WatermarkController {
    
    @Autowired
    private WatermarkService watermarkService;

    /**
     * 给视频添加文字水印
     */
    @PostMapping("/addTextWatermark2Video")
    @Operation(summary = "给视频添加文字水印", description = "上传视频文件并添加文字水印，返回带水印的视频文件")
    public ResponseEntity<InputStreamResource> addTextWatermark2Video(
            @Parameter(description = "视频文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "水印文字内容", required = true) @RequestParam("watermarkText") String watermarkText) {
        
        log.info("接收到视频水印请求，文件名：{}，水印内容：{}", file.getOriginalFilename(), watermarkText);
        
        try {
            // 调用service处理视频水印
            File watermarkedFile = watermarkService.addTextWatermarkToVideo(file, watermarkText);
            
            // 设置响应头，触发浏览器下载
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename*=UTF-8''" + java.net.URLEncoder.encode(
                            "watermarked_" + file.getOriginalFilename(), "UTF-8"));
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            
            // 返回文件流
            InputStream inputStream = new FileInputStream(watermarkedFile);
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            log.info("视频水印处理完成，返回文件：{}", watermarkedFile.getName());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(watermarkedFile.length())
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("视频水印处理失败", e);
            throw new RuntimeException("视频水印处理失败：" + e.getMessage(), e);
        }
    }
    
}
