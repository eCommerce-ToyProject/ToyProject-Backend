package com.idrsys.toyprojectbackend.dto.orders;

import com.idrsys.toyprojectbackend.dto.goods.GoodsItemDto;
import com.idrsys.toyprojectbackend.dto.goods.GoodsSearchDto;
import lombok.*;

import java.math.BigDecimal;

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
    private GoodsSearchDto goods_no;
    private GoodsItemDto item_no;



}
