package com.idrsys.toyprojectbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "order_item")
@JsonIgnoreProperties({"ord_no"})
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_cd")
    private Long ordItemCd;

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

    @Column(name = "goods_nm")
    private String gNm;

    @Column(name = "brand_no")
    private Long bNo;

    @Column(name = "cat_cd")
    private Long catCd;

    @ManyToOne
    @JoinColumn(name = "item_no")
    private GoodsItem item_no;

    @Column(name = "item_nm")
    private String iNm;

    @Column(name = "option_val1")
    private String optVal1;

    @Column(name = "option_val2")
    private String optVal2;

    @Column(name = "item_amt_add")
    private BigDecimal iAmtAdd;

    @Override
    public String toString() {
        return "OrderItem{" +
                "ordItemCd=" + ordItemCd +
                ", ordQty=" + ordQty +
                ", ordPrc=" + ordPrc +
                ", ord_no=" + ord_no.getOrdNo() +
                ", goods_no=" + goods_no.getGNo() +
                ", gNm='" + gNm + '\'' +
                ", bNo=" + bNo +
                ", catCd=" + catCd +
                ", item_no=" + item_no +
                ", iNm='" + iNm + '\'' +
                ", optVal1='" + optVal1 + '\'' +
                ", optVal2='" + optVal2 + '\'' +
                ", iAmtAdd=" + iAmtAdd +
                '}';
    }

}
