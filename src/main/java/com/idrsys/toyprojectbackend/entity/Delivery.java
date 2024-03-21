package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
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

    @OneToMany(mappedBy = "delivery")
    private List<Orders> orders = new ArrayList<>();
}
