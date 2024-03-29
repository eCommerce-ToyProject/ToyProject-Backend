package com.idrsys.toyprojectbackend.dto.goods;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDto {

    private Long gNo;
    private String gName;
    private Long bNo;
    private String gImg;
    private BigDecimal gPrice;
    private String opt1;
    private String opt2;
    private Long cCd;
    private List<GoodsItemDto> goodsItem = new ArrayList<>();

}
