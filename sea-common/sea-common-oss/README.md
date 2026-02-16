# sea-common-oss 使用指南

## 概述

`sea-common-oss` 是一个基于 AWS S3 SDK v2 的对象存储服务公共模块，支持所有 S3 协议兼容的存储服务：
- **MinIO**（本地开发/私有化部署）
- **华为云 OBS**
- **阿里云 OSS**
- **腾讯云 COS**
- **AWS S3**

## 模块结构

```
sea-common-oss/
├── src/main/java/com/example/sea/common/oss/
│   ├── OssProperties.java              # 配置属性类
│   ├── config/
│   │   └── OssAutoConfiguration.java   # 自动配置类
│   ├── service/
│   │   ├── OssStorageService.java      # 存储服务接口
│   │   └── OssStorageServiceImpl.java  # 存储服务实现
│   └── dto/
│       └── UploadResult.java           # 上传结果 DTO
└── src/main/resources/
    └── META-INF/
        └── spring.factories             # Spring Boot 自动配置
```

## 快速开始

### 1. 添加依赖

在需要使用的服务模块（如 `sea-media`）的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>sea-common-oss</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 配置文件

#### 本地开发（MinIO）

在 `application-local.yml` 或 Nacos `sea-media-local.yaml` 中配置：

```yaml
sea:
  oss:
    enabled: true
    endpoint: http://localhost:9000                    # MinIO 服务地址
    access-key-id: minioadmin                         # MinIO 用户名
    secret-access-key: minioadmin                     # MinIO 密码
    bucket-name: sea-media                            # 存储桶名称（需要提前创建）
    region: us-east-1                                 # 区域（MinIO 可不设置或设为 us-east-1）
    path-style-access: true                           # Path-Style 访问（MinIO 必须启用）
    connection-timeout: 10000                         # 连接超时（毫秒）
    read-timeout: 60000                               # 读取超时（毫秒）
    write-timeout: 60000                              # 写入超时（毫秒）
    max-connections: 50                               # 最大连接数
```

#### 生产环境（华为云 OBS）

在 Nacos `sea-media-prod.yaml` 中配置：

```yaml
sea:
  oss:
    enabled: true
    endpoint: https://obs.cn-north-4.myhuaweicloud.com  # 华为云 OBS 端点（按实际区域）
    access-key-id: YOUR_ACCESS_KEY_ID                   # 华为云 Access Key
    secret-access-key: YOUR_SECRET_ACCESS_KEY           # 华为云 Secret Key
    bucket-name: sea-media-prod                         # 生产环境存储桶
    region: cn-north-4                                  # 华为云区域（如华北-北京四）
    path-style-access: false                            # Virtual-Hosted-Style（云服务商用这个）
```

### 3. 创建 MinIO 存储桶

本地开发前，需要先在 MinIO 中创建存储桶：

#### 方式一：MinIO Console（Web 界面）

1. 访问 `http://localhost:9000`
2. 登录（默认用户名/密码：minioadmin/minioadmin）
3. 点击 "Buckets" → "Create Bucket"
4. 输入存储桶名称（如 `sea-media`）
5. 点击 "Create Bucket"

#### 方式二：MinIO Client（mc 命令行）

```bash
# 添加 MinIO 服务
mc alias set myminio http://localhost:9000 minioadmin minioadmin

# 创建存储桶
mc mb myminio/sea-media

# 设置存储桶为公共可读（可选）
mc anonymous set download myminio/sea-media
```

### 4. 使用示例

#### 在 Service 中注入使用

```java
import com.example.sea.common.oss.dto.UploadResult;
import com.example.sea.common.oss.service.OssStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final OssStorageService ossStorageService;

    /**
     * 上传文件到 OSS
     */
    public String uploadFile(MultipartFile file, String fileType) {
        try {
            // 生成对象名称（存储路径），按日期分层
            String objectName = generateObjectName(file.getOriginalFilename(), fileType);

            // 上传文件
            UploadResult result = ossStorageService.upload(file, objectName);

            log.info("文件上传成功: {}, URL: {}", result.getObjectName(), result.getUrl());

            // 返回访问 URL
            return result.getUrl();

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 生成对象名称（示例：videos/2026/02/15/uuid-filename.mp4）
     */
    private String generateObjectName(String originalFilename, String fileType) {
        // 获取文件扩展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成 UUID
        String uuid = java.util.UUID.randomUUID().toString();
        // 获取当前日期（年/月/日）
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        // 拼接对象名称
        return String.format("%s/%s/%s%s", fileType, datePath, uuid, extension);
    }

    /**
     * 删除文件
     */
    public void deleteFile(String objectName) {
        ossStorageService.delete(objectName);
        log.info("文件删除成功: {}", objectName);
    }

    /**
     * 获取文件访问 URL
     */
    public String getFileUrl(String objectName) {
        return ossStorageService.getUrl(objectName);
    }

    /**
     * 获取预签名 URL（临时访问私有文件）
     */
    public String getPresignedUrl(String objectName, int expirationSeconds) {
        return ossStorageService.getPresignedUrl(objectName, expirationSeconds);
    }
}
```

#### 在 Controller 中使用

