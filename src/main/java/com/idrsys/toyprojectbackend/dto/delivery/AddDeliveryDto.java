package com.idrsys.toyprojectbackend.dto.delivery;

import com.idrsys.toyprojectbackend.entity.Member;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class AddDeliveryDto {

    private String dlivPlc;
    private String memberId;
    private String zipCode;
    private String detailAddress;
    private String designation;

}
