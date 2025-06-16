package com.idrsys.toyprojectbackend.excel.upload.celltype;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateTimeCellValueSetter implements CellValueSetter {
    @Override
    public void setFieldValue(Field field, Object instance, Cell cell) throws IllegalAccessException {
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            field.set(instance, date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        } else {
            field.set(instance, LocalDateTime.parse(cell.getStringCellValue()));
        }
    }
}
