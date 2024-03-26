package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.AddOrdersDto;
import com.idrsys.toyprojectbackend.entity.Orders;
import com.idrsys.toyprojectbackend.repository.OrdersRepository;
import jakarta.annotation.PostConstruct;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class CreateOrderWithDistributedLock {

    @Autowired
    private RedissonClient redissonClient;

    private static final String ORDER_LOCK_PREFIX = "order-lock:";

    private static final long LOCK_TIMEOUT = 30;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderService orderService;

    private Redisson redisson;

    @PostConstruct
    public void init() {
        redisson = (Redisson) redissonClient;
    }

    public Orders createOrder(AddOrdersDto addOrdersDto) {
        String orderLockKey = ORDER_LOCK_PREFIX + addOrdersDto.getMemberId() + addOrdersDto.getOptVal1() +addOrdersDto.getOptVal2(); /*order.getOrdNo();*/
        RLock lock = redisson.getLock(orderLockKey);
        try {
            boolean isLocked = lock.tryLock(20, LOCK_TIMEOUT, TimeUnit.SECONDS);
            if (isLocked) {
                 return orderService.createOrder(addOrdersDto);
            } else {
                throw new RuntimeException("Failed to acquire lock for order processing.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to acquire lock due to interruption.", e);
        } finally {
            lock.unlock();
        }
    }
}
