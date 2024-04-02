package com.idrsys.toyprojectbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "delivery")
@JsonIgnoreProperties({"orders","member"})
public class Delivery {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dliv_no")
    private Long dlivNo;

    @Column(name = "dliv_plc", nullable = false)
    private String dlivPlc;

    @ManyToOne
    @JoinColumn(name = "mem_no")
    private Member member;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "detailed address", nullable = false)
    private String detailAddress;

    @Column(name = "designation")
    private String designation;

    @Column(name = "dliv_create_dt", nullable = false)
    private LocalDateTime dlivCreateDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "dliv_deleted_dt")
    private LocalDateTime dlivDeletedDt;

    @Column(name = "dliv_change_dt")
    private LocalDateTime dlivChangeDt;

    @OneToMany(mappedBy = "delivery")
    private List<Orders> orders = new ArrayList<>();

}
