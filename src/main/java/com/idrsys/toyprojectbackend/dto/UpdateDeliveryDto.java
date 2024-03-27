package com.idrsys.toyprojectbackend.dto;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UpdateDeliveryDto {

    private Long delNo;
    private String delPlc;
//    private String memberId;
    private String zipCode;
    private String detailAddress;
    private String designation;

}
