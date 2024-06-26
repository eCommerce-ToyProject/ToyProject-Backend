package com.idrsys.toyprojectbackend.excel.utils;

import com.idrsys.toyprojectbackend.enums.OrderPay;
import com.idrsys.toyprojectbackend.enums.OrderStatus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DataFormatterUtil {
    private static final Map<String, Class<?>> fieldEnumMapping = new HashMap<>();

    static {
        fieldEnumMapping.put("payMn", OrderPay.class);
        fieldEnumMapping.put("ord_status_cd", OrderStatus.class);
    }

    public static String format(String fieldName, String code) {
        if (code == null) return null;
        Class<?> enumClass = fieldEnumMapping.get(fieldName);

        if (enumClass != null) {
            try {
                Method fromCodeMethod = enumClass.getMethod("fromCode", String.class);
                Object enumInstance = fromCodeMethod.invoke(null, code);
                if (enumInstance != null) {
                    Method getDefMethod = enumClass.getMethod("getDef");
                    return (String) getDefMethod.invoke(enumInstance);
                }
            } catch (Exception e) {
                // Log the error or handle it as appropriate
                e.printStackTrace();
            }
        }

        return code;
    }
}
