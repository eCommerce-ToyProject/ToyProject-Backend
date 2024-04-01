package com.idrsys.toyprojectbackend.dto.orders;

import com.idrsys.toyprojectbackend.dto.orders.OrderItemDto;
import com.idrsys.toyprojectbackend.dto.orders.OrderStatusCodeDto;
import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.OrderItem;
import com.idrsys.toyprojectbackend.entity.OrderStatusCode;
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

    private Long ordNo;
    private LocalDateTime ordDt;
    private BigDecimal toPrc;
    private String payMn;
    private OrderStatusCode ord_status_cd;
    private List<OrderItem> orderItem;
    private Delivery delivery;

}
