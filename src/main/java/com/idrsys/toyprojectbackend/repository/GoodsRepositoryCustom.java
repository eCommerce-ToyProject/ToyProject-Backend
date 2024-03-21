package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.GoodsSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoodsRepositoryCustom {
    Page<GoodsSearchDto> goodsSearch(String values, Pageable pageable);
}
