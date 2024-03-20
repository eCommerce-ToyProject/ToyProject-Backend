package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Member;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    private Long no;
    private String id;
    private String username;
    private String email;
    private String phone;

    static public MemberDto toDto(Member member) {
        return MemberDto.builder()
                .no(member.getNo())
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .phone(member.getPhone()).build();
    }

    public Member toEntity() {
        return Member.builder()
                .no(no)
                .id(id)
                .username(username)
                .email(email)
                .phone(phone).build();
    }



}
