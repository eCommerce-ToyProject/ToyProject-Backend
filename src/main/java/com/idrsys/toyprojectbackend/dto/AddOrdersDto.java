package com.idrsys.toyprojectbackend.dto;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class AddOrdersDto {
    private String memberId;
    private Long goodsId;
    private String optVal1;
    private String optVal2;
    private Long quantity;
    private String paymn;
    private String delPlc;
    private String zipCode;
    private String detailAddress;
    private String designation;
}
