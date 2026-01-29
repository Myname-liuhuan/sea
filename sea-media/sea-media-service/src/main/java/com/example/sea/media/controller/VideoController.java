package com.example.sea.media.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.media.service.WatermarkService;

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
@RequestMapping("/watermark")
@Tag(name = "视频处理", description = "视频处理接口")
public class VideoController {
    
    private final WatermarkService watermarkService;

    
}
