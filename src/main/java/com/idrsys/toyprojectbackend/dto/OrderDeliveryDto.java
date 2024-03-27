package com.idrsys.toyprojectbackend.dto;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class OrderDeliveryDto {

    private Long delNo;
    private String delPlc;
    private String zCode;
    private String detailAddress;
    private String designation;
}
