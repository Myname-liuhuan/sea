# SpringDoc常见问题排查

**衍生自：** [SpringDoc统一网关文档配置](springdoc-gateway-config.md)
**拓展点：** SpringDoc配置和使用过程中的常见问题及解决方案
**创建日期：** 2026-02-24

---

## 1. 网关404错误问题

### 1.1 问题表现
访问 `http://localhost:9080/swagger-ui.html` 出现404错误。

### 1.2 原因分析

#### 可能原因1：缺少文档路由配置
网关没有正确配置到各个微服务的文档路由。

#### 可能原因2：依赖冲突
网关使用了错误的SpringDoc依赖（webmvc-ui vs webflux-ui）。

#### 可能原因3：配置冲突
网关的SpringDoc配置与其他配置冲突，导致配置未生效。

### 1.3 解决方案

#### 方案1：检查并添加文档路由
确保网关路由配置中包含文档路由：

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
```

#### 方案2：检查依赖配置
确保网关使用正确的SpringDoc依赖：

**网关（基于WebFlux）正确依赖：**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```

**业务服务（基于Web MVC）正确依赖：**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```

#### 方案3：检查配置优先级
确保网关使用独立的SpringDoc配置，避免与共享配置冲突：

```java
@Configuration
@Primary  // 确保网关配置优先级最高
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

### 1.4 诊断步骤
1. **检查网关日志**：查看是否有路由匹配失败的日志
2. **直接访问微服务文档**：尝试直接访问各个微服务的Swagger UI
3. **检查依赖树**：使用`mvn dependency:tree`检查依赖冲突
4. **验证配置加载**：检查网关是否加载了正确的SpringDoc配置

## 2. 文档聚合失败问题

### 2.1 问题表现
网关Swagger UI中显示"Failed to load API definition"错误。

### 2.2 原因分析

#### 可能原因1：微服务未启动
聚合的微服务没有正常启动。

#### 可能原因2：网络不可达
网关无法访问微服务的API文档端点。

#### 可能原因3：CORS问题
微服务的API文档端点没有配置CORS，导致网关无法访问。

### 2.3 解决方案

#### 方案1：检查微服务状态
确保所有需要聚合的微服务都正常启动：
```bash
# 检查各个微服务是否正常运行
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

#### 方案2：配置CORS
在各个微服务中配置CORS，允许网关访问：

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("http://localhost:9080")  // 网关地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

#### 方案3：使用服务发现
确保网关正确配置了服务发现：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: public
        group: DEFAULT_GROUP
```

### 2.4 诊断步骤
1. **直接访问微服务文档**：确认各个微服务的`/v3/api-docs`端点可访问
2. **检查网关路由**：确认网关路由配置正确
3. **查看网络连接**：使用`telnet`或`curl`测试网关到微服务的网络连接
4. **检查服务注册**：确认微服务已注册到服务发现中心

## 3. 安全配置问题

### 3.1 问题表现
访问Swagger UI时需要认证，或者返回403错误。

### 3.2 原因分析

#### 可能原因1：安全白名单未配置
SpringDoc相关路径没有添加到安全白名单。

#### 可能原因2：JWT过滤器拦截
JWT认证过滤器拦截了文档请求。

#### 可能原因3：权限配置错误
错误的权限配置导致文档无法访问。

### 3.3 解决方案

#### 方案1：配置安全白名单
在各个微服务的安全配置中添加SpringDoc路径白名单：

```yaml
security:
  ignore:
    - /v3/api-docs/**
    - /swagger-ui/**
    - /swagger-ui.html
    - /webjars/**
    - /swagger-resources/**
    - /favicon.ico
```

#### 方案2：调整过滤器顺序
确保JWT过滤器在SpringDoc路径之前不生效：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/favicon.ico"
                ).permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .csrf().disable();
    }
}
```

#### 方案3：网关安全配置
在网关中配置文档路径免认证：

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

## 4. 性能问题

### 4.1 问题表现
Swagger UI加载缓慢，或者API文档生成耗时过长。

### 4.2 原因分析

#### 可能原因1：API数量过多
Controller和API数量过多，导致文档生成缓慢。

#### 可能原因2：缓存未启用
SpringDoc缓存未启用，每次请求都重新生成文档。

#### 可能原因3：复杂模型
复杂的DTO模型导致文档生成耗时。

### 4.3 解决方案

#### 方案1：启用缓存
在生产环境启用SpringDoc缓存：

```yaml
springdoc:
  cache:
    disabled: false
  swagger-ui:
    cache:
      disabled: false
