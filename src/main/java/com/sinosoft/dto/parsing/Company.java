package com.sinosoft.dto.parsing;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * @Auther: luodejun
 * @Date: 2020/5/14 09:51
 * @Description:
 */
@XmlRootElement(name = "company")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id","info","phoen"})
public class Company implements Serializable {

    private String id;

    private String info;

    private String phoen;

    public Company(){
        super();
    }

    public Company(String id, String info, String phoen) {
        this.id = id;
        this.info = info;
        this.phoen = phoen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPhoen() {
        return phoen;
    }

    public void setPhoen(String phoen) {
        this.phoen = phoen;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                ", info='" + info + '\'' +
                ", phoen='" + phoen + '\'' +
                '}';
    }
}
