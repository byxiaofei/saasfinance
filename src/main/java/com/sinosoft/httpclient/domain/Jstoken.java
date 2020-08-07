package com.sinosoft.httpclient.domain;

public class Jstoken {
    private String access_token;
    private Integer expires_in;

    //有多层的需要新增实体类，保存list。类似下面的；Son 的写法和该类类似，提供get/set方法、构造方法
    // private List<Son> son;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    public Jstoken(String access_token, Integer expires_in) {
        this.access_token = access_token;
        this.expires_in = expires_in;
    }


    @Override
    public String toString() {
        return "Jstoken{" +
                "access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                '}';
    }
}
