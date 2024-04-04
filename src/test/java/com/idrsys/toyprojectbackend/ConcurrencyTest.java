package com.idrsys.toyprojectbackend;

import com.idrsys.toyprojectbackend.dto.orders.AddOrdersDto;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.repository.goods.GoodsItemRepository;
import com.idrsys.toyprojectbackend.service.OrderFacade;
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
    private OrderFacade orderFacade;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsItemRepository goodsItemRepository;

//    @Test
//    void 동시에_같은_물품_100개구매() throws InterruptedException {
//        AddOrdersDto addOrdersDto = new AddOrdersDto("hello",5L,"L", null, 1L, "CREDIT_CARD", "서울 광진구 광장로1길 1", "04966", "광장중학교 1층 교무실", "학교");
//        ExecutorService executorService = Executors.newFixedThreadPool(100);
//        CountDownLatch countDownLatch = new CountDownLatch(100);
//
//        for (int i = 0; i < 100; i++) {
//            executorService.submit(() -> {
//                try {
//                    orderFacade.CreateOrderWithDistributedLock(addOrdersDto);
//                } finally {
//                    countDownLatch.countDown();
//                }
//            });
//        }
//
//        countDownLatch.await();
//        GoodsItem actual = goodsItemRepository.findByOptVal1AndOptVal2(addOrdersDto.getOptVal1(), addOrdersDto.getOptVal2());
//        assertThat(actual.getISaveQty()).isZero();
//    }
//    @Test
//    void 동시에_같은_물품_100개구매_lock사용_안함() throws InterruptedException {
//        AddOrdersDto addOrdersDto = new AddOrdersDto(2L,3L,"S", "핑크", 1L, "CREDIT_CARD", 103L);
//        ExecutorService executorService = Executors.newFixedThreadPool(100);
//        CountDownLatch countDownLatch = new CountDownLatch(100);
//
//        for (int i = 0; i < 100; i++) {
//            executorService.submit(() -> {
//                try {
//                    orderService.createOrder(addOrdersDto);
//                } finally {
//                    countDownLatch.countDown();
//                }
//            });
//        }
//
//        countDownLatch.await();
//        GoodsItem actual = goodsItemRepository.findByOptVal1AndOptVal2(addOrdersDto.getOptVal1(), addOrdersDto.getOptVal2());
//        assertThat(actual.getIQty()).isZero();
//    }
}
