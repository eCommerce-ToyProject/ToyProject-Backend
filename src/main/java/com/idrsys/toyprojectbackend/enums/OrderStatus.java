package com.idrsys.toyprojectbackend.enums;

public enum OrderStatus {
    STATUS_OUT_OF_STACK("STATUS_OUT_OF_STACK", "품절"),
    STATUS_PAYMENT_COMPLETED("STATUS_PAYMENT_COMPLETED", "결제완료"),
    STATUS_PAYMENT_FAILED("STATUS_PAYMENT_FAILED","결제실패"),
    STATUS_SHIPPED("STATUS_SHIPPED", "배송완료"),
    STATUS_SHIPPING("STATUS_SHIPPING","배송중");

    private final String ordCd;
    private final String ordDef;

    private OrderStatus(String code, String def) {
        this.ordCd = code;
        this.ordDef = def;
    }

    public String getCode() {
        return ordCd;
    }

    public String getDef() {
        return ordDef;
    }

    public static String getDefByCode(String code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status.getDef();
            }
        }
        return null;
    }
}




