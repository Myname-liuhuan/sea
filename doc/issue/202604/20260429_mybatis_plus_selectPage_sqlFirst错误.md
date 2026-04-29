# MyBatis-Plus selectPage 方法 sqlFirst 错误

## 问题时间
2026-04-29

## 问题描述
- 场景: 调用 `/sysUser/page` 接口进行分页查询用户列表
- 触发条件: 使用 `baseMapper.selectPage(page, lambdaQuery().eq(...))` 链式调用时
- 报错信息:
```
Error evaluating expression 'ew != null and ew.sqlFirst != null'. 
Cause: org.apache.ibatis.builder.BuilderException: Error evaluating expression 'ew != null and ew.sqlFirst != null'. 
Cause: org.mybatis.ibatis.ognl.OgnlException: sqlFirst [com.baomidou.mybatisplus.core.exceptions.MybatisPlusException: can not use this method for "getSqlFirst"]
```

## 问题示例代码
```java
// 错误写法 - 会导致 sqlFirst 错误
this.baseMapper.selectPage(page, lambdaQuery().eq(SysUserPO::getDelFlag, 0)
        .likeRight(StringUtils.isNotBlank(sysUserQueryParam.getUsername()), SysUserPO::getUsername, sysUserQueryParam.getUsername())
        // ... 其他条件
);
```

## 修改方案 / 解决方法
### 最终解决方案
```java
// 正确写法 - 使用 LambdaQueryWrapper 对象
LambdaQueryWrapper<SysUserPO> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(SysUserPO::getDelFlag, DeletedEnum.NORMAL.getCode())
        .likeRight(StringUtils.isNotBlank(sysUserQueryParam.getUsername()), SysUserPO::getUsername, sysUserQueryParam.getUsername())
        // ... 其他条件
        .gt(Objects.nonNull(sysUserQueryParam.getCreateTimeStart()), SysUserPO::getCreateTime, sysUserQueryParam.getCreateTimeStart())
        .le(Objects.nonNull(sysUserQueryParam.getCreateTimeEnd()), SysUserPO::getCreateTime, sysUserQueryParam.getCreateTimeEnd());
this.baseMapper.selectPage(page, wrapper);
```

### 其他尝试过的方案
1. 检查依赖版本 - mybatis-plus 3.5.10.1 + mybatis-spring 3.0.4 组合正确
2. 检查 Mapper XML - 无自定义分页 SQL，不涉及 `${ew.sqlFirst}`
3. 检查 PaginationInnerInterceptor 配置 - 未配置自定义分页插件

## 问题原理 / 分析
MyBatis-Plus 3.5.10.1 中，当使用 `lambdaQuery()` 链式调用直接作为参数传入 `selectPage` 方法时，内部的 `PaginationInnerInterceptor` 在处理分页时会尝试访问 `sqlFirst` 属性，但这种写法下 `QueryWrapper` 的状态不完整，导致 OGNL 表达式求值失败。

根本原因可能是 `lambdaQuery()` 返回的 `LambdaQueryWrapper` 在链式调用过程中，某些内部状态（如 `sqlFirst`、`sqlSegment`）没有被正确初始化，而 `PaginationInnerInterceptor` 的 MySQL 分页方言在生成 COUNT SQL 时会检查这些状态。

## lambdaQuery() 推荐使用场景

`lambdaQuery()` 适用于**简单链式调用后直接执行**的场景：

### 推荐使用
```java
// 场景1: 简单条件 + 单次查询
List<User> list = userMapper.selectList(
    lambdaQuery().eq(User::getStatus, 1).likeRight(User::getName, "张").list()
);

// 场景2: 条件组装后直接调用 list() / one() / count()
long count = lambdaQuery().eq(User::getDelFlag, 0).count();

// 场景3: 链式调用作为参数传入（仅限非分页方法）
userMapper.selectList(wrapper.eq(User::getId, id));
```

### 不推荐使用
```java
// 场景1: 直接作为 selectPage 的第二个参数（会导致 sqlFirst 错误）
this.baseMapper.selectPage(page, lambdaQuery().eq(...));  // ❌ 不推荐

// 场景2: 复杂条件分页查询时内联链式调用
this.baseMapper.selectPage(page, lambdaQuery()
    .eq(Condition.isTrue(condition), User::getId, id)
    .likeRight(StringUtils.isNotBlank(name), User::getName, name)
    // ... 多个条件
);  // ❌ 不推荐

// 场景3: 需要复用 wrapper 的场景
LambdaQueryWrapper<User> wrapper = lambdaQuery().eq(...);  // ❌ 写法不正确
```

### 正确替代方案
```java
// 分页查询正确写法
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getDelFlag, 0)
       .likeRight(StringUtils.isNotBlank(name), User::getName, name);
this.baseMapper.selectPage(page, wrapper);

// 需要复用 wrapper 的正确写法
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getDelFlag, 0);
if (StringUtils.isNotBlank(name)) {
    wrapper.likeRight(User::getName, name);
}
List<User> list = this.baseMapper.selectList(wrapper);  // 复用同一个 wrapper
IPage<User> page = new Page<>(1, 10);
this.baseMapper.selectPage(page, wrapper);  // 复用同一个 wrapper
```

## 经验或注意事项
1. MyBatis-Plus 分页查询时，**必须**先创建 `LambdaQueryWrapper` 对象，再进行条件链式组装，最后传入 `selectPage` 方法
2. 不要直接使用 `lambdaQuery().eq().likeRight()` 这种内联链式写法作为 `selectPage` 的参数
3. `lambdaQuery()` 适合简单场景（单次查询、条件简单、不需要复用）；复杂查询或分页查询使用 `new LambdaQueryWrapper<>()` 更稳妥
4. 此问题与 mybatis/mybatis-spring 版本兼容性无关，是 MyBatis-Plus 内部 `PaginationInnerInterceptor` 处理链式 QueryWrapper 时的特定问题

## 相关文件
- sea-system/sea-system-service/src/main/java/com/example/sea/system/service/impl/SysUserServiceImpl.java:99-109
