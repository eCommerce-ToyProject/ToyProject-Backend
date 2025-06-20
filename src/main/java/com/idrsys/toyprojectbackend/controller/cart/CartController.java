package com.idrsys.toyprojectbackend.controller.cart;

import com.idrsys.toyprojectbackend.dto.cart.*;
import com.idrsys.toyprojectbackend.exception.CartException;
import com.idrsys.toyprojectbackend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Cart API", description = "장바구니 관리 API")
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 조회
     * GET /cart/{memberId}
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<CartDto> getCart(@PathVariable String memberId) {
        try {
            CartDto cart = cartService.getCartByMemberId(memberId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("장바구니 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 장바구니에 상품 추가
     * POST /cart/{memberId}/items
     */
    @PostMapping("/{memberId}/items")
    public ResponseEntity<CartDto> addItemToCart(
            @PathVariable String memberId,
            @Valid @RequestBody AddCartItemRequest request) {
        try {
            CartDto cart = cartService.addItemToCart(memberId, request);
            return ResponseEntity.ok(cart);
        } catch (CartException e) {
            log.warn("장바구니 상품 추가 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("장바구니 상품 추가 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 장바구니 상품 수량 수정
     * PUT /cart/{memberId}/items/{cartItemId}
     */
    @PutMapping("/{memberId}/items/{cartItemId}")
    public ResponseEntity<CartDto> updateCartItem(
            @PathVariable String memberId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        try {
            CartDto cart = cartService.updateCartItem(memberId, cartItemId, request);
            return ResponseEntity.ok(cart);
        } catch (CartException e) {
            log.warn("장바구니 상품 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("장바구니 상품 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 장바구니 상품 삭제
     * DELETE /cart/{memberId}/items/{cartItemId}
     */
    @DeleteMapping("/{memberId}/items/{cartItemId}")
    public ResponseEntity<CartDto> removeCartItem(
            @PathVariable String memberId,
            @PathVariable Long cartItemId) {
        try {
            CartDto cart = cartService.removeCartItem(memberId, cartItemId);
            return ResponseEntity.ok(cart);
        } catch (CartException e) {
            log.warn("장바구니 상품 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("장바구니 상품 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 장바구니 전체 비우기
     * DELETE /cart/{memberId}
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> clearCart(@PathVariable String memberId) {
        try {
            cartService.clearCart(memberId);
            return ResponseEntity.ok().build();
        } catch (CartException e) {
            log.warn("장바구니 비우기 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("장바구니 비우기 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 장바구니 상품 개수 조회
     * GET /cart/{memberId}/count
     */
    @GetMapping("/{memberId}/count")
    public ResponseEntity<Integer> getCartItemCount(@PathVariable String memberId) {
        try {
            Integer itemCount = cartService.getCartItemCount(memberId);
            return ResponseEntity.ok(itemCount);
        } catch (Exception e) {
            log.error("장바구니 상품 개수 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
