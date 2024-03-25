package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.DeliveryDto;
import com.idrsys.toyprojectbackend.dto.GoodsDto;
import com.idrsys.toyprojectbackend.dto.GoodsItemDto;
import com.idrsys.toyprojectbackend.dto.MemberDto;
import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.Goods;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.entity.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.idrsys.toyprojectbackend.entity.QDelivery.delivery;
import static com.idrsys.toyprojectbackend.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryCustomImpl implements DeliveryRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<DeliveryDto> deliverySearch(Long id, Pageable pageable){
        List<Delivery> rs = jpaQueryFactory.select(delivery)
                .from(delivery)
                .leftJoin(delivery.member, member)
                .fetchJoin()
                .where(delivery.member.no.eq(id))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DeliveryDto> deliveryDtos = rs.stream()
                .map(this::mapToDeliveryDto)
                .collect(Collectors.toList());

        return new PageImpl<>(deliveryDtos, pageable, deliveryDtos.size());
    }

    private List<DeliveryDto> mapToDeliveryDtoList(List<Delivery> deliveryList){
        return deliveryList.stream()
                .map(this::mapToDeliveryDto)
                .collect(Collectors.toList());
    }

    private DeliveryDto mapToDeliveryDto(Delivery delivery) {
        return new DeliveryDto(
                delivery.getDelNo(),
                delivery.getDelPlc(),
                MemberDto.toDto(delivery.getMember()),
                delivery.getZCode(),
                delivery.getDetailAddress(),
                delivery.getDesignation()
        );
    }


}
