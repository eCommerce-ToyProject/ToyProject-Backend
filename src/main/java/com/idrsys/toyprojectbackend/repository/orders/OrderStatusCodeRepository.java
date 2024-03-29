package com.idrsys.toyprojectbackend.repository.orders;

import com.idrsys.toyprojectbackend.entity.OrderStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusCodeRepository extends JpaRepository<OrderStatusCode, String> {
}
