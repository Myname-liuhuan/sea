package com.example.sea.code.common.utils;

import org.apache.commons.lang3.StringUtils;

import com.example.sea.code.common.constant.EntityTypeConstant;


/**
 * 行操作工具类
 * @author liuhuan
 * @date 2025-05-08
 */
public class ColumnUtil {

    private static final String INT_LOWCASE = "int";
    private static final String LONG_LOWCASE = "long";
    private static final String FLOAT_LOWCASE = "float";
    private static final String DOUBLE_LOWCASE = "double";
    private static final String CHAR_LOWCASE = "char";
    private static final String VARCHAR_LOWCASE = "varchar";
    private static final String TEXT_LOWCASE = "text";
    private static final String DATE_LOWCASE = "date";
    private static final String TIMESTAMP_LOWCASE = "timestamp";
    private static final String BIGINT_LOWCASE = "bigint";

    

    
    /**
     * 获取数据表列类型对应的建议的实体类类型
     * @param columnType 列类型
     * @return 实体类类型
     */
    public static String getEntityTypeByColumnType(String columnType) {
        if(StringUtils.isBlank(columnType)) {
            return EntityTypeConstant.STRING;
        }

        columnType = columnType.toLowerCase();
        if (columnType.startsWith(INT_LOWCASE)) {
            return EntityTypeConstant.INTEGER;
        }else if (columnType.startsWith(LONG_LOWCASE)
                || columnType.startsWith(BIGINT_LOWCASE)) {
            return EntityTypeConstant.LONG;
        }else if (columnType.startsWith(FLOAT_LOWCASE)) {
            return EntityTypeConstant.FLOAT;
        }else if (columnType.startsWith(DOUBLE_LOWCASE)) {
            return EntityTypeConstant.DOUBLE;
        }else if (columnType.startsWith(CHAR_LOWCASE) ||
                    columnType.startsWith(VARCHAR_LOWCASE) || 
                        columnType.startsWith(TEXT_LOWCASE)) {
            return EntityTypeConstant.STRING;
        }else if (columnType.startsWith(DATE_LOWCASE) || 
                    columnType.startsWith(TIMESTAMP_LOWCASE)) {
            return EntityTypeConstant.LOCAL_DATE_TIME;
        }
        return EntityTypeConstant.STRING;
    }
}
