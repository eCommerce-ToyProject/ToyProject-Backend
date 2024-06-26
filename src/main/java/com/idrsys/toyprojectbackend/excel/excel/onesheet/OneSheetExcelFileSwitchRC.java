package com.idrsys.toyprojectbackend.excel.excel.onesheet;

import com.idrsys.toyprojectbackend.excel.excel.SXSSFExcelFile;
import com.idrsys.toyprojectbackend.excel.exception.ExcelInternalException;
import com.idrsys.toyprojectbackend.excel.resource.DataFormatDecider;
import com.idrsys.toyprojectbackend.excel.resource.ExcelRenderLocation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.idrsys.toyprojectbackend.excel.utils.SuperClassReflectionUtils.getField;

/**
 * OneSheetExcelFile
 *
 * - support Excel Version over 2007
 * - support one sheet rendering
 * - support different DataFormat by Class Type
 * - support Custom CellStyle according to (header or body) and data field
 */
public final class OneSheetExcelFileSwitchRC<T> extends SXSSFExcelFile<T> {

	private int ROW_START_INDEX = 0;
	private static final int COLUMN_START_INDEX = 0;
	private static final Logger log = LoggerFactory.getLogger(OneSheetExcelFileSwitchRC.class);
	private int currentColumnIndex = COLUMN_START_INDEX;

	public OneSheetExcelFileSwitchRC(Class<T> type) {
		super(type);
	}

	public OneSheetExcelFileSwitchRC(List<T> data, Class<T> type) {
		super(data, type);
	}

	public OneSheetExcelFileSwitchRC(List<T> data, Class<T> type, DataFormatDecider dataFormatDecider) {
		super(data, type, dataFormatDecider);
	}

	@Override
	protected void validateData(List<T> data) {
		int maxRows = supplyExcelVersion.getMaxRows();
		if (data.size() > maxRows) {
			throw new IllegalArgumentException(
					String.format("This concrete ExcelFile does not support over %s rows", maxRows));
		}
	}

	@Override
	public void renderExcel(List<T> data) {
		// 1. Create sheet and renderHeader
		sheet = wb.createSheet();
		renderHeadersWithNewSheetRowColumnSwitch(sheet, currentColumnIndex++, ROW_START_INDEX);


		if (data.isEmpty()) {
			return;
		}

		// 2. Render Bodynnn
		for (Object renderedData : data) {
			log.info("render excel data is List: {}", renderedData instanceof List<?>);
			renderBodyRowColumnSwitch(renderedData, currentColumnIndex++, ROW_START_INDEX);
		}
	}

	@Override
	public void addRows(List<T> data) {
		renderBodyRowColumnSwitch(data, currentColumnIndex++, ROW_START_INDEX);
	}

}
