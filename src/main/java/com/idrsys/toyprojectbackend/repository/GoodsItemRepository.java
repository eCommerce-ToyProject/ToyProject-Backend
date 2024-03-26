package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.entity.Goods;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsItemRepository extends JpaRepository<GoodsItem, Long> {

    GoodsItem findByOptVal1AndOptVal2(String optVal1, String optVal2);

//    GoodsItem updateGoodsItemByNo
}
