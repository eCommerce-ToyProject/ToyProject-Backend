package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.GoodsDto;
import com.idrsys.toyprojectbackend.dto.GoodsItemDto;
import com.idrsys.toyprojectbackend.dto.OrderItemDto;
import com.idrsys.toyprojectbackend.dto.OrdersDto;
import com.idrsys.toyprojectbackend.entity.Goods;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.entity.OrderItem;
import com.idrsys.toyprojectbackend.entity.Orders;
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
    public Page<OrdersDto> orders(Long id, Pageable pageable) {
        List<Orders> ordersDtoList = jpaQueryFactory.select(orders)
                .from(orders)
                .where(orders.member.no.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Page<OrdersDto> PageImpl = new PageImpl<>(mapToGoodsDtoList(ordersDtoList), pageable, mapToGoodsDtoList(ordersDtoList).size());
        return PageImpl;
    }

    private List<OrdersDto> mapToGoodsDtoList(List<Orders> ordersList) {
        return ordersList.stream()
                .map(this::mapToOrdersDto)
                .collect(Collectors.toList());
    }

    
    // 현재 여기 member와 delivery에서 순환참조 일어나는 중
    private OrdersDto mapToOrdersDto(Orders orders) {
        return new OrdersDto(
                orders.getOrdNo(),
                orders.getOrdDt(),
                orders.getToPrc(),
                orders.getPayMn(),
                orders.getMember(),
                orders.getOrd_status_cd(),
                orders.getDelivery(),
                mapToOrderItemDto(orders.getOrderItems())
        );
    }

    private List<OrderItemDto> mapToOrderItemDto(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(orderItem -> new OrderItemDto(
                        orderItem.getOrdItemCd(),
                        orderItem.getOrdQty(),
                        orderItem.getOrdPrc(),
                        orderItem.getOrd_no(),
                        orderItem.getGoods_no(),
                        orderItem.getItem_no()
                ))
                .collect(Collectors.toList());
    }
}
