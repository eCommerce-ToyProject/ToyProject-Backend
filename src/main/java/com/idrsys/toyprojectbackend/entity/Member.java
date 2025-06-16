package com.idrsys.toyprojectbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "member")
@JsonIgnoreProperties({"orders", "deliveryList"})
public class Member implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mem_no", nullable = false)
    private Long no;

    @Column(name = "mem_id", nullable = false, unique = true)
    private String id;

    @Column(name = "mem_name", nullable = false)
    private String username;

    @Column(name = "mem_email")
    private String email;

    @Column(name = "mem_pwd", nullable = false)
    private String password;

    @Column(name = "mem_phone", nullable = false)
    private String phone;

    @Column(name = "mem_created_dt", nullable = false)
    private LocalDateTime memCreateDt;

    @Column(name = "mem_deleted", nullable = false)
    private boolean memDeleted;

    @Column(name = "mem_deleted_dt")
    private LocalDateTime memDeletedDt;

    @Column(name = "mem_change_dt")
    private LocalDateTime memChangeDt;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Delivery> deliveryList;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Orders> orders;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getAccessTokenClaims(){
        return Map.of(
                "id", getId(),
                "username", getUsername(),
                "createDateTime", getMemCreateDt().toString()
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    /**
     * 회원 이름 조회 (비즈니스 로직용)
     */
    public String getMemName() {
        return this.username;
    }
    
    /**
     * 회원 번호 조회 (비즈니스 로직용)
     */
    public Integer getMemNo() {
        return this.no != null ? this.no.intValue() : null;
    }



}
