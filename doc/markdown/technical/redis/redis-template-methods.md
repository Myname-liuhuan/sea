# RedisTemplate方法参考

**分类：** 技术/Redis
**创建日期：** 2026-02-24
**最后更新：** 2026-02-24

## 相关拓展文档
- [Redis性能调优指南](redis-template-methods-ext-01.md)
- [Redis集群配置与使用](redis-template-methods-ext-02.md)

---

## 1. 概述

RedisTemplate是Spring Data Redis提供的核心类，用于简化Redis操作。它提供了类型安全的操作方法，支持Redis的所有数据类型。

### 1.1 Redis数据类型与对应方法
| Redis 类型    | RedisTemplate 方法         | 操作接口                  |
| ----------- | ------------------------ | ----------------------- |
| String      | `opsForValue()`          | `ValueOperations`       |
| Hash        | `opsForHash()`           | `HashOperations`        |
| List        | `opsForList()`           | `ListOperations`        |
| Set         | `opsForSet()`            | `SetOperations`         |
| ZSet        | `opsForZSet()`           | `ZSetOperations`        |
| HyperLogLog | `opsForHyperLogLog()`    | `HyperLogLogOperations` |
| Geo         | `opsForGeo()`            | `GeoOperations`         |
| Bitmap      | `opsForValue().setBit()` | `ValueOperations`       |

## 2. String（字符串）操作

对应 `opsForValue()`

### 2.1 基本操作
```java
ValueOperations<String, String> ops = redisTemplate.opsForValue();

// 设置值
ops.set("key1", "value1");

// 获取值
String v = ops.get("key1");

// 设置带过期时间
ops.set("key2", "value2", 10, TimeUnit.MINUTES);

// 检查键是否存在
Boolean exists = ops.getOperations().hasKey("key1");

// 删除键
ops.getOperations().delete("key1");
```

### 2.2 原子操作
```java
// 自增/自减
ops.increment("count", 1);
ops.decrement("count");

// 设置并获取旧值
String oldValue = ops.getAndSet("key", "newValue");

// 如果不存在则设置
Boolean setIfAbsent = ops.setIfAbsent("lock:key", "locked", 30, TimeUnit.SECONDS);
```

### 2.3 批量操作
```java
Map<String, String> map = new HashMap<>();
map.put("key1", "value1");
map.put("key2", "value2");
map.put("key3", "value3");

ops.multiSet(map);

List<String> keys = Arrays.asList("key1", "key2", "key3");
List<String> values = ops.multiGet(keys);
```

## 3. Hash（哈希/散列）操作

对应 `opsForHash()`

### 3.1 基本操作
```java
HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

// 单个field操作
hashOps.put("user:1", "name", "Tom");
hashOps.put("user:1", "age", "20");

// 获取field
String name = hashOps.get("user:1", "name");

// 检查field是否存在
Boolean hasField = hashOps.hasKey("user:1", "name");

// 删除field
hashOps.delete("user:1", "age");
```

### 3.2 批量操作
```java
// 批量存放
Map<String, String> map = new HashMap<>();
map.put("name", "Jerry");
map.put("age", "22");
map.put("email", "jerry@example.com");
hashOps.putAll("user:2", map);

// 获取所有字段
Map<String, String> user = hashOps.entries("user:2");

// 获取多个field
List<String> fields = Arrays.asList("name", "age");
List<String> values = hashOps.multiGet("user:2", fields);
```

### 3.3 增量操作
```java
// 数值类型field自增
hashOps.increment("counter:1", "count", 1L);
hashOps.increment("counter:1", "count", -1L);

// 浮点数自增
hashOps.increment("stats:1", "score", 1.5);
```

## 4. List（列表）操作

对应 `opsForList()`

### 4.1 基本操作
```java
ListOperations<String, String> listOps = redisTemplate.opsForList();

// 左侧插入
listOps.leftPush("list1", "a");
listOps.leftPushAll("list1", "b", "c");

// 右侧插入
listOps.rightPush("list1", "x");
listOps.rightPushAll("list1", "y", "z");

// 获取列表范围
List<String> list = listOps.range("list1", 0, -1);

// 获取指定位置元素
String element = listOps.index("list1", 1);
```

