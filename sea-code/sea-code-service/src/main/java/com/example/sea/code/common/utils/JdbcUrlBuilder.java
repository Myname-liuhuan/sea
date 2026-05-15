package com.example.sea.code.common.utils;

public final class JdbcUrlBuilder {

    private JdbcUrlBuilder() {}

    /**
     * 构建 JDBC URL（不指定数据库，用于连接服务器列出数据库）
     */
    public static String build(String dbType, String host, int port) {
        return switch (dbType.toLowerCase()) {
            case "mysql" -> String.format("jdbc:mysql://%s:%d/?useSSL=false&serverTimezone=UTC", host, port);
            case "postgresql" -> String.format("jdbc:postgresql://%s:%d/postgres", host, port);
            case "oracle" -> String.format("jdbc:oracle:thin:@%s:%d:orcl", host, port);
            default -> throw new IllegalArgumentException("不支持的数据库类型: " + dbType);
        };
    }

    /**
     * 构建 JDBC URL（指定数据库，用于连接具体库查询表和字段）
     */
    public static String build(String dbType, String host, int port, String dbName) {
        return switch (dbType.toLowerCase()) {
            case "mysql" -> String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC", host, port, dbName);
            case "postgresql" -> String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
            case "oracle" -> String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, dbName);
            default -> throw new IllegalArgumentException("不支持的数据库类型: " + dbType);
        };
    }
}
