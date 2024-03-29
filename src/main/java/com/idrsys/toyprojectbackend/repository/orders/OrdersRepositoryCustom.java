package com.idrsys.toyprojectbackend.repository.orders;

import com.idrsys.toyprojectbackend.dto.orders.SearchOrderDto;
import com.idrsys.toyprojectbackend.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersRepositoryCustom {

    Page<SearchOrderDto> ordersList(String id, Pageable pageable);

    Page<SearchOrderDto> ordersPage(String id, Pageable pageable);

//    Page<TestOrderDto> ordersList(String id, Pageable pageable);
}