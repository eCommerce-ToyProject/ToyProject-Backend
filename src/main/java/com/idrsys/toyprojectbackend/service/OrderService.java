package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.AddOrdersDto;
import com.idrsys.toyprojectbackend.dto.OrderItemDto;
import com.idrsys.toyprojectbackend.dto.OrdersDto;
import com.idrsys.toyprojectbackend.entity.*;
import com.idrsys.toyprojectbackend.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
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
    public boolean createOrder(AddOrdersDto addOrdersDto) {

        if(addOrdersDto.getOptVal2().isBlank()){
            addOrdersDto.setOptVal2(null);
        }

        if(addOrdersDto.getOptVal1().isBlank()){
            addOrdersDto.setOptVal1(null);
        }

        Member member = memberRepository.findById(addOrdersDto.getMemberId()).orElseThrow(IllegalAccessError::new);
        Goods goods = goodsRepository.findById(addOrdersDto.getGoodsId()).orElseThrow(IllegalAccessError::new);
        GoodsItem item = goodsItemRepository.findByOptVal1AndOptVal2AndGoods(addOrdersDto.getOptVal1(), addOrdersDto.getOptVal2(), goods);
        Delivery delivery = deliveryRepository.findByDelPlcAndMemberAndZipCodeAndDetailAddressAndDesignation(addOrdersDto.getDelPlc(), member, addOrdersDto.getZipCode(), addOrdersDto.getDetailAddress(), addOrdersDto.getDesignation());
        OrderStatusCode statusCode = orderStatusCodeRepository.findById("STATUS_PAYMENT_COMPLETED").orElseThrow(IllegalAccessError::new);


        if(delivery == null){
            Delivery buildDelivery = Delivery.builder()
                    .delPlc(addOrdersDto.getDelPlc())
                    .member(member)
                    .zipCode(addOrdersDto.getZipCode())
                    .detailAddress(addOrdersDto.getDetailAddress())
                    .designation(addOrdersDto.getDesignation())
                    .build();

            try{
                delivery = deliveryRepository.save(buildDelivery);
            }catch (DataAccessException e){
                log.info("error : "+e);
                return false;
            }
        }

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



        try {
            ordersRepository.save(orders);
            orderItemRepository.save(orderItem);
            return true;
        }catch (Exception e){
            log.info("error : "+e);
            return false;
        }
    }
}
