package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @Column(name = "order_item_cd")
    private int ordItemCd;

    @Column(name = "ord_qty", nullable = false)
    private Long ordQty;

    @Column(name = "ord_prc", nullable = false)
    private BigDecimal ordPrc;

    @ManyToOne
    @JoinColumn(name = "ord_no")
    private Orders ord_no;

    @ManyToOne
    @JoinColumn(name = "goods_no")
    private Goods goods_no;

    @ManyToOne
    @JoinColumn(name = "item_no")
    private GoodsItem item_no;

}
