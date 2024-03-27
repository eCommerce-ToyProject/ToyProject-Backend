package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.GoodsDto;
import com.idrsys.toyprojectbackend.dto.GoodsSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GoodsRepositoryCustom {
    Page<GoodsSearchDto> goodsSearch(String values, Pageable pageable);

    List<GoodsDto> goodsDetail(Long id);

//    List<GoodsDto> goodsDetailTest(Long id);
}
