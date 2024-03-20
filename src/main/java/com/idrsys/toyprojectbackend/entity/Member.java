package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "member")
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

    @OneToMany(mappedBy = "member")
    private List<Delivery> deliveryList;

    @OneToMany(mappedBy = "member")
    private List<Orders> orders = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return memberRoles.stream()
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());

        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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



}
