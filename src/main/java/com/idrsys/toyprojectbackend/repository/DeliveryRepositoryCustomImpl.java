package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.DeliveryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.idrsys.toyprojectbackend.entity.QDelivery.delivery;
import static com.idrsys.toyprojectbackend.entity.QMember.member;

public class DeliveryRepositoryCustomImpl implements DeliveryRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<DeliveryDto> deliverySearch(Long values, Pageable pageable){
        List<DeliveryDto> rs = jpaQueryFactory.select(
                Projections.fields(DeliveryDto.class,
                        delivery.delNo
                        ,delivery.delPlc
                        ,delivery.member
                        ,member.no))
                .from(delivery)
                .leftJoin(member).on(delivery.member.no.eq(member.no))
                .where(delivery.member.no.eq(values))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(rs, pageable, rs.size());
    }
}
