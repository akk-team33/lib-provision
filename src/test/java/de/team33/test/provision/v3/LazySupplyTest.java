package de.team33.test.provision.v3;

import de.team33.libs.provision.v3.LazySupply;
import de.team33.libs.provision.v3.LazySupply.Key;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class LazySupplyTest {

    private static final Key<LazySupplyTest, Date> DATE0 = LazySupplyTest::newDate0;

    private final LazySupply<LazySupplyTest> lazy = new LazySupply<>(this);
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

        assertTrue("time0Count: " + time0Count, 1 < time0Count);
    }
}
