package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
