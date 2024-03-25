package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.DeliveryDto;
import com.idrsys.toyprojectbackend.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryRepositoryCustom {
    Page<DeliveryDto> deliverySearch(Long id, Pageable pageable);
}
