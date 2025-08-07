# Spring Security 鉴权认证配置说明

## 项目结构
本项目采用微服务架构，包含以下模块：
- **sea-auth**: 认证服务，负责用户登录和token生成
- **sea-gateway**: 网关服务，负责统一入口和token验证
- **sea-code**: 代码生成服务，业务模块示例
- **sea-system**: 系统管理服务
- **sea-media**: 媒体管理服务

## 认证流程

### 1. 用户登录
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "admin123"
}
```

### 2. 获取Token
登录成功返回：
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 86400000
}
```

### 3. 访问受保护资源
在请求头中添加：
```
Authorization: Bearer <token>
```

## 配置说明

### Auth服务配置 (sea-auth)
- **端口**: 8081
- **JWT配置**:
  - secret: Base64编码的密钥
  - expirationMs: 24小时
  - refreshExpirationMs: 7天

### Gateway网关配置 (sea-gateway)
- **端口**: 8080
- **路由配置**:
  - `/api/auth/**` -> sea-auth服务
  - `/api/code/**` -> sea-code服务
  - `/api/system/**` -> sea-system服务
  - `/api/media/**` -> sea-media服务

### 业务服务配置 (以sea-code为例)
- **端口**: 8082
- **安全配置**:
  - 所有请求需要认证
  - `/api/public/**` 和 `/api/code/public/**` 允许匿名访问

## 跳过认证的URL
以下URL不需要认证即可访问：
- `/api/auth/login` - 用户登录
- `/api/auth/refresh` - 刷新token
- `/api/public/**` - 公共资源

## 用户信息传递
网关验证token后，会将用户信息添加到请求头：
- `X-User-Name`: 用户名
- `X-User-Roles`: 用户角色列表

## 快速开始

### 1. 启动Nacos
确保Nacos服务运行在 `localhost:8848`

### 2. 启动服务
按以下顺序启动服务：
1. sea-auth (端口8081)
2. sea-code (端口8082)
3. sea-gateway (端口8080)

### 3. 测试认证
```bash
# 1. 登录获取token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. 使用token访问受保护资源
curl -X GET http://localhost:8080/api/code/test \
  -H "Authorization: Bearer <your-token>"
```

## 注意事项
1. 当前使用模拟用户验证，实际项目中应从数据库验证用户
2. JWT密钥需要定期更换
3. 生产环境应使用HTTPS
4. 刷新token机制需要完善
5. 使用nacos config的情况下要注意data-id要完全一样,特别是后缀！！