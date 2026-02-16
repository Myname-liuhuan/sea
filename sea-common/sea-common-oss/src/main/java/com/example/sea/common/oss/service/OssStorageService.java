package com.example.sea.common.oss.service;

import java.io.InputStream;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.sea.common.oss.dto.UploadResult;

/**
 * 对象存储服务接口
 * <p>
 * 提供文件上传、下载、删除、URL 生成等基础操作
 *
 * @author liuhuan
 * @date 2026/02/15
 */
public interface OssStorageService {

    /**
     * 上传文件（使用 MultipartFile）
     *
     * @param file    要上传的文件
     * @param objectName 对象名称（存储路径），如 "videos/2026/02/test.mp4"
     * @return 上传结果（包含访问URL）
     */
    UploadResult upload(MultipartFile file, String objectName);

    /**
     * 上传文件（使用 InputStream）
     *
     * @param inputStream 文件输入流
     * @param objectName  对象名称（存储路径）
     * @param contentType 内容类型（MIME），如 "video/mp4"
     * @param contentLength 文件大小（字节）
     * @return 上传结果（包含访问URL）
     */
    UploadResult upload(InputStream inputStream, String objectName, String contentType, long contentLength);

    /**
     * 上传文件（使用 InputStream，自动分片上传）
     *
     * @param inputStream 文件输入流
     * @param objectName  对象名称（存储路径）
     * @param contentType 内容类型
     * @param contentLength 文件大小
     * @return 上传结果（包含访问URL）
     */
    UploadResult uploadWithMultiPart(InputStream inputStream, String objectName, String contentType, long contentLength);

    /**
     * 下载文件（获取输入流）
     *
     * @param objectName 对象名称
     * @return 文件输入流
     */
    InputStream download(String objectName);

    /**
     * 下载文件（到字节数组）
     *
     * @param objectName 对象名称
     * @return 文件内容字节数组
     */
    byte[] downloadAsBytes(String objectName);

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    void delete(String objectName);

    /**
     * 批量删除文件
     *
     * @param objectNames 对象名称列表
     */
    void deleteBatch(String... objectNames);

    /**
     * 检查文件是否存在
     *
     * @param objectName 对象名称
     * @return true-存在，false-不存在
     */
    boolean exists(String objectName);

    /**
     * 获取文件访问 URL
     * <p>
     * 注意：此方法返回的是公共访问 URL，如果文件是私有的，需要使用 {@link #getPresignedUrl}
     *
     * @param objectName 对象名称
     * @return 访问 URL
     */
    String getUrl(String objectName);

    /**
     * 获取预签名 URL（带签名，临时访问私有文件）
     *
     * @param objectName 对象名称
     * @param expiration 过期时间（秒）
     * @return 预签名 URL
     */
    String getPresignedUrl(String objectName, int expiration);

    /**
     * 复制文件
     *
     * @param sourceObjectKey 源对象名称
     * @param targetObjectKey 目标对象名称
     */
    void copy(String sourceObjectKey, String targetObjectKey);

    /**
     * 获取文件元数据
     *
     * @param objectName 对象名称
     * @return 元数据 Map（包含 contentType, contentLength, lastModified 等）
     */
    Map<String, Object> getObjectMetadata(String objectName);
}
