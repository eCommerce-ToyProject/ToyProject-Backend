package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "orders")
public class
Orders {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ord_no")
    private Long ordNo;

    @Column(name = "ord_dt", nullable = false)
    private LocalDateTime ordDt;

    @Column(name = "total_prc", nullable = false)
    private BigDecimal toPrc;

    @Column(name = "pay_Mn", nullable = false)
    private String payMn;

    @Column(name = "mem_id")
    private String memId;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "detailed_address")
    private String detailAddress;

    @Column(name = "goods_prc")
    private BigDecimal goodsPrc;

    @Column(name = "item_amt_add")
    private BigDecimal iAmtAdd;

    @Column(name = "dliv_fee")
    private BigDecimal dlivFee;

    @ManyToOne
    @JoinColumn(name = "mem_no")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "ord_status_cd")
    private OrderStatusCode ord_status_cd;

    @ManyToOne
    @JoinColumn(name = "dliv_no")
    private Delivery delivery;

    @OneToMany(mappedBy = "ord_no", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

}
