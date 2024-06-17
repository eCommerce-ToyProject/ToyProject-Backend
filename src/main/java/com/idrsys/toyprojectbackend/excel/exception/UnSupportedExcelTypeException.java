package com.idrsys.toyprojectbackend.excel.exception;

import com.idrsys.toyprojectbackend.excel.ExcelException;

public class UnSupportedExcelTypeException extends ExcelException {

	public UnSupportedExcelTypeException(String message) {
		super(message, null);
	}

}
