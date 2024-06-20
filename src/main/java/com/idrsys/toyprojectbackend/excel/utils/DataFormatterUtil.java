package com.idrsys.toyprojectbackend.excel.utils;

import com.idrsys.toyprojectbackend.enums.OrderPay;
import com.idrsys.toyprojectbackend.enums.OrderStatus;

public class DataFormatterUtil {
    public static String formatOrderPayCode(String code) {
        return OrderPay.getDefByCode(code);
    }

    public static String formatOrderStatusCode(String code) {
        return OrderStatus.getDefByCode(code);
    }
}
