package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.annotation.DistributedLock;
import com.idrsys.toyprojectbackend.dto.orders.AddOrdersDto;
import com.idrsys.toyprojectbackend.enums.LockType;
import com.idrsys.toyprojectbackend.repository.orders.OrdersRepositoryCustom;
import com.idrsys.toyprojectbackend.util.AopForTransaction;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class OrderFacade {
    private static final String LOCK_PREFIX = "LOCK ";

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrdersRepositoryCustom ordersRepositoryCustom;

    private final AopForTransaction aopForTransaction;

    private final RedissonClient redissonClient;
    @Pointcut("@annotation(com.idrsys.toyprojectbackend.annotation.DistributedLock)")
    private void distributedLock(){

    }

    // 트랜잭션이 커밋 후 락 해제
    // 커밋 전에 해제 시 갱신손실 발생 및 데드락 발생 위험
    @Around("distributedLock()")
    public Object executeWithDistributedLock(final ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String lockKey = LOCK_PREFIX + getLockKey(joinPoint, distributedLock);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), TimeUnit.SECONDS);
            log.info("{} - lock 획득 시도", lockKey);
            if (isLocked) {
                log.info("{} - lock 획득 성공", lockKey);
                return aopForTransaction.proceed(joinPoint);
            } else {
                throw new RuntimeException("Failed to acquire lock for the operation.");
            }
        } catch (InterruptedException e) {
            log.error("{} - lock 획득 실패", lockKey);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while trying to acquire lock.", e);
        } finally {
            try {
                lock.unlock();
                log.info("{} - lock 해제", lockKey);
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock { serviceName: "+ method.getName() +"} { key: "+lockKey+"}");
            }
        }
    }

    private String getLockKey(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        switch (distributedLock.lockType()) {
            case ORDER:
                return "ORDER"; /** + ordersRepositoryCustom.getMaxOrderNo(); **/
            default:
                throw new IllegalArgumentException("Invalid lock type: " + distributedLock.lockType());
        }
    }

}
