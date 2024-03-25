package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.OrdersDto;
import org.springframework.data.domain.Page;

public interface OrdersRepositoryCustom {
    Page<OrdersDto> orders(String values);
}