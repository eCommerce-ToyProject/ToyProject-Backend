package com.idrsys.toyprojectbackend.controller.orders;

import com.idrsys.toyprojectbackend.dto.orders.AddOrdersDto;
import com.idrsys.toyprojectbackend.dto.orders.SearchOrderDto;
import com.idrsys.toyprojectbackend.entity.Orders;
import com.idrsys.toyprojectbackend.repository.orders.OrdersRepositoryCustom;
import com.idrsys.toyprojectbackend.service.OrderFacade;
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
    private OrderFacade orderFacade;

    @Autowired
    private OrdersRepositoryCustom ordersRepositoryCustom;

    @PostMapping("/createOrder")
    public boolean addOrder(@RequestBody AddOrdersDto addOrdersDto){
        if(addOrdersDto.getOptVal2().isBlank()){
            addOrdersDto.setOptVal2(null);
        }

        if(addOrdersDto.getOptVal1().isBlank()) {
            addOrdersDto.setOptVal1(null);
        }
        return orderFacade.CreateOrderWithDistributedLock(addOrdersDto);
    }
    @GetMapping("/myOrderList")
    public Page<SearchOrderDto> myOrder(@RequestParam(name = "id",required = false) String id, Pageable pageable){
        return ordersRepositoryCustom.ordersList(id,pageable);
    }

    @GetMapping("/myOrderTest")
    public Page<SearchOrderDto> myOrdertest(@RequestParam(name = "id",required = false) String id, Pageable pageable){
        return ordersRepositoryCustom.ordersPage(id,pageable);
    }

//    @GetMapping("/ordering")
//    public List<>


}
