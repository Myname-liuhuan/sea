package com.example.sea.media.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.sea.media.service.WatermarkService;

import lombok.extern.slf4j.Slf4j;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * 水印服务实现类
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
        
        FFmpegFrameGrabber grabber = null;
        FFmpegFrameRecorder recorder = null;
        
        try {
            // 保存上传的文件到临时文件
            File tempInputFile = File.createTempFile("input_", fileExtension);
            try (InputStream inputStream = videoFile.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(tempInputFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            // 初始化视频抓取器
            grabber = new FFmpegFrameGrabber(tempInputFile);
            grabber.start();
            
            // 获取视频信息
            int videoWidth = grabber.getImageWidth();
            int videoHeight = grabber.getImageHeight();
            int videoBitrate = grabber.getVideoBitrate();
            double frameRate = grabber.getFrameRate();
            int audioChannels = grabber.getAudioChannels();
            int audioBitrate = grabber.getAudioBitrate();
            int sampleRate = grabber.getSampleRate();
            
            log.info("视频信息 - 宽度：{}，高度：{}，帧率：{}，视频码率：{}", 
                    videoWidth, videoHeight, frameRate, videoBitrate);
            
            // 初始化视频录制器
            recorder = new FFmpegFrameRecorder(outputFile, videoWidth, videoHeight, audioChannels);
            recorder.setVideoCodec(grabber.getVideoCodec());
            recorder.setFormat(grabber.getFormat());
            recorder.setFrameRate(frameRate);
            recorder.setVideoBitrate(videoBitrate);
            recorder.setAudioCodec(grabber.getAudioCodec());
            recorder.setAudioBitrate(audioBitrate);
            recorder.setSampleRate(sampleRate);
            
            recorder.start();
            
            Java2DFrameConverter converter = new Java2DFrameConverter();
            Frame frame;
            int frameCount = 0;
            
            // 处理每一帧
            while ((frame = grabber.grab()) != null) {
                if (frame.image != null) {
                    // 处理视频帧
                    BufferedImage bufferedImage = converter.convert(frame);
                    BufferedImage watermarkedImage = addTextWatermark(bufferedImage, watermarkText, frameCount);
                    Frame watermarkedFrame = converter.convert(watermarkedImage);
                    recorder.record(watermarkedFrame);
                    frameCount++;
                } else if (frame.samples != null) {
                    // 处理音频帧
                    recorder.record(frame);
                }
            }
            
            log.info("视频水印添加完成，共处理 {} 帧", frameCount);
            
            // 停止录制器
            recorder.stop();
            recorder.release();
            
            // 停止抓取器
            grabber.stop();
            grabber.release();
            
            // 删除临时文件
            tempInputFile.delete();
            
            return outputFile;
            
        } catch (Exception e) {
            log.error("添加视频水印失败", e);
            throw new RuntimeException("添加视频水印失败：" + e.getMessage(), e);
        } finally {
            // 确保资源被释放
            if (recorder != null) {
                try {
                    recorder.stop();
                    recorder.release();
                } catch (FrameRecorder.Exception e) {
                    log.error("释放录制器资源失败", e);
                }
            }
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (FrameGrabber.Exception e) {
                    log.error("释放抓取器资源失败", e);
                }
            }
        }
    }
    
    /**
     * 给图片添加文字水印
     * 
     * @param sourceImage 源图片
     * @param watermarkText 水印文字
     * @param frameNumber 帧号
     * @return 添加水印后的图片
     */
    private BufferedImage addTextWatermark(BufferedImage sourceImage, String watermarkText, int frameNumber) {
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        
        // 创建目标图片
        BufferedImage targetImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = targetImage.createGraphics();
        
        // 设置渲染提示
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 绘制原图
        g2d.drawImage(sourceImage, 0, 0, null);
        
        // 设置水印文字属性
        g2d.setColor(new Color(255, 255, 255, 128)); // 白色半透明
        int fontSize = Math.min(width, height) / 20; // 根据视频尺寸调整字体大小
        Font font = new Font("微软雅黑", Font.BOLD, fontSize);
        g2d.setFont(font);
        
        // 计算文字位置（右下角）
        int textWidth = g2d.getFontMetrics().stringWidth(watermarkText);
        int textHeight = g2d.getFontMetrics().getHeight();
        int x = width - textWidth - 20;
        int y = height - textHeight - 20;
        
        // 绘制文字阴影
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.drawString(watermarkText, x + 2, y + 2);
        
        // 绘制文字
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.drawString(watermarkText, x, y);
        
        g2d.dispose();
        
        return targetImage;
    }
}
