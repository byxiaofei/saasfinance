package com.sinosoft.log.exception;

/**
 * 参数为空异常
 * @author liushuang
 */
public class ParameterIsNullException extends RuntimeException {

	private static final long serialVersionUID = 479255002303446816L;

	public ParameterIsNullException() { }

	public ParameterIsNullException(String message) {
		super(message);
	}

	public ParameterIsNullException(Throwable cause) {
		super(cause);
	}

	public ParameterIsNullException(String message, Throwable cause) {
		super(message, cause);
	}

}
