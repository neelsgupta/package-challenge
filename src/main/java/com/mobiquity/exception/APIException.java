package com.mobiquity.exception;

public class APIException extends Exception {

	private String line;
	private long lineNumber;
	
	public APIException(String message, Exception e) {
		super(message, e);
	}

	public APIException(String message) {
		super(message);
	}

	public APIException(Throwable exception) {
		super(exception);
	}

	public APIException(Throwable exception, String line, long lineNumber) {
		super(exception);
		this.line = line;
		this.lineNumber = lineNumber;
	}

	public APIException(String message, String line, long lineNumber) {
		super(message);
		this.line = line;
		this.lineNumber = lineNumber;
	}

	public APIException(String message, Throwable exception, String line, long lineNumber) {
		super(message, exception);
		this.line = line;
		this.lineNumber = lineNumber;
	}

	public APIException(String message, Throwable exception, boolean enableSuppression, boolean writableStackTrace) {
		super(message, exception, enableSuppression, writableStackTrace);
	}
}
