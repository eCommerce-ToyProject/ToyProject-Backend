package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "order_status_code")
public class OrderStatusCode {

    @Id
    @Column(name = "ord_status_cd")
    private String ordCd;

    @Column(name = "ord_status_def", nullable = false)
    private String ordDef;

    @OneToMany(mappedBy = "ord_status_cd")
    private List<Orders> orders = new ArrayList<>();
}
