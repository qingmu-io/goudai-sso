package io.goudai.sso.filter;

import io.goudai.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by freeman on 2016/5/4.
 */
public class SSOFilter implements Filter {
    private final static String DEFAULT_SSO_KEY = "goudai_sso_key";
    Logger logger = LoggerFactory.getLogger(SSOFilter.class);
    private Storage storage;
    private String ssoKey;
    private String domain;
    private String loginUrl;
    private String baseUrl;

    public void init(FilterConfig filterConfig) throws ServletException {
        loadStorage();
        domain = filterConfig.getInitParameter("domain");
        if (domain == null || "".equals(domain))
            throw new NullPointerException("cookie domain is null");

        loginUrl = filterConfig.getInitParameter("loginUrl");
        if (loginUrl == null || "".equals(loginUrl))
            throw new NullPointerException("loginUrl is null");

        baseUrl = filterConfig.getInitParameter("baseUrl");
        if (baseUrl == null || "".equals(baseUrl))
            throw new NullPointerException("baseUrl is null");


        ssoKey = filterConfig.getInitParameter("ssoKey");
        if (ssoKey == null || "".equals(ssoKey)) {
            ssoKey = DEFAULT_SSO_KEY;
            logger.info("ssoKey is null ,using default ssoKey {}", DEFAULT_SSO_KEY);
        }


    }


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Cookie ssoCookie = findSSOCookie(req, response);
        String redirectUrl = loginUrl + "?callback" + URLEncoder.encode(baseUrl+req.getRequestURI(),"UTF-8");
        logger.info("redirectUrl url {}",redirectUrl);
        /*未找到sso的cookie 没登录*/
        if (ssoCookie == null) {
            response.sendRedirect(redirectUrl);
            return;
        }
        String ssoId = ssoCookie.getValue();
        /*ssoId 不存在 重新登录*/
        if (ssoId == null || "".equals(ssoId)) {
            response.sendRedirect(redirectUrl);
            return;
        }
        /*不在集中缓存中 重新登录*/
        if (!storage.exists(ssoId)) {
            response.sendRedirect(redirectUrl);
            return;
        }
        /*走到这里表示用户已经登录在系统中*/
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Cookie findSSOCookie(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(ssoKey)) {
                return cookie;
            }
        }
        return null;
    }

    private void loadStorage() {
        ServiceLoader<Storage> service = ServiceLoader.load(Storage.class);
        for (Iterator<Storage> iterator = service.iterator(); iterator.hasNext(); ) {
            storage = iterator.next();
        }

    }

    public void destroy() {

    }
}
