package com.idrsys.toyprojectbackend.excel.utils;

import com.idrsys.toyprojectbackend.enums.OrderPay;
import com.idrsys.toyprojectbackend.enums.OrderStatus;

import java.util.HashMap;
import java.util.Map;

public class DataFormatterUtil {
    private static final Map<String, DataFormatter> formatters = new HashMap<>();

    static {
        formatters.put("payMn", code -> {
            OrderPay pay = OrderPay.fromCode(code);
            return pay != null ? pay.getDef() : code;
        });
    }

    static {
        formatters.put("ord_status_cd", code -> {
            OrderStatus status = OrderStatus.fromCode(code);
            return status != null ? status.getDef() : code;
        });
    }

    public static String format(String fieldName, String code) {
        DataFormatter formatter = formatters.get(fieldName);
        if (formatter != null) {
            System.out.println("Formatting field: " + fieldName + " with code: " + code);
            return formatter.format(code);
        } else {
            System.out.println("No formatter found for field: " + fieldName);
            return code;
        }
    }
}
