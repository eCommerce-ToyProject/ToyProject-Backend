package com.idrsys.toyprojectbackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSearchDto {

    private Long no;
    private String id;
    private String username;
    private String email;
    private String phone;
    private List<String> roles;

}
