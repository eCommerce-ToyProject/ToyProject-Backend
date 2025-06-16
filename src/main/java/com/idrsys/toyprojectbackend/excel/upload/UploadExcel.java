package com.idrsys.toyprojectbackend.excel.upload;

import com.idrsys.toyprojectbackend.excel.ExcelColumn;
import com.idrsys.toyprojectbackend.excel.exception.NoExcelColumnAnnotationsException;
import com.idrsys.toyprojectbackend.excel.upload.celltype.CellValueSetter;
import com.idrsys.toyprojectbackend.excel.upload.celltype.LocalDateTimeCellValueSetter;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UploadExcel {
    private static final Map<Class<?>, CellValueSetter> CELL_VALUE_SETTERS = new HashMap<>();

    static {
        CELL_VALUE_SETTERS.put(String.class, (field, instance, cell) -> {
            field.set(instance, cell.getStringCellValue());
        });
        CELL_VALUE_SETTERS.put(int.class, (field, instance, cell) -> {
            field.set(instance, (int) cell.getNumericCellValue());
        });
        CELL_VALUE_SETTERS.put(Integer.class, (field, instance, cell) -> {
            field.set(instance, (int) cell.getNumericCellValue());
        });
        CELL_VALUE_SETTERS.put(boolean.class, (field, instance, cell) -> {
            field.set(instance, cell.getBooleanCellValue());
        });
        CELL_VALUE_SETTERS.put(Boolean.class, (field, instance, cell) -> {
            field.set(instance, cell.getBooleanCellValue());
        });
        CELL_VALUE_SETTERS.put(long.class, (field, instance, cell) -> {
            field.set(instance, (long) cell.getNumericCellValue());
        });
        CELL_VALUE_SETTERS.put(Long.class, (field, instance, cell) -> {
            field.set(instance, (long) cell.getNumericCellValue());
        });
        CELL_VALUE_SETTERS.put(double.class, (field, instance, cell) -> {
            field.set(instance, cell.getNumericCellValue());
        });
        CELL_VALUE_SETTERS.put(Double.class, (field, instance, cell) -> {
            field.set(instance, cell.getNumericCellValue());
        });
        CELL_VALUE_SETTERS.put(BigDecimal.class, (field, instance, cell) -> {
            field.set(instance, BigDecimal.valueOf(cell.getNumericCellValue()));
        });
        CELL_VALUE_SETTERS.put(LocalDateTime.class, new LocalDateTimeCellValueSetter());
        // ... 다른 타입에 대한 처리 추가
    }

    public static <T> List<T> readExcel(MultipartFile file, Class<T> type) throws IOException, ReflectiveOperationException {
        List<T> result = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Map<String, Integer> headerMap = getHeaderMap(sheet.getRow(0), type);

        try {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                T instance = type.getDeclaredConstructor().newInstance();
                for (Field field : type.getDeclaredFields()) {
                    if (field.isAnnotationPresent(ExcelColumn.class)) {
                        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                        String headerName = excelColumn.value();
                        Integer columnIndex = headerMap.get(headerName);
                        if (columnIndex != null) {
                            Cell cell = row.getCell(columnIndex);
                            field.setAccessible(true);
                            setFieldValue(field, instance, cell);
                        }
                    }
                }
                result.add(instance);
            }
        }catch (Exception e){
            throw new ReflectiveOperationException("엑셀 데이터 처리 중 오류 발생: " + e.getMessage());

        }

        workbook.close();
        inputStream.close();
        return result;
    }

    private static <T> Map<String, Integer> getHeaderMap(Row headerRow, Class<T> type) throws NoExcelColumnAnnotationsException {
        Map<String, Integer> headerMap = new HashMap<>();

        for (Cell cell : headerRow) {
            String headerName = cell.getStringCellValue();
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(ExcelColumn.class)) {
                    ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                    if (excelColumn.value().equals(headerName)) {
                        headerMap.put(headerName, cell.getColumnIndex());
                        break;
                    }
                }
            }
        }

        if (headerMap.isEmpty()) {
            throw new NoExcelColumnAnnotationsException(type.getSimpleName() + " DTO에 ExcelColumn 어노테이션이 존재하지 않습니다.");
        }

        return headerMap;
    }

    private static void setFieldValue(Field field, Object instance, Cell cell) throws IllegalAccessException {
        if (cell == null) {
            if (field.getType().isPrimitive()) {
                throw new IllegalArgumentException(field.getName() + " 필드는 primitive 타입으로 null을 할당할 수 없습니다.");
            }
            field.set(instance, null);
            return;
        }

        Class<?> fieldType = field.getType();

        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        String separator = excelColumn.separator();

        try {
            if (CELL_VALUE_SETTERS.containsKey(fieldType)) {
                CELL_VALUE_SETTERS.get(fieldType).setFieldValue(field, instance, cell);
            } else if (fieldType.isArray() && fieldType.getComponentType() == String.class && !separator.isEmpty()) {
                String cellValue = getCellValueAsString(cell);
                String[] values = cellValue.split(separator);
                field.set(instance, values);
            } else if (List.class.isAssignableFrom(fieldType) && !separator.isEmpty()) {
                String cellValue = getCellValueAsString(cell);
                String[] values = cellValue.split(separator);
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType parameterizedType) {
                    Type listType = parameterizedType.getActualTypeArguments()[0];
                    if (CELL_VALUE_SETTERS.containsKey(listType)) {
                        List<?> convertedList = Arrays.stream(values)
                                .map(value -> {
                                    try {
                                        // 리스트 타입에 맞는 변환 로직을 적용
                                        // 예: String -> Integer, String -> Long, etc.
                                        // CELL_VALUE_SETTERS.get(listType).setFieldValue(field, instance, cell); // 이렇게 직접 호출은 안됨
                                        if (listType == Integer.class) return Integer.parseInt(value);
                                        if (listType == Long.class) return Long.parseLong(value);
                                        if (listType == Double.class) return Double.parseDouble(value);
                                        if (listType == Boolean.class) return Boolean.parseBoolean(value);
                                        return value; // 기본적으로 String으로 처리
                                    } catch (NumberFormatException e) {
                                        return null; // 변환 실패 시 null 또는 예외 처리
                                    }
                                })
                                .collect(Collectors.toList());
                        field.set(instance, convertedList);
                    }
                }
            } else {
                throw new IllegalArgumentException(fieldType.getName() + " 타입은 지원하지 않습니다.");
            }
        } catch (Exception e) {
            throw new IllegalStateException(field.getName() + " 필드 값 변환 중 오류 발생: " + e.getMessage());
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }

        if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cellType == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cellType == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cellType == CellType.BLANK) {
            return "";
        } else if (cellType == CellType.ERROR) {
            return ""; // or throw 하기
        } else {
            return "";
        }
    }

}
