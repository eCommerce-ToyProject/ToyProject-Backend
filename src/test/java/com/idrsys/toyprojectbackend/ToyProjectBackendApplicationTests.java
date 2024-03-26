package com.idrsys.toyprojectbackend;

import com.idrsys.toyprojectbackend.dto.AddOrdersDto;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.repository.GoodsItemRepository;
import com.idrsys.toyprojectbackend.service.CreateOrderWithDistributedLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ToyProjectBackendApplicationTests {

    @Test
    void contextLoads() {
    }



}
