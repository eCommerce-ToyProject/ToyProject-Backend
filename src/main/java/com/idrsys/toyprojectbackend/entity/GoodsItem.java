package com.idrsys.toyprojectbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "goods_item")
public class GoodsItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_no")
    private Long no;

    @Column(name = "item_nm", nullable = false)
    private String name;

    @Column(name = "option_val1")
    private String optVal1;

    @Column(name = "option_val2")
    private String optVal2;

    @Column(name = "item_qty", nullable = false)
    private Long iQty;

    @Column(name = "item_amt_add")
    private BigDecimal iAmtAdd;

    @Column(name = "item_save_qty", nullable = false)
    private Long iSaveQty;

    @ManyToOne
    @JoinColumn(name = "goods_no")
    private Goods goods;

    @OneToMany(mappedBy = "item_no")
    private List<OrderItem> orderItems;
}
