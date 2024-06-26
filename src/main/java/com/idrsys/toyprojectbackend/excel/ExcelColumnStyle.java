package com.idrsys.toyprojectbackend.excel;


import com.idrsys.toyprojectbackend.excel.style.CustomExcelCellStyle;
import com.idrsys.toyprojectbackend.excel.style.DefaultExcelCellStyle;
import com.idrsys.toyprojectbackend.excel.style.ExcelCellStyle;
import com.idrsys.toyprojectbackend.excel.style.NoExcelCellStyle;
import org.apache.poi.ss.formula.functions.T;

public @interface ExcelColumnStyle {

	/**
	 * Specifies the class that implements {@link com.idrsys.toyprojectbackend.excel.style.ExcelCellStyle}.
	 * This can be an enum or any class that implements the interface.
	 * If an enum is not used, the {@code enumName} will be ignored.
	 *
	 * @return the class implementing ExcelCellStyle
	 * @see com.idrsys.toyprojectbackend.excel.style.DefaultExcelCellStyle
	 * @see com.idrsys.toyprojectbackend.excel.style.CustomExcelCellStyle
	 */
	Class<? extends ExcelCellStyle> excelCellStyleClass();

	/**
	 * name of Enum implements {@link com.idrsys.toyprojectbackend.excel.style.ExcelCellStyle}
	 * if not use Enum, enumName will be ignored
	 */
	String enumName() default "";

}
