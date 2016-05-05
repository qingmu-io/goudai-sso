package io.goudai.sso.filter;

import io.goudai.storage.Storage;
import io.goudai.storage.StorageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by freeman on 2016/5/4.
 */
public class SSOFilter implements Filter {
    Logger logger = LoggerFactory.getLogger(SSOFilter.class);
    private Storage storage;
    private String domain;
    private String loginUrl;
    private String baseUrl;

    public void init(FilterConfig filterConfig) throws ServletException {
        storage = StorageFactory.getStorage();
        domain = filterConfig.getInitParameter("domain");
        if (domain == null || "".equals(domain))
            throw new NullPointerException("cookie domain is null");

        loginUrl = filterConfig.getInitParameter("loginUrl");
        if (loginUrl == null || "".equals(loginUrl))
            throw new NullPointerException("loginUrl is null");

        baseUrl = filterConfig.getInitParameter("baseUrl");
        if (baseUrl == null || "".equals(baseUrl))
            throw new NullPointerException("baseUrl is null");

    }


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String st = req.getParameter("st");
        System.out.println(req.getRequestURI());
        System.out.println(req.getRequestURL());
        //sso server 回调

        //TODO st超时先不处理
        if (st != null && !"".equals(st)) {
            String tgt = (String) storage.get(st);
            storage.delete(st);
            String user = (String) storage.get(tgt);
            Cookie cookie = new Cookie("goudai_sso_key", tgt);
            response.addCookie(cookie);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Cookie ssoCookie = findSSOCookie(req);
        String redirectUrl = loginUrl + "?callback=" + URLEncoder.encode(baseUrl + req.getRequestURI(), "UTF-8");
        /*未找到sso的cookie 没登录*/
        if (ssoCookie == null) {
            response.sendRedirect(redirectUrl);
            logger.info("redirectUrl url {}", redirectUrl);
            return;
        }
        String ssoId = ssoCookie.getValue();
        /*ssoId 不存在 重新登录*/
        if (ssoId == null || "".equals(ssoId)) {
            response.sendRedirect(redirectUrl);
            logger.info("redirectUrl url {}", redirectUrl);
            return;
        }
        /*不在集中缓存中 重新登录*/
        if (!storage.exists(ssoId)) {
            response.sendRedirect(redirectUrl);
            logger.info("redirectUrl url {}", redirectUrl);
            return;
        }
        /*走到这里表示用户已经登录在系统中*/
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Cookie findSSOCookie(HttpServletRequest req) throws IOException {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Const.SSO_COOKIE_KEY)) {
                return cookie;
            }
        }
        return null;
    }


    public void destroy() {

    }
}
