package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Orders {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ord_no")
    private Long ordNo;

    @Column(name = "ord_dt", nullable = false)
    private Date ordDt;

    @Column(name = "total_prc", nullable = false)
    private BigDecimal toPrc;

    @Column(name = "pay_Mn", nullable = false)
    private String payMn;

    @ManyToOne
    @JoinColumn(name = "mem_no")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "ord_status_cd")
    private OrderStatusCode ord_status_cd;

    @ManyToOne
    @JoinColumn(name = "del_no")
    private Delivery delivery;

    @OneToMany(mappedBy = "ord_no")
    private List<OrderItem> orderItems = new ArrayList<>();

}
