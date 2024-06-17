package com.idrsys.toyprojectbackend.excel.style.configurer;

import com.idrsys.toyprojectbackend.excel.style.align.ExcelAlign;
import com.idrsys.toyprojectbackend.excel.style.align.NoExcelAlign;
import com.idrsys.toyprojectbackend.excel.style.border.ExcelBorders;
import com.idrsys.toyprojectbackend.excel.style.border.NoExcelBorders;
import com.idrsys.toyprojectbackend.excel.style.color.DefaultExcelColor;
import com.idrsys.toyprojectbackend.excel.style.color.ExcelColor;
import com.idrsys.toyprojectbackend.excel.style.color.NoExcelColor;
import org.apache.poi.ss.usermodel.CellStyle;

public class ExcelCellStyleConfigurer {

	private ExcelAlign excelAlign = new NoExcelAlign();
	private ExcelColor foregroundColor = new NoExcelColor();
	private ExcelBorders excelBorders = new NoExcelBorders();

	public ExcelCellStyleConfigurer() {

	}

	public ExcelCellStyleConfigurer excelAlign(ExcelAlign excelAlign) {
		this.excelAlign = excelAlign;
		return this;
	}

	public ExcelCellStyleConfigurer foregroundColor(int red, int blue, int green) {
		this.foregroundColor = DefaultExcelColor.rgb(red, blue, green);
		return this;
	}

	public ExcelCellStyleConfigurer excelBorders(ExcelBorders excelBorders) {
		this.excelBorders = excelBorders;
		return this;
	}

	public void configure(CellStyle cellStyle) {
		excelAlign.apply(cellStyle);
		foregroundColor.applyForeground(cellStyle);
		excelBorders.apply(cellStyle);
	}

}
