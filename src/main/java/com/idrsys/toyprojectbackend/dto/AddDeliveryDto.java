package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Member;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class AddDeliveryDto {

    private String delPlc;
    private String memberId;
    private String zipCode;
    private String detailAddress;
    private String designation;

}
