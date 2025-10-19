# SpringDoc 接口文档框架配置说明

## 已配置模块
- ❌ sea-gateway (网关模块) - 网关作为路由转发，不包含业务API
- ✅ sea-auth (认证模块)  
- ✅ sea-system (系统模块)

## 配置内容

### 1. 依赖配置
在主pom.xml中添加了springdoc版本管理：
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

### 2. 模块依赖
为业务模块添加了springdoc依赖：
- sea-auth/pom.xml  
- sea-system/sea-system-service/pom.xml

**注意**: gateway模块不包含springdoc依赖，因为网关作为路由转发层，不包含业务API。

### 3. Nacos配置
在doc/yaml目录下的配置文件中添加了springdoc配置：

**sea-auth.yaml:**
```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    display-request-duration: true
  packages-to-scan: com.example.sea.auth
  paths-to-match: /**
```

**sea-system.yaml:**
```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    display-request-duration: true
  packages-to-scan: com.example.sea.system
  paths-to-match: /**
```

## 访问方式

启动各个模块后，可以通过以下URL访问接口文档：

### Auth模块  
- Swagger UI: http://localhost:8081/swagger-ui.html
- API Docs: http://localhost:8081/v3/api-docs

### System模块
- Swagger UI: http://localhost:8083/swagger-ui.html  
- API Docs: http://localhost:8083/v3/api-docs

**注意**: Gateway模块作为路由转发层，不提供独立的API文档。业务API通过网关路由到对应的服务模块。

## 功能特性

1. **自动API文档生成**: 基于Spring MVC注解自动生成OpenAPI 3.0规范文档
2. **交互式UI**: 提供Swagger UI界面，支持在线测试API
3. **分组扫描**: 每个模块只扫描自己的包路径
4. **排序功能**: 支持按方法和标签排序
5. **请求时长显示**: 显示API请求处理时长

## 安全配置说明

由于项目使用了JWT认证过滤器，需要将springdoc相关路径添加到安全白名单中：

**已配置的白名单路径：**
- `/v3/api-docs/**` - OpenAPI规范文档
- `/swagger-ui/**` - Swagger UI静态资源
- `/swagger-ui.html` - Swagger UI主页面
- `/webjars/**` - WebJars资源
- `/swagger-resources/**` - Swagger资源
- `/favicon.ico` - 网站图标

## 注意事项

1. 确保Nacos配置中心已启动并加载配置
2. 各模块启动时会自动加载springdoc配置
3. 接口文档会自动包含所有Controller中的API
4. 可以通过@Operation、@Parameter等注解增强文档信息
5. 安全白名单配置确保API文档可以正常访问，无需token验证
