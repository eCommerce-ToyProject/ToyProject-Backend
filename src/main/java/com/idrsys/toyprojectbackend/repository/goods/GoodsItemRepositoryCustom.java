package com.idrsys.toyprojectbackend.repository.goods;

import com.idrsys.toyprojectbackend.dto.goods.GoodsItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoodsItemRepositoryCustom {
    Page<GoodsItemDto> goodsItemDtos(Long no, Pageable pageable);
}
