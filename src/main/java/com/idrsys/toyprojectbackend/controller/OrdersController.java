package com.idrsys.toyprojectbackend.controller;

import com.idrsys.toyprojectbackend.dto.AddOrdersDto;
import com.idrsys.toyprojectbackend.dto.OrdersDto;
import com.idrsys.toyprojectbackend.dto.SearchOrderDto;
import com.idrsys.toyprojectbackend.entity.Orders;
import com.idrsys.toyprojectbackend.repository.OrdersRepository;
import com.idrsys.toyprojectbackend.repository.OrdersRepositoryCustom;
import com.idrsys.toyprojectbackend.service.CreateOrderWithDistributedLock;
import com.idrsys.toyprojectbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CreateOrderWithDistributedLock createOrderWithDistributedLock;

    @Autowired
    private OrdersRepositoryCustom ordersRepositoryCustom;

    @PostMapping("/createOrder")
    public Orders addOrder(@RequestBody AddOrdersDto addOrdersDto){
        return createOrderWithDistributedLock.createOrder(addOrdersDto);
    }
    @GetMapping("/orderList")
    public Page<SearchOrderDto> addOrder(@RequestParam(name = "no",required = false) Long no, Pageable pageable){
        return ordersRepositoryCustom.orders(no,pageable);
    }


}
