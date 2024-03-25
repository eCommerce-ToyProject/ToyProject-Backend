package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

}
