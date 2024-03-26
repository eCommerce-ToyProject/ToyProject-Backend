package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.OrdersDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersRepositoryCustom {

    Page<OrdersDto> orders(Long id, Pageable pageable);
}