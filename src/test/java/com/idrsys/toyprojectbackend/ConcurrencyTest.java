package com.idrsys.toyprojectbackend;

import com.idrsys.toyprojectbackend.dto.AddOrdersDto;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.repository.GoodsItemRepository;
import com.idrsys.toyprojectbackend.service.CreateOrderWithDistributedLock;
import com.idrsys.toyprojectbackend.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private CreateOrderWithDistributedLock createOrderWithDistributedLock;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsItemRepository goodsItemRepository;

    @Test
    void 동시에_같은_물품_100개구매() throws InterruptedException {
        AddOrdersDto addOrdersDto = new AddOrdersDto(2L,3L,"S", "핑크", 1L, "CREDIT_CARD", 103L);
        ExecutorService executorService = Executors.newFixedThreadPool(500);
        CountDownLatch countDownLatch = new CountDownLatch(500);

        for (int i = 0; i < 500; i++) {
            executorService.submit(() -> {
                try {
                    createOrderWithDistributedLock.createOrder(addOrdersDto);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        GoodsItem actual = goodsItemRepository.findByOptVal1AndOptVal2(addOrdersDto.getOptVal1(), addOrdersDto.getOptVal2());
        assertThat(actual.getISaveQty()).isZero();
    }
    @Test
    void 동시에_같은_물품_100개구매_lock사용_안함() throws InterruptedException {
        AddOrdersDto addOrdersDto = new AddOrdersDto(2L,3L,"S", "핑크", 1L, "CREDIT_CARD", 103L);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    orderService.createOrder(addOrdersDto);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        GoodsItem actual = goodsItemRepository.findByOptVal1AndOptVal2(addOrdersDto.getOptVal1(), addOrdersDto.getOptVal2());
        assertThat(actual.getIQty()).isZero();
    }
}
