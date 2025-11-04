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

import com.example.sea.media.service.WatermarkService;

import lombok.extern.slf4j.Slf4j;

/**
 * 水印服务实现类 - 使用宿主机ffmpeg
 * @author liuhuan
 * @date 2025/11/4
 */
@Slf4j
@Service
public class WatermarkServiceImpl implements WatermarkService {
    
    private final String tmpPath = "tempVideo";
    
    @Override
    public File addTextWatermarkToVideo(MultipartFile videoFile, String watermarkText) throws Exception {
        log.info("开始给视频添加文字水印，文件名：{}，水印内容：{}", videoFile.getOriginalFilename(), watermarkText);
        
        // 创建输出目录
        Path outputDir = Files.createTempDirectory(tmpPath);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        
        // 生成输出文件名
        String originalFilename = videoFile.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String outputFileName = UUID.randomUUID().toString() + "_watermarked" + fileExtension;
        File outputFile = outputDir.resolve(outputFileName).toFile();
        
        File tempInputFile = null;
        
        try {
            // 保存上传的文件到临时文件
            tempInputFile = File.createTempFile("input_", fileExtension);
            try (InputStream inputStream = videoFile.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(tempInputFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            // 使用ffmpeg添加文字水印
            addTextWatermarkWithFFmpeg(tempInputFile, outputFile, watermarkText);
            
            log.info("视频水印添加完成，输出文件：{}", outputFile.getAbsolutePath());
            
            return outputFile;
            
        } catch (Exception e) {
            log.error("添加视频水印失败", e);
            throw new RuntimeException("添加视频水印失败：" + e.getMessage(), e);
        } finally {
            // 删除临时输入文件
            if (tempInputFile != null && tempInputFile.exists()) {
                tempInputFile.delete();
            }
        }
    }
    
    /**
     * 使用ffmpeg命令行添加文字水印
     * 
     * @param inputFile 输入视频文件
     * @param outputFile 输出视频文件
     * @param watermarkText 水印文字
     * @throws Exception 处理异常
     */
    private void addTextWatermarkWithFFmpeg(File inputFile, File outputFile, String watermarkText) {
        // 首先尝试使用drawtext滤镜
        try {
            addTextWatermarkWithAlternative(inputFile, outputFile, watermarkText);
        } catch (Exception e) {
            log.warn("使用drawtext滤镜失败，尝试替代方案：{}", e.getMessage());
            throw new RuntimeException("使用drawtext滤镜添加水印失败：" + e.getMessage(), e);
        }
    }
    
    
    /**
     * 使用替代方案添加文字水印（使用overlay滤镜）
     * 创建一个透明图片作为水印，然后overlay到视频上
     */
    private void addTextWatermarkWithAlternative(File inputFile, File outputFile, String watermarkText) throws Exception {
        log.info("使用替代方案添加水印：创建透明图片并overlay");
        
        // 创建一个临时图片文件作为水印
        File watermarkImage = createTextWatermarkImage(watermarkText);
        
        try {
            // 构建ffmpeg命令
            List<String> command = new ArrayList<>();
            
            // 检查ffmpeg是否可用
            String ffmpegCmd = getFFmpegCommand();
            command.add(ffmpegCmd);
            
            // 输入文件
            command.add("-i");
            command.add(inputFile.getAbsolutePath());
            
            // 水印图片
            command.add("-i");
            command.add(watermarkImage.getAbsolutePath());
            
            // 视频滤镜：overlay水印图片
            String filter = "overlay=W-w-10:H-h-10:format=auto,format=yuv420p";
            
            command.add("-filter_complex");
            command.add(filter);
            
            // 音频编码：直接复制，不重新编码
            command.add("-c:a");
            command.add("copy");
            
            // 视频编码：使用libx264，保持质量
            command.add("-c:v");
            command.add("libx264");
            
            // 预设：快速编码
            command.add("-preset");
            command.add("fast");
            
            // 输出文件
            command.add("-y"); // 覆盖输出文件
            command.add(outputFile.getAbsolutePath());
            
            // 执行命令
            executeFFmpegCommand(command);
            
        } finally {
            // 删除临时水印图片
            if (watermarkImage != null && watermarkImage.exists()) {
                watermarkImage.delete();
            }
        }
    }
    
    /**
     * 创建文字水印图片
     * 使用纯Java创建透明背景的文字图片，避免FFmpeg字体问题
     */
    private File createTextWatermarkImage(String text) throws Exception {
        log.info("使用纯Java创建文字水印图片");
        
        File watermarkFile = File.createTempFile("watermark_", ".png");
        // 使用纯Java创建透明背景的文字图片
        int width = 200;
        int height = 50;
        
        // 创建BufferedImage
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        
        // 获取Graphics2D对象
        java.awt.Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, 
                            java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 设置透明背景
        g2d.setComposite(java.awt.AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        
        // 设置字体和颜色
        java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.BOLD, 20);
        g2d.setFont(font);
        g2d.setColor(java.awt.Color.WHITE);
        
        // 计算文字位置（居中）
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + fm.getAscent();
        
        // 绘制文字
        g2d.drawString(text, x, y);
        
        // 释放资源
        g2d.dispose();
        
        // 保存为PNG文件
        javax.imageio.ImageIO.write(image, "PNG", watermarkFile);
        
        log.info("文字水印图片创建成功：{}", watermarkFile.getAbsolutePath());
        return watermarkFile;
    }
    
    
    /**
     * 获取ffmpeg命令
     * 仅检查系统PATH中的ffmpeg，如果没有则直接报错
     * 
     * @return ffmpeg命令路径
     * @throws Exception 如果找不到ffmpeg
     */
    private String getFFmpegCommand() throws Exception {
        // 仅尝试系统PATH中的ffmpeg命令
        String[] checkCommands = {"ffmpeg", "ffmpeg.exe"};
        
        for (String cmd : checkCommands) {
            try {
                Process process = new ProcessBuilder(cmd, "-version")
                    .redirectErrorStream(true)
                    .start();
                
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("找到ffmpeg命令：{}", cmd);
                    return cmd;
                }
            } catch (Exception e) {
                log.debug("尝试命令 {} 失败：{}", cmd, e.getMessage());
            }
        }
        
        throw new RuntimeException("未找到ffmpeg命令，请确保ffmpeg已安装并在系统PATH环境变量中");
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
        
        // 设置环境变量以避免Fontconfig错误
        setupEnvironmentVariables(processBuilder);
        
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
    
    /**
     * 设置环境变量以避免Fontconfig错误
     */
    private void setupEnvironmentVariables(ProcessBuilder processBuilder) {
        try {
            // 获取当前环境变量
            java.util.Map<String, String> env = processBuilder.environment();
            
            // 设置Fontconfig相关环境变量
            // 在Windows上，这可以帮助避免Fontconfig错误
            String fontConfigFile = env.get("FONTCONFIG_FILE");
            if (fontConfigFile == null || fontConfigFile.isEmpty()) {
                // 尝试设置一个默认的字体配置
                env.put("FONTCONFIG_FILE", "nul");
                log.debug("设置FONTCONFIG_FILE环境变量为nul");
            }
            
            // 设置其他可能相关的环境变量
            env.put("FC_DEBUG", "0"); // 禁用Fontconfig调试输出
            
            log.debug("已设置环境变量以避免Fontconfig错误");
        } catch (Exception e) {
            log.warn("设置环境变量时出错：{}", e.getMessage());
        }
    }
    
}
