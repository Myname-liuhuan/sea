# SpringDoc统一网关文档配置

**分类：** 技术/Spring
**创建日期：** 2026-02-24
**最后更新：** 2026-02-24

## 相关拓展文档
- [SpringDoc常见问题排查](springdoc-gateway-config-ext-01.md)
- [OpenAPI注解使用指南](springdoc-gateway-config-ext-02.md)

---

## 1. 概述

SpringDoc OpenAPI是一个基于OpenAPI 3规范的Spring Boot文档生成工具，能够自动从Spring MVC控制器生成API文档。本项目采用统一配置方案，通过网关聚合所有微服务的API文档。

### 1.1 已配置模块
- ✅ **sea-gateway** (网关模块) - 提供统一API文档聚合入口
- ✅ **sea-auth** (认证模块) - 用户认证和授权服务
- ✅ **sea-system** (系统模块) - 系统管理服务
- ✅ **sea-media** (媒体模块) - 媒体文件处理服务

## 2. 统一配置架构

### 2.1 依赖管理

#### 主pom.xml版本管理
```xml
<!-- 统一管理SpringDoc版本 -->
<springdoc.version>2.6.0</springdoc.version>
```

#### 依赖配置
```xml
<!-- 在dependencyManagement中统一配置 -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>

<!-- 网关特殊依赖（基于WebFlux） -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```

### 2.2 配置分层设计

#### 2.2.1 共享基础配置 (`shared_bootstrap.yaml`)
```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    operations-sorter: method        # 按HTTP方法排序
    tags-sorter: alpha              # 按字母顺序排序标签
    display-request-duration: true  # 显示请求时长
    display-operation-id: false     # 不显示操作ID
    disable-swagger-default-url: true  # 禁用默认URL
  paths-to-match: /**               # 匹配所有路径
  show-actuator: false              # 不显示Actuator端点
```

#### 2.2.2 服务特定配置

**认证服务 (`sea-auth.yaml`):**
```yaml
springdoc:
  packages-to-scan: com.example.sea.auth
  group-name: auth-service
  cache:
    disabled: false
```

**系统服务 (`sea-system.yaml`):**
```yaml
springdoc:
  packages-to-scan: com.example.sea.system
  group-name: system-service
  cache:
    disabled: false
```

**媒体服务 (`sea-media.yaml`):**
```yaml
springdoc:
  packages-to-scan: com.example.sea.media
  group-name: media-service
  cache:
    disabled: false
```

### 2.3 统一配置类

在`sea-common-core`模块中创建了`SpringDocConfig.java`：

```java
@Configuration
public class SpringDocConfig {
    
    @Bean
    public OpenAPI customOpenAPI(
            @Value("${spring.application.name:unknown}") String appName,
            @Value("${spring.application.version:1.0.0}") String appVersion) {
        
        return new OpenAPI()
                .info(new Info()
                        .title(getServiceTitle(appName))
                        .version(appVersion)
                        .description(getServiceDescription(appName))
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目文档")
                        .url("https://docs.example.com"))
                .addServersItem(new Server()
                        .url("/")
                        .description("当前服务"));
    }
    
    private String getServiceTitle(String appName) {
        Map<String, String> serviceTitles = Map.of(
            "sea-auth", "认证服务API文档",
            "sea-system", "系统服务API文档", 
            "sea-media", "媒体服务API文档",
            "sea-gateway", "统一网关API文档"
        );
        return serviceTitles.getOrDefault(appName, appName + " API文档");
    }
    
    private String getServiceDescription(String appName) {
        Map<String, String> descriptions = Map.of(
            "sea-auth", "用户认证、授权、权限管理相关接口",
            "sea-system", "系统管理、用户管理、角色管理相关接口",
            "sea-media", "媒体文件上传、处理、管理相关接口"
        );
        return descriptions.getOrDefault(appName, appName + "接口文档");
    }
}
```

## 3. 网关聚合配置

### 3.1 网关SpringDoc配置

**网关专用配置类 (`GatewaySpringDocConfig.java`):**
```java
@Configuration
@Primary
public class GatewaySpringDocConfig {
    
    @Bean
    public GroupedOpenApi gatewayApi() {
        return GroupedOpenApi.builder()
                .group("gateway")
                .pathsToMatch("/**")
                .build();
    }
    
    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("统一网关API文档")
                        .version("1.0.0")
                        .description("聚合所有微服务的API文档"))
                .externalDocs(new ExternalDocumentation()
                        .description("完整项目文档")
                        .url("https://docs.example.com"));
    }
}
```

### 3.2 网关路由配置 (`sea-gateway.yaml`)

```yaml
springdoc:
  swagger-ui:
    urls:
      - name: 认证服务
        url: /sea-auth/v3/api-docs
        primary-name: auth-service
      - name: 系统服务
        url: /sea-system/v3/api-docs
        primary-name: system-service
      - name: 媒体服务
        url: /sea-media/v3/api-docs
        primary-name: media-service
    tags-sorter: alpha
    operations-sorter: method
    display-request-duration: true
    default-models-expand-depth: 2
    default-model-expand-depth: 2
```

### 3.3 网关路由规则

