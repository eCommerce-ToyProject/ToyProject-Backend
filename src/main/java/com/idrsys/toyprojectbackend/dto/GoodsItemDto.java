package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Goods;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class GoodsItemDto {

    private Long no;
    private String name;
    private String optVal1;
    private String optVal2;
    private Long iQty;
    private BigDecimal iAmtAdd;
    private Long iSaveQty;


    public GoodsItemDto(Long no, String name, String optVal1, String optVal2) {
        this.no = no;
        this.name = name;
        this.optVal1 = optVal1;
        this.optVal2 = optVal2;
    }
}
