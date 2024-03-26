package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.entity.OrderItem;
import com.idrsys.toyprojectbackend.entity.OrderStatusCode;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class OrdersDto {

    private Long ordNo;
    private Date ordDt;
    private BigDecimal toPrc;
    private String payMn;
    private Member member;
    private OrderStatusCode ord_status_cd;
    private Delivery delivery;
    private List<OrderItemDto> orderItems;


}