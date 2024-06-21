package com.idrsys.toyprojectbackend.enums;

public enum OrderPay {
    BNK_ACC("BNK_ACC", "계좌이체"),
    KKO_PAY("KKO_PAY", "카카오페이"),
    CREDIT_CARD("CREDIT_CARD", "카드결제");

    private final String code;
    private final String def;

    private OrderPay(String code, String def) {
        this.code = code;
        this.def = def;
    }

    public String getCode() {
        return code;
    }

    public String getDef() {
        return def;
    }

    public static OrderPay fromCode(String code) {
        for (OrderPay pay : OrderPay.values()) {
            if (pay.getCode().equals(code)) {
                return pay;
            }
        }
        return null;
    }
}
