package com.sinosoft.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Auther: luodejun
 * @Date: 2020/4/24 15:43
 * @Description:
 */
@Entity
@Table(name = "accsegmentdefine")
public class AccSegmentDefine implements Serializable {

    @Id
    private String segmentCol;
    @Column(nullable = false)
    private String segmentName;
    @Column(nullable = false)
    private int segmentColLen;
    @Column(nullable = false)
    private String segmentLen;
    @Column(nullable = false)
    private String segmentFlag;
    private String temp;

    public String getSegmentCol() {
        return segmentCol;
    }

    public void setSegmentColl(String segmentCol) {
        this.segmentCol = segmentCol;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public int getSegmentColLen() {
        return segmentColLen;
    }

    public void setSegmentColLen(int segmentColLen) {
        this.segmentColLen = segmentColLen;
    }

    public String getSegmentLen() {
        return segmentLen;
    }

    public void setSegmentLen(String segmentLen) {
        this.segmentLen = segmentLen;
    }

    public String getSegmentFlag() {
        return segmentFlag;
    }

    public void setSegmentFlag(String segmentFlag) {
        this.segmentFlag = segmentFlag;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
