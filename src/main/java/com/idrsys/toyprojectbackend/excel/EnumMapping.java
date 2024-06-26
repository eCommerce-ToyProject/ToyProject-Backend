package com.idrsys.toyprojectbackend.excel;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumMapping {
    Class<?> enumClass();
}
