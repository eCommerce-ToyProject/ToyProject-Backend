package com.idrsys.toyprojectbackend.dto.orders;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class OrderDeliveryDto {

    private Long dlivNo;
    private String delPlc;
    private String zCode;
    private String detailAddress;
    private String designation;
}
