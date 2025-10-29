**SpringBoot项目静态资源说明**

在 Spring Boot 中，如果请求路径无法匹配任何：
- ✅ @Controller / @RestController
- ✅ @RequestMapping
- ✅或者没有被任何 HandlerMapping 匹配上
Spring会最后尝试静态资源映射
这时如果请求一个不存在的地址就会报错NoResourceFoundException

**修改配置不允许去访问静态资源**
```yaml
spring:
  web:
    resources:
      add-mappings: false
```
这时访问不存在的地址，报错就是_了