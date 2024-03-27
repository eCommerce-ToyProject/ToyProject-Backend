package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.*;
import com.idrsys.toyprojectbackend.entity.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.idrsys.toyprojectbackend.entity.QOrders.orders;

@Repository
@RequiredArgsConstructor
public class OrdersRepositoryCustomImpl implements OrdersRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<SearchOrderDto> orders(String id, Pageable pageable) {
        List<Orders> ordersDtoList = jpaQueryFactory.select(orders)
                .from(orders)
                .where(orders.member.id.contains(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Page<SearchOrderDto> PageImpl = new PageImpl<>(mapToSearchOrderDtoDtoList(ordersDtoList), pageable, mapToSearchOrderDtoDtoList(ordersDtoList).size());
        return PageImpl;
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
                orders.getToPrc(),
                orders.getPayMn(),
                mapToOrderStatusCodeDto(orders.getOrd_status_cd()),
                mapToOrderItemDto(orders.getOrderItems())
        );
    }

    private List<OrderItemDto> mapToOrderItemDto(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(orderItem -> new OrderItemDto(
                        orderItem.getOrdItemCd(),
                        orderItem.getOrdQty(),
                        orderItem.getOrdPrc(),
                        mapToGoodsSearchDto(orderItem.getGoods_no()),
                        mapToDeliveryDto(orderItem.getItem_no())
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
    private GoodsItemDto mapToDeliveryDto(GoodsItem goodsItem) {
        return new GoodsItemDto(
                goodsItem.getNo(),
                goodsItem.getName(),
                goodsItem.getOptVal1(),
                goodsItem.getOptVal2()
        );
    }
    private OrderStatusCodeDto mapToOrderStatusCodeDto(OrderStatusCode orderStatusCode) {
        return new OrderStatusCodeDto(
                orderStatusCode.getOrdCd(),
                orderStatusCode.getOrdDef()
        );
    }

}
