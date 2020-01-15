package com.yance.exceptionhandle.decorated;

import com.yance.exceptionhandle.ExceptionHandler;
import com.yance.exceptionhandle.interfaces.ExceptionNoticeHandlerDecoration;

public class DefaultExceptionNoticeHandler implements ExceptionNoticeHandlerDecoration{

	
	private final ExceptionHandler exceptionHandler;
	
	
	
	public DefaultExceptionNoticeHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}


	@Override
	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

}
