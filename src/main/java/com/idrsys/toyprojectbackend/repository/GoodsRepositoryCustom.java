package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.GoodsDto;
import com.idrsys.toyprojectbackend.dto.GoodsSearchDto;
import com.idrsys.toyprojectbackend.entity.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GoodsRepositoryCustom {
    Page<GoodsSearchDto> goodsSearch(String values, Pageable pageable);

//    Page<GoodsDto> goodsDetail(Long id, Pageable pageable);

    List<GoodsDto> goodsDetail(Long id);
}
