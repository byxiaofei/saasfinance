//package com.sinosoft.config;
//
//
//import com.sinosoft.service.DataDockingService;
//import org.apache.cxf.Bus;
//import org.apache.cxf.jaxws.EndpointImpl;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.Resource;
//import javax.xml.ws.Endpoint;
//
//
///**
// * @Auther: luodejun
// * @Date: 2020/5/8 15:16
// * @Description:
// */
//@Configuration
//public class CxfConfig {
//
//    @Resource
//    private Bus bus;
//    @Resource
//    private DataDockingService dataDockingService;
//
//    //这里需要注意  由于springmvc 的核心类 为DispatcherServlet
//    //此处若不重命名此bean的话 原本的mvc就被覆盖了。可查看配置类：DispatcherServletAutoConfiguration
//    //一种方法是修改方法名称 或者指定bean名称
//    //这里需要注意 若beanName命名不是 cxfServletRegistration 时，会创建两个CXFServlet的。
//    //具体可查看下自动配置类：Declaration org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration
//    //也可以不设置此bean 直接通过配置项 cxf.path 来修改访问路径的
////    @Bean(name = "cxfServlet")
////    public ServletRegistrationBean cxfServlet(){
////        return new ServletRegistrationBean(new CXFServlet(),"/services/*");
////    }
////    @Bean(name = "cxfServlet")
////    public ServletRegistrationBean cxfServlet(){
////        return new ServletRegistrationBean(new CXFServlet(),"/dataDockingService/*");
////    }
//
//    @Bean
//    public Endpoint endpoint(){
//        EndpointImpl endpoint = new EndpointImpl(bus,dataDockingService);
//        endpoint.publish("/api");
//        return endpoint;
//    }
//}
