package com.example.sea.media.controller;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.sea.media.service.VideoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频处理接口
 * @author liuhuan
 * @date 2026/01/19
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/video")
@Tag(name = "视频处理", description = "视频处理接口")
public class VideoController {

    private final VideoService videoService;

    /**
     * 提取视频音频为MP3
     *
     * @param videoFile 视频文件
     * @return MP3音频文件
     */
    @PostMapping("/extract-audio")
    @Operation(summary = "提取视频音频为MP3", description = "将视频文件中的音频提取为MP3格式（320k高音质）")
    public ResponseEntity<FileSystemResource> extractAudioToMp3(
            @Parameter(description = "视频文件", required = true)
            @RequestPart("file") MultipartFile videoFile) {

        try {
            File audioFile = videoService.extractAudioToMp3(videoFile);

            String originalFilename = videoFile.getOriginalFilename();
            String baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            String downloadFilename = baseName + ".mp3";

            FileSystemResource resource = new FileSystemResource(audioFile);

            // 使用 ContentDisposition 正确处理中文文件名编码 (RFC 5987)
            ContentDisposition disposition = ContentDisposition.builder("attachment")
                    .filename(downloadFilename, StandardCharsets.UTF_8)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .contentLength(audioFile.length())
                    .body(resource);

        } catch (Exception e) {
            log.error("提取音频失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
