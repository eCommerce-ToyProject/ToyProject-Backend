package com.idrsys.toyprojectbackend.enums

enum class OrderStatus(val code: String, val def: String) {
    STATUS_OUT_OF_STACK("STATUS_OUT_OF_STACK", "품절"),
    STATUS_PAYMENT_COMPLETED("STATUS_PAYMENT_COMPLETED", "결제완료"),
    STATUS_PAYMENT_FAILED("STATUS_PAYMENT_FAILED", "결제실패"),
    STATUS_SHIPPED("STATUS_SHIPPED", "배송완료"),
    STATUS_SHIPPING("STATUS_SHIPPING", "배송중");

    companion object {
        @JvmStatic
        fun fromCode(code: String): OrderStatus? {
            for (status in entries) {
                if (status.code == code) {
                    return status
                }
            }
            return null
        }
    }
}




