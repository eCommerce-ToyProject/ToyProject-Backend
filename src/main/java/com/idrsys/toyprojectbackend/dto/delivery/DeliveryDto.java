package com.idrsys.toyprojectbackend.dto.delivery;

import com.idrsys.toyprojectbackend.dto.member.MemberDto;
import lombok.*;

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
    private boolean deleted;

    public DeliveryDto(Long delNo, String delPlc, String zCode, String detailAddress, String designation, boolean deleted) {
        this.delNo = delNo;
        this.delPlc = delPlc;
        this.zCode = zCode;
        this.detailAddress = detailAddress;
        this.designation = designation;
        this.deleted = deleted;
    }
}
