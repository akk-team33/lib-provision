package de.team33.test.provision.v2;

import de.team33.libs.provision.v2.Lazy;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LazyTest {

    private int counter = 0;
    private Lazy<Date> lazy = new Lazy<>(() -> {
        try {
            counter += 1;
            Thread.sleep(1);
            return new Date();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    });
    private Date first = new Date();

    @Test
    public void get() throws InterruptedException {
        Thread.sleep(1);
        assertTrue(first.compareTo(lazy.get()) < 0);
        assertSame(lazy.get(), lazy.get());
        assertEquals(1, counter);
    }

    @Test
    public void getMultiThreaded() throws InterruptedException {
        final Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> {
                lazy.get();
            });
        }

        assertEquals(0, counter);

        for (final Thread thread : threads) {
            thread.start();
        }
        for (final Thread thread : threads) {
            thread.join();
        }

        assertEquals(1, counter);
    }
}