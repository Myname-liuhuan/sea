# 华为云 OBS SDK `putObject` 不指定 Content-Length 时的行为分析

> SDK 版本：`com.huaweicloud:esdk-obs-java:3.21.4.1`
> 分析日期：2026-05-15

---

## 一、背景

项目中有如下代码调用华为云 OBS SDK 上传文件：

```java
obsClient.putObject(bucketName, objectKey, inputStream);
```

这个 3 参数版本的 `putObject` 没有传入 `ObjectMetadata`，因此也没有指定 `Content-Length`。围绕这个写法产生了两种观点：

| | 观点 | 预期行为 |
|---|------|----------|
| 观点1 | SDK 会自动把整个流读入内存来计算长度 | 导致 OOM |
| 观点2 | SDK 会把流写入临时文件来计算长度 | 压力给到磁盘，不会 OOM |

为消除争议，我们对 SDK 的 class 文件进行了反编译，逐层追踪了 `putObject` 的完整调用链路。

---

## 二、调用链全景

```
putObject(String bucket, String key, InputStream)          ← 用户调用入口
  │
  ├─ putObject(String bucket, String key, InputStream, ObjectMetadata=null)
  │     └─ 创建 PutObjectRequest（metadata=null）
  │
  └─ putObject(PutObjectRequest)                           ← AbstractObjectClient
        │
        ├─ doActionWithResult("putObject", bucket, callback)
        │
        └─ putObjectImpl(PutObjectRequest)                  ← ObsObjectBaseService
              │
              ├─ transPutObjectRequest(request)              ← RequestConvertor ★核心★
              │     │
              │     ├─ 解析 contentLength（未设置时为 -1）
              │     ├─ 创建 RepeatableRequestEntity
              │     └─ 返回 TransResult(headers, body)
              │
              └─ performRestPut(bucket, key, headers, null, body, true)
                    └─ OkHttp 发送请求
```

### 涉及的关键类

| 类 | 所在包 | 职责 |
|----|--------|------|
| `AbstractObjectClient` | `com.obs.services` | `putObject` 的多个重载入口 |
| `ObsObjectBaseService` | `com.obs.services.internal.service` | `putObjectImpl` 实现 |
| `RequestConvertor` | `com.obs.services.internal.service` | 请求转换，决定 contentLength |
| `RepeatableRequestEntity` | `com.obs.services.internal` | OkHttp RequestBody 实现 |
| `MayRepeatableInputStream` | `com.obs.services.internal.io` | 流包装，支持 mark/reset |

---

## 三、逐层源码分析

### 3.1 入口：3 参数 putObject

**文件**：`AbstractObjectClient.class`

```java
// putObject(String, String, InputStream) 直接调用 4 参数版本，metadata 传 null
public PutObjectResult putObject(String bucketName, String objectKey, InputStream input)
        throws ObsException {
    return this.putObject(bucketName, objectKey, input, null);  // metadata = null
}

// 4 参数版本创建 PutObjectRequest，metadata 仍然是 null
public PutObjectResult putObject(String bucketName, String objectKey,
        InputStream input, ObjectMetadata metadata) throws ObsException {
    PutObjectRequest request = new PutObjectRequest();
    request.setBucketName(bucketName);
    request.setObjectKey(objectKey);
    request.setInput(input);
    request.setMetadata(metadata);  // null
    return this.putObject(request);
}
```

**要点**：metadata 为 null，后续不会设置 contentLength。

### 3.2 核心转换：transPutObjectRequest

**文件**：`RequestConvertor.class`

这是决定 contentLength 取值的关键方法。反编译还原后的逻辑如下：

