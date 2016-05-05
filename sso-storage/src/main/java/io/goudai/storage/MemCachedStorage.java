package io.goudai.storage;


import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by freeman on 2016/5/4.
 */
public class MemCachedStorage implements Storage {

    Logger logger = LoggerFactory.getLogger(MemCachedStorage.class);
    private MemcachedClient memcachedClient;

    public MemCachedStorage() throws IOException {
        memcachedClient = new XMemcachedClientBuilder("localhost:11211").build();
    }

    @Override
    public void add(String ssoId, Object value, int exp) {
        try {
            if (!memcachedClient.set(ssoId, exp, value)) {
                throw new StorageException("add ssion[{" + ssoId + "}] object [{" + value + "}] to storage fail");
            }
        } catch (Exception e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    @Override
    public Object get(String ssoId) {
        try {
            return this.memcachedClient.get(ssoId);
        } catch (TimeoutException e) {
            throw new StorageException(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new StorageException(e.getMessage(), e);
        } catch (MemcachedException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String ssoId) {
        return get(ssoId) != null;
    }

    @Override
    public boolean delete(String key) {
        try {
            return this.memcachedClient.delete(key);
        } catch (TimeoutException e) {
            throw new StorageException(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new StorageException(e.getMessage(), e);
        } catch (MemcachedException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }
}
