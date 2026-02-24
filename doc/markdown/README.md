# 项目文档索引

欢迎访问项目文档中心！本文档系统采用新的组织结构，旨在提供更好的文档查找和管理体验。

## 文档分类

### 📁 架构设计 (architecture/)
- [安全架构](architecture/security/README.md) - RBAC权限设计、安全策略等
- [系统架构](architecture/system/README.md) - 系统整体架构设计

### 💻 技术文档 (technical/)
- [Spring相关](technical/spring/README.md) - Spring Boot配置、SpringDoc文档等
- [Redis相关](technical/redis/README.md) - RedisTemplate使用方法
- [数据库相关](technical/database/README.md) - 数据库设计和使用

### 📚 知识库 (knowledge/)
- [核心概念](knowledge/concepts/README.md) - 项目核心概念解析
- [设计模式](knowledge/patterns/README.md) - 常用设计模式和应用

### 📖 使用指南 (guides/)
- [安装配置](guides/setup/README.md) - 环境搭建和项目配置
- [开发指南](guides/development/README.md) - 开发规范和最佳实践

### 📄 API文档 (api/)
- API规范和参考文档

### 🖼️ 资源文件 (resources/)
- 图片、图表等资源文件

## 文档命名规范

### 主文档命名
`[主题]-[简要描述].md`

示例：
- `rbac-design.md` - RBAC权限设计
- `redis-template-methods.md` - RedisTemplate方法参考

### 拓展文档命名
`[主文档名]-ext-[序号].md`

示例：
- `rbac-design-ext-01.md` - RBAC与其他权限模型的关系
- `redis-template-methods-ext-01.md` - Redis性能调优指南

## 文档查找技巧

### 1. 按分类查找
- 根据文档主题选择对应的分类目录
- 查看各目录下的README.md了解详细内容

### 2. 按前缀查找
- 相关文档使用相同的前缀命名
- 例如：所有RBAC相关文档都以`rbac-`开头

### 3. 使用索引文件
- 每个目录都有README.md索引文件
- 索引文件列出了该目录下的所有文档及其关系

## 最近更新

### 2026-02-24
- ✅ 完成文档结构重构
- ✅ 转换现有文档到新结构
- ✅ 创建索引文件系统
- ✅ 实施新的命名规范

### 已转换文档
1. **RABC+角色部门岗位关系图.md** → `architecture/security/rbac-design.md`
   - 拓展文档：`rbac-design-ext-01.md` (RBAC与其他权限模型的关系)

2. **Redis与RedisTemplate对应方法.md** → `technical/redis/redis-template-methods.md`
   - 拓展文档：`redis-template-methods-ext-01.md` (Redis性能调优指南)

3. **SPRING_STATIC说明.md** → `technical/spring/spring-static-resources.md`
   - 拓展文档：`spring-static-resources-ext-01.md` (自定义静态资源配置)

4. **SPRINGDOC统一网关文档配置说明.md** → `technical/spring/springdoc-gateway-config.md`
   - 拓展文档：`springdoc-gateway-config-ext-01.md` (常见问题排查)

## 如何使用拓展文档

每个主文档都可能有一个或多个拓展文档，用于深入探讨特定主题：

1. **查看相关拓展**：在主文档的"相关拓展文档"部分查看所有拓展
2. **按需阅读**：根据兴趣和需求选择阅读相关拓展
3. **贡献拓展**：欢迎为现有文档添加新的拓展文档

## 贡献指南

### 添加新文档
1. 确定文档分类（架构/技术/知识/指南）
2. 按照命名规范创建文件
3. 在对应目录的README.md中添加索引
4. 如果需要，创建拓展文档

### 更新现有文档
1. 更新文档内容
2. 更新"最后更新"日期
3. 如果添加了新的拓展文档，在主文档中添加链接

### 文档规范
- 使用标准Markdown语法
- 包含分类、创建日期、最后更新信息
- 为拓展文档添加"衍生自"说明
- 保持文档结构清晰

## 问题反馈

如果在使用文档过程中遇到问题，或对文档结构有改进建议，请：

1. 检查相关目录的README.md
2. 使用文档查找技巧
3. 如果问题仍未解决，请联系文档维护团队

---

*本文档系统将持续改进，以提供更好的文档体验。感谢您的使用！*