```java
protected TransResult transPutObjectRequest(PutObjectRequest request)
        throws ServiceException {

    Map<String, String> headers = new HashMap<>();

    // ① metadata 为 null 时，创建一个空的 ObjectMetadata
    ObjectMetadata metadata = (request.getMetadata() != null)
        ? request.getMetadata()
        : new ObjectMetadata();    // ← 走这个分支，metadata 是空对象

    // ... 设置各种 header（SSE、ACL、expires 等）...

    // ② 从 metadata 获取 contentLength
    Object contentLengthObj = metadata.getContentLength();  // null（空 metadata）
    if (contentLengthObj == null) {
        contentLengthObj = metadata.getValue("Content-Length");  // 也是 null
    }

    // ③ 都没设置，contentLength = -1
    long contentLength = (contentLengthObj != null)
        ? Long.parseLong(contentLengthObj.toString())
        : -1L;   // ← 走这个分支，contentLength = -1

    // ④ 只有 contentLength > -1 时才设置 Content-Length header
    //    当 contentLength = -1 时，不设置该 header
    if (contentLength > -1) {
        headers.put("Content-Length", String.valueOf(contentLength));
        // ← 不会执行
    }

    // ⑤ 创建 RequestBody
    RepeatableRequestEntity entity = new RepeatableRequestEntity(
        request.getInput(),       // InputStream
        contentType,
        contentLength,            // -1
        this.obsProperties
    );

    return new TransResult(headers, entity);
}
```

**关键结论**：当不指定 contentLength 时，SDK 将其设为 **-1**，**不设置** `Content-Length` header。

### 3.3 RequestBody 实现：RepeatableRequestEntity

**文件**：`RepeatableRequestEntity.class`

```java
public class RepeatableRequestEntity extends RequestBody implements Closeable {
    private long contentLength;
    private InputStream inputStream;

    public RepeatableRequestEntity(InputStream inputStream, String contentType,
            long contentLength, ObsProperties obsProperties) {

        this.contentLength = -1;        // 默认值
        this.inputStream = inputStream;
        this.contentLength = contentLength;  // 设为 -1
        this.contentType = contentType;

        // 如果不是 MayRepeatableInputStream，就包装一层（8KB 缓冲区）
        if (!(inputStream instanceof MayRepeatableInputStream)) {
            this.inputStream = new MayRepeatableInputStream(inputStream,
                obsProperties.getIntProperty("httpclient.write-buffer-size", 8192));
        }
        this.inputStream.mark(0);
    }

    // OkHttp 调用此方法获取长度
    public long contentLength() {
        return this.contentLength;  // 返回 -1
    }

    // 实际写入数据的方法
    protected void writeToBIO(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;

        // 当 contentLength < 0 时，进入"未知长度"分支
        if (this.contentLength < 0) {
            // 边读边写，4096 字节一块，直到流结束
            while ((bytesRead = this.inputStream.read(buffer)) != -1) {
                this.bytesWritten += bytesRead;
                sink.write(buffer, 0, bytesRead);
            }
        } else {
            // 已知长度时，精确读取
            long remaining = this.contentLength;
            while (remaining > 0) {
                bytesRead = this.inputStream.read(buffer, 0,
                    (int) Math.min(4096, remaining));
                if (bytesRead == -1) break;
                sink.write(buffer, 0, bytesRead);
                this.bytesWritten += bytesRead;
                remaining -= bytesRead;
            }
        }
    }
}
```

**关键结论**：当 contentLength = -1 时，`writeToBIO` 使用 **4KB buffer 边读边写**，不会把整个流缓存到内存中。

### 3.4 流包装：MayRepeatableInputStream

**文件**：`MayRepeatableInputStream.class`

