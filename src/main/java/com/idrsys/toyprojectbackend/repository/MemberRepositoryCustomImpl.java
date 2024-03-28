package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.MemberDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.idrsys.toyprojectbackend.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MemberDto> memberOrdering(String id){
        List<MemberDto> rs = jpaQueryFactory.select(
                Projections.fields(MemberDto.class,
                        member.no
                        ,member.id
                        ,member.username
                        ,member.email
                        ,member.phone
                        ,member.deliveryList))
                .from(member)
                .where(member.id.contains(id))
                .fetch();
        return rs;
    }

//    @Override
//    public Long findNoById(String id){
//
//    }

}
