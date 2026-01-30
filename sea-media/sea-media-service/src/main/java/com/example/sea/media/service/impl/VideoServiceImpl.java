package com.example.sea.media.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.sea.media.service.VideoService;

import lombok.extern.slf4j.Slf4j;

/**
 * 视频处理服务实现类
 * @author liuhuan
 * @date 2026/01/19
 */
@Slf4j
@Service
public class VideoServiceImpl implements VideoService {

    private final String tmpPath = "tempAudio";

    @Override
    public File extractAudioToMp3(MultipartFile videoFile) throws Exception {
        log.info("开始提取视频音频为MP3，文件名：{}", videoFile.getOriginalFilename());

        // 创建输出目录
        Path outputDir = Files.createTempDirectory(tmpPath);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        // 生成输出文件名
        String originalFilename = videoFile.getOriginalFilename();
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String outputFileName = UUID.randomUUID().toString() + "_" + baseName + ".mp3";
        File outputFile = outputDir.resolve(outputFileName).toFile();

        File tempInputFile = null;

        try {
            // 保存上传的文件到临时文件
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            tempInputFile = File.createTempFile("input_", fileExtension);
            try (InputStream inputStream = videoFile.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(tempInputFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // 使用ffmpeg提取音频为MP3
            extractAudioWithFFmpeg(tempInputFile, outputFile);

            log.info("音频提取完成，输出文件：{}", outputFile.getAbsolutePath());

            return outputFile;

        } catch (Exception e) {
            log.error("提取音频失败", e);
            throw new RuntimeException("提取音频失败：" + e.getMessage(), e);
        } finally {
            // 删除临时输入文件
            if (tempInputFile != null && tempInputFile.exists()) {
                tempInputFile.delete();
            }
        }
    }

    /**
     * 使用ffmpeg命令行提取音频为MP3
     *
     * @param inputFile 输入视频文件
     * @param outputFile 输出MP3文件
     * @throws Exception 处理异常
     */
    private void extractAudioWithFFmpeg(File inputFile, File outputFile) throws Exception {
        log.info("使用ffmpeg提取音频为MP3");

        // 构建ffmpeg命令
        List<String> command = new ArrayList<>();

        String ffmpegCmd = "ffmpeg";
        command.add(ffmpegCmd);

        // 输入文件
        command.add("-i");
        command.add(inputFile.getAbsolutePath());

        // 不重新编码视频（忽略视频流）
        command.add("-vn");

        // 音频编码器：libmp3lame
        command.add("-c:a");
        command.add("libmp3lame");

        // 比特率：320k 高音质
        command.add("-b:a");
        command.add("320k");

        // 音频质量
        command.add("-q:a");
        command.add("0");

        // 输出文件
        command.add("-y"); // 覆盖输出文件
        command.add(outputFile.getAbsolutePath());

        // 执行命令
        executeFFmpegCommand(command);
    }

    /**
     * 执行ffmpeg命令
     *
     * @param command 命令列表
     * @throws Exception 执行异常
     */
    private void executeFFmpegCommand(List<String> command) throws Exception {
        String commandStr = String.join(" ", command);
        log.info("执行ffmpeg命令：{}", commandStr);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 合并错误流到标准输出

        Process process = processBuilder.start();

        // 读取输出（用于调试和错误诊断）
        StringBuilder output = new StringBuilder();
        try (InputStream inputStream = process.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String chunk = new String(buffer, 0, bytesRead);
                output.append(chunk);
                log.debug("ffmpeg输出片段：{}", chunk.trim());
            }
        }

        // 等待命令执行完成
        int exitCode = process.waitFor();

        String fullOutput = output.toString();
        if (fullOutput.length() > 0) {
            log.info("ffmpeg完整输出：{}", fullOutput);
        }

        if (exitCode != 0) {
            String errorMsg = String.format("ffmpeg命令执行失败，退出码：%d，命令：%s，输出：%s",
                                          exitCode, commandStr, fullOutput);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        log.info("ffmpeg命令执行成功");
    }
}
