package com.idrsys.toyprojectbackend.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCartItemRequest {
    
    @NotNull(message = "상품 번호는 필수입니다.")
    @Min(value = 1, message = "상품 번호는 1 이상이어야 합니다.")
    private Integer goodsNo;
    
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer itemQty;
    
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;
}
