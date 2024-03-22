package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "goods")
public class Goods {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_no")
    private Long gNo;

    @Column(name = "goods_name", nullable = false)
    private String gName;

    @Column(name = "brand_no")
    private Long bNo;

    @Column(name = "goods_img")
    private String gImg;

    @Column(name = "goods_prc", nullable = false)
    private BigDecimal gPrice;

    @Column(name = "opt1")
    private String opt1;

    @Column(name = "opt2")
    private String opt2;

    @Column(name = "cat_cd")
    private Long cCd;

    @OneToMany(mappedBy = "goods", fetch = FetchType.EAGER)
    private List<GoodsItem> goodsItem = new ArrayList<>();

    @OneToMany(mappedBy = "goods_no")
    private List<OrderItem> orderItems = new ArrayList<>();

}
