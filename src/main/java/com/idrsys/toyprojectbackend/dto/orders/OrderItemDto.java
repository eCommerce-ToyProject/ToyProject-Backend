package com.idrsys.toyprojectbackend.dto.orders;

import com.idrsys.toyprojectbackend.dto.goods.GoodsItemDto;
import com.idrsys.toyprojectbackend.dto.goods.GoodsSearchDto;
import com.idrsys.toyprojectbackend.excel.ExcelColumn;
import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class OrderItemDto {

    @ExcelColumn(headerName = "주분 번호")
    private Long ordItemCd;

    @ExcelColumn(headerName = "주분 수량")
    private Long ordQty;

    @ExcelColumn(headerName = "주분 가격")
    private BigDecimal ordPrc;

    @ExcelColumn(headerName = "주분 상품")
    private GoodsSearchDto goods_no;

    @ExcelColumn(headerName = "주분 상품")
    private GoodsItemDto item_no;



}
