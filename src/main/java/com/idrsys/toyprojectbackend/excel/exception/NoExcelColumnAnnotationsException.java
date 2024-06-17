package com.idrsys.toyprojectbackend.excel.exception;

import com.idrsys.toyprojectbackend.excel.ExcelException;

public class NoExcelColumnAnnotationsException extends ExcelException {

	public NoExcelColumnAnnotationsException(String message) {
		super(message, null);
	}

}
