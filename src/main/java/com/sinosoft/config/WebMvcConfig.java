package com.sinosoft.config;

import com.sinosoft.common.interceptor.TokenInterceptor;
import com.sinosoft.util.TokenGenerateServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/10 09:38
 * @Description:
 */

@Configuration
@ComponentScan(useDefaultFilters = true)
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("login","error"); //拦截路径


    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public ServletRegistrationBean registrationToken(){
        ServletRegistrationBean tokenGenerateServlet = new ServletRegistrationBean(new TokenGenerateServlet(), "/springmvc.token");
        tokenGenerateServlet.setLoadOnStartup(1);
        tokenGenerateServlet.setName("tokenGenerate");

        return tokenGenerateServlet;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        //用于解决 IE浏览器返回JSON文件的问题
        supportedMediaTypes.add(new MediaType(MediaType.TEXT_HTML, Charset.forName("UTF-8")));
        supportedMediaTypes.add(new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8")));
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(mappingJackson2HttpMessageConverter);
    }
}
