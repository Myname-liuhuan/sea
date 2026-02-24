# Redis 数据类型与 RedisTemplate 对应方法

## 总结
| Redis 类型    | RedisTemplate 方法         |
| ----------- | ------------------------ |
| String      | `opsForValue()`          |
| Hash        | `opsForHash()`           |
| List        | `opsForList()`           |
| Set         | `opsForSet()`            |
| ZSet        | `opsForZSet()`           |
| HyperLogLog | `opsForHyperLogLog()`    |
| Geo         | `opsForGeo()`            |
| Bitmap      | `opsForValue().setBit()` |


## 1. String（字符串）
对应 `opsForValue()`

```java
ValueOperations<String, String> ops = redisTemplate.opsForValue();

// 设置值
ops.set("key1", "value1");

// 获取值
String v = ops.get("key1");

// 设置带过期时间
ops.set("key2", "value2", 10, TimeUnit.MINUTES);

// 自增/自减
ops.increment("count", 1);
ops.decrement("count");
```

## 2. Hash（哈希/散列）
对应 `opsForHash()`
```java
HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

// 单个 field
hashOps.put("user:1", "name", "Tom");
hashOps.put("user:1", "age", "20");

// 获取 field
String name = hashOps.get("user:1", "name");

// 批量存放
Map<String, String> map = new HashMap<>();
map.put("name", "Jerry");
map.put("age", "22");
hashOps.putAll("user:2", map);

// 获取所有字段
Map<String, String> user = hashOps.entries("user:2");

// 删除 field
hashOps.delete("user:1", "age");
```

## 3. List（列表）
对应 `opsForList()`
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

// 弹出元素
String left = listOps.leftPop("list1");
String right = listOps.rightPop("list1");

// 获取列表长度
Long size = listOps.size("list1");

```

## 4. Set（集合，无序）
对应 `opsForSet()`
```java
SetOperations<String, String> setOps = redisTemplate.opsForSet();

// 添加元素
setOps.add("set1", "a", "b", "c");

// 获取所有元素
Set<String> members = setOps.members("set1");

// 判断是否存在
Boolean exists = setOps.isMember("set1", "a");

// 求交集、并集、差集
Set<String> inter = setOps.intersect("set1", "set2");
Set<String> union = setOps.union("set1", "set2");
Set<String> diff = setOps.difference("set1", "set2");

// 随机弹出
String val = setOps.pop("set1");

```

## 5. ZSet（有序集合）
对应 `opsForZSet()`
```java
ZSetOperations<String, String> zsetOps = redisTemplate.opsForZSet();

// 添加元素（带分数）
zsetOps.add("zset1", "Alice", 100);
zsetOps.add("zset1", "Bob", 80);

// 按分数升序/降序获取
Set<String> range = zsetOps.range("zset1", 0, -1);
Set<String> revRange = zsetOps.reverseRange("zset1", 0, -1);

// 获取分数和排名
Double score = zsetOps.score("zset1", "Alice");
Long rank = zsetOps.rank("zset1", "Alice");
Long revRank = zsetOps.reverseRank("zset1", "Alice");

// 删除元素
zsetOps.remove("zset1", "Bob");

// 按分数区间获取
Set<String> scoreRange = zsetOps.rangeByScore("zset1", 50, 100);
```

## 6. HyperLogLog
对应 `opsForHyperLogLog()`
```java
HyperLogLogOperations<String, String> hyperOps = redisTemplate.opsForHyperLogLog();

// 添加数据
hyperOps.add("hll1", "a", "b", "c");

// 统计基数
Long size = hyperOps.size("hll1");

// 合并多个 HyperLogLog
hyperOps.union("hllUnion", "hll1", "hll2");
```

## 7. Geo（地理位置）
对应 `opsForGeo()`
```java
GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

// 添加经纬度
geoOps.add("city", new Point(116.40, 39.90), "Beijing");
geoOps.add("city", new Point(121.47, 31.23), "Shanghai");

// 获取经纬度
List<Point> points = geoOps.position("city", "Beijing");

// 计算距离
Distance distance = geoOps.distance("city", "Beijing", "Shanghai");

// 查找附近
Circle circle = new Circle(new Point(116.40, 39.90), new Distance(100, Metrics.KILOMETERS));
GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius("city", circle);
```

## 8. Bitmap
使用 `opsForValue().setBit()` 和 `getBit()`
```java
// 设置第5位为1
redisTemplate.opsForValue().setBit("bitmap1", 5, true);

// 获取第5位
Boolean bit = redisTemplate.opsForValue().getBit("bitmap1", 5);
```