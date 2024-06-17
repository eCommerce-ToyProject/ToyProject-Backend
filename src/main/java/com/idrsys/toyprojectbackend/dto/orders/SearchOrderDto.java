package com.idrsys.toyprojectbackend.dto.orders;

import com.idrsys.toyprojectbackend.dto.orders.OrderItemDto;
import com.idrsys.toyprojectbackend.dto.orders.OrderStatusCodeDto;
import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.OrderItem;
import com.idrsys.toyprojectbackend.entity.OrderStatusCode;
import com.idrsys.toyprojectbackend.excel.ExcelColumn;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchOrderDto {

    @ExcelColumn(headerName = "주분 번호")
    private Long ordNo;
    @ExcelColumn(headerName = "주문일자")
    private LocalDateTime ordDt;
    @ExcelColumn(headerName = "주문 가격")
    private BigDecimal toPrc;
    @ExcelColumn(headerName = "결재 수단")
    private String payMn;
    @ExcelColumn(headerName = "주문 상태")
    private OrderStatusCode ord_status_cd;
    @ExcelColumn(headerName = "주문 상품")
    private List<OrderItem> orderItem;
    @ExcelColumn(headerName = "배송지")
    private Delivery delivery;

}
