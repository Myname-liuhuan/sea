package com.example.sea.media.config.handler;


import com.example.sea.common.encryption.DES;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mybatis自动加解密句柄
 */
public class EncryptTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 对参数进行加密
            String encryptedValue = DES.encrypt(parameter);
            ps.setString(i, encryptedValue);
        } catch (Exception e) {
            throw new SQLException("Error encrypting field", e);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return decryptValue(value);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return decryptValue(value);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return decryptValue(value);
    }

    private String decryptValue(String value) throws SQLException {
        try {
            return value != null ? DES.decrypt(value) : null;
        } catch (Exception e) {
            throw new SQLException("Error decrypting field", e);
        }
    }
}
