package com.sinosoft.common;

public class JsonAnalyzeException extends Exception {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 错误编码
     */
    private String errorCode;
    //是否为中文信息
    private boolean propertiesKey = true;

    /**
     * 基本异常
     */
    public JsonAnalyzeException() {
        super();
    }
    /**
     *
     * @param message
     */
    public JsonAnalyzeException(String message) {
        super(message);
    }
    /**
     *
     * @param errorCode
     * @param message
     */
    public JsonAnalyzeException(String errorCode, String message) {
        this(errorCode, message, true);
    }
    /**
     *
     * @param errorCode
     * @param message
     * @param propertiesKey
     */
    public JsonAnalyzeException(String errorCode, String message, boolean propertiesKey) {
        super(message);
        this.setErrorCode(errorCode);
        this.setPropertiesKey(propertiesKey);
    }
    /**
     *
     * @param errorCode
     * @param message
     * @param cause
     * @param propertiesKey
     */
    public JsonAnalyzeException(String errorCode, String message, Throwable cause, boolean propertiesKey) {
        super(message, cause);
        this.setErrorCode(errorCode);
        this.setPropertiesKey(propertiesKey);
    }
    /**
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public JsonAnalyzeException(String message, Throwable cause, boolean enableSuppression,boolean writableStackTrace) {
//		super(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     *
     * @param message
     * @param cause
     */
    public JsonAnalyzeException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     *
     * @param cause
     */
    public JsonAnalyzeException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public boolean isPropertiesKey() {
        return propertiesKey;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setPropertiesKey(boolean propertiesKey) {
        this.propertiesKey = propertiesKey;
    }
}
