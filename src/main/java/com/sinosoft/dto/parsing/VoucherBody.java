package com.sinosoft.dto.parsing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * @Auther: luodejun
 * @Date: 2020/5/13 21:00
 * @Description:
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class VoucherBody {

    @XmlElement(name = "entry")
    private List<Entry> entry;

    public List<Entry> getEntry() {
        return entry;
    }

    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }
}
