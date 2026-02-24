# Spring Boot静态资源配置

**分类：** 技术/Spring
**创建日期：** 2026-02-24
**最后更新：** 2026-02-24

## 相关拓展文档
- [自定义静态资源配置](spring-static-resources-ext-01.md)
- [静态资源缓存策略](spring-static-resources-ext-02.md)

---

## 1. 概述

Spring Boot提供了自动配置的静态资源处理机制，使得开发Web应用时能够方便地提供静态资源（如HTML、CSS、JavaScript、图片等）。

### 1.1 静态资源处理流程
```
HTTP请求 → DispatcherServlet → HandlerMapping匹配
    ↓
如果匹配到Controller → 执行Controller方法
    ↓
如果未匹配到Controller → 尝试静态资源映射
    ↓
如果找到静态资源 → 返回静态资源
    ↓
如果未找到静态资源 → 返回404错误
```

## 2. 默认静态资源位置

Spring Boot默认会在以下位置查找静态资源：

### 2.1 类路径下的标准目录
1. `/static` - 最常用的静态资源目录
2. `/public` - 公共资源目录
3. `/resources` - 资源文件目录
4. `/META-INF/resources` - META-INF下的资源目录

### 2.2 优先级顺序
Spring Boot会按以下顺序查找静态资源（从上到下，找到即止）：
1. `/META-INF/resources/`
2. `/resources/`
3. `/static/`
4. `/public/`

### 2.3 示例目录结构
```
src/main/resources/
├── static/
│   ├── css/
│   │   └── style.css
│   ├── js/
│   │   └── app.js
│   └── images/
│       └── logo.png
├── templates/     # Thymeleaf模板（非静态资源）
└── application.properties
```

## 3. 访问静态资源

### 3.1 默认访问路径
静态资源可以通过根路径直接访问：
- `http://localhost:8080/css/style.css`
- `http://localhost:8080/js/app.js`
- `http://localhost:8080/images/logo.png`

### 3.2 自定义上下文路径
如果配置了`server.servlet.context-path`：
```yaml
server:
  servlet:
    context-path: /api
```
则访问路径变为：
- `http://localhost:8080/api/css/style.css`
- `http://localhost:8080/api/js/app.js`

## 4. 配置静态资源映射

### 4.1 基本配置
```yaml
spring:
  web:
    resources:
      # 静态资源位置（可以添加自定义位置）
      static-locations:
        - classpath:/static/
        - classpath:/public/
        - classpath:/resources/
        - classpath:/META-INF/resources/
        - file:/opt/static/  # 外部目录
      
      # 缓存配置
      cache:
        period: 3600  # 缓存时间（秒）
        cachecontrol:
          max-age: 3600
          must-revalidate: true
      
      # 链式配置
      chain:
        enabled: true
        compressed: false
        strategy:
          content:
            enabled: true
            paths: /**
```

### 4.2 禁用静态资源映射
在某些API-only的应用中，可能需要禁用静态资源映射：
```yaml
spring:
  web:
    resources:
      add-mappings: false
```

**效果：**
- 当请求路径无法匹配任何Controller时
- 不会尝试查找静态资源
- 直接返回404错误（而不是NoResourceFoundException）

### 4.3 自定义静态资源路径
```yaml
spring:
  web:
    resources:
      static-locations:
        - classpath:/assets/  # 自定义目录
        - file:/var/www/html/ # 外部文件系统目录
```

## 5. 高级配置

### 5.1 版本控制（资源指纹）
```yaml
spring:
  web:
    resources:
      chain:
        strategy:
          content:
            enabled: true
            paths: /**
```

启用后，Spring Boot会自动为静态资源添加版本号（基于内容哈希）：
- 原始文件：`/js/app.js`
- 版本化：`/js/app-2f8f1b2c7d.js`

### 5.2 Gzip压缩
```yaml
spring:
  web:
    resources:
      chain:
        compressed: true
```

Spring Boot会自动为`.js`、`.css`等文本文件提供Gzip压缩版本。

### 5.3 缓存控制
```yaml
spring:
  web:
    resources:
      cache:
        period: 86400  # 24小时缓存
        cachecontrol:
          max-age: 86400
          s-maxage: 86400
          public: true
          immutable: true  # 对于版本化资源
```

## 6. 自定义静态资源处理器

### 6.1 通过配置类自定义
```java
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加自定义静态资源路径
        registry.addResourceHandler("/files/**")
                .addResourceLocations("classpath:/files/", "file:/opt/uploads/");
        
        // 配置缓存策略
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS));
        
        // 配置版本化资源
        registry.addResourceHandler("/versioned/**")
                .addResourceLocations("classpath:/versioned/")
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
    }
}
```