### 4.2 弹出操作
```java
// 左侧弹出
String left = listOps.leftPop("list1");

// 右侧弹出
String right = listOps.rightPop("list1");

// 阻塞式弹出（超时时间）
String blockedPop = listOps.leftPop("list1", 10, TimeUnit.SECONDS);
```

### 4.3 列表管理
```java
// 获取列表长度
Long size = listOps.size("list1");

// 修剪列表（保留指定范围）
listOps.trim("list1", 0, 9);

// 设置指定位置的值
listOps.set("list1", 0, "newValue");

// 移除元素
listOps.remove("list1", 1, "valueToRemove");
```

## 5. Set（集合）操作

对应 `opsForSet()`

### 5.1 基本操作
```java
SetOperations<String, String> setOps = redisTemplate.opsForSet();

// 添加元素
setOps.add("set1", "a", "b", "c");

// 获取所有元素
Set<String> members = setOps.members("set1");

// 判断元素是否存在
Boolean exists = setOps.isMember("set1", "a");

// 移除元素
setOps.remove("set1", "a");
```

### 5.2 集合运算
```java
// 求交集
Set<String> inter = setOps.intersect("set1", "set2");
Set<String> interMulti = setOps.intersect("set1", Arrays.asList("set2", "set3"));

// 求并集
Set<String> union = setOps.union("set1", "set2");
Set<String> unionMulti = setOps.union("set1", Arrays.asList("set2", "set3"));

// 求差集
Set<String> diff = setOps.difference("set1", "set2");
Set<String> diffMulti = setOps.difference("set1", Arrays.asList("set2", "set3"));
```

### 5.3 随机操作
```java
// 随机获取一个元素
String random = setOps.randomMember("set1");

// 随机获取多个元素
Set<String> randoms = setOps.distinctRandomMembers("set1", 3);
List<String> randomsWithDuplicate = setOps.randomMembers("set1", 5);

// 随机弹出（移除并返回）
String popped = setOps.pop("set1");
```

## 6. ZSet（有序集合）操作

对应 `opsForZSet()`

### 6.1 基本操作
```java
ZSetOperations<String, String> zsetOps = redisTemplate.opsForZSet();

// 添加元素（带分数）
zsetOps.add("zset1", "Alice", 100);
zsetOps.add("zset1", "Bob", 80);
zsetOps.add("zset1", "Charlie", 90);

// 批量添加
Set<TypedTuple<String>> tuples = new HashSet<>();
tuples.add(new DefaultTypedTuple<>("David", 70.0));
tuples.add(new DefaultTypedTuple<>("Eve", 85.0));
zsetOps.add("zset1", tuples);
```

### 6.2 查询操作
```java
// 按分数升序/降序获取
Set<String> range = zsetOps.range("zset1", 0, -1);
Set<String> revRange = zsetOps.reverseRange("zset1", 0, -1);

// 获取带分数的结果
Set<TypedTuple<String>> rangeWithScores = zsetOps.rangeWithScores("zset1", 0, -1);

// 按分数区间获取
Set<String> scoreRange = zsetOps.rangeByScore("zset1", 50, 100);
Set<TypedTuple<String>> scoreRangeWithScores = zsetOps.rangeByScoreWithScores("zset1", 50, 100);
```

### 6.3 排名和分数
```java
// 获取分数
Double score = zsetOps.score("zset1", "Alice");

// 获取排名（从0开始）
Long rank = zsetOps.rank("zset1", "Alice");      // 升序排名
Long revRank = zsetOps.reverseRank("zset1", "Alice"); // 降序排名

// 获取元素数量
Long count = zsetOps.count("zset1", 80, 100);
Long size = zsetOps.size("zset1");
```

## 7. HyperLogLog 操作

对应 `opsForHyperLogLog()`

### 7.1 基本操作
```java
HyperLogLogOperations<String, String> hyperOps = redisTemplate.opsForHyperLogLog();

// 添加数据
hyperOps.add("hll1", "a", "b", "c");
hyperOps.add("hll1", "d", "e", "f");

// 统计基数（估算不重复元素数量）
Long size = hyperOps.size("hll1");

// 合并多个HyperLogLog
hyperOps.union("hllUnion", "hll1", "hll2");
hyperOps.union("hllUnion", Arrays.asList("hll1", "hll2", "hll3"));
```

## 8. Geo（地理位置）操作

对应 `opsForGeo()`

