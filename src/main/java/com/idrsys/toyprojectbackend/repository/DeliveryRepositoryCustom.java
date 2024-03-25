package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.DeliveryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryRepositoryCustom {
    Page<DeliveryDto> deliverySearch(Long values, Pageable pageable);
}
