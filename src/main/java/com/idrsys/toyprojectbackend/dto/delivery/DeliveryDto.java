package com.idrsys.toyprojectbackend.dto.delivery;

import com.idrsys.toyprojectbackend.dto.member.MemberDto;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DeliveryDto {

    private Long dlivNo;
    private String dlivPlc;
    private MemberDto member;
    private String zCode;
    private String detailAddress;
    private String designation;
    private boolean deleted;

    public DeliveryDto(Long dlivNo, String dlivPlc, String zCode, String detailAddress, String designation, boolean deleted) {
        this.dlivNo = dlivNo;
        this.dlivPlc = dlivPlc;
        this.zCode = zCode;
        this.detailAddress = detailAddress;
        this.designation = designation;
        this.deleted = deleted;
    }
}
