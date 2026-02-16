package com.example.sea.common.oss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传结果
 *
 * @author liuhuan
 * @date 2026/02/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResult {

    /**
     * 对象名称（存储路径）
     * <p>
     * 示例: videos/2026/02/15/test-uuid.mp4
     */
    private String objectName;

    /**
     * 文件访问 URL
     * <p>
     * 示例: http://localhost:9000/bucket/videos/2026/02/15/test-uuid.mp4
     */
    private String url;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 内容类型（MIME）
     * <p>
     * 示例: video/mp4
     */
    private String contentType;

    /**
     * ETag（文件唯一标识）
     */
    private String etag;

    /**
     * 存储桶名称
     */
    private String bucketName;
}
