package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.entity.Orders;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DeliveryDto {

    private Long delNo;
    private String delPlc;
    private MemberDto member;
    private String zCode;
    private String detailAddress;
    private String designation;
}
