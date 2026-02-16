package com.example.sea.common.oss.service;

import com.example.sea.common.oss.OssProperties;
import com.example.sea.common.oss.dto.UploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 对象存储服务实现类
 * <p>
 * 基于 AWS S3 SDK v2，支持所有 S3 协议兼容的存储服务
 *
 * @author liuhuan
 * @date 2026/02/15
 */
@Slf4j
@RequiredArgsConstructor
public class OssStorageServiceImpl implements OssStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3TransferManager transferManager;
    private final OssProperties properties;

    @Override
    public UploadResult upload(MultipartFile file, String objectName) {
        try {
            String contentType = file.getContentType();
            long contentLength = file.getSize();

            return upload(file.getInputStream(), objectName, contentType, contentLength);

        } catch (IOException e) {
            log.error("读取 MultipartFile 输入流失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public UploadResult upload(InputStream inputStream, String objectName, String contentType, long contentLength) {
        try {
            log.info("开始上传文件到 OSS: {}, contentType: {}, size: {}", objectName, contentType, contentLength);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(objectName)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(inputStream, contentLength);

            var putObjectResponse = s3Client.putObject(putObjectRequest, requestBody);

            String url = getUrl(objectName);

            log.info("文件上传成功: {}, etag: {}, url: {}", objectName, putObjectResponse.eTag(), url);

            return UploadResult.builder()
                    .objectName(objectName)
                    .url(url)
                    .size(contentLength)
                    .contentType(contentType)
                    .etag(putObjectResponse.eTag())
                    .bucketName(properties.getBucketName())
                    .build();

        } catch (Exception e) {
            log.error("上传文件到 OSS 失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.warn("关闭输入流失败: {}", e.getMessage());
            }
        }
    }

    @Override
    public UploadResult uploadWithMultiPart(InputStream inputStream, String objectName, String contentType, long contentLength) {
        try {
            log.info("开始分片上传文件到 OSS: {}, contentType: {}, size: {}", objectName, contentType, contentLength);

            // 创建临时文件
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("s3-upload-", ".tmp");
            java.nio.file.Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            UploadRequest uploadRequest = UploadRequest.builder()
                    .putObjectRequest(b -> b.bucket(properties.getBucketName())
                            .key(objectName)
                            .contentType(contentType))
                    .requestBody(AsyncRequestBody.fromFile(tempFile))
                    .build();

            var uploadResponse = transferManager.upload(uploadRequest);
            uploadResponse.completionFuture().join();

            String etag = getEtag(objectName);
            String url = getUrl(objectName);

            log.info("分片上传文件成功: {}, etag: {}, url: {}", objectName, etag, url);

            // 清理临时文件
            java.nio.file.Files.deleteIfExists(tempFile);

            return UploadResult.builder()
                    .objectName(objectName)
                    .url(url)
                    .size(contentLength)
                    .contentType(contentType)
                    .etag(etag)
                    .bucketName(properties.getBucketName())
                    .build();

        } catch (Exception e) {
            log.error("分片上传文件到 OSS 失败: {}", e.getMessage(), e);
            throw new RuntimeException("分片上传文件失败: " + e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.warn("关闭输入流失败: {}", e.getMessage());
            }
        }
    }

    @Override
    public InputStream download(String objectName) {
        try {
            log.info("开始下载文件: {}", objectName);

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(objectName)
                    .build();

            return s3Client.getObject(getRequest);

        } catch (NoSuchKeyException e) {
            log.error("文件不存在: {}", objectName);
            throw new RuntimeException("文件不存在: " + objectName, e);
        } catch (Exception e) {
            log.error("下载文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("下载文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] downloadAsBytes(String objectName) {
        try (InputStream inputStream = download(objectName)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("读取文件字节数组失败: {}", e.getMessage(), e);
            throw new RuntimeException("读取文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String objectName) {
        try {
            log.info("开始删除文件: {}", objectName);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(objectName)
                    .build();

            s3Client.deleteObject(deleteRequest);

            log.info("文件删除成功: {}", objectName);

        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteBatch(String... objectNames) {
        if (objectNames == null || objectNames.length == 0) {
            log.warn("批量删除: objectNames 为空，跳过删除");
            return;
        }

        try {
            log.info("开始批量删除文件，数量: {}", objectNames.length);

            ObjectIdentifier[] identifiers = new ObjectIdentifier[objectNames.length];
            for (int i = 0; i < objectNames.length; i++) {
                identifiers[i] = ObjectIdentifier.builder()
                        .key(objectNames[i])
                        .build();
            }

            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                    .bucket(properties.getBucketName())
                    .delete(b -> b.objects(identifiers))
                    .build();

            s3Client.deleteObjects(deleteRequest);

            log.info("批量删除成功，数量: {}", objectNames.length);

        } catch (Exception e) {
            log.error("批量删除文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量删除文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String objectName) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(objectName)
                    .build();

            s3Client.headObject(headRequest);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getUrl(String objectName) {
        // 构建访问 URL
        String endpoint = properties.getEndpoint();
        String bucketName = properties.getBucketName();

        if (properties.getPathStyleAccess()) {
            // Path-Style: http://endpoint/bucket/key
            return String.format("%s/%s/%s", endpoint, bucketName, objectName);
        } else {
            // Virtual-Hosted-Style: http://bucket.endpoint/key
            return String.format("%s/%s", replaceEndpointHost(endpoint, bucketName), objectName);
        }
    }

    @Override
    public String getPresignedUrl(String objectName, int expiration) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(objectName)
                    .build();

            var presignedRequest = s3Presigner.presignGetObject(b -> b
                    .signatureDuration(Duration.ofSeconds(expiration))
                    .getObjectRequest(getObjectRequest));

            return presignedRequest.url().toString();

        } catch (Exception e) {
            log.error("生成预签名 URL 失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成预签名 URL 失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void copy(String sourceObjectKey, String targetObjectKey) {
        try {
            log.info("开始复制文件: {} -> {}", sourceObjectKey, targetObjectKey);

            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(properties.getBucketName())
                    .sourceKey(sourceObjectKey)
                    .destinationBucket(properties.getBucketName())
                    .destinationKey(targetObjectKey)
                    .build();

            s3Client.copyObject(copyRequest);

            log.info("文件复制成功: {} -> {}", sourceObjectKey, targetObjectKey);

        } catch (Exception e) {
            log.error("复制文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("复制文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getObjectMetadata(String objectName) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(objectName)
                    .build();

            var headResponse = s3Client.headObject(headRequest);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("contentType", headResponse.contentType());
            metadata.put("contentLength", headResponse.contentLength());
            metadata.put("lastModified", headResponse.lastModified());
            metadata.put("etag", headResponse.eTag());
            metadata.put("metadata", headResponse.metadata());

            return metadata;

        } catch (NoSuchKeyException e) {
            log.error("文件不存在: {}", objectName);
            throw new RuntimeException("文件不存在: " + objectName, e);
        } catch (Exception e) {
            log.error("获取文件元数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取文件元数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取文件 ETag
     */
    private String getEtag(String objectName) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(objectName)
                    .build();

            var headResponse = s3Client.headObject(headRequest);
            return headResponse.eTag();
        } catch (Exception e) {
            log.warn("获取 ETag 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 替换 endpoint 的 host 为 bucket.endpoint
     * <p>
     * 示例: http://localhost:9000 -> http://mybucket.localhost:9000
     */
    private String replaceEndpointHost(String endpoint, String bucketName) {
        try {
            URL url = new URL(endpoint);
            String newHost = bucketName + "." + url.getHost();
            return new URL(url.getProtocol(), newHost, url.getPort(), url.getFile()).toString();
        } catch (Exception e) {
            log.warn("替换 endpoint host 失败，返回原 endpoint: {}", endpoint);
            return endpoint;
        }
    }
}
