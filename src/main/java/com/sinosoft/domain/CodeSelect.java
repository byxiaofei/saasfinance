package com.sinosoft.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 */
@Entity
@Table(name = "codemanage")
public class CodeSelect {
    //主键（codeType、codeCode）
    @EmbeddedId
    private CodeSelectId id;
    private String codeName;
    private String temp;
    private String orderBy;

    public CodeSelectId getId() { return id; }
    public void setId(CodeSelectId id) { this.id = id; }
    public String getCodeName() { return codeName; }
    public void setCodeName(String codeName) { this.codeName = codeName; }
    public String getTemp() { return temp; }
    public void setTemp(String temp) { this.temp = temp; }
    public String getOrderBy() { return orderBy; }
    public void setOrderBy(String orderBy) { this.orderBy = orderBy; }
}
