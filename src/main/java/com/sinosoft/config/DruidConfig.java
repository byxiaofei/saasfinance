package com.sinosoft.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/13 22:27
 * @Description:
 */

@Configuration
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource dataSource(){
        return new DruidDataSource();
    }

    /**
     * 注册一个StatViewServlet
     * @return
     */
    @Bean
    public ServletRegistrationBean statViewServlet(){
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        //初始化druid控制台用户名/密码
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "123456");

        //设置允许访问白名单
        servletRegistrationBean.addInitParameter("allow","127.0.0.1");
        //设置访问黑名单(存在共同时，deny优先于allow)
        servletRegistrationBean.addInitParameter("deny","192.168.1.2");

        //是否能够重置数据.
        servletRegistrationBean.addInitParameter("resetEnable","false");

        return servletRegistrationBean;
    }

    /**
     * 注册一个filterRegistrationBean
     * @return
     */
    @Bean
    public FilterRegistrationBean statFilter(){

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());

        //添加过滤规则
        filterRegistrationBean.addUrlPatterns("/*");
        //添加需要忽略的格式信息
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.png,*.css,*.ico,/druid/*,/soap/*");

        return filterRegistrationBean;
    }
}
