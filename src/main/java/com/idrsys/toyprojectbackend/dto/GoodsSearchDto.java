package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.entity.OrderItem;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class GoodsSearchDto {

    private Long gNo;
    private String gName;
    private Long bNo;
    private BigDecimal gPrice;
    private String gImg;
    private String bName;
    private String cName;
    private String opt1;
    private String opt2;
    private Long cCd;



}
