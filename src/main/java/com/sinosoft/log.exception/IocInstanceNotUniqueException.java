package com.sinosoft.log.exception;

/**
 * 
 * @author zh
 *
 */
public class IocInstanceNotUniqueException extends IocException {

	private static final long serialVersionUID = -742775077430352894L;

	public IocInstanceNotUniqueException() {
	}

	public IocInstanceNotUniqueException(String message) {
		super(message);
	}

	public IocInstanceNotUniqueException(Throwable cause) {
		super(cause);
	}

	public IocInstanceNotUniqueException(String message, Throwable cause) {
		super(message, cause);
	}

}
