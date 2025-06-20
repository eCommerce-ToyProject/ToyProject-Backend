package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "cart_item", 
       uniqueConstraints = @UniqueConstraint(name = "uk_cart_goods", columnNames = {"cart_id", "goods_no"}))
@EntityListeners(AuditingEntityListener.class)
public class CartItem {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "goods_no", nullable = false)
    private Integer goodsNo;

    @Column(name = "item_qty", nullable = false)
    private Integer itemQty = 1;

    @Column(name = "price", nullable = false)
    private Integer price;

    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    /**
     * 장바구니 설정
     */
    public void setCart(Cart cart) {
        this.cart = cart;
        if (cart != null) {
            List<CartItem> cartItems = cart.getCartItems();
            if (cartItems != null && !cartItems.contains(this)) {
                cartItems.add(this);
            }
        }
    }

    /**
     * 총 가격 계산
     */
    public Integer getTotalPrice() {
        return this.itemQty * this.price;
    }
}
