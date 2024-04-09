package com.idrsys.toyprojectbackend;

import com.idrsys.toyprojectbackend.controller.orders.OrdersController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT30S")
@Transactional
@Rollback
public class ConcurrencyTest {

    @LocalServerPort
    private int port;

    private final Random random = new Random();

    @Autowired
    private WebTestClient webTestClient;
    private static final Logger log = LoggerFactory.getLogger(ConcurrencyTest.class);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    @Test
    @DisplayName("Concurrent Order API Test")
    public void concurrencyOrderTest() throws InterruptedException {
        int numThreads = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    sendCreateOrderRequest();
                    successCount.incrementAndGet();
                    log.info("Success count: {}", successCount.get());
                } catch (Exception e) {
                    log.error("주문을 생성하는데 오류가 발샐하였습니다 : {}", e.getMessage());
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }

    private void sendCreateOrderRequest() {
        String[] memberIds = {"kjc0603", "hello", "JohD", "SmithJ", "MJohnson"};
        int[] goodsIds = {1, 1, 2, 2, 3, 4, 5, 2, 2, 6, 6, 7, 7, 8, 8, 9, 10};
        String[] optVal1Values = {"M", "M", "32", "34", "S", "", "L", "32", "34", "100g", "200g", "R", "L", "300g", "200g", "200g", "5개"};
        String[] optVal2Values = {"레드", "블루", "블랙", "블루", "핑크", "", "", "블루", "블랙", "보통맛", "매운맛", "매운맛", "보통맛", "매운맛", "보통맛", "", ""};

        int randomGoodsIdIndex = random.nextInt(goodsIds.length);
        String memberId = memberIds[random.nextInt(memberIds.length)];
        int goodsId = goodsIds[randomGoodsIdIndex];
        String optVal1 = optVal1Values[randomGoodsIdIndex];
        String optVal2 = optVal2Values[randomGoodsIdIndex];

        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("memberId", memberId);
        requestBody.put("goodsId", goodsId);
        requestBody.put("optVal1", optVal1);
        requestBody.put("optVal2", optVal2);
        requestBody.put("dlivPlc", "서울특별시 용산구 회나무로12길 27");
        requestBody.put("zipCode", "04346");
        requestBody.put("detailAddress", "310호");
        requestBody.put("designation", "학교");
        requestBody.put("quantity", 1);
        requestBody.put("paymn", "CREDIT_CARD");
        requestBody.put("dlivFee", 2500);

        webTestClient.post()
                .uri("/orders/createOrder")
//                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJib2R5Ijoie1wiY3JlYXRlRGF0ZVRpbWVcIjpcIjIwMjQtMDQtMDJUMTk6MDE6MzNcIixcInVzZXJuYW1lXCI6XCJKb2huIERvZVwiLFwiaWRcIjpcIkpvaERcIn0iLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzEyNjI3OTA3fQ.9XoWOfc1IukTBJfQUfsEecWuaxwl4KA7Hrril47ItQo")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Order created successfully");
    }

    @AfterEach
    void printTestResults() {
        log.info("Total successful requests: {}", successCount.get());
        log.info("Total failed requests: {}", failureCount.get());
    }
}

