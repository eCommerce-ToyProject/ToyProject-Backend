package com.idrsys.toyprojectbackend.dto.orders;

import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.OrderItem;
import com.idrsys.toyprojectbackend.entity.OrderStatusCode;
import com.idrsys.toyprojectbackend.excel.DefaultHeaderStyle;
import com.idrsys.toyprojectbackend.excel.ExcelColumn;
import com.idrsys.toyprojectbackend.excel.ExcelColumnStyle;
import com.idrsys.toyprojectbackend.excel.style.DefaultExcelCellStyle;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DefaultHeaderStyle(style = @ExcelColumnStyle(excelCellStyleClass = DefaultExcelCellStyle.class, enumName = "BLUE_HEADER"))
public class OrderListExcelDto {

    @ExcelColumn(headerName = "주문 번호")
    private Long ordNo;
    @ExcelColumn(headerName = "주문일자")
    private LocalDateTime ordDt;
    @ExcelColumn(headerName = "주문 가격")
    private Long toPrc;
    @ExcelColumn(headerName = "결재 수단")
    private String payMn;
    @ExcelColumn(headerName = "주문 상태")
    private OrderStatusCode ord_status_cd;
    @ExcelColumn(headerName = "주문 상품")
    private List<OrderItem> orderItem;
    @ExcelColumn(headerName = "배송지")
    private Delivery delivery;
}
