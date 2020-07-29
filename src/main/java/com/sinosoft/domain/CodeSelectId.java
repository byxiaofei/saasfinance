package com.sinosoft.domain;

import java.io.Serializable;
import java.util.Objects;

public class CodeSelectId implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String codeType;
    private String codeCode;

    public CodeSelectId() { }
    public CodeSelectId(String codeType, String codeCode) {
        this.codeType = codeType;
        this.codeCode = codeCode;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCodeCode() {
        return codeCode;
    }

    public void setCodeCode(String codeCode) {
        this.codeCode = codeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeSelectId)) return false;
        CodeSelectId that = (CodeSelectId) o;
        return Objects.equals(codeType, that.codeType) &&
                Objects.equals(codeCode, that.codeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeType, codeCode);
    }
}
