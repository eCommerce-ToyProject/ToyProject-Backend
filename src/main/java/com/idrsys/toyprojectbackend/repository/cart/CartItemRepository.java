package com.idrsys.toyprojectbackend.repository.cart;

import com.idrsys.toyprojectbackend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByCartCartId(Long cartId);
    
    Optional<CartItem> findByCartCartIdAndGoodsNo(Long cartId, Integer goodsNo);
    
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.goodsNo = :goodsNo")
    void deleteByCartCartIdAndGoodsNo(@Param("cartId") Long cartId, @Param("goodsNo") Integer goodsNo);
    
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    void deleteByCartCartId(@Param("cartId") Long cartId);
    
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    Integer countByCartId(@Param("cartId") Long cartId);
    
    @Query("SELECT SUM(ci.itemQty * ci.price) FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    Integer calculateTotalPrice(@Param("cartId") Long cartId);
}