### 8.1 基本操作
```java
GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

// 添加地理位置
geoOps.add("city", new Point(116.40, 39.90), "Beijing");
geoOps.add("city", new Point(121.47, 31.23), "Shanghai");
geoOps.add("city", new Point(113.27, 23.13), "Guangzhou");

// 批量添加
Map<String, Point> locations = new HashMap<>();
locations.put("Shenzhen", new Point(114.07, 22.62));
locations.put("Chengdu", new Point(104.07, 30.67));
geoOps.add("city", locations);
```

### 8.2 查询操作
```java
// 获取经纬度
List<Point> points = geoOps.position("city", "Beijing", "Shanghai");

// 计算距离
Distance distance = geoOps.distance("city", "Beijing", "Shanghai");
Distance distanceWithUnit = geoOps.distance("city", "Beijing", "Shanghai", Metrics.KILOMETERS);

// 获取GeoHash
List<String> geohashes = geoOps.hash("city", "Beijing", "Shanghai");
```

### 8.3 附近位置查询
```java
// 查找指定半径内的位置
Circle circle = new Circle(new Point(116.40, 39.90), new Distance(100, Metrics.KILOMETERS));
GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius("city", circle);

// 查找指定成员附近的位置
GeoResults<RedisGeoCommands.GeoLocation<String>> nearby = geoOps.radius("city", "Beijing", new Distance(50, Metrics.KILOMETERS));

// 带参数查询
RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
    .includeDistance()
    .includeCoordinates()
    .sortAscending()
    .limit(10);
GeoResults<RedisGeoCommands.GeoLocation<String>> resultsWithArgs = geoOps.radius("city", circle, args);
```

## 9. Bitmap 操作

使用 `opsForValue().setBit()` 和 `getBit()`

### 9.1 基本操作
```java
// 设置位
redisTemplate.opsForValue().setBit("bitmap1", 5, true);
redisTemplate.opsForValue().setBit("bitmap1", 10, true);

// 获取位
Boolean bit5 = redisTemplate.opsForValue().getBit("bitmap1", 5);
Boolean bit7 = redisTemplate.opsForValue().getBit("bitmap1", 7); // 返回false

// 批量设置
for (int i = 0; i < 100; i++) {
    if (i % 2 == 0) {
        redisTemplate.opsForValue().setBit("bitmap2", i, true);
    }
}
```

### 9.2 位运算
```java
// 使用execute方法执行位运算命令
Long bitCount = redisTemplate.execute((RedisCallback<Long>) connection -> 
    connection.bitCount("bitmap1".getBytes())
);

// 位操作（AND, OR, XOR, NOT）
redisTemplate.execute((RedisCallback<Long>) connection -> 
    connection.bitOp(
        RedisStringCommands.BitOperation.AND,
        "bitmap-and".getBytes(),
        "bitmap1".getBytes(),
        "bitmap2".getBytes()
    )
);
```

## 10. 通用操作

### 10.1 键操作
```java
// 获取所有键（谨慎使用，生产环境避免）
Set<String> keys = redisTemplate.keys("*");

// 扫描键（推荐用于生产环境）
Cursor<byte[]> cursor = redisTemplate.execute((RedisCallback<Cursor<byte[]>>) connection -> 
    connection.scan(ScanOptions.scanOptions().match("*").count(100).build())
);

// 键过期时间
Long expireTime = redisTemplate.getExpire("key1");
Boolean expired = redisTemplate.expire("key1", 60, TimeUnit.SECONDS);
Boolean persist = redisTemplate.persist("key1");
```

### 10.2 事务操作
```java
// 执行事务
List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
    @Override
    public List<Object> execute(RedisOperations operations) throws DataAccessException {
        operations.multi();
        operations.opsForValue().set("key1", "value1");
        operations.opsForValue().set("key2", "value2");
        return operations.exec();
    }
});
```

### 10.3 管道操作
```java
// 使用管道批量执行命令
List<Object> pipelineResults = redisTemplate.executePipelined(new SessionCallback<List<Object>>() {
    @Override
    public List<Object> execute(RedisOperations operations) throws DataAccessException {
        for (int i = 0; i < 100; i++) {
            operations.opsForValue().set("key" + i, "value" + i);
        }
        return null;
    }
});
```

---

*本文档提供了RedisTemplate的完整方法参考，涵盖了Redis所有数据类型的操作。在实际使用中，请根据具体业务场景选择合适的数据类型和操作方法。*