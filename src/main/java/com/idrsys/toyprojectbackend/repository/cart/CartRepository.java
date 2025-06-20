package com.idrsys.toyprojectbackend.repository.cart;

import com.idrsys.toyprojectbackend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findByMemNo(Integer memNo);
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.memNo = :memNo")
    Optional<Cart> findByMemNoWithItems(@Param("memNo") Integer memNo);
    
    boolean existsByMemNo(Integer memNo);
}
