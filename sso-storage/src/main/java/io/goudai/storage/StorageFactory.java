package io.goudai.storage;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by freeman on 2016/5/5.
 */
public final class StorageFactory {

    public static Storage getStorage() {
        ServiceLoader<Storage> service = ServiceLoader.load(Storage.class);
        for (Iterator<Storage> iterator = service.iterator(); iterator.hasNext(); ) {
            return iterator.next();
        }
        throw new NullPointerException("not found storage spi implement ");
    }
}