package com.example.sea.media.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.media.service.WatermarkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 水印接口
 * @author liuhuan
 * @date 2025/11/4
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/watermark")
@Tag(name = "水印管理", description = "视频水印相关接口")
public class WatermarkController {
    
    private final WatermarkService watermarkService;

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
            // 抛出业务异常，由全局异常处理器统一处理并返回 CommonResult
            throw new BusinessException("视频水印处理失败：" + e.getMessage(), e);
        }
    }

    /**
     * 给PDF添加文字水印
     */
    @PostMapping("/addTextWatermark2PDF")
    @Operation(summary = "给PDF添加文字水印", description = "上传PDF文件并添加文字水印，返回带水印的PDF文件")
    public ResponseEntity<InputStreamResource> addTextWatermark2PDF(
            @Parameter(description = "PDF文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "水印文字内容", required = true) @RequestParam("watermarkText") String watermarkText) {
        
        log.info("接收到PDF水印请求，文件名：{}，水印内容：{}", file.getOriginalFilename(), watermarkText);
        
        try {
            // 调用service处理PDF水印
            File watermarkedFile = watermarkService.addTextWatermark2PDF(file, watermarkText);
            
            // 设置响应头，触发浏览器下载
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename*=UTF-8''" + java.net.URLEncoder.encode(
                            "watermarked_" + file.getOriginalFilename(), "UTF-8"));
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
            
            // 返回文件流
            InputStream inputStream = new FileInputStream(watermarkedFile);
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            log.info("PDF水印处理完成，返回文件：{}", watermarkedFile.getName());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(watermarkedFile.length())
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("PDF水印处理失败", e);
            // 抛出业务异常，由全局异常处理器统一处理并返回 CommonResult
            throw new BusinessException("PDF水印处理失败：" + e.getMessage(), e);
        }
    }

    /**
     * 给图片添加文字水印
     */
    @PostMapping("/addTextWatermark2Image")
    @Operation(summary = "给图片添加文字水印", description = "上传图片并添加文字水印，返回带水印的图片文件")
    public ResponseEntity<InputStreamResource> addTextWatermark2Image(
            @Parameter(description = "图片文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "水印文字内容", required = true) @RequestParam("watermarkText") String watermarkText) {
        
        log.info("接收到图片水印请求，文件名：{}，水印内容：{}", file.getOriginalFilename(), watermarkText);
        
        try {
            // 调用service处理图片水印
            File watermarkedFile = watermarkService.addTextWatermark2Image(file, watermarkText);
            
            // 根据原文件扩展名确定MIME类型
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            String mimeType = getImageMimeType(fileExtension);
            
            // 设置响应头，触发浏览器下载
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename*=UTF-8''" + java.net.URLEncoder.encode(
                            "watermarked_" + originalFilename, "UTF-8"));
            headers.add(HttpHeaders.CONTENT_TYPE, mimeType);
            
            // 返回文件流
            InputStream inputStream = new FileInputStream(watermarkedFile);
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            log.info("图片水印处理完成，返回文件：{}", watermarkedFile.getName());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(watermarkedFile.length())
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("图片水印处理失败", e);
            // 抛出业务异常，由全局异常处理器统一处理并返回 CommonResult
            throw new BusinessException("图片水印处理失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 根据文件扩展名获取图片MIME类型
     */
    private String getImageMimeType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".bmp":
                return "image/bmp";
            case ".webp":
                return "image/webp";
            default:
                return "image/jpeg"; // 默认使用jpeg
        }
    }

    
}
