package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.OrdersDto;
import com.idrsys.toyprojectbackend.dto.SearchOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersRepositoryCustom {

    Page<SearchOrderDto> orders(Long id, Pageable pageable);
}