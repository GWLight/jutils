/*
 * Copyright 2015-2016 nongfa365.com All rights reserved.
 * Support: http://www.shhn.com
 * License: http://www.shhn.com/license
 */
package com.shhn.pmm.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.shhn.pmm.Page;
import com.shhn.pmm.service.BaseService;
import net.sf.json.JSONObject;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utils - JSON
 *
 * @author Milanosoft Team
 * @version 4.0
 */
public final class JsonUtils {

    /** ObjectMapper */
    private static ObjectMapper OBJECT_MAPPER = null;

    /**
     * 不可实例化
     */
    private JsonUtils() {
    }

    public static ObjectMapper getInstance() {
        if (OBJECT_MAPPER == null) {
            OBJECT_MAPPER = new ObjectMapper();
            //过滤Json字符串冗余字段
            OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //数值也加引号，处理js截取id问题
            OBJECT_MAPPER.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
            //设置日期格式
            OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        }
        return OBJECT_MAPPER;
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param value 对象
     *
     * @return JSON字符串
     */
    public static String toJson(Object value) {
        Assert.notNull(value);

        try {
            return getInstance().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将page对象转换为JSON字符串
     *
     * @param page 对象
     *
     * @return JSON字符串
     */
    public static String pageToJson(Page page) {
        Assert.notNull(page);
        try {

            String json = "[";
            //将实体list转换为json
           for(int i=0; i<page.getContent().size(); i++){
               Object object =  page.getContent().get(i);
               String str = getInstance().writeValueAsString(object);
               str = str.substring(0,str.length()-1);
               Field[] fields=object.getClass().getDeclaredFields();
               //拼接代理对象name,code值
               for(int j=0;j<fields.length;j++){
                   if( (!fields[j].getType().isPrimitive()) && (!fields[j].getType().toString().equals("class java.lang.String"))
                            && (!fields[j].getType().toString().equals("interface java.util.Set"))){
                       str += ",\""+fields[j].getName()+"Name\":\""+getFieldValueByName ("name", getFieldValueByName(fields[j].getName(), object)).toString()+"\"";
                       str += ",\""+fields[j].getName()+"Code\":\""+getFieldValueByName ("code", getFieldValueByName(fields[j].getName(), object)).toString()+"\"";
                   }
               }
               str += "}";
               if(i==0){
                   json += str;
               }else{
                   json += ","+str;
               }
           }
            return json+"]";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将page对象转换为JSON字符串
     *
     * @param list 对象
     *
     * @return JSON字符串
     */
    public static String listToJson(List list) {
        Assert.notNull(list);
        try {

            String json = "[";
            //将实体list转换为json
            for(int i=0; i<list.size(); i++){
                Object object =  list.get(i);
                String str = getInstance().writeValueAsString(object);
                str = str.substring(0,str.length()-1);
                Field[] fields=object.getClass().getDeclaredFields();
                //拼接代理对象name,code值
                for(int j=0;j<fields.length;j++){
                    if( (!fields[j].getType().isPrimitive()) && (!fields[j].getType().toString().equals("class java.lang.String"))
                            && (!fields[j].getType().toString().equals("interface java.util.Set"))){
                        str += ",\""+fields[j].getName()+"Name\":\""+getFieldValueByName ("name", getFieldValueByName(fields[j].getName(), object)).toString()+"\"";
                        str += ",\""+fields[j].getName()+"Code\":\""+getFieldValueByName ("code", getFieldValueByName(fields[j].getName(), object)).toString()+"\"";
                    }
                }
                str += "}";
                if(i==0){
                    json += str;
                }else{
                    json += ","+str;
                }
            }
            return json+"]";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 根据属性名称获取属性值
     *
     * @param fieldName 字段名
     * @param o 字段名
     *
     * @return 属性值
     */
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            if(o == null){
                return "";
            }
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json      JSON字符串
     * @param valueType 类型
     *
     * @return 对象
     */
    public static <T> T toObject(String json, Class<T> valueType) {
        Assert.hasText(json);
        Assert.notNull(valueType);

        try {
            return getInstance().readValue(json, valueType);
        } catch (JsonParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json          JSON字符串
     * @param typeReference 类型
     *
     * @return 对象
     */
    public static <T> T toObject(String json, TypeReference<?> typeReference) {
        Assert.hasText(json);
        Assert.notNull(typeReference);

        try {
            return getInstance().readValue(json, typeReference);
        } catch (JsonParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json     JSON字符串
     * @param javaType 类型
     *
     * @return 对象
     */
    public static <T> T toObject(String json, JavaType javaType) {
        Assert.hasText(json);
        Assert.notNull(javaType);

        try {
            return getInstance().readValue(json, javaType);
        } catch (JsonParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串转换为树
     *
     * @param json JSON字符串
     *
     * @return 树
     */
    public static JsonNode toTree(String json) {
        Assert.hasText(json);

        try {
            return getInstance().readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将对象转换为JSON流
     *
     * @param writer Writer
     * @param value  对象
     */
    public static void writeValue(Writer writer, Object value) {
        Assert.notNull(writer);
        Assert.notNull(value);

        try {
            getInstance().writeValue(writer, value);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 构造类型
     *
     * @param type 类型
     *
     * @return 类型
     */
    public static JavaType constructType(Type type) {
        Assert.notNull(type);

        return TypeFactory.defaultInstance().constructType(type);
    }

    /**
     * 构造类型
     *
     * @param typeReference 类型
     *
     * @return 类型
     */
    public static JavaType constructType(TypeReference<?> typeReference) {
        Assert.notNull(typeReference);

        return TypeFactory.defaultInstance().constructType(typeReference);
    }


    /**
    * 获取泛型的Collection Type
    * @param collectionClass 泛型的Collection
    * @param elementClasses 元素类
    * @return JavaType Java类型
    * @since 1.0
    */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
         return getInstance().getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 将JSON字符串转换为list对象集合
     *
     * @param json          JSON字符串
     * @param valueType 类型
     *
     * @return 对象
     */
    public static <T> T toListBean(String json,Class<T> valueType) {
        JavaType javaType = getCollectionType(ArrayList.class, valueType);
        try {
            return getInstance().readValue(json, javaType);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * 将JSON字符串转换为set对象集合
     *
     * @param json          JSON字符串
     * @param valueType 类型
     *
     * @return 对象
     */
    public static <T> T toSetBean(String json,Class<T> valueType) {
        JavaType javaType = getCollectionType(Set.class, valueType);
        try {
            return getInstance().readValue(json, javaType);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将Map转换为对象
     *
     * @param map          JSON字符串
     * @param valueType 类型
     *
     * @return 对象
     */
    public static <T> T mapToBean(Map map,Class<T> valueType) {
        String json = toJson(map);
        return toObject(json,valueType);
    }

    /**
     * 将对象转换为map
     *
     * @param value 实体
     *
     * @return 对象
     */
    public static <T> Map beanToMap(Object value) {
        String json = toJson(value);
        return toObject(json,Map.class);
    }

    /**
     * json string 转换为 map 对象
     * @param jsonObj
     * @return
     */
    public static Map<Object, Object> jsonToMap(Object jsonObj) {
        JSONObject jsonObject = JSONObject.fromObject(jsonObj);
        Map<Object, Object> map = (Map)jsonObject;
        return map;
    }

    /**
     * 判断字符串是否为数值
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        String reg = "^[-\\+]?([0-9]+\\.?)?[0-9]+$";
        return str.matches(reg);
    }

    /**
     * 判断map的Key以及值是否正常
     * @return
     */
    public static boolean istMapKey(Map map,String key){
        try {
            if(!map.containsKey(key))return false;
            if(map.get(key) == null)return false;
        }catch (Exception e){
            System.out.println(e.toString());
            return false;
        }
        return true;
    }
}