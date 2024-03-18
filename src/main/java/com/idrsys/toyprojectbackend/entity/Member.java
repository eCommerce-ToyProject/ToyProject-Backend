package com.idrsys.toyprojectbackend.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id
    @Column(name = "mem_id")
    private String id;

    @Column(name = "mem_name")
    private String name;

    @Column(name = "mem_email")
    private String email;

    @Column(name = "mem_pwd", nullable = false)
    private String password;

    @Column(name = "mem_phone")
    private String phone;

    @OneToMany(mappedBy = "member")
    private List<Delivery> deliveryList;

    @OneToMany(mappedBy = "member")
    private List<Orders> orders = new ArrayList<>();

}
