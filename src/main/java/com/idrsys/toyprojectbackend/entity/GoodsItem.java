package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "goods_item")
public class GoodsItem {

    @Id
    @Column(name = "item_no")
    private int no;

    @Column(name = "item_nm", nullable = false)
    private String name;

    @Column(name = "option_val1")
    private String optVal1;

    @Column(name = "option_val2")
    private String optVal2;

    @Column(name = "item_qty", nullable = false)
    private Long iQty;

    @Column(name = "item_amt_add")
    private Long iAmtAdd;

    @ManyToOne
    @JoinColumn(name = "goods_no")
    private Goods goods;

    @OneToMany(mappedBy = "item_no")
    private List<OrderItem> orderItems = new ArrayList<>();
}
