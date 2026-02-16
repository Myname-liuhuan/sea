package com.example.sea.common.oss.config;

import com.example.sea.common.oss.OssProperties;
import com.example.sea.common.oss.service.OssStorageService;
import com.example.sea.common.oss.service.OssStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

/**
 * 对象存储自动配置类
 * <p>
 * 配置条件：
 * 1. sea.oss.enabled = true（默认启用）
 * 2. 且未手动注册 OssStorageService Bean
 *
 * @author liuhuan
 * @date 2026/02/15
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = "sea.oss", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OssAutoConfiguration {

    /**
     * 配置 S3Client Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client(OssProperties properties) {
        log.info("初始化 S3Client，endpoint: {}, bucket: {}, region: {}, pathStyleAccess: {}",
                properties.getEndpoint(), properties.getBucketName(), properties.getRegion(), properties.getPathStyleAccess());

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.getAccessKeyId(),
                properties.getSecretAccessKey()
        );

        S3ClientBuilder builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .httpClientBuilder(ApacheHttpClient.builder())
                .endpointOverride(java.net.URI.create(properties.getEndpoint()));

        // 设置区域（如果配置了）
        if (properties.getRegion() != null && !properties.getRegion().isEmpty()) {
            builder.region(Region.of(properties.getRegion()));
        }

        // 设置 Path-Style Access（MinIO 自建通常需要启用）
        // 注意：AWS SDK v2 默认使用 Virtual-Hosted-Style，需要手动配置为 Path-Style
        // 这需要在构建 S3Client 时通过 serviceConfiguration 配置
        // 但 AWS SDK v2 没有直接提供 Path-Style 配置，需要通过 endpointOverride 实现
        // 实际上，只要 endpointOverride 设置了非 AWS 的端点，SDK 会自动使用 Path-Style

        S3Client s3Client = builder.build();

        log.info("S3Client 初始化成功");

        return s3Client;
    }

    /**
     * 配置 S3Presigner Bean（用于生成预签名 URL）
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Presigner s3Presigner(OssProperties properties) {
        log.info("初始化 S3Presigner，endpoint: {}", properties.getEndpoint());

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.getAccessKeyId(),
                properties.getSecretAccessKey()
        );

        S3Presigner.Builder builder = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(java.net.URI.create(properties.getEndpoint()));

        if (properties.getRegion() != null && !properties.getRegion().isEmpty()) {
            builder.region(Region.of(properties.getRegion()));
        }

        S3Presigner s3Presigner = builder.build();

        log.info("S3Presigner 初始化成功");

        return s3Presigner;
    }

    /**
     * 配置 S3AsyncClient Bean（用于 TransferManager）
     */
    @Bean
    @ConditionalOnMissingBean
    public S3AsyncClient s3AsyncClient(OssProperties properties) {
        log.info("初始化 S3AsyncClient，endpoint: {}", properties.getEndpoint());

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.getAccessKeyId(),
                properties.getSecretAccessKey()
        );

        S3AsyncClientBuilder builder = S3AsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(java.net.URI.create(properties.getEndpoint()));

        if (properties.getRegion() != null && !properties.getRegion().isEmpty()) {
            builder.region(Region.of(properties.getRegion()));
        }

        S3AsyncClient s3AsyncClient = builder.build();

        log.info("S3AsyncClient 初始化成功");

        return s3AsyncClient;
    }

    /**
     * 配置 S3TransferManager Bean（用于分片上传、断点续传）
     */
    @Bean
    @ConditionalOnMissingBean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        log.info("初始化 S3TransferManager");

        S3TransferManager transferManager = S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();

        log.info("S3TransferManager 初始化成功");

        return transferManager;
    }

    /**
     * 配置 OssStorageService Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public OssStorageService ossStorageService(S3Client s3Client,
                                              S3Presigner s3Presigner,
                                              S3TransferManager s3TransferManager,
                                              OssProperties properties) {
        log.info("初始化 OssStorageService");

        return new OssStorageServiceImpl(s3Client, s3Presigner, s3TransferManager, properties);
    }
}