```

#### 方案2：分组API
将API按功能分组，减少单组API数量：

```java
@Configuration
public class OpenApiGroupsConfig {
    
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户管理")
                .pathsToMatch("/api/users/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("产品管理")
                .pathsToMatch("/api/products/**")
                .build();
    }
}
```

#### 方案3：优化DTO模型
简化DTO模型，避免复杂的嵌套结构：

```java
// 避免过度嵌套
@Schema(description = "简化后的用户信息")
public class SimpleUserDTO {
    private Long id;
    private String username;
    private String email;
    // 避免嵌套其他复杂对象
}
```

## 5. 版本兼容性问题

### 5.1 问题表现
升级Spring Boot或SpringDoc版本后，文档功能异常。

### 5.2 原因分析

#### 可能原因1：API变更
新版本SpringDoc的API发生变化。

#### 可能原因2：配置变更
新版本的配置项发生变化。

#### 可能原因3：依赖冲突
新版本与其他依赖存在冲突。

### 5.3 解决方案

#### 方案1：查看升级指南
在升级前查看SpringDoc的官方升级指南：
- [SpringDoc Migration Guide](https://springdoc.org/#migrating-from-springfox)

#### 方案2：逐步升级
采用逐步升级策略：
1. 先升级到中间版本
2. 测试文档功能
3. 再升级到目标版本

#### 方案3：版本锁定
在dependencyManagement中锁定SpringDoc版本：

```xml
<properties>
    <springdoc.version>2.6.0</springdoc.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 6. 自定义配置问题

### 6.1 问题表现
自定义的OpenAPI配置不生效。

### 6.2 原因分析

#### 可能原因1：配置类未加载
自定义配置类没有被Spring加载。

#### 可能原因2：配置冲突
多个配置类之间存在冲突。

#### 可能原因3：配置顺序问题
配置加载顺序不正确。

### 6.3 解决方案

#### 方案1：检查配置类注解
确保配置类有正确的注解：

```java
@Configuration
@EnableWebMvc  // 如果是Web MVC项目
public class CustomOpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("自定义API文档")
                        .version("1.0.0"));
    }
}
```

#### 方案2：使用@Primary注解
当有多个OpenAPI Bean时，使用@Primary指定主配置：

```java
@Configuration
@Primary
public class PrimaryOpenApiConfig {
    
    @Bean
    public OpenAPI primaryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("主API文档")
                        .version("1.0.0"));
    }
}
```

#### 方案3：检查包扫描路径
确保配置类在Spring的包扫描路径内：

```yaml
spring:
  main:
    allow-bean-definition-overriding: true
```

## 7. 诊断工具和命令

### 7.1 健康检查端点
SpringDoc提供了健康检查端点：
- `http://localhost:8080/actuator/health` - 应用健康状态
- `http://localhost:8080/actuator/info` - 应用信息
- `http://localhost:8080/actuator/metrics` - 应用指标

### 7.2 调试日志
启用SpringDoc调试日志：

```yaml
logging:
  level:
    org.springdoc: DEBUG
    org.springframework.web: DEBUG
```

### 7.3 常用诊断命令

```bash
# 检查应用健康状态
curl http://localhost:8080/actuator/health

# 检查API文档端点
curl http://localhost:8080/v3/api-docs

# 检查依赖冲突
mvn dependency:tree -Dincludes=org.springdoc

# 检查端口占用
netstat -ano | findstr :8080
```

## 8. 预防措施

### 8.1 配置检查清单
在部署前检查以下配置：
- [ ] SpringDoc依赖版本一致
- [ ] 网关路由配置正确
- [ ] 安全白名单配置完整
- [ ] CORS配置正确
- [ ] 缓存配置合理

### 8.2 监控告警
配置监控告警，及时发现文档问题：
- API文档端点可用性监控
- 文档生成耗时监控
- 错误率监控

### 8.3 定期维护
定期进行文档系统维护：
- 清理过时的API文档
- 更新文档示例和描述
- 优化文档性能
- 备份文档配置

---

*通过系统的问题排查和预防措施，可以确保SpringDoc文档系统的稳定运行，为开发团队提供可靠的API文档服务。*