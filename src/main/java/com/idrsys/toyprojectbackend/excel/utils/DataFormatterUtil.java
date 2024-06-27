package com.idrsys.toyprojectbackend.excel.utils;

import com.idrsys.toyprojectbackend.enums.OrderPay;
import com.idrsys.toyprojectbackend.enums.OrderStatus;
import com.idrsys.toyprojectbackend.excel.EnumMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DataFormatterUtil {
    private static final Map<String, Class<?>> fieldEnumMapping = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(DataFormatterUtil.class);

    static {
        // You can manually add mappings here if needed
        // fieldEnumMapping.put("payMn", OrderPay.class);
        // fieldEnumMapping.put("ord_status_cd", OrderStatus.class);
    }

    public static void registerFieldEnumMapping(String fieldName, Class<?> enumClass) {
        fieldEnumMapping.put(fieldName, enumClass);
    }

    public static void scanAndRegisterMappings(Class<?> dtoClass) {
        Field[] fields = dtoClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EnumMapping.class)) {
                EnumMapping enumMapping = field.getAnnotation(EnumMapping.class);
                String fieldName = field.getName();
                Class<?> enumClass = enumMapping.enumClass();
                registerFieldEnumMapping(fieldName, enumClass);
            }
        }
    }

    public static String format(String fieldName, String code) {
        if (code == null) return null;
        Class<?> enumClass = fieldEnumMapping.get(fieldName);

        if (enumClass != null) {
            try {
                Method fromCodeMethod = enumClass.getMethod("fromCode", String.class);
                Object enumInstance = fromCodeMethod.invoke(null, code);
                if (enumInstance != null) {
                    Method getCodeNmMethod = enumClass.getMethod("getCodeNm");
                    return (String) getCodeNmMethod.invoke(enumInstance);
                }
            } catch (Exception e) {
                // Log the error or handle it as appropriate
                e.printStackTrace();
            }
        }

        return code;
    }
}
