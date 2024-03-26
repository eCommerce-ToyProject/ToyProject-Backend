package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Goods;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.entity.OrderItem;
import com.idrsys.toyprojectbackend.entity.Orders;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class OrderItemDto {

    private Long ordItemCd;
    private Long ordQty;
    private BigDecimal ordPrc;
    private Orders ord_no;
    private Goods goods_no;
    private GoodsItem item_no;

}
