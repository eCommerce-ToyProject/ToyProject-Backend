package com.idrsys.toyprojectbackend.enums

enum class OrderPay(val codeName: String) {
    BNK_ACC("계좌이체"),
    KKO_PAY("카카오페이"),
    CREDIT_CARD("카드결제");
    fun getCodeNm() = this.codeName

    companion object {
        @JvmStatic
        fun fromCode(code: String): OrderPay? {
            return values().find { it.name == code }
        }
    }
}
