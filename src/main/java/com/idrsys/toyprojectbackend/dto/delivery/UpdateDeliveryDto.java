package com.idrsys.toyprojectbackend.dto.delivery;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UpdateDeliveryDto {

    private Long dlivNo;
    private String dlivPlc;
//    private String memberId;
    private String zipCode;
    private String detailAddress;
    private String designation;

}
