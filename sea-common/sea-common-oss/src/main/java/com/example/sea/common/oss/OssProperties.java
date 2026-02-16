package com.example.sea.common.oss;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 对象存储配置属性类
 * <p>
 * 支持 S3 协议兼容的对象存储服务，如：
 * - MinIO（自建）
 * - 华为云 OBS
 * - 阿里云 OSS
 * - 腾讯云 COS
 * - AWS S3
 * <p>
 * 配置前缀：sea.oss
 *
 * @author liuhuan
 * @date 2026/02/15
 */
@Data
@ConfigurationProperties(prefix = "sea.oss")
public class OssProperties {

    /**
     * 是否启用 OSS（默认启用）
     */
    private Boolean enabled = true;

    /**
     * OSS 服务端点地址
     * <p>
     * 示例：
     * - MinIO: http://localhost:9000
     * - 华为云 OBS: https://obs.cn-north-4.myhuaweicloud.com
     * - 阿里云 OSS: https://oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;

    /**
     * 访问密钥 ID（Access Key ID）
     */
    private String accessKeyId;

    /**
     * 访问密钥 Secret（Access Key Secret）
     */
    private String secretAccessKey;

    /**
     * 存储桶名称（Bucket）
     */
    private String bucketName;

    /**
     * 区域（Region）
     * <p>
     * 某些云服务商需要指定区域（如华为云 OBS、阿里云 OSS）
     * MinIO 可不设置或设置为 "us-east-1"
     */
    private String region;

    /**
     * 路径样式访问（Path-Style Access）
     * <p>
     * true: 使用 http://endpoint/bucket/key 格式（MinIO 自建通常用这个）
     * false: 使用 http://bucket.endpoint/key 格式（云服务商通常用这个）
     * <p>
     * 默认: true（兼容 MinIO 和自建服务）
     */
    private Boolean pathStyleAccess = true;

    /**
     * 连接超时时间（毫秒）
     * 默认: 10000 (10秒)
     */
    private Integer connectionTimeout = 10000;

    /**
     * 读取超时时间（毫秒）
     * 默认: 60000 (60秒)
     */
    private Integer readTimeout = 60000;

    /**
     * 写入超时时间（毫秒）
     * 默认: 60000 (60秒)
     */
    private Integer writeTimeout = 60000;

    /**
     * 最大连接数
     * 默认: 50
     */
    private Integer maxConnections = 50;
}
