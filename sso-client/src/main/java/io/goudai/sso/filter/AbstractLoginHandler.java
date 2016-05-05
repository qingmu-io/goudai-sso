package io.goudai.sso.filter;

import io.goudai.storage.Storage;
import io.goudai.storage.StorageFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.UUID;

/**
 * Created by freeman on 2016/5/5.
 */
public abstract class AbstractLoginHandler extends HttpServlet {

    private Storage storage = StorageFactory.getStorage();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter(getUsername());
        String password = req.getParameter(getPassword());
        String callback = req.getParameter("callback");
        String decode = URLDecoder.decode(callback, "UTF-8");
        if (authentic(username, password)) {
            String tgt = UUID.randomUUID().toString();
            String st = username + "-" + UUID.randomUUID().toString().replace("-", "");
            if (decode.contains("?")) {
                decode += "&st=" + st;
            } else {
                decode += "?st=" + st;
            }
            storage.add(st, tgt, 0);
            handleCookie(resp, tgt);
            storage.add(tgt, username, 0);
            resp.sendRedirect(decode);
        }
    }

    private final void handleCookie(HttpServletResponse resp, String tgt) {
        Cookie cookie = new Cookie(Const.SSO_COOKIE_KEY, tgt);
        cookie.setHttpOnly(true);
        resp.addCookie(cookie);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("callback", req.getParameter("callback"));
        req.getRequestDispatcher(getLoginPage()).forward(req, resp);
    }

    public abstract boolean authentic(String username, String password);

    public String getUsername() {
        return "username";
    }

    public String getPassword() {
        return "password";
    }

    public String getLoginPage() {
        return "login.jsp";
    }

}
