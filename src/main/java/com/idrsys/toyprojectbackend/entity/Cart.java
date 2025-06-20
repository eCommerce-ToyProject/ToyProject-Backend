package com.idrsys.toyprojectbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cart")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"cartItems"})
@Slf4j
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "mem_no", nullable = false, unique = true)
    private Integer memNo;

    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Builder 패턴 생성자
     */
    @Builder
    public Cart(Long cartId, Integer memNo, LocalDateTime createAt, LocalDateTime updateAt, List<CartItem> cartItems) {
        this.cartId = cartId;
        this.memNo = memNo;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
    }

    /**
     * cartItems getter - null 체크 및 Lazy Loading 안전 처리
     */
    public List<CartItem> getCartItems() {
        if (this.cartItems == null) {
            log.debug("cartItems가 null입니다. 빈 ArrayList로 초기화합니다.");
            this.cartItems = new ArrayList<>();
        }
        return this.cartItems;
    }

    /**
     * cartItems setter - null 체크 추가
     */
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
    }

    /**
     * 회원 정보 조회 (비즈니스 로직용)
     */
    public Integer getMemNo() {
        return this.memNo;
    }
}
