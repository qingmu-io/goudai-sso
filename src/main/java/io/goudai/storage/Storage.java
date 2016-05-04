package io.goudai.storage;

/**
 * Created by freeman on 2016/5/4.
 */
public interface Storage {
    /**
     *
     * @param ssoId
     * @param value
     * @param exp 毫秒
     */
    void add(String ssoId, Object value,int exp);

    Object get(String ssoId);

    boolean exists(String ssoId);
}
