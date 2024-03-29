package com.idrsys.toyprojectbackend.dto.orders;

import com.idrsys.toyprojectbackend.dto.goods.GoodsItemDto;
import com.idrsys.toyprojectbackend.dto.goods.GoodsSearchDto;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestOrderDto {

    private Long ordNo;
    private Date ordDt;
    private BigDecimal toPrc;
    private String payMn;
    private String ordCd;
    private String ordDef;
    private Long ordItemCd;
    private Long ordQty;
    private BigDecimal ordPrc;
    private GoodsSearchDto goods_no;
    private GoodsItemDto item_no;
    private Long gNo;
    private String gName;
    private Long bNo;
    private BigDecimal gPrice;
    private String gImg;
    private String opt1;
    private String opt2;
    private Long cCd;
    private Long itemNo;
    private String iName;
    private String optVal1;
    private String optVal2;



}
