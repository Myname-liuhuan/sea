# SpringDoc 接口文档框架配置说明

## 已配置模块
- ✅ sea-gateway (网关模块) - 提供统一API文档聚合入口
- ✅ sea-auth (认证模块)  
- ✅ sea-system (系统模块)
- ✅ sea-media (媒体模块)

## 统一配置方案

### 1. 依赖管理
在主pom.xml中统一管理springdoc版本：
```xml
<springdoc.version>2.6.0</springdoc.version>
```

在dependencyManagement中添加了springdoc依赖：
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```
说明：因为springdoc有自己的默认实现方式，所以在yaml中配置完可以不用写config类。同时，如果发现yaml配置满足不了的情况下，可以考虑config实现的方式

### 2. 统一配置架构

#### 共享配置 (shared_bootstrap.yaml)
包含所有服务共享的SpringDoc基础配置：
```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    display-request-duration: true
    display-operation-id: false
    disable-swagger-default-url: true
  paths-to-match: /**
```

#### 服务特定配置
每个服务只配置自己的包扫描路径：

**sea-auth.yaml:**
```yaml
springdoc:
  packages-to-scan: com.example.sea.auth
```

**sea-system.yaml:**
```yaml
springdoc:
  packages-to-scan: com.example.sea.system
```

**sea-media.yaml:**
```yaml
springdoc:
  packages-to-scan: com.example.sea.media
```

### 3. 统一配置类

在`sea-common-core`模块中创建了`SpringDocConfig.java`，提供：
- 统一的API信息配置
- 自动服务名称识别
- 服务器信息配置
- 可扩展的OpenAPI自定义配置

### 4. 网关聚合配置

网关模块提供统一的API文档入口，聚合所有微服务的文档：

**网关配置 (sea-gateway.yaml):**
```yaml
springdoc:
  swagger-ui:
    urls:
      - name: 认证服务
        url: /sea-auth/v3/api-docs
      - name: 系统服务
        url: /sea-system/v3/api-docs
      - name: 媒体服务
        url: /sea-media/v3/api-docs
```

## 访问方式

### 统一入口（推荐）
- **网关Swagger UI**: http://localhost:9080/swagger-ui.html
- **网关API Docs**: http://localhost:9080/v3/api-docs

通过网关可以统一访问所有微服务的API文档，支持服务间切换。

### 独立访问
启动各个模块后，也可以通过以下URL独立访问接口文档：

#### Auth模块 (端口: 8081)
- Swagger UI: http://localhost:8081/swagger-ui.html
- API Docs: http://localhost:8081/v3/api-docs

#### System模块 (端口: 8083)
- Swagger UI: http://localhost:8083/swagger-ui.html  
- API Docs: http://localhost:8083/v3/api-docs

#### Media模块 (端口: 8082)
- Swagger UI: http://localhost:8082/swagger-ui.html
- API Docs: http://localhost:8082/v3/api-docs

## 功能特性

1. **统一配置管理**: 通过共享配置减少重复配置
2. **自动API文档生成**: 基于Spring MVC注解自动生成OpenAPI 3.0规范文档
3. **交互式UI**: 提供Swagger UI界面，支持在线测试API
4. **服务聚合**: 网关提供统一的API文档入口，聚合所有服务
5. **分组扫描**: 每个模块只扫描自己的包路径
6. **排序功能**: 支持按方法和标签排序
7. **请求时长显示**: 显示API请求处理时长
8. **服务识别**: 自动识别服务类型并显示对应名称

## 安全配置说明

由于项目使用了JWT认证过滤器，需要将springdoc相关路径添加到安全白名单中：

**已配置的白名单路径：**
- `/v3/api-docs/**` - OpenAPI规范文档
- `/swagger-ui/**` - Swagger UI静态资源
- `/swagger-ui.html` - Swagger UI主页面
- `/webjars/**` - WebJars资源
- `/swagger-resources/**` - Swagger资源
- `/favicon.ico` - 网站图标

## 配置优势

1. **减少重复配置**: 共享配置避免了每个服务的重复配置
2. **统一维护**: 基础配置变更只需修改一处
3. **易于扩展**: 新服务只需添加包扫描配置即可
4. **集中访问**: 通过网关统一访问所有API文档
5. **灵活性**: 仍支持各服务独立访问和自定义配置

## 注意事项

1. 确保Nacos配置中心已启动并加载配置
2. 各模块启动时会自动加载springdoc配置
3. 接口文档会自动包含所有Controller中的API
4. 可以通过@Operation、@Parameter等注解增强文档信息
5. 安全白名单配置确保API文档可以正常访问，无需token验证
6. 网关聚合功能需要所有相关服务都正常启动
7. 新服务接入只需：添加依赖 → 创建Nacos配置 → 配置包扫描路径

## 常见问题排查

### 1. 网关404错误
**问题表现**：访问 `http://localhost:9080/swagger-ui.html` 出现404错误

**原因分析**：
- 缺少SpringDoc文档路由配置
- 网关使用了错误的SpringDoc依赖（webmvc-ui vs webflux-ui）
- 配置冲突导致SpringDoc配置未生效

**解决方案**：
1. 在网关路由中添加文档路由：
```yaml
- id: sea-auth-docs
  uri: lb://sea-auth
  order: 10
  predicates:
    - Path=/v3/api-docs/sea-auth/**
  filters:
    - StripPrefix=3
```

2. 网关使用正确的依赖：
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
</dependency>
```

3. 避免配置冲突，网关使用独立的SpringDoc配置

### 2. 依赖冲突
**注意**：网关服务基于WebFlux，必须使用`springdoc-openapi-starter-webflux-ui`，而业务服务基于Web MVC，使用`springdoc-openapi-starter-webmvc-ui`

### 3. 配置优先级
- 网关的`GatewaySpringDocConfig`使用`@Primary`注解确保优先级
- 共享配置中的springdoc配置不适用于网关服务
