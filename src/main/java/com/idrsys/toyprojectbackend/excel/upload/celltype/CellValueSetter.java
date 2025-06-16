package com.idrsys.toyprojectbackend.excel.upload.celltype;

import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;

@FunctionalInterface
public interface CellValueSetter {
    void setFieldValue(Field field, Object instance, Cell cell) throws IllegalAccessException;
}
