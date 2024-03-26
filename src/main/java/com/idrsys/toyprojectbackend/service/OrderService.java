package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.AddOrdersDto;
import com.idrsys.toyprojectbackend.dto.OrderItemDto;
import com.idrsys.toyprojectbackend.dto.OrdersDto;
import com.idrsys.toyprojectbackend.entity.*;
import com.idrsys.toyprojectbackend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
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

    @Transactional
    public Orders createOrder(AddOrdersDto addOrdersDto) {

        Member member = memberRepository.findById(addOrdersDto.getMemberId()).orElseThrow(IllegalAccessError::new);
        Goods goods = goodsRepository.findById(addOrdersDto.getGoodsId()).orElseThrow(IllegalAccessError::new);
        GoodsItem item = goodsItemRepository.findByOptVal1AndOptVal2(addOrdersDto.getOptVal1(), addOrdersDto.getOptVal2());
        Delivery delivery = deliveryRepository.findById(addOrdersDto.getDelNo()).orElseThrow(IllegalAccessError::new);
        OrderStatusCode statusCode = orderStatusCodeRepository.findById("STATUS_PAYMENT_COMPLETED").orElseThrow(IllegalAccessError::new);

        goodsItemService.updateQty(item, addOrdersDto.getQuantity());

        BigDecimal totalPrice = goods.getGPrice().add(item.getIAmtAdd()).multiply(BigDecimal.valueOf(addOrdersDto.getQuantity()));

        Orders orders = Orders.builder()
                .ordDt(new java.sql.Date(System.currentTimeMillis()))
                .toPrc(totalPrice)
                .payMn(addOrdersDto.getPaymn())
                .member(member)
                .ord_status_cd(statusCode)
                .delivery(delivery)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .ordQty(addOrdersDto.getQuantity())
                .ordPrc(totalPrice)
                .ord_no(orders)
                .goods_no(goods)
                .item_no(item)
                .build();

        Orders saveOrder = ordersRepository.save(orders);
        orderItemRepository.save(orderItem);
        return saveOrder;
    }
}
