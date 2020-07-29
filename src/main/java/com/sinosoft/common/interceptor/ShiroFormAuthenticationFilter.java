package com.sinosoft.common.interceptor;

import com.sinosoft.common.Constant;
import com.sinosoft.common.CurrentUser;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @auther LiuShuang
 * @date 2019/9/23 14:00
 */
public class ShiroFormAuthenticationFilter extends FormAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(ShiroFormAuthenticationFilter.class);

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean flag = super.isAccessAllowed(request, response, mappedValue) ||
                (!isLoginRequest(request, response) && isPermissive(mappedValue));

        if (flag) { // 登录认证已通过
            List<String> urlList = Constant.urlList;
            if (urlList!=null && urlList.size()!=0) {
                String url = ((HttpServletRequest) request).getRequestURI();
                if (urlList.contains(url) || urlList.contains(url.substring(0, url.lastIndexOf("/")))) {
                    //判断当前登录管理机构是否停用，如果停用需要拦截
                    //返回 false 即表示拦截，剩余由 onAccessDenied() 处理
                    String currentManageBranchFlag = CurrentUser.getCurrentManageBranchFlag();
                    if (currentManageBranchFlag!=null && "0".equals(currentManageBranchFlag)) {
                        flag = false;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 在访问controller前判断登录是否失效，返回json，不进行重定向（解决ajax请求重定向问题）。
     * 重写 FormAuthenticationFilter 类的 onAccessDenied 方法
     * @param request
     * @param response
     * @return true-继续往下执行，false-该filter过滤器已经处理，不继续执行其他过滤器
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException,Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Login page view.");
                }
                //allow them to see the login page ;)
                return true;
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                        "Authentication url [" + getLoginUrl() + "]");
            }

            if (isAjax(request)) {// ajax请求
                HttpSession session = httpServletRequest.getSession();
                // 如果session不为空，则可以浏览其他页面
                if (session.getAttribute("currentUser") == null) {
                    // 处理ajax请求
                    httpServletResponse.setHeader("REDIRECT", "REDIRECT");//告诉ajax这是重定向
                    httpServletResponse.setHeader("CONTEXTPATH", httpServletRequest.getContextPath() + "/login.html");//重定向地址
                    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                } else {
                    List<String> urlList = Constant.urlList;
                    if (urlList!=null && urlList.size()!=0) {
                        String url = ((HttpServletRequest) request).getRequestURI();
                        if (urlList.contains(url)) {
                            String currentManageBranchFlag = CurrentUser.getCurrentManageBranchFlag();
                            if (currentManageBranchFlag!=null && "0".equals(currentManageBranchFlag)) {
                                // 处理ajax请求
                                // 告诉ajax没有权限访问该服务
                                httpServletResponse.setHeader("HASH_NO_PRIVILEGE", "HASH_NO_PRIVILEGE");
                                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            }
                        }
                    }
                }
            } else {
                saveRequestAndRedirectToLogin(request, response);
            }
            return false;
        }
    }

    private boolean isAjax(ServletRequest request){
        String header = ((HttpServletRequest) request).getHeader("X-Requested-With");
        if("XMLHttpRequest".equalsIgnoreCase(header)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
