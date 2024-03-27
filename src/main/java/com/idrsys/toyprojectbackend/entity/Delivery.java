package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "delivery")
public class Delivery {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "del_no")
    private Long delNo;

    @Column(name = "del_plc", nullable = false)
    private String delPlc;

    @ManyToOne
    @JoinColumn(name = "mem_no")
    private Member member;

    @Column(name = "zip_code", nullable = false)
    private String zCode;

    @Column(name = "detailed address", nullable = false)
    private String detailAddress;

    @Column(name = "designation")
    private String designation;

    @OneToMany(mappedBy = "delivery")
    private List<Orders> orders = new ArrayList<>();
}
