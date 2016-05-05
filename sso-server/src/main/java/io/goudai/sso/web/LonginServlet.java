package io.goudai.sso.web;

import io.goudai.sso.filter.AbstractLoginHandler;

/**
 * Created by freeman on 2016/5/5.
 */
public class LonginServlet extends AbstractLoginHandler {


    @Override
    public boolean authentic(String username, String password) {
        return true;
    }

    @Override
    public String getLoginPage() {
        return "sso-login.jsp";
    }
}
