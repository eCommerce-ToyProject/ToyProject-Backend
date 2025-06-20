package com.idrsys.toyprojectbackend.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private Long cartItemId;
    private Integer goodsNo;
    private String goodsName;
    private String goodsImage;
    private Integer itemQty;
    private Integer price;
    private Integer totalItemPrice;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