### 6.2 自定义资源解析器
```java
@Configuration
public class CustomResourceConfig {
    
    @Bean
    public ResourceResolver customResourceResolver() {
        return new ResourceResolver() {
            @Override
            public Resource resolveResource(HttpServletRequest request, String requestPath,
                    List<? extends Resource> locations, ResourceResolverChain chain) {
                // 自定义资源解析逻辑
                return chain.resolveResource(request, requestPath, locations);
            }
            
            @Override
            public String resolveUrlPath(String resourcePath, List<? extends Resource> locations,
                    ResourceResolverChain chain) {
                // 自定义URL解析逻辑
                return chain.resolveUrlPath(resourcePath, locations);
            }
        };
    }
}
```

## 7. 常见问题与解决方案

### 7.1 静态资源404错误

#### 问题描述：
访问静态资源时返回404错误。

#### 可能原因及解决方案：
1. **资源位置错误**
   - 检查资源是否在正确的目录（`/static`、`/public`等）
   - 检查文件名和路径大小写

2. **配置覆盖**
   - 检查是否有自定义的`WebMvcConfigurer`覆盖了默认配置
   - 确保没有禁用静态资源映射

3. **上下文路径问题**
   - 如果配置了`server.servlet.context-path`，需要在URL中包含该路径
   - 检查`spring.web.resources.static-locations`配置

### 7.2 静态资源缓存问题

#### 问题描述：
修改静态资源后，浏览器仍然使用缓存版本。

#### 解决方案：
1. **启用版本控制**
   ```yaml
   spring:
     web:
       resources:
         chain:
           strategy:
             content:
               enabled: true
   ```

2. **禁用缓存（开发环境）**
   ```yaml
   spring:
     web:
       resources:
         cache:
           period: 0  # 禁用缓存
   ```

3. **强制刷新**
   - 浏览器强制刷新（Ctrl+F5）
   - 清除浏览器缓存

### 7.3 性能优化

#### 7.3.1 启用Gzip压缩
```yaml
spring:
  web:
    resources:
      chain:
        compressed: true
```

#### 7.3.2 配置合适的缓存策略
```yaml
spring:
  web:
    resources:
      cache:
        period: 31536000  # 一年（适用于版本化资源）
        cachecontrol:
          max-age: 31536000
          immutable: true
```

#### 7.3.3 使用CDN
对于生产环境，建议将静态资源部署到CDN：
```java
@Configuration
public class CdnConfig implements WebMvcConfigurer {
    
    @Value("${cdn.url}")
    private String cdnUrl;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 开发环境使用本地资源
        if (!cdnUrl.isEmpty()) {
            registry.addResourceHandler("/static/**")
                    .addResourceLocations(cdnUrl + "/static/");
        } else {
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:/static/");
        }
    }
}
```

## 8. 最佳实践

### 8.1 目录组织建议
```
src/main/resources/static/
├── css/           # 样式文件
│   ├── base/
│   ├── components/
│   └── pages/
├── js/            # JavaScript文件
│   ├── lib/       # 第三方库
│   ├── utils/     # 工具函数
│   └── pages/     # 页面脚本
├── images/        # 图片资源
│   ├── icons/     # 图标
│   ├── logos/     # Logo
│   └── banners/   # 横幅图片
├── fonts/         # 字体文件
└── favicon.ico    # 网站图标
```

### 8.2 配置建议

#### 开发环境配置：
```yaml
spring:
  web:
    resources:
      cache:
        period: 0  # 禁用缓存，便于开发调试
      chain:
        compressed: false  # 开发环境可禁用压缩
```

#### 生产环境配置：
```yaml
spring:
  web:
    resources:
      cache:
        period: 31536000  # 长期缓存
        cachecontrol:
          max-age: 31536000
          immutable: true
      chain:
        compressed: true  # 启用压缩
        strategy:
          content:
            enabled: true  # 启用版本控制
            paths: /**
```

### 8.3 安全考虑
1. **避免敏感信息**：不要在静态资源中包含敏感信息
2. **文件上传限制**：如果允许文件上传，需要限制文件类型和大小
3. **路径遍历防护**：确保用户无法通过路径遍历访问系统文件
4. **CORS配置**：如果需要跨域访问静态资源，配置合适的CORS策略

---

*Spring Boot的静态资源处理机制非常灵活，可以根据项目需求进行各种定制。合理配置静态资源可以显著提升应用性能和用户体验。*