需要在网关路由配置中添加文档路由：

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 认证服务文档路由
        - id: sea-auth-docs
          uri: lb://sea-auth
          order: 10
          predicates:
            - Path=/v3/api-docs/sea-auth/**
          filters:
            - StripPrefix=3
        
        # 系统服务文档路由
        - id: sea-system-docs
          uri: lb://sea-system
          order: 10
          predicates:
            - Path=/v3/api-docs/sea-system/**
          filters:
            - StripPrefix=3
        
        # 媒体服务文档路由
        - id: sea-media-docs
          uri: lb://sea-media
          order: 10
          predicates:
            - Path=/v3/api-docs/sea-media/**
          filters:
            - StripPrefix=3
        
        # Swagger UI静态资源路由
        - id: swagger-ui-resources
          uri: lb://sea-gateway
          order: 20
          predicates:
            - Path=/swagger-ui/**, /webjars/**, /swagger-resources/**
```

## 4. 访问方式

### 4.1 统一入口（推荐）

通过网关统一访问所有微服务的API文档：

- **网关Swagger UI**: http://localhost:9080/swagger-ui.html
- **网关API Docs**: http://localhost:9080/v3/api-docs

**优势：**
- 统一入口，无需记住各个服务的地址
- 支持服务间快速切换
- 统一的界面和体验

### 4.2 独立访问

各个服务也可以独立访问：

#### 认证服务 (端口: 8081)
- Swagger UI: http://localhost:8081/swagger-ui.html
- API Docs: http://localhost:8081/v3/api-docs

#### 系统服务 (端口: 8083)
- Swagger UI: http://localhost:8083/swagger-ui.html
- API Docs: http://localhost:8083/v3/api-docs

#### 媒体服务 (端口: 8082)
- Swagger UI: http://localhost:8082/swagger-ui.html
- API Docs: http://localhost:8082/v3/api-docs

## 5. 安全配置

### 5.1 安全白名单

由于项目使用JWT认证，需要将SpringDoc相关路径添加到安全白名单：

```yaml
# 各个服务的security配置中
security:
  ignore:
    - /v3/api-docs/**
    - /swagger-ui/**
    - /swagger-ui.html
    - /webjars/**
    - /swagger-resources/**
    - /favicon.ico
```

### 5.2 网关安全配置

网关需要允许文档相关请求通过：

```java
@Configuration
public class GatewaySecurityConfig {
    
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .pathMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/favicon.ico"
                ).permitAll()
                .anyExchange().authenticated()
                .and()
                .csrf().disable()
                .build();
    }
}
```

## 6. 功能特性

### 6.1 统一配置管理
- **共享配置**：基础配置统一管理，减少重复
- **服务特定配置**：每个服务只配置包扫描路径
- **版本统一**：所有服务使用相同的SpringDoc版本

### 6.2 自动文档生成
- **基于注解**：自动从`@RestController`、`@RequestMapping`等注解生成文档
- **实时更新**：代码变更后文档自动更新
- **类型安全**：基于Java类型系统，确保文档准确性

### 6.3 交互式UI
- **在线测试**：直接在Swagger UI中测试API
- **参数验证**：支持请求参数验证
- **响应预览**：查看API响应格式和示例

### 6.4 服务聚合
- **统一入口**：通过网关访问所有服务文档
- **服务切换**：在UI中快速切换不同服务
- **集中管理**：统一管理所有API文档

## 7. 新服务接入流程

### 7.1 添加依赖
在服务的pom.xml中添加依赖：
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```

### 7.2 创建Nacos配置
在Nacos中创建服务配置（如`sea-new-service.yaml`）：
```yaml
springdoc:
  packages-to-scan: com.example.sea.newservice
  group-name: new-service
```

### 7.3 更新网关配置
在网关配置中添加新服务的文档路由：
```yaml
springdoc:
  swagger-ui:
    urls:
      # 现有服务...
      - name: 新服务
        url: /sea-new-service/v3/api-docs
        primary-name: new-service
```

### 7.4 添加网关路由
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: sea-new-service-docs
          uri: lb://sea-new-service
          predicates:
            - Path=/v3/api-docs/sea-new-service/**
          filters:
            - StripPrefix=3
```

## 8. 最佳实践

### 8.1 注解使用建议

#### 控制器级别注解
```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关的CRUD操作")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "通过用户ID获取用户详细信息")
    @ApiResponse(responseCode = "200", description = "成功获取用户")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    public User getUser(@Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        // 业务逻辑
    }
}
```

#### 模型注解
```java
@Schema(description = "用户信息")
public class User {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "张三", required = true)
    private String username;
    
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
}
```

### 8.2 配置优化建议

#### 开发环境配置
```yaml
springdoc:
  cache:
    disabled: true  # 禁用缓存，实时更新文档
  swagger-ui:
    display-request-duration: true
    try-it-out-enabled: true  # 启用"Try it out"功能
```

#### 生产环境配置
```yaml
springdoc:
  cache:
    disabled: false  # 启用缓存，提升性能
  swagger-ui:
    display-request-duration: false
    try-it-out-enabled: false  # 禁用"Try it out"功能
```

### 8.3 文档维护建议
1. **及时更新注解**：代码变更后及时更新相关注解
2. **提供完整示例**：为每个API提供完整的请求/响应示例
3. **描述清晰准确**：使用清晰准确的语言描述API功能
4. **版本管理**：API变更时更新版本号，保持向后兼容

---

*SpringDoc统一网关文档配置方案提供了高效、统一的API文档管理方式，既支持各个服务的独立文档，又通过网关实现了文档聚合，大大提升了开发效率和文档可维护性。*