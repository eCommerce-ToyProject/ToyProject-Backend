package com.idrsys.toyprojectbackend.repository.impl;

import com.idrsys.toyprojectbackend.dto.GoodsSearchDto;
import com.idrsys.toyprojectbackend.repository.GoodsRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.idrsys.toyprojectbackend.entity.QBrand.brand;
import static com.idrsys.toyprojectbackend.entity.QCategory.category;
import static com.idrsys.toyprojectbackend.entity.QGoods.goods;

@Repository
@RequiredArgsConstructor
public class GoodsCustomRepositoryImpl implements GoodsRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<GoodsSearchDto> goodsSearch(String values, Pageable pageable){
        List<GoodsSearchDto> rs =jpaQueryFactory.select(
                Projections.fields(GoodsSearchDto.class,
                        goods.gNo
                        ,goods.gName
                        ,goods.bNo
                        ,brand.bName
                        ,goods.cCd
                        ,category.cName
                        ,goods.opt1
                        ,goods.opt2
                        ,goods.gPrice))
                .from(goods)
                .leftJoin(brand).on(goods.bNo.eq(brand.no))
                .leftJoin(category).on(goods.cCd.eq(category.cd))
                .where(goods.gName.contains(values)
                        .or(brand.bName.contains(values))
                        .or(category.cName.contains(values)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(rs, pageable, rs.size());
    }
}
