package com.idrsys.toyprojectbackend.controller.memebr;

import com.idrsys.toyprojectbackend.dto.orders.SearchOrderDto;
import com.idrsys.toyprojectbackend.repository.orders.OrdersRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/myinfo")
public class MyInfoController {

    @Autowired
    private OrdersRepositoryCustom ordersRepositoryCustom;


    @GetMapping("/orderlist")
    public Page<SearchOrderDto> myOrder(@RequestParam(name = "id",required = false) String id, Pageable pageable){
        return ordersRepositoryCustom.ordersList(id,pageable);
    }

}
