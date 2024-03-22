package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.GoodsItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoodsItemRepositoryCustom {
    Page<GoodsItemDto> goodsItemDtos(Long no, Pageable pageable);
}
