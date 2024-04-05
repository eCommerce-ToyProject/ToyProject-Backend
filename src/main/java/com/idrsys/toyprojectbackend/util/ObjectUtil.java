package com.idrsys.toyprojectbackend.util;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ObjectUtil {

    public static <T> T NullCheckElseReturnObject(Optional<T> optionalT) {
        T t = optionalT.orElse(null);

        if(t == null) {
            throw new NullPointerException("%s 데이터가 존재하지 않습니다.".formatted(t.getClass()));
        }
        return t;
    }
    public static <T> T NullCheckElseReturnObject(Optional<T> optionalT, String message) {
        T t = optionalT.orElse(null);

        if(t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

}
