package com.sinosoft.config;

import com.sinosoft.common.interceptor.ShiroFormAuthenticationFilter;
import com.sinosoft.shiro.realm.MyRealm;
import com.sinosoft.util.EncryptService;
import com.sinosoft.util.MD5EncryptService;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/13 22:44
 * @Description:
 */
@Configuration
public class ShiroConfig {

    /**
     * 注入自定的Realm
     * @return
     */
    @Bean
    public MyRealm myRealm(){
        return new MyRealm();
    }



    /**
     * 注入SecurityManager
     * @return
     */
    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myRealm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //要求登录时的链接(可根据项目的URL进行替换),非必须的属性,默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的连接
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //用户访问未对其授权的资源时,所显示的连接
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        //Map<String, String> map = new HashMap<String, String>();
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("/login", "anon");
        map.put("/loadimage", "anon");
        map.put("/js/**", "anon");
        map.put("/commons/**", "anon");
        map.put("/images/*.*", "anon");
        map.put("/css/**", "anon");
        map.put("/font-awesome/**", "anon");
        map.put("/error/**", "anon");
        map.put("/druid/**", "anon");
        map.put("/springmvc.token", "anon");
        map.put("/codeSelect/loginPageFindComCom", "anon");
        map.put("/codeSelect/loginPageFindAccountByComComId", "anon");
        map.put("/soap/**", "anon");

        map.put("/dataDockingService/**","anon");

        map.put("/**", "authc");
        map.put("/login.html", "authc");
        map.put("/index", "authc");
        map.put("/index.html", "authc");
        map.put("/upload/**", "authc");
        LinkedHashMap<String, Filter> filtsMap=new LinkedHashMap<String, Filter>();
        //登录验证
        filtsMap.put("authc",new ShiroFormAuthenticationFilter() );
        shiroFilterFactoryBean.setFilters(filtsMap);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    /**
     * 通过ConfigurationProperties 注入 前缀为 security.shiro的配置信息
     * @return
     */
    @ConfigurationProperties(prefix = "security.shiro")
    @Bean
    public EncryptService encryptService(){
        MD5EncryptService md5EncryptService = new MD5EncryptService();

        //是否启用盐值加密
        //md5EncryptService.setSaltDisabled();
        //加密次数
        //md5EncryptService.setHashIterations();
        return md5EncryptService;
    }

}
