package com.tomato.tomato_mall.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON工具类
 * <p>
 * 提供JSON序列化和反序列化的工具方法，封装了Jackson库的常用操作。
 * </p>
 *
 * @author Team Tomato
 * @version 1.0
 */
public class JsonUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 将对象转换为JSON字符串
     *
     * @param object 要转换的对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private JsonUtils() {
        throw new AssertionError("No JsonUtils instances for you!");
    }
}