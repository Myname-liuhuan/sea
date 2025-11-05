package com.example.sea.media.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.sea.media.service.WatermarkService;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

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

    @Override
    public File addTextWatermark2PDF(MultipartFile pdfFile, String watermarkText) throws Exception {
        log.info("开始给PDF添加文字水印，文件名：{}，水印内容：{}", pdfFile.getOriginalFilename(), watermarkText);
        
        // 创建输出目录
        Path outputDir = Files.createTempDirectory("tempPDF");
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        
        // 生成输出文件名
        String originalFilename = pdfFile.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String outputFileName = UUID.randomUUID().toString() + "_watermarked" + fileExtension;
        File outputFile = outputDir.resolve(outputFileName).toFile();
        
        File tempInputFile = null;
        
        try {
            // 保存上传的PDF文件到临时文件
            tempInputFile = File.createTempFile("input_", fileExtension);
            try (InputStream inputStream = pdfFile.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(tempInputFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            // 使用PDFBox添加文字水印
            addTextWatermarkToPDF(tempInputFile, outputFile, watermarkText);
            
            log.info("PDF水印添加完成，输出文件：{}", outputFile.getAbsolutePath());
            
            return outputFile;
            
        } catch (Exception e) {
            log.error("添加PDF水印失败", e);
            throw new RuntimeException("添加PDF水印失败：" + e.getMessage(), e);
        } finally {
            // 删除临时输入文件
            if (tempInputFile != null && tempInputFile.exists()) {
                tempInputFile.delete();
            }
        }
    }

    @Override
    public File addTextWatermark2Image(MultipartFile file, String watermarkText) {
        log.info("开始给图片添加文字水印，文件名：{}，水印内容：{}", file.getOriginalFilename(), watermarkText);
        try {
            // 创建输出目录
            Path outputDir = Files.createTempDirectory("tempImage");
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            // 生成输出文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String outputFileName = UUID.randomUUID().toString() + "_watermarked" + fileExtension;
            File outputFile = outputDir.resolve(outputFileName).toFile();
            
            // 创建文字水印图片
            File watermarkImage = createTextWatermarkImage(watermarkText);
            
            try {
                // 使用thumbnailator添加水印
                Thumbnails.of(file.getInputStream())
                    .scale(1.0) // 保持原始尺寸
                    .watermark(Positions.BOTTOM_RIGHT, javax.imageio.ImageIO.read(watermarkImage), 0.8f) // 80%透明度
                    .toFile(outputFile);
                
                log.info("图片水印添加完成，输出文件：{}", outputFile.getAbsolutePath());
                return outputFile;
                
            } finally {
                // 删除临时水印图片
                if (watermarkImage != null && watermarkImage.exists()) {
                    watermarkImage.delete();
                }
            }
            
        } catch (Exception e) {
            log.error("添加图片水印失败", e);
            throw new RuntimeException("添加图片水印失败：" + e.getMessage(), e);
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
    private void addTextWatermarkWithFFmpeg(File inputFile, File outputFile, String watermarkText) throws Exception{
        log.info("添加水印方案：创建透明图片并overlay");
        
        // 创建一个临时图片文件作为水印
        File watermarkImage = createTextWatermarkImage(watermarkText);
        try {
           // 构建ffmpeg命令
            List<String> command = new ArrayList<>();
            
            String ffmpegCmd = "ffmpeg";
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
        } catch (Exception e) {
            throw new RuntimeException("使用drawtext滤镜添加水印失败：" + e.getMessage(), e);
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
     * 使用PDFBox给PDF添加文字水印
     * 
     * @param inputFile 输入PDF文件
     * @param outputFile 输出PDF文件
     * @param watermarkText 水印文字
     * @throws Exception 处理异常
     */
    private void addTextWatermarkToPDF(File inputFile, File outputFile, String watermarkText) throws Exception {
        log.info("使用PDFBox给PDF添加文字水印");
        
        try (PDDocument document = Loader.loadPDF(inputFile)) {
            // 设置水印透明度
            PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
            graphicsState.setNonStrokingAlphaConstant(0.3f); // 30%透明度
            
            // 获取字体
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            
            // 遍历所有页面添加水印
            for (PDPage page : document.getPages()) {
                try (PDPageContentStream contentStream = new PDPageContentStream(
                        document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    
                    // 设置图形状态（透明度）
                    contentStream.setGraphicsStateParameters(graphicsState);
                    
                    // 设置字体和大小
                    contentStream.setFont(font, 50);
                    
                    // 设置水印颜色（灰色）- PDFBox 3.0需要使用0..1范围的浮点数
                    contentStream.setNonStrokingColor(0.5f, 0.5f, 0.5f);
                    
                    // 获取页面尺寸
                    float pageWidth = page.getMediaBox().getWidth();
                    float pageHeight = page.getMediaBox().getHeight();
                    
                    // 计算文字尺寸（近似）
                    float textWidth = watermarkText.length() * 30; // 近似估算
                    float textHeight = 50;
                    
                    // 计算水印位置（页面中心）
                    float centerX = pageWidth / 2;
                    float centerY = pageHeight / 2;
                    
                    // 保存当前转换矩阵
                    contentStream.saveGraphicsState();
                    
                    // 旋转45度，创建斜水印效果
                    Matrix rotationMatrix = Matrix.getRotateInstance(Math.toRadians(45), centerX, centerY);
                    contentStream.transform(rotationMatrix);
                    
                    // 开始文本
                    contentStream.beginText();
                    
                    // 设置文本位置（相对于旋转后的坐标系，居中显示）
                    contentStream.newLineAtOffset(-textWidth / 2, -textHeight / 2);
                    
                    // 显示文本
                    contentStream.showText(watermarkText);
                    
                    // 结束文本
                    contentStream.endText();
                    
                    // 恢复图形状态
                    contentStream.restoreGraphicsState();
                }
            }
            
            // 保存文档
            document.save(outputFile);
            log.info("PDF水印添加成功，输出文件：{}", outputFile.getAbsolutePath());
            
        } catch (Exception e) {
            String errorMsg = "PDF水印处理失败：" + e.getMessage();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
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
