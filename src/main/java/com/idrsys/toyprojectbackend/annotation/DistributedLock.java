package com.idrsys.toyprojectbackend.annotation;

import com.idrsys.toyprojectbackend.enums.LockType;
import com.idrsys.toyprojectbackend.repository.orders.OrdersRepositoryCustom;
import com.idrsys.toyprojectbackend.service.OrderService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    LockType lockType() default LockType.DEFAULT;

    long waitTime() default 15L;

    long leaseTime() default 10L;

}
