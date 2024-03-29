package com.idrsys.toyprojectbackend.dto.orders;

import com.idrsys.toyprojectbackend.dto.orders.OrderItemDto;
import com.idrsys.toyprojectbackend.dto.orders.OrderStatusCodeDto;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchOrderDto {

    private Long ordNo;
    private Date ordDt;
    private BigDecimal toPrc;
    private String payMn;
    private OrderStatusCodeDto ordStatusCd;
    private List<OrderItemDto> orderItems;

}
