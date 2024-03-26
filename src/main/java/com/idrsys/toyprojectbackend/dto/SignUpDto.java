package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Member;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {

//    @NotBlank(message = "아이디는 필수항목 입니다.")
    private String id;

//    @NotBlank(message = "회원명은 필수항목 입니다.")
    private String username;

    private String email;

//    @NotBlank(message = "비밀번호는 필수항목 입니다.")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{5,16}$", message = "비밀번호는 영문, 숫자, 특수문자를 적어도 하나 이상 포함하는 5~16자의 문자열입니다.")
    private String password;

//    @NotBlank(message = "전화번호는 필수항목 입니다.")
    private String phone;

    private List<String> roles;

    public Member toEntity(String encodedPassword, List<String> roles) {

        return Member.builder()
                .id(id)
                .password(encodedPassword)
                .username(username)
                .email(email)
                .phone(phone)
                .roles(roles)
                .build();
    }

}
