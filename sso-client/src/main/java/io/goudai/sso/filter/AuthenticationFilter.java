package io.goudai.sso.filter;

import io.goudai.storage.Storage;
import io.goudai.storage.StorageFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.UUID;

/**
 * Created by freeman on 2016/5/5.
 */
public class AuthenticationFilter implements Filter {

    Storage storage = StorageFactory.getStorage();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Cookie ssoCookie = findSSOCookie(request);
        /*未找到sso的cookie 没登录 继续流转到登录页面进行登录认证*/
        if (ssoCookie == null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String ssoId = ssoCookie.getValue();
        /*ssoId 不存在 继续流转到登录页面进行登录认证*/
        if (ssoId == null || "".equals(ssoId)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        /*不在集中缓存中 继续流转到登录页面进行登录认证*/
        if (!storage.exists(ssoId)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        /*走到这里表示用户已经登录在系统中*/
        String callback = request.getParameter("callback");
        String decode = URLDecoder.decode(callback, "UTF-8");
        Object o = storage.get(ssoId);
        String st = o + "-" + UUID.randomUUID().toString().replace("-", "");
        if (decode.contains("?")) {
            decode += "&st=" + st;
        } else {
            decode += "?st=" + st;
        }
        storage.add(st, ssoId, 0);

        response.sendRedirect(decode);
    }

    private Cookie findSSOCookie(HttpServletRequest req) throws IOException {
        Cookie[] cookies = req.getCookies();
        if(cookies == null)return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Const.SSO_COOKIE_KEY)) {
                return cookie;
            }
        }
        return null;
    }

    @Override
    public void destroy() {

    }
}
