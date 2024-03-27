package com.idrsys.toyprojectbackend.dto;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class OrderStatusCodeDto {

    private String ordCd;
    private String ordDef;
}