```java
import com.example.sea.common.oss.service.OssStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final OssStorageService ossStorageService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "path", defaultValue = "uploads") String path) {

        String objectName = path + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        var result = ossStorageService.upload(file, objectName);

        return ResponseEntity.ok(Map.of(
                "url", result.getUrl(),
                "objectName", result.getObjectName(),
                "size", result.getSize().toString()
        ));
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{objectName}")
    public ResponseEntity<Void> delete(@PathVariable String objectName) {
        ossStorageService.delete(objectName);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取文件访问 URL
     */
    @GetMapping("/url/{objectName}")
    public ResponseEntity<Map<String, String>> getUrl(@PathVariable String objectName) {
        String url = ossStorageService.getUrl(objectName);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
```

## API 说明

### OssStorageService 接口

| 方法 | 说明 |
|------|------|
| `upload(MultipartFile, String)` | 上传文件（MultipartFile） |
| `upload(InputStream, String, String, long)` | 上传文件（InputStream） |
| `uploadWithMultiPart(...)` | 分片上传（适合大文件） |
| `download(String)` | 下载文件（返回 InputStream） |
| `downloadAsBytes(String)` | 下载文件（返回字节数组） |
| `delete(String)` | 删除单个文件 |
| `deleteBatch(String...)` | 批量删除文件 |
| `exists(String)` | 检查文件是否存在 |
| `getUrl(String)` | 获取文件访问 URL |
| `getPresignedUrl(String, int)` | 获取预签名 URL（临时访问） |
| `copy(String, String)` | 复制文件 |
| `getObjectMetadata(String)` | 获取文件元数据 |

### UploadResult 返回结果

```java
{
    "objectName": "videos/2026/02/15/uuid-123.mp4",  // 对象名称
    "url": "http://localhost:9000/sea-media/videos/...",  // 访问 URL
    "size": 10485760,                                 // 文件大小（字节）
    "contentType": "video/mp4",                       // MIME 类型
    "etag": "\"abc123...\"",                          // 文件 ETag
    "bucketName": "sea-media"                         // 存储桶名称
}
```

## MinIO 安装（本地开发）

### Docker 方式（推荐）

```bash
docker run -d \
  -p 9000:9000 \
  -p 9001:9001 \
  --name minio \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  quay.io/minio/minio server /data --console-address ":9001"
```

访问：
- MinIO API: `http://localhost:9000`
- MinIO Console: `http://localhost:9001`

### Docker Compose 方式

```yaml
version: '3.8'

services:
  minio:
    image: quay.io/minio/minio:latest
    container_name: sea-minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    command: server /data --console-address ":9001"
    volumes:
      - minio-data:/data

volumes:
  minio-data:
```

启动：`docker-compose up -d`

## 华为云 OBS 配置说明

### 获取 Access Key

1. 登录华为云控制台
2. 进入"统一身份认证服务" → "访问密钥" → "新增访问密钥"
3. 下载 `credentials.csv` 文件，获取 `Access Key Id` 和 `Secret Access Key`

### 创建存储桶

1. 进入"对象存储服务 OBS"
2. 点击"创建桶"
3. 配置桶参数：
   - 桶名称：`sea-media-prod`
   - 区域：选择实际业务区域（如华北-北京四）
   - 存储类别：标准存储
   - 桶策略：公共读（如果需要直接访问文件）

### 端点地址（Endpoint）

华为云 OBS 不同区域的端点地址参考：

| 区域 | Endpoint |
|------|----------|
| 华北-北京四 | `https://obs.cn-north-4.myhuaweicloud.com` |
| 华南-广州 | `https://obs.cn-south-1.myhuaweicloud.com` |
| 华东-上海一 | `https://obs.cn-east-3.myhuaweicloud.com` |

完整端点列表：https://developer.huaweicloud.com/endpoint?OBS

## 常见问题

### 1. 文件上传后无法访问（403）

**原因**：存储桶策略设置为私有，或者路径样式配置错误。

**解决方案**：
- **MinIO**：设置 `path-style-access: true`，并在 MinIO Console 中设置存储桶为 Public
- **华为云 OBS**：设置 `path-style-access: false`，并在桶策略中添加公共读权限

### 2. 连接超时

**解决方案**：
- 检查 `endpoint` 配置是否正确
- 检查防火墙/安全组是否允许访问端口
- 增加 `connection-timeout` 配置值

### 3. 认证失败（403 Forbidden）

**解决方案**：
- 检查 `access-key-id` 和 `secret-access-key` 是否正确
- 检查账户权限是否包含 OBS/S3 操作权限
- 检查 `region` 配置是否正确

### 4. Path-Style vs Virtual-Hosted-Style

| 模式 | URL 格式 | 适用场景 |
|------|----------|----------|
| **Path-Style** | `http://endpoint/bucket/key` | MinIO、自建 S3 服务 |
| **Virtual-Hosted-Style** | `http://bucket.endpoint/key` | 云服务商（阿里云、华为云、AWS） |

**配置规则**：
```yaml
# MinIO（本地开发）
path-style-access: true

# 华为云 OBS（生产）
path-style-access: false
```

## 下一步

1. 在 `sea-media` 服务中集成 `sea-common-oss`
2. 重构 `VideoServiceImpl.java:39-94`，使用 OSS 存储替代本地临时文件
3. 在 Nacos 中为不同环境配置对应的 OSS 参数
4. 测试文件上传、下载、删除功能
