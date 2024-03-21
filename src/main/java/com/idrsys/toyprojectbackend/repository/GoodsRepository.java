package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.entity.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods, String> {

}
