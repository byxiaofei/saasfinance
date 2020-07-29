package com.sinosoft.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @Auther: luodejun
 * @Date: 2020/5/11 10:55
 * @Description:
 */
@WebService
public interface DataDockingService {

    /**
     *
     * 功能描述:    接收费控Xml报文文件。
     *
     */
    @WebMethod
    public String receiveInformation(@WebParam(name = "xmlMessage") String xmlMessage);
}
