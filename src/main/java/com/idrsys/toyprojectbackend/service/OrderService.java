package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.orders.AddOrdersDto;
import com.idrsys.toyprojectbackend.entity.*;
import com.idrsys.toyprojectbackend.repository.delivery.DeliveryRepository;
import com.idrsys.toyprojectbackend.repository.goods.GoodsItemRepository;
import com.idrsys.toyprojectbackend.repository.goods.GoodsRepository;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import com.idrsys.toyprojectbackend.repository.orders.OrderItemRepository;
import com.idrsys.toyprojectbackend.repository.orders.OrderStatusCodeRepository;
import com.idrsys.toyprojectbackend.repository.orders.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderStatusCodeRepository orderStatusCodeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsItemRepository goodsItemRepository;

    @Autowired
    private GoodsItemService goodsItemService;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional(rollbackOn = {Exception.class})
    public boolean createOrder(AddOrdersDto addOrdersDto) {
        try {
            Member member = getMemberById(addOrdersDto.getMemberId());
            Goods goods = getGoodsById(addOrdersDto.getGoodsId());
            GoodsItem item = getGoodsItemByOptions(goods, addOrdersDto.getOptVal1(), addOrdersDto.getOptVal2());
            Delivery delivery = getDelivery(addOrdersDto, member);
            if(delivery == null){
                delivery = createDelivery(addOrdersDto, member);
            }
            OrderStatusCode statusCode = getOrderStatusCode();

            updateGoodsItemQuantity(item, addOrdersDto.getQuantity());

            BigDecimal totalPrice = calculateTotalPrice(goods, item, addOrdersDto.getQuantity());

            Orders orders = buildOrders(member, delivery, statusCode, totalPrice, addOrdersDto.getPaymn());

            OrderItem orderItem = buildOrderItem(orders, goods, item, totalPrice, addOrdersDto.getQuantity());

            saveOrderAndOrderItem(orders, orderItem);

            return true;
        } catch (Exception e) {
            log.error("Error while creating order: {}", e.getMessage());
            return false;
        }
    }

    private Member getMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(memberId + " 아이디를 찾을 수 없습니다."));
    }

    private Goods getGoodsById(Long goodsId) {
        return goodsRepository.findById(goodsId)
                .orElseThrow(() -> new IllegalArgumentException(goodsId + " 상품 아이디를 찾을 수 없습니다."));
    }

    private GoodsItem getGoodsItemByOptions(Goods goods, String optVal1, String optVal2) {
        return goodsItemRepository.findByOptVal1AndOptVal2AndGoods(optVal1, optVal2, goods);
    }

    private Delivery getDelivery(AddOrdersDto addOrdersDto, Member member) {
        return deliveryRepository.findByDelPlcAndMemberAndZipCodeAndDetailAddressAndDesignation(
                addOrdersDto.getDelPlc(), member, addOrdersDto.getZipCode(),
                addOrdersDto.getDetailAddress(), addOrdersDto.getDesignation());
    }

    private Delivery createDelivery(AddOrdersDto addOrdersDto, Member member){
        Delivery buildDelivery = Delivery.builder()
                .delPlc(addOrdersDto.getDelPlc())
                .member(member)
                .zipCode(addOrdersDto.getZipCode())
                .detailAddress(addOrdersDto.getDetailAddress())
                .designation(addOrdersDto.getDesignation())
                .deleted(false)
                .build();

        return deliveryRepository.save(buildDelivery);
    }

    private OrderStatusCode getOrderStatusCode() {
        return orderStatusCodeRepository.findById("STATUS_PAYMENT_COMPLETED")
                .orElseThrow(() -> new IllegalArgumentException("Order status code not found"));
    }

    private void updateGoodsItemQuantity(GoodsItem item, Long quantity) {
        goodsItemService.updateQty(item, quantity);
    }

    private BigDecimal calculateTotalPrice(Goods goods, GoodsItem item, Long quantity) {
        BigDecimal itemPrice = goods.getGPrice().add(item.getIAmtAdd());
        return itemPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private Orders buildOrders(Member member, Delivery delivery, OrderStatusCode statusCode, BigDecimal totalPrice, String payMn) {
        return Orders.builder()
                .ordDt(LocalDateTime.now())
                .toPrc(totalPrice)
                .payMn(payMn)
                .member(member)
                .ord_status_cd(statusCode)
                .delivery(delivery)
                .build();
    }

    private OrderItem buildOrderItem(Orders orders, Goods goods, GoodsItem item, BigDecimal totalPrice, Long quantity) {
        return OrderItem.builder()
                .ordQty(quantity)
                .ordPrc(totalPrice)
                .ord_no(orders)
                .goods_no(goods)
                .item_no(item)
                .build();
    }

    private void saveOrderAndOrderItem(Orders orders, OrderItem orderItem) {
        ordersRepository.save(orders);
        orderItemRepository.save(orderItem);
    }

}
