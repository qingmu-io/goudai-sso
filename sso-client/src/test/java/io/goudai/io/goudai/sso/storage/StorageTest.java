package io.goudai.io.goudai.sso.storage;

import io.goudai.storage.MemCachedStorage;
import io.goudai.storage.Storage;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by freeman on 2016/5/4.
 */
public class StorageTest {

    @Test
    public void testStorageSPI() throws Exception {
        ServiceLoader<Storage> service = ServiceLoader.load(Storage.class);
        for (Iterator<Storage> iterator = service.iterator(); iterator.hasNext(); ) {
            Storage next = iterator.next();
            Object o = next.get("6ea9d683-9336-48aa-bc97-0b574e380e34");
            System.out.println(o);
        }

    }
}
