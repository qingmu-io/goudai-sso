package io.goudai.sso.filter.io.goudai.utils;

import io.goudai.sso.filter.Const;
import io.goudai.storage.Storage;
import io.goudai.storage.StorageFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by freeman on 2016/5/5.
 */
public class SecurityUtils {
    private static Storage storage = StorageFactory.getStorage();

    public static String getUser(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.equals(Const.SSO_COOKIE_KEY)) {
                return (String) storage.get(cookie.getValue());
            }
        }
        throw new NullPointerException("user not login ");
    }
}
