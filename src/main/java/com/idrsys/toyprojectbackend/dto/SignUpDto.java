package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Member;
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

    private String id;
    private String username;
    private String email;
    private String password;
    private String phone;
    private List<String> roles = new ArrayList<>();

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

//    public List<GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        if (this.getRole().equals("ADMIN")) {
//            authorities.add(new SimpleGrantedAuthority("ADMIN"));
//        } else if (this.getRole().equals("SUBADMIN")) {
//            authorities.add(new SimpleGrantedAuthority("SUBADMIN"));
//        } else {
//            authorities.add(new SimpleGrantedAuthority("MEMBER"));
//        }
//        return authorities;
//    }

}
