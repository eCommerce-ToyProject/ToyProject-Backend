package com.idrsys.toyprojectbackend.enums;

public enum OrderStatus {
    STATUS_OUT_OF_STACK("STATUS_OUT_OF_STACK", "품절"),
    STATUS_PAYMENT_COMPLETED("STATUS_PAYMENT_COMPLETED", "결제완료"),
    STATUS_PAYMENT_FAILED("STATUS_PAYMENT_FAILED","결제실패"),
    STATUS_SHIPPED("STATUS_SHIPPED", "배송완료"),
    STATUS_SHIPPING("STATUS_SHIPPING","배송중");

    private final String code;
    private final String def;

    private OrderStatus(String code, String def) {
        this.code = code;
        this.def = def;
    }
}




