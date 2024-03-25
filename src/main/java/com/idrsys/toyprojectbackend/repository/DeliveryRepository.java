package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
