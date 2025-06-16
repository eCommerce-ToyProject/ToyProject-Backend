package com.idrsys.toyprojectbackend.dto.goods;

import com.idrsys.toyprojectbackend.excel.ExcelColumn;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRequestDto {

    @ExcelColumn("상품명")
    private String gName;

    @ExcelColumn("브랜드ID")
    private Long bNo;

    @ExcelColumn("상품 이미지")
    private String gImg;

    @ExcelColumn("상품가격")
    private BigDecimal gPrice;

    @ExcelColumn("옵션1")
    private String opt1;

    @ExcelColumn("옵션2")
    private String opt2;

    @ExcelColumn("카테고리ID")
    private Long cCd;

    @ExcelColumn(value = "상품옵션 코드", separator = ",")
    private List<String> goodsItemCd = new ArrayList<>();
}
