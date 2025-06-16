package com.idrsys.toyprojectbackend.dto.delivery;

import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.excel.ExcelColumn;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class AddDeliveryDto {

    @ExcelColumn("배송지")
    private String dlivPlc;

    @ExcelColumn("사용자")
    private String memberId;

    @ExcelColumn("우편번호")
    private String zipCode;

    @ExcelColumn("상세주소")
    private String detailAddress;

    @ExcelColumn("배송지 명칭")
    private String designation;

}
