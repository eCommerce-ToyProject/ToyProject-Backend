package com.idrsys.toyprojectbackend.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoginCheckDto {
    private String id;
    private String token;
}