```java
public class MayRepeatableInputStream extends FilterInputStream {
    private FileChannel fileChannel;  // 仅 FileInputStream 时有值
    private long markPos;
    private InputStream originInputStream;

    public MayRepeatableInputStream(InputStream in, int bufferSize) {
        super(in);
        this.originInputStream = in;
        this.init(bufferSize);
    }

    private void init(int bufferSize) {
        // ① 如果是 FileInputStream，获取 FileChannel 用于 reset
        if (in instanceof FileInputStream) {
            this.fileChannel = ((FileInputStream) in).getChannel();
            this.markPos = fileChannel.position();
        }

        // ② 用 8KB 缓冲流包装（仅缓冲，不是缓存全部数据）
        if (bufferSize > 0) {
            this.in = new SdkBufferedInputStream(in, bufferSize);  // 8192
        }
    }

    // 只有 FileInputStream 或 ByteArrayInputStream 支持 mark/reset
    public boolean markSupported() {
        return fileChannel != null || originInputStream instanceof ByteArrayInputStream;
    }
}
```

**关键结论**：
- 内部缓冲区只有 **8KB**，不是全量缓存
- 对于普通 InputStream，`markSupported()` 返回 false，不支持重试

---

## 四、最终结论

### 两个观点都不正确

| 观点 | 是否正确 | 实际情况 |
|------|----------|----------|
| 观点1：读入内存导致 OOM | **错误** | SDK 不会把整个流读入内存。它使用 4KB buffer 边读边写 |
| 观点2：写入临时文件 | **错误** | SDK 也不会写临时文件。没有任何 `File.createTempFile` 或磁盘 IO |

### 实际发生的事情

SDK 的处理方式是：

1. **contentLength 设为 -1**，不设置 `Content-Length` HTTP header
2. OkHttp 检测到 `contentLength() == -1`，自动使用 **HTTP chunked transfer encoding**（分块传输编码）
3. 数据以 4KB 为单位，**边读边发送**，不需要知道总长度
4. 原始 InputStream 被包装为 `MayRepeatableInputStream`，内部只有 **8KB** 缓冲区

整个过程中，内存占用始终是 **KB 级别**（4KB 读缓冲 + 8KB 包装缓冲），与上传文件大小无关。

### 不指定 contentLength 的真正风险

虽然没有 OOM 风险，但仍存在以下问题：

| 风险 | 说明 |
|------|------|
| HTTP chunked 兼容性 | 某些代理、网关或旧版 OBS 服务端可能对 chunked 请求有限制 |
| 不可重试 | 对于普通 InputStream（非 FileInputStream），一旦开始传输就无法 reset，网络失败时无法重试 |
| 上传进度不精确 | SDK 的进度回调依赖 contentLength 计算百分比，-1 时无法准确计算 |
| 性能略差 | chunked 编码有额外的分块开销（每块需要写 hex 长度 + CRLF） |

### 最佳实践

```java
// 推荐：指定 contentLength
ObjectMetadata metadata = new ObjectMetadata();
metadata.setContentLength(knownSize);  // 设置已知长度
obsClient.putObject(bucketName, objectKey, inputStream, metadata);

// 如果确实不知道长度，至少确保传入 FileInputStream（可重试）
// 而不是内存中的 ByteArrayInputStream（小文件才用）
```

---

## 五、附录：反编译验证方法

本次分析通过以下方式完成：

```bash
# 1. 定位 OBS SDK jar（Maven 本地仓库）
#    groupId: com.huaweicloud  artifactId: esdk-obs-java  version: 3.21.4.1

# 2. 列出关键类
jar tf esdk-obs-java-3.21.4.1.jar | grep "AbstractObjectClient\|RequestConvertor\|RepeatableRequestEntity\|MayRepeatableInputStream"

# 3. 提取 class 文件
jar xf esdk-obs-java-3.21.4.1.jar com/obs/services/AbstractObjectClient.class ...

# 4. 反编译查看字节码
javap -c -p com/obs/services/internal/service/RequestConvertor.class
javap -c -p com/obs/services/internal/RepeatableRequestEntity.class
javap -c -p com/obs/services/internal/io/MayRepeatableInputStream.class
```

### 典型调用位置

通常出现在工具类中，如 `ObsUtil.uploadFromInputStream()`、`ObsUtil.uploadFile()` 等方法内，搜索 `obsClient.putObject` 即可定位所有调用点。
