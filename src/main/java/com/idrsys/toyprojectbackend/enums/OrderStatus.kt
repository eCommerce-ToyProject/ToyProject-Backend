package com.idrsys.toyprojectbackend.enums

enum class OrderStatus(val codeName: String) {
    STATUS_OUT_OF_STACK("품절"),
    STATUS_PAYMENT_COMPLETED("결제완료"),
    STATUS_PAYMENT_FAILED("결제실패"),
    STATUS_SHIPPED("배송완료"),
    STATUS_SHIPPING("배송중");
    fun getCodeNm() = this.codeName

    companion object {
        @JvmStatic
        fun fromCode(code: String): OrderStatus? {
            return values().find { it.name == code }
        }
    }
}




