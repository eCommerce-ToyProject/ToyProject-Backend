package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.delivery.AddDeliveryDto;
import com.idrsys.toyprojectbackend.dto.orders.AddOrdersDto;
import com.idrsys.toyprojectbackend.entity.*;
import com.idrsys.toyprojectbackend.repository.delivery.DeliveryRepository;
import com.idrsys.toyprojectbackend.repository.goods.GoodsItemRepository;
import com.idrsys.toyprojectbackend.repository.goods.GoodsRepository;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import com.idrsys.toyprojectbackend.repository.orders.OrderItemRepository;
import com.idrsys.toyprojectbackend.repository.orders.OrderStatusCodeRepository;
import com.idrsys.toyprojectbackend.repository.orders.OrdersRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    private DeliveryService deliveryService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = {Exception.class})
    public boolean createOrder(AddOrdersDto addOrdersDto) {
        try {
            Member member = getMemberById(addOrdersDto.getMemberId());
            Goods goods = getGoodsById(addOrdersDto.getGoodsId());
            GoodsItem item = getGoodsItemByOptions(goods, addOrdersDto.getOptVal1(), addOrdersDto.getOptVal2());
            Delivery delivery = getDelivery(addOrdersDto, member);
            if(delivery == null){
                delivery = deliveryService.createDeliveryWithOrder(buildAddDeliveryDto(addOrdersDto, member));
            }
            OrderStatusCode statusCode = getOrderStatusCode();

            updateGoodsItemQuantity(item, addOrdersDto.getQuantity());

            BigDecimal totalPrice = calculateTotalPrice(goods, item, addOrdersDto.getQuantity(), addOrdersDto);

            BigDecimal orderPrice = calculateOrderPrice(goods, item, addOrdersDto.getQuantity());

            Orders orders = buildOrders(member, delivery, statusCode, totalPrice, addOrdersDto.getPaymn(), item, goods, addOrdersDto);

            OrderItem orderItem = buildOrderItem(orders, goods, item, orderPrice, addOrdersDto.getQuantity());

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
        return deliveryRepository.findByDlivPlcAndMemberAndZipCodeAndDetailAddressAndDesignation(
                addOrdersDto.getDlivPlc(), member, addOrdersDto.getZipCode(),
                addOrdersDto.getDetailAddress(), addOrdersDto.getDesignation());
    }

    private AddDeliveryDto buildAddDeliveryDto(AddOrdersDto addOrdersDto, Member member){

        return AddDeliveryDto.builder()
                .dlivPlc(addOrdersDto.getDlivPlc())
                .memberId(member.getId())
                .zipCode(addOrdersDto.getZipCode())
                .detailAddress(addOrdersDto.getDetailAddress())
                .designation(addOrdersDto.getDesignation())
                .build();
    }

    private OrderStatusCode getOrderStatusCode() {
        return orderStatusCodeRepository.findById("STATUS_PAYMENT_COMPLETED")
                .orElseThrow(() -> new IllegalArgumentException("Order status code not found"));
    }


    private void updateGoodsItemQuantity(GoodsItem item, Long quantity) {
        goodsItemService.updateQty(item, quantity);
    }

    private BigDecimal calculateTotalPrice(Goods goods, GoodsItem item, Long quantity, AddOrdersDto addOrdersDto) {
        BigDecimal itemPrice = goods.getGPrice().add(item.getIAmtAdd()).add(addOrdersDto.getDlivFee());
        return itemPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private BigDecimal calculateOrderPrice(Goods goods, GoodsItem item, Long quantity) {
        BigDecimal itemPrice = goods.getGPrice().add(item.getIAmtAdd());
        return itemPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private Orders buildOrders(Member member, Delivery delivery, OrderStatusCode statusCode, BigDecimal totalPrice, String payMn, GoodsItem item, Goods goods, AddOrdersDto addOrdersDto) {
        return Orders.builder()
                .ordDt(LocalDateTime.now())
                .toPrc(totalPrice)
                .payMn(payMn)
                .member(member)
                .ord_status_cd(statusCode)
                .delivery(delivery)
                .memId(member.getId())
                .zipCode(delivery.getZipCode())
                .detailAddress(delivery.getDetailAddress())
                .goodsPrc(goods.getGPrice())
                .iAmtAdd(item.getIAmtAdd())
                .dlivFee(addOrdersDto.getDlivFee())
                .build();
    }

    private OrderItem buildOrderItem(Orders orders, Goods goods, GoodsItem item, BigDecimal orderPrice, Long quantity) {
        return OrderItem.builder()
                .ordQty(quantity)
                .ordPrc(orderPrice)
                .ord_no(orders)
                .goods_no(goods)
                .gNm(goods.getGName())
                .bNo(goods.getBNo())
                .catCd(goods.getCCd())
                .item_no(item)
                .iNm(item.getName())
                .optVal1(item.getOptVal1())
                .optVal2(item.getOptVal2())
                .iAmtAdd(item.getIAmtAdd())
                .build();
    }

    private void saveOrderAndOrderItem(Orders orders, OrderItem orderItem) {
        ordersRepository.save(orders);
        orderItemRepository.save(orderItem);
    }

}
