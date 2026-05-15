# 技术文档

本目录包含项目技术相关文档，涵盖各种技术框架、工具、库的使用方法和配置说明。

## 子目录

### 🌱 Spring相关 (spring/)
- [Spring Boot静态资源配置](spring/spring-static-resources.md) - 静态资源处理配置
- [SpringDoc统一网关文档配置](spring/springdoc-gateway-config.md) - API文档聚合配置
- 其他Spring相关文档

### 🗄️ Redis相关 (redis/)
- [RedisTemplate方法参考](redis/redis-template-methods.md) - Redis各种数据类型的操作方法
- Redis配置和优化

### 🗃️ 数据库相关 (database/)
- 数据库设计和使用文档

### ☁️ 云服务相关 (cloud/)
- [华为云 OBS SDK Content-Length 行为分析](cloud/obs-sdk-putobject-content-length-analysis.md) - putObject不指定Content-Length时的底层机制分析

## 文档列表


### Spring相关文档

#### 1. Spring Boot静态资源配置
- [Spring Boot静态资源配置](spring/spring-static-resources.md)
  - **创建日期**: 2026-02-24
  - **最后更新**: 2026-02-24
  - **分类**: 技术/Spring
  - **描述**: Spring Boot静态资源处理机制，包含默认位置、配置方法、常见问题等
  
  - **相关拓展文档**:
    - [自定义静态资源配置](spring/spring-static-resources-ext-01.md) *（待创建）*
    - [静态资源缓存策略](spring/spring-static-resources-ext-02.md) *（待创建）*

#### 2. SpringDoc统一网关文档配置
- [SpringDoc统一网关文档配置](spring/springdoc-gateway-config.md)
  - **创建日期**: 2026-02-24
  - **最后更新**: 2026-02-24
  - **分类**: 技术/Spring
  - **描述**: SpringDoc API文档统一配置方案，网关聚合所有微服务文档
  
  - **相关拓展文档**:
    - [SpringDoc常见问题排查](spring/springdoc-gateway-config-ext-01.md) *（待创建）*
    - [OpenAPI注解使用指南](spring/springdoc-gateway-config-ext-02.md) *（待创建）*

### Redis相关文档

#### 1. RedisTemplate方法参考
- [RedisTemplate方法参考](redis/redis-template-methods.md)
  - **创建日期**: 2026-02-24
  - **最后更新**: 2026-02-24
  - **分类**: 技术/Redis
  - **描述**: Redis各种数据类型的操作方法，包含String、Hash、List、Set、ZSet等
  
  - **相关拓展文档**:
    - [Redis性能调优指南](redis/redis-template-methods-ext-01.md) *（待创建）*
    - [Redis集群配置与使用](redis/redis-template-methods-ext-02.md) *（待创建）*

### 云服务相关文档

#### 1. 华为云 OBS SDK Content-Length 行为分析
- [华为云 OBS SDK Content-Length 行为分析](cloud/obs-sdk-putobject-content-length-analysis.md)
  - **创建日期**: 2026-05-15
  - **最后更新**: 2026-05-15
  - **分类**: 技术/云存储
  - **描述**: 深入分析华为云 OBS SDK 在 putObject 不指定 Content-Length 时的底层行为，追踪源码调用链路，消除 OOM 误解
  
  - **相关拓展文档**:
    - *暂无*

## 待创建文档


### Spring相关
- [ ] Spring Security配置指南
- [ ] Spring Cloud微服务配置
- [ ] Spring Boot监控和指标
- [ ] Spring事务管理详解

### Redis相关
- [ ] Redis持久化配置
- [ ] Redis哨兵模式配置
- [ ] Redis集群部署指南
- [ ] Redis性能监控

### 数据库相关
- [ ] MySQL配置优化
- [ ] 数据库分库分表方案
- [ ] 数据库连接池配置
- [ ] 数据库迁移脚本管理

### 其他技术
- [ ] Docker容器化部署
- [ ] Kubernetes编排配置
- [ ] Nginx反向代理配置
- [ ] ELK日志收集方案

## 文档规范

### 文件命名
- 主文档: `[技术]-[主题]-[描述].md` (如: `spring-static-resources.md`)
- 拓展文档: `[主文档名]-ext-[序号].md` (如: `spring-static-resources-ext-01.md`)

### 文档结构要求
1. **标题和元信息**: 分类、创建日期、最后更新
2. **相关拓展文档**: 列出所有相关拓展文档链接
3. **目录**: 清晰的文档结构
4. **正文内容**: 分章节详细说明
5. **示例代码**: 提供可运行的代码示例
6. **常见问题**: 列出常见问题和解决方案
7. **最佳实践**: 提供实施建议和最佳实践
8. **参考资料**: 相关文档和资源链接

## 贡献指南

### 添加新技术文档
1. 确定技术分类和主题
2. 按照命名规范创建文件
3. 编写完整的文档内容（包含示例和最佳实践）
4. 在本README.md中添加索引
5. 如果需要，创建拓展文档

### 更新技术文档
1. 更新文档内容，确保技术准确性
2. 更新"最后更新"日期
3. 如果技术有重大变更，添加版本说明
4. 更新相关拓展文档链接

### 技术文档审核
技术文档需要经过技术审核：
1. **技术准确性**: 确保技术内容正确无误
2. **代码示例**: 示例代码可运行且符合最佳实践
3. **实用性**: 文档对实际开发有帮助
4. **完整性**: 覆盖技术的主要方面

## 最佳实践

### 技术选型建议
1. **成熟度**: 选择成熟稳定的技术
2. **社区支持**: 考虑技术社区活跃度
3. **学习曲线**: 评估团队学习成本
4. **兼容性**: 确保与现有技术栈兼容

### 配置管理建议
1. **环境分离**: 开发、测试、生产环境配置分离
2. **版本控制**: 配置文件纳入版本控制
3. **安全敏感**: 敏感信息使用环境变量或配置中心
4. **文档同步**: 配置变更时同步更新文档

### 性能优化建议
1. **基准测试**: 重要配置进行性能测试
2. **监控告警**: 配置性能监控和告警
3. **定期优化**: 定期review和优化配置
4. **容量规划**: 根据业务增长规划容量

## 工具推荐

### 开发工具
- **IDE**: IntelliJ IDEA, VS Code
- **构建工具**: Maven, Gradle
- **版本控制**: Git, GitHub/GitLab
- **API测试**: Postman, Insomnia

### 监控工具
- **应用监控**: Prometheus, Grafana
- **日志管理**: ELK Stack, Loki
- **性能分析**: Arthas, JProfiler
- **链路追踪**: SkyWalking, Zipkin

### 部署工具
- **容器化**: Docker, Docker Compose
- **编排调度**: Kubernetes, Docker Swarm
- **配置管理**: Ansible, Terraform
- **CI/CD**: Jenkins, GitLab CI

## 学习资源

### 官方文档
- [Spring官方文档](https://spring.io/projects)
- [Redis官方文档](https://redis.io/documentation)
- [MySQL官方文档](https://dev.mysql.com/doc/)
- [Docker官方文档](https://docs.docker.com/)

### 技术社区
- [Stack Overflow](https://stackoverflow.com/)
- [GitHub](https://github.com/)
- [掘金](https://juejin.cn/)
- [InfoQ](https://www.infoq.cn/)

### 在线课程
- [慕课网](https://www.imooc.com/)
- [极客时间](https://time.geekbang.org/)
- [Coursera](https://www.coursera.org/)
- [Udemy](https://www.udemy.com/)

---

*技术文档是团队知识沉淀的重要载体，良好的技术文档能够显著提升开发效率和质量。*