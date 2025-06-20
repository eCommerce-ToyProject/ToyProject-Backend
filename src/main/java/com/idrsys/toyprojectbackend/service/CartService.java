package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.cart.*;
import com.idrsys.toyprojectbackend.entity.Cart;
import com.idrsys.toyprojectbackend.entity.CartItem;
import com.idrsys.toyprojectbackend.entity.Goods;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.exception.CartException;
import com.idrsys.toyprojectbackend.repository.cart.CartRepository;
import com.idrsys.toyprojectbackend.repository.cart.CartItemRepository;
import com.idrsys.toyprojectbackend.repository.goods.GoodsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final GoodsRepository goodsRepository;
    private final MemberService memberService;

    /**
     * 장바구니 조회 (회원 ID로)
     */
    @Transactional(readOnly = true)
    public CartDto getCartByMemberId(String memberId) {
        // memberId로 회원 조회
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new CartException("회원을 찾을 수 없습니다."));
        
        return getCartByMemNo(member.getMemNo());
    }

    /**
     * 장바구니 조회 (회원번호로)
     */
    @Transactional(readOnly = true)
    public CartDto getCartByMemNo(Integer memNo) {
        Optional<Cart> cartOpt = cartRepository.findByMemNoWithItems(memNo);
        
        if (cartOpt.isEmpty()) {
            // 장바구니가 없으면 빈 장바구니 반환
            return CartDto.builder()
                    .memNo(memNo)
                    .cartItems(List.of())
                    .totalItemCount(0)
                    .totalPrice(0)
                    .build();
        }

        Cart cart = cartOpt.get();
        return convertToCartDto(cart);
    }

    /**
     * 장바구니에 상품 추가 (회원 ID로)
     */
    public CartDto addItemToCart(String memberId, AddCartItemRequest request) {
        // memberId로 회원 조회
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new CartException("회원을 찾을 수 없습니다."));
        
        return addItemToCart(member.getMemNo(), request);
    }

    /**
     * 장바구니에 상품 추가 (회원번호로)
     */
    public CartDto addItemToCart(Integer memNo, AddCartItemRequest request) {
        log.debug("장바구니 상품 추가 시작 - 회원번호: {}, 상품번호: {}", memNo, request.getGoodsNo());
        
        // 상품 존재 여부 확인
        Goods goods = goodsRepository.findById(request.getGoodsNo().longValue())
                .orElseThrow(() -> new CartException("존재하지 않는 상품입니다."));
        
        // 장바구니 조회 또는 생성
        Cart cart = getOrCreateCart(memNo);
        log.debug("장바구니 조회/생성 완료 - cartId: {}", cart.getCartId());
        
        // 기존에 같은 상품이 있는지 확인
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartCartIdAndGoodsNo(cart.getCartId(), request.getGoodsNo());
        
        if (existingItem.isPresent()) {
            // 기존 상품이 있으면 수량 증가
            CartItem item = existingItem.get();
            item.setItemQty(item.getItemQty() + request.getItemQty());
            cartItemRepository.save(item);
            log.debug("기존 상품 수량 증가 - cartItemId: {}, 새 수량: {}", item.getCartItemId(), item.getItemQty());
        } else {
            // 새로운 상품 추가
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .goodsNo(request.getGoodsNo())
                    .itemQty(request.getItemQty())
                    .price(request.getPrice())
                    .build();
            cartItemRepository.save(newItem);
            log.debug("새 상품 추가 완료 - cartItemId: {}", newItem.getCartItemId());
        }
        
        // 업데이트된 장바구니 반환 - 다시 조회하지 않고 직접 변환
        Cart updatedCart = cartRepository.findByMemNoWithItems(memNo)
                .orElseThrow(() -> new CartException("장바구니 조회 실패"));
        
        log.debug("장바구니 업데이트 완료");
        return convertToCartDto(updatedCart);
    }

    /**
     * 장바구니 상품 수량 수정 (회원 ID로)
     */
    public CartDto updateCartItem(String memberId, Long cartItemId, UpdateCartItemRequest request) {
        // memberId로 회원 조회
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new CartException("회원을 찾을 수 없습니다."));
        
        return updateCartItem(member.getMemNo(), cartItemId, request);
    }

    /**
     * 장바구니 상품 수량 수정 (회원번호로)
     */
    public CartDto updateCartItem(Integer memNo, Long cartItemId, UpdateCartItemRequest request) {
        // 장바구니 아이템 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartException("장바구니 상품을 찾을 수 없습니다."));
        
        // 권한 확인 (해당 회원의 장바구니인지)
        if (!cartItem.getCart().getMemNo().equals(memNo)) {
            throw new CartException("권한이 없습니다.");
        }
        
        // 수량 업데이트
        cartItem.setItemQty(request.getItemQty());
        cartItemRepository.save(cartItem);
        
        return getCartByMemNo(memNo);
    }

    /**
     * 장바구니 상품 삭제 (회원 ID로)
     */
    public CartDto removeCartItem(String memberId, Long cartItemId) {
        // memberId로 회원 조회
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new CartException("회원을 찾을 수 없습니다."));
        
        return removeCartItem(member.getMemNo(), cartItemId);
    }

    /**
     * 장바구니 상품 삭제 (회원번호로)
     */
    public CartDto removeCartItem(Integer memNo, Long cartItemId) {
        // 장바구니 아이템 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartException("장바구니 상품을 찾을 수 없습니다."));
        
        // 권한 확인
        if (!cartItem.getCart().getMemNo().equals(memNo)) {
            throw new CartException("권한이 없습니다.");
        }
        
        cartItemRepository.delete(cartItem);
        
        return getCartByMemNo(memNo);
    }

    /**
     * 장바구니 전체 비우기 (회원 ID로)
     */
    public void clearCart(String memberId) {
        // memberId로 회원 조회
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new CartException("회원을 찾을 수 없습니다."));
        
        clearCart(member.getMemNo());
    }

    /**
     * 장바구니 전체 비우기 (회원번호로)
     */
    public void clearCart(Integer memNo) {
        Cart cart = cartRepository.findByMemNo(memNo)
                .orElseThrow(() -> new CartException("장바구니를 찾을 수 없습니다."));
        
        cartItemRepository.deleteByCartCartId(cart.getCartId());
    }

    /**
     * 장바구니 상품 개수 조회 (회원 ID로)
     */
    @Transactional(readOnly = true)
    public Integer getCartItemCount(String memberId) {
        // memberId로 회원 조회
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new CartException("회원을 찾을 수 없습니다."));
        
        return getCartItemCount(member.getMemNo());
    }

    /**
     * 장바구니 상품 개수 조회 (회원번호로)
     */
    @Transactional(readOnly = true)
    public Integer getCartItemCount(Integer memNo) {
        Optional<Cart> cartOpt = cartRepository.findByMemNo(memNo);
        
        if (cartOpt.isEmpty()) {
            return 0;
        }
        
        return cartItemRepository.countByCartId(cartOpt.get().getCartId());
    }

    /**
     * 장바구니 조회 또는 생성
     */
    private Cart getOrCreateCart(Integer memNo) {
        log.debug("장바구니 조회 또는 생성 - 회원번호: {}", memNo);
        
        Optional<Cart> existingCart = cartRepository.findByMemNo(memNo);
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            log.debug("기존 장바구니 발견 - cartId: {}", cart.getCartId());
            // cartItems가 null인 경우 초기화
            if (cart.getCartItems() == null) {
                cart.setCartItems(new ArrayList<>());
            }
            return cart;
        }
        
        // 새 장바구니 생성
        Cart newCart = new Cart();
        newCart.setMemNo(memNo);
        newCart.setCartItems(new ArrayList<>());
        Cart savedCart = cartRepository.save(newCart);
        log.debug("새 장바구니 생성 완료 - cartId: {}", savedCart.getCartId());
        
        return savedCart;
    }

    /**
     * Cart Entity를 CartDto로 변환
     */
    private CartDto convertToCartDto(Cart cart) {
        log.debug("CartDto 변환 시작 - cartId: {}", cart.getCartId());
        
        // cartItems null 체크 및 안전한 처리
        List<CartItem> cartItems = cart.getCartItems();
        log.debug("Cart.getCartItems() 결과: {}", cartItems != null ? cartItems.size() + "개" : "null");
        
        if (cartItems == null) {
            log.warn("cartItems가 null입니다. 빈 리스트로 초기화합니다.");
            cartItems = new ArrayList<>();
        }
        
        List<CartItemDto> cartItemDtos = cartItems.stream()
                .map(this::convertToCartItemDto)
                .collect(Collectors.toList());
        
        Integer totalItemCount = cartItemDtos.stream()
                .mapToInt(CartItemDto::getItemQty)
                .sum();
        
        Integer totalPrice = cartItemDtos.stream()
                .mapToInt(CartItemDto::getTotalItemPrice)
                .sum();
        
        log.debug("CartDto 변환 완료 - 총 아이템 수: {}, 총 가격: {}", totalItemCount, totalPrice);
        
        return CartDto.builder()
                .cartId(cart.getCartId())
                .memNo(cart.getMemNo())
                .createAt(cart.getCreateAt())
                .updateAt(cart.getUpdateAt())
                .cartItems(cartItemDtos)
                .totalItemCount(totalItemCount)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * CartItem Entity를 CartItemDto로 변환
     */
    private CartItemDto convertToCartItemDto(CartItem cartItem) {
        // 상품 정보 조회
        Optional<Goods> goodsOpt = goodsRepository.findById(cartItem.getGoodsNo().longValue());
        String goodsName = goodsOpt.map(Goods::getGName).orElse("상품명 없음");
        String goodsImage = goodsOpt.map(Goods::getGImg).orElse("");
        
        return CartItemDto.builder()
                .cartItemId(cartItem.getCartItemId())
                .goodsNo(cartItem.getGoodsNo())
                .goodsName(goodsName)
                .goodsImage(goodsImage)
                .itemQty(cartItem.getItemQty())
                .price(cartItem.getPrice())
                .totalItemPrice(cartItem.getItemQty() * cartItem.getPrice())
                .createAt(cartItem.getCreateAt())
                .updateAt(cartItem.getUpdateAt())
                .build();
    }
}
