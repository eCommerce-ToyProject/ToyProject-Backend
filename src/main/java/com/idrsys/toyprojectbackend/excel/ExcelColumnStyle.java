package com.idrsys.toyprojectbackend.excel;


import com.idrsys.toyprojectbackend.excel.style.NoExcelCellStyle;

public @interface ExcelColumnStyle {

	/**
     * Enum implements {@link com.idrsys.toyprojectbackend.excel.style.ExcelCellStyle}
     * Also, can use just class.
     * If not use Enum, enumName will be ignored
     *
     * @see com.idrsys.toyprojectbackend.excel.style.DefaultExcelCellStyle
     * @see com.idrsys.toyprojectbackend.excel.style.CustomExcelCellStyle
     */
	Class<NoExcelCellStyle> excelCellStyleClass();

	/**
	 * name of Enum implements {@link com.idrsys.toyprojectbackend.excel.style.ExcelCellStyle}
	 * if not use Enum, enumName will be ignored
	 */
	String enumName() default "";

}
