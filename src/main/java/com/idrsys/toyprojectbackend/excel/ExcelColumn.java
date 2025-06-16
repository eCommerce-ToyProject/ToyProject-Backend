package com.idrsys.toyprojectbackend.excel;

import com.idrsys.toyprojectbackend.excel.style.NoExcelCellStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

	String value() default "";
	String separator() default "";

	ExcelColumnStyle headerStyle() default @ExcelColumnStyle(excelCellStyleClass = NoExcelCellStyle.class);
	ExcelColumnStyle bodyStyle() default @ExcelColumnStyle(excelCellStyleClass = NoExcelCellStyle.class);

}
