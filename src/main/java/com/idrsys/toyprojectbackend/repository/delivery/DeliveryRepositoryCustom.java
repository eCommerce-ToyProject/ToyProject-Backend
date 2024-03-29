package com.idrsys.toyprojectbackend.repository.delivery;

import com.idrsys.toyprojectbackend.dto.delivery.DeliveryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryRepositoryCustom {
    Page<DeliveryDto> deliverySearch(String id, Pageable pageable);
}
