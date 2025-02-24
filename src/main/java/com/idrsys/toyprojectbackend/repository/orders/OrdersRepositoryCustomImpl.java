package com.idrsys.toyprojectbackend.repository.orders;

import com.idrsys.toyprojectbackend.dto.goods.GoodsItemDto;
import com.idrsys.toyprojectbackend.dto.goods.GoodsSearchDto;
import com.idrsys.toyprojectbackend.dto.orders.OrderItemDto;
import com.idrsys.toyprojectbackend.dto.orders.SearchOrderDto;
import com.idrsys.toyprojectbackend.entity.*;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.idrsys.toyprojectbackend.entity.QOrders.orders;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrdersRepositoryCustomImpl implements OrdersRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Page<SearchOrderDto> ordersList(String id, Pageable pageable) {
        Member member = memberRepository.findById(id).orElse(null);

        List<Orders> ordersDtoList;

        if(member == null){
            return null;
        }else{
            ordersDtoList = jpaQueryFactory.select(orders)
                    .from(orders)
                    .where(orders.member.id.contains(member.getId()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(orders.ordDt.desc())
                    .fetch();
        }


        return new PageImpl<>(mapToSearchOrderDtoDtoList(ordersDtoList), pageable, mapToSearchOrderDtoDtoList(ordersDtoList).size());
    }

    @Override
    public List<SearchOrderDto> orderList(String id, Pageable pageable) {

        Member member = memberRepository.findById(id).orElse(null);

        List<Orders> ordersDtoList;

        if(member == null){
            return null;
        }else{
            ordersDtoList = jpaQueryFactory.select(orders)
                    .from(orders)
                    .where(orders.member.id.contains(member.getId()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(orders.ordDt.desc())
                    .fetch();
        }

        return mapToSearchOrderDtoDtoList(ordersDtoList);
    }

    private List<SearchOrderDto> mapToSearchOrderDtoDtoList(List<Orders> ordersList) {
        return ordersList.stream()
                .map(this::mapToOrdersDto)
                .collect(Collectors.toList());
    }


    // 현재 여기 member와 delivery에서 순환참조 일어나는 중
    private SearchOrderDto mapToOrdersDto(Orders orders) {
        return new SearchOrderDto(
                orders.getOrdNo(),
                orders.getOrdDt(),
                orders.getToPrc().longValueExact(),
                orders.getPayMn(),
                orders.getOrd_status_cd(),
                orders.getOrderItems(),
                orders.getDelivery()
        );
    }

    private List<OrderItemDto> mapToOrderItemDto(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(orderItem -> new OrderItemDto(
                        orderItem.getOrdItemCd(),
                        orderItem.getOrdQty(),
                        orderItem.getOrdPrc(),
                        mapToGoodsSearchDto(orderItem.getGoods_no()),
                        mapToGoodsItemDto(orderItem.getItem_no())
                ))
                .collect(Collectors.toList());
    }

    private GoodsSearchDto mapToGoodsSearchDto(Goods goods) {
        return new GoodsSearchDto(
                goods.getGNo(),
                goods.getGName(),
                goods.getBNo(),
                goods.getGPrice(),
                goods.getGImg(),
                goods.getOpt1(),
                goods.getOpt2(),
                goods.getCCd()
        );
    }
    private GoodsItemDto mapToGoodsItemDto(GoodsItem goodsItem) {
        return new GoodsItemDto(
                goodsItem.getNo(),
                goodsItem.getName(),
                goodsItem.getOptVal1(),
                goodsItem.getOptVal2()
        );
    }
//    private OrderStatusCodeDto mapToOrderStatusCodeDto(OrderStatusCode orderStatusCode) {
//        return new OrderStatusCodeDto(
//                orderStatusCode.getOrdCd(),
//                orderStatusCode.getOrdDef()
//        );
//    }

    @Override
    public Page<SearchOrderDto> ordersPage(String id, Pageable pageable){
        Member member = memberRepository.findById(id).orElse(null);

        List<SearchOrderDto> ordersList;

        if(member == null){
            return null;
        }else{
            ordersList = jpaQueryFactory.select(
                Projections.fields(SearchOrderDto.class,
                    orders.ordNo,
                    orders.ordDt,
                    orders.toPrc,
                    orders.payMn,
                    orders.ord_status_cd,
                     ExpressionUtils.as(
                            Projections.list(orders.orderItems), "orderItem")
                    ))
                .from(orders)
                .where(orders.member.id.contains(member.getId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        }


        return new PageImpl<>(ordersList, pageable, ordersList.size());
    }

    @Override
    public Long getMaxOrderNo(){
        Long getOrdNoMax = jpaQueryFactory.select(
                orders.ordNo.max().coalesce(0L).as("ordNo"))
                .from(orders)
                .fetchFirst();

        return getOrdNoMax + 1L;
    }

}
