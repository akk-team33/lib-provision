package de.team33.test.provision.v3;

import de.team33.libs.provision.v3.LazyMap;
import de.team33.libs.provision.v3.LazyMap.Key;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class LazyMapTest {

    private static final Key<LazyMapTest, Date> DATE0 = LazyMapTest::newDate0;

    private final LazyMap<LazyMapTest> lazy = new LazyMap<>(this);
    private final long time0 = System.currentTimeMillis();

    private int time0Count = 0;

    private Date newDate0() throws InterruptedException {
        time0Count += 1;
        Thread.sleep(1);
        return new Date(time0);
    }

    @Test
    public void getDate0() {
        final Date date0 = lazy.get(DATE0);
        assertSame(date0, lazy.get(DATE0));
        assertSame(date0, lazy.get(DATE0));
        assertEquals(1L, time0Count);
    }

    @Test
    public void getDate0afterReset() {
        final Date date0 = lazy.get(DATE0);
        lazy.reset();
        assertNotSame(date0, lazy.get(DATE0));
        assertEquals(date0, lazy.get(DATE0));
        assertEquals(2L, time0Count);
    }

    @Test
    public void getMultiThreaded() throws InterruptedException {
        final Thread[] threads = new Thread[500];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> lazy.get(DATE0));
        }

        assertEquals(0, time0Count);

        for (final Thread thread : threads) {
            thread.start();
        }
        for (final Thread thread : threads) {
            thread.join();
        }

        assertTrue(1 < time0Count);
    }
}
