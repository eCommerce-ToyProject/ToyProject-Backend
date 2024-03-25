package com.idrsys.toyprojectbackend.controller;

import com.idrsys.toyprojectbackend.repository.OrdersRepository;
import com.idrsys.toyprojectbackend.repository.OrdersRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;

public class OrdersController {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersRepositoryCustom ordersRepositoryCustom;
}
