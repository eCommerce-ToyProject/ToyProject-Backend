package com.idrsys.toyprojectbackend;

import com.idrsys.toyprojectbackend.controller.orders.OrdersController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
                } catch (Exception e) {
                    log.error("주문을 생성하는데 오류가 발샐하였습니다 : {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
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
        requestBody.put("dlivPlc", "서울특별시 마포구 만리재로 47");
        requestBody.put("zipCode", "04209");
        requestBody.put("detailAddress", "공덕코어빌딩 13층");
        requestBody.put("designation", "회사");
        requestBody.put("quantity", 1);
        requestBody.put("paymn", "CREDIT_CARD");
        requestBody.put("dlivFee", 2500);

        webTestClient.post()
                .uri("/orders/createOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Order created successfully");
    }
}

