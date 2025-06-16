package com.idrsys.toyprojectbackend.dto.orders;

import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.OrderItem;
import com.idrsys.toyprojectbackend.entity.OrderStatusCode;
import com.idrsys.toyprojectbackend.enums.OrderPay;
import com.idrsys.toyprojectbackend.enums.OrderStatus;
import com.idrsys.toyprojectbackend.excel.DefaultHeaderStyle;
import com.idrsys.toyprojectbackend.excel.EnumMapping;
import com.idrsys.toyprojectbackend.excel.ExcelColumn;
import com.idrsys.toyprojectbackend.excel.ExcelColumnStyle;
import com.idrsys.toyprojectbackend.excel.style.DefaultExcelCellStyle;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DefaultHeaderStyle(style = @ExcelColumnStyle(excelCellStyleClass = DefaultExcelCellStyle.class, enumName = "BLUE_HEADER"))
public class SearchOrderDto {

    @ExcelColumn(value = "주문 번호")
    private Long ordNo;
    @ExcelColumn(value = "주문일자")
    private LocalDateTime ordDt;
    @ExcelColumn(value = "주문 가격")
    private Long toPrc;
    @ExcelColumn(value = "결재 수단")
    @EnumMapping(enumClass = OrderPay.class)
    private String payMn;
    @ExcelColumn(value = "주문 상태")
    @EnumMapping(enumClass = OrderStatus.class)
    private OrderStatusCode ord_status_cd;
    @ExcelColumn(value = "주문 상품")
    private List<OrderItem> orderItem;
    @ExcelColumn(value = "배송지")
    private Delivery delivery;

    @Override
    public String toString() {
        return "SearchOrderDto{" +
                "ordNo=" + ordNo +
                ", ordDt=" + ordDt +
                ", toPrc=" + toPrc +
                ", payMn='" + payMn + '\'' +
                ", ord_status_cd=" + ord_status_cd +
                ", orderItem=" + orderItem +
                ", delivery=" + delivery +
                '}';
    }
}
