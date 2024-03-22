package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Goods;
import jakarta.persistence.*;
import lombok.*;

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
    private Long iAmtAdd;
    private Long iSaveQty;

}
