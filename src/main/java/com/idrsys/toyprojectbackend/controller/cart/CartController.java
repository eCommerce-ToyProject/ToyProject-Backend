package com.idrsys.toyprojectbackend.controller.cart;

import com.idrsys.toyprojectbackend.dto.cart.*;
import com.idrsys.toyprojectbackend.exception.CartException;
import com.idrsys.toyprojectbackend.service.CartService;
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
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 조회
     * GET /cart/{memNo}
     */
    @GetMapping("/{memNo}")
    public ResponseEntity<CartDto> getCart(@PathVariable Integer memNo) {
        try {
            CartDto cart = cartService.getCartByMemNo(memNo);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("장바구니 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 장바구니에 상품 추가
     * POST /cart/{memNo}/items
     */
    @PostMapping("/{memNo}/items")
    public ResponseEntity<CartDto> addItemToCart(
            @PathVariable Integer memNo,
            @Valid @RequestBody AddCartItemRequest request) {
        try {
            CartDto cart = cartService.addItemToCart(memNo, request);
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
     * PUT /cart/{memNo}/items/{cartItemId}
     */
    @PutMapping("/{memNo}/items/{cartItemId}")
    public ResponseEntity<CartDto> updateCartItem(
            @PathVariable Integer memNo,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        try {
            CartDto cart = cartService.updateCartItem(memNo, cartItemId, request);
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
     * DELETE /cart/{memNo}/items/{cartItemId}
     */
    @DeleteMapping("/{memNo}/items/{cartItemId}")
    public ResponseEntity<CartDto> removeCartItem(
            @PathVariable Integer memNo,
            @PathVariable Long cartItemId) {
        try {
            CartDto cart = cartService.removeCartItem(memNo, cartItemId);
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
     * DELETE /cart/{memNo}
     */
    @DeleteMapping("/{memNo}")
    public ResponseEntity<Void> clearCart(@PathVariable Integer memNo) {
        try {
            cartService.clearCart(memNo);
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
     * GET /cart/{memNo}/count
     */
    @GetMapping("/{memNo}/count")
    public ResponseEntity<Integer> getCartItemCount(@PathVariable Integer memNo) {
        try {
            Integer itemCount = cartService.getCartItemCount(memNo);
            return ResponseEntity.ok(itemCount);
        } catch (Exception e) {
            log.error("장바구니 상품 개수 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
