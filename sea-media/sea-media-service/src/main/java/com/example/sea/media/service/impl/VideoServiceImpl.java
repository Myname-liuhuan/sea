package com.example.sea.media.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private static final String TMP_PATH_PREFIX = "tempAudio";
    private static final int FILE_COPY_BUFFER_SIZE = 8192;
    private static final String FFMPEG_COMMAND = "ffmpeg";
    private static final String AUDIO_BITRATE = "320k";
    private static final int AUDIO_QUALITY = 0;
    private static final String MP3_EXTENSION = ".mp3";

    @Override
    public File extractAudioToMp3(MultipartFile videoFile) throws Exception {
        log.info("开始提取视频音频为MP3，文件名：{}", videoFile.getOriginalFilename());

        // 验证文件名
        String originalFilename = videoFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 创建输出目录
        Path outputDir = Files.createTempDirectory(TMP_PATH_PREFIX);

        File outputFile = null;
        File tempInputFile = null;

        try {
            // 生成输出文件名
            String baseName = getFileBaseName(originalFilename);
            String outputFileName = UUID.randomUUID() + "_" + baseName + MP3_EXTENSION;
            outputFile = outputDir.resolve(outputFileName).toFile();

            // 保存上传的文件到临时文件
            String fileExtension = getFileExtension(originalFilename);
            tempInputFile = File.createTempFile("input_", fileExtension);
            copyToFile(videoFile.getInputStream(), tempInputFile);

            // 使用ffmpeg提取音频为MP3
            extractAudioWithFFmpeg(tempInputFile, outputFile);

            log.info("音频提取完成，输出文件：{}", outputFile.getAbsolutePath());

            return outputFile;

        } catch (IOException e) {
            log.error("提取音频失败", e);
            // 清理已创建的输出文件
            if (outputFile != null && outputFile.exists()) {
                outputFile.delete();
            }
            throw new RuntimeException("提取音频失败：" + e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error("ffmpeg执行被中断", e);
            Thread.currentThread().interrupt();
            // 清理已创建的输出文件
            if (outputFile != null && outputFile.exists()) {
                outputFile.delete();
            }
            throw new RuntimeException("ffmpeg执行被中断：" + e.getMessage(), e);
        } finally {
            // 删除临时输入文件
            if (tempInputFile != null && tempInputFile.exists()) {
                tempInputFile.delete();
            }
            // 注意：outputFile 需要由调用者使用后清理，这里不删除
        }
    }

    /**
     * 获取文件扩展名（包含点号）
     * @param filename 文件名
     * @return 扩展名，如 ".mp4"，无扩展名返回 ""
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }

    /**
     * 获取文件基础名（不含扩展名）
     * @param filename 文件名
     * @return 基础名
     */
    private String getFileBaseName(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(0, lastDotIndex) : filename;
    }

    /**
     * 将输入流复制到文件
     * @param inputStream 输入流
     * @param file 目标文件
     * @throws IOException 复制失败
     */
    private void copyToFile(InputStream inputStream, File file) throws IOException {
        try (InputStream is = inputStream;
             FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[FILE_COPY_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 使用ffmpeg命令行提取音频为MP3
     *
     * @param inputFile 输入视频文件
     * @param outputFile 输出MP3文件
     * @throws IOException 执行异常
     * @throws InterruptedException 执行被中断
     */
    private void extractAudioWithFFmpeg(File inputFile, File outputFile)
            throws IOException, InterruptedException {
        log.info("使用ffmpeg提取音频为MP3");

        // 构建ffmpeg命令
        List<String> command = new ArrayList<>();
        command.add(FFMPEG_COMMAND);
        command.add("-i");
        command.add(inputFile.getAbsolutePath());
        command.add("-vn"); // 不重新编码视频（忽略视频流）
        command.add("-c:a");
        command.add("libmp3lame"); // 音频编码器：libmp3lame
        command.add("-b:a");
        command.add(AUDIO_BITRATE); // 比特率：320k 高音质
        command.add("-q:a");
        command.add(String.valueOf(AUDIO_QUALITY)); // 音频质量
        command.add("-y"); // 覆盖输出文件
        command.add(outputFile.getAbsolutePath());

        // 执行命令
        executeFFmpegCommand(command);
    }

    /**
     * 执行ffmpeg命令
     *
     * @param command 命令列表
     * @throws IOException 执行异常
     * @throws InterruptedException 执行被中断
     */
    private void executeFFmpegCommand(List<String> command)
            throws IOException, InterruptedException {
        log.info("执行ffmpeg命令：{}", String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 合并错误流到标准输出

        Process process = processBuilder.start();

        // 使用 BufferedReader 读取字符流，避免编码问题
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                log.debug("ffmpeg输出：{}", line);
            }
        }

        // 等待命令执行完成
        int exitCode = process.waitFor();

        String fullOutput = output.toString();
        if (!fullOutput.isEmpty()) {
            log.info("ffmpeg完整输出：{}", fullOutput);
        }

        if (exitCode != 0) {
            String errorMsg = String.format("ffmpeg命令执行失败，退出码：%d，输出：%s",
                                          exitCode, fullOutput);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        log.info("ffmpeg命令执行成功");
    }
}
