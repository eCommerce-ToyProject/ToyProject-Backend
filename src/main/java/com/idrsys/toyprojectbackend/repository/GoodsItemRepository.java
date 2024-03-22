package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsItemRepository extends JpaRepository<Goods, Long> {
}
