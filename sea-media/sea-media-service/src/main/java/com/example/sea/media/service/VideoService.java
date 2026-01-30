package com.example.sea.media.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

/**
 * 视频处理服务
 * @author liuhuan
 * @date 2026/01/19
 */
public interface VideoService {

    /**
     * 提取视频音频为MP3
     *
     * @param videoFile 视频文件
     * @return 提取的MP3音频文件
     * @throws Exception 处理异常
     */
    File extractAudioToMp3(MultipartFile videoFile) throws Exception;

}
