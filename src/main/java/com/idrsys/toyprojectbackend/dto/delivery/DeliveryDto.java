package com.idrsys.toyprojectbackend.dto.delivery;

import com.idrsys.toyprojectbackend.dto.member.MemberDto;
import com.idrsys.toyprojectbackend.excel.ExcelColumn;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DeliveryDto {

    @ExcelColumn(headerName = "배송지 번호")
    private Long dlivNo;
    @ExcelColumn(headerName = "배송지")
    private String dlivPlc;
    @ExcelColumn(headerName = "사용자")
    private MemberDto member;
    @ExcelColumn(headerName = "우편번호")
    private String zCode;
    @ExcelColumn(headerName = "상세주소")
    private String detailAddress;
    @ExcelColumn(headerName = "배송지 명칭")
    private String designation;
    @ExcelColumn(headerName = "삭제여부")
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
