package com.idrsys.toyprojectbackend;

import okhttp3.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentOrderTest {

    private static final String BASE_URL = "http://localhost:8080/orders/createOrder";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static final String[] memberIds = {"kjc0603", "hello", "JohD", "SmithJ", "MJohnson"};

    private static final int[] goodsIds = {1, 2, 2, 3, 4};
    private static final String[] optVal1Values =
            {"M", "32", "34", "S", ""};
    private static final String[] optVal2Values = {"레드", "블랙", "블루", "핑크", ""};
    private static final Random random = new Random();
    private static final Logger log = LoggerFactory.getLogger(ConcurrentOrderTest.class);
    private static final HashMap<Integer, Integer> goodsIdCount = new HashMap<>();

    public static void main(String[] args) {
        int numThreads = 3000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    sendCreateOrderRequest();
                } catch (IOException e) {
//                    e.printStackTrace();
                    log.error("주문을 생성하는데 오류가 발샐하였습니다 : {}", e.getMessage());
                }
            });
        }
        printGoodsIdCount();
        executor.shutdown();
    }

    private static void sendCreateOrderRequest() throws IOException {
        int randomIndex = random.nextInt(memberIds.length);
        String memberId = memberIds[randomIndex];

        int randomGoodsIdIndex = random.nextInt(goodsIds.length);
        int goodsId = goodsIds[randomGoodsIdIndex];

        String optVal1 = optVal1Values[randomGoodsIdIndex];
        String optVal2 = optVal2Values[randomGoodsIdIndex];

        goodsIdCount.put(goodsId, goodsIdCount.getOrDefault(randomGoodsIdIndex, 0) + 1);

        String json = "{\"memberId\": \"" + memberId + "\", \"goodsId\": "+goodsId+", \"optVal1\": \""+optVal1+"\", \"optVal2\": \""+optVal2+"\", \"dlivPlc\": \"서울특별시 마포구 만리재로 47\", \"zipCode\": \"04209\", \"detailAddress\": \"공덕코어빌딩 13층\", \"designation\": \"회사\", \"quantity\": 1, \"paymn\": \"CREDIT_CARD\", \"dlivFee\": 2500}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            System.out.println("Order created successfully for user: " + memberId +"at " + LocalDateTime.now());
        }
    }

    // Method to print goodsId count
    private static void printGoodsIdCount() {
        System.out.println("GoodsId Counts:");
        for (int goodsId : goodsIdCount.keySet()) {
            System.out.println("GoodsId: " + goodsId + ", Count: " + goodsIdCount.get(goodsId));
        }
    }
}


