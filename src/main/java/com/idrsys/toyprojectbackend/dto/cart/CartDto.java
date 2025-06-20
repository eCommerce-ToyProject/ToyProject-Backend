package com.idrsys.toyprojectbackend.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private Long cartId;
    private Integer memNo;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<CartItemDto> cartItems;
    private Integer totalItemCount;
    private Integer totalPrice;
}
