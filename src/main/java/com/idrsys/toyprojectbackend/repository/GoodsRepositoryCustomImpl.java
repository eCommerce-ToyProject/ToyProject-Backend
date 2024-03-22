package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.GoodsDto;
import com.idrsys.toyprojectbackend.dto.GoodsItemDto;
import com.idrsys.toyprojectbackend.dto.GoodsSearchDto;
import com.idrsys.toyprojectbackend.entity.Goods;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.entity.QGoodsItem;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.idrsys.toyprojectbackend.entity.QBrand.brand;
import static com.idrsys.toyprojectbackend.entity.QCategory.category;
import static com.idrsys.toyprojectbackend.entity.QGoods.goods;
import static com.idrsys.toyprojectbackend.entity.QGoodsItem.goodsItem;


@Repository
@RequiredArgsConstructor
public class GoodsRepositoryCustomImpl implements GoodsRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<GoodsSearchDto> goodsSearch(String values, Pageable pageable){
        List<GoodsSearchDto> rs =jpaQueryFactory.select(
                Projections.fields(GoodsSearchDto.class,
                        goods.gNo
                        ,goods.gName
                        ,goods.bNo
                        ,brand.bName
                        ,goods.gImg
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
    @Override
    public List<GoodsDto> goodsDetail(Long id){


        List<Goods> goodsList = jpaQueryFactory
                .select(goods)
                .from(goods)
                .leftJoin(goods.goodsItem, goodsItem).fetchJoin()
                .where(goods.gNo.eq(id))
                .fetch();

        return mapToGoodsDtoList(goodsList);

    }

    private List<GoodsDto> mapToGoodsDtoList(List<Goods> goodsList) {
        return goodsList.stream()
                .map(this::mapToGoodsDto)
                .collect(Collectors.toList());
    }

    private GoodsDto mapToGoodsDto(Goods goods) {
        return new GoodsDto(
                goods.getGNo(),
                goods.getGName(),
                goods.getBNo(),
                goods.getGImg(),
                goods.getGPrice(),
                goods.getOpt1(),
                goods.getOpt2(),
                goods.getCCd(),
                mapToGoodsItemDtoList(goods.getGoodsItem())
        );
    }

    private List<GoodsItemDto> mapToGoodsItemDtoList(List<GoodsItem> goodsItemList) {
        return goodsItemList.stream()
                .map(goodsItem -> new GoodsItemDto(
                        goodsItem.getNo(),
                        goodsItem.getName(),
                        goodsItem.getOptVal1(),
                        goodsItem.getOptVal2(),
                        goodsItem.getIQty(),
                        goodsItem.getIAmtAdd(),
                        goodsItem.getISaveQty()
                ))
                .collect(Collectors.toList());
    }

}
