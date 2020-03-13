package de.team33.test.provision.v4;

import org.junit.Test;

import java.util.Date;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class LazyTest {

    private int counter = 0;
    private Supplier<Date> lazy = new LazyDate();
    private Date first = new Date();

    private Date newDate() throws InterruptedException {
        counter += 1;
        Thread.sleep(1);
        return new Date();
    }

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

    private class LazyDate implements Supplier<Date> {
        @Override
        public synchronized Date get() {
            if (this == lazy) {
                try {
                    final Date result = newDate();
                    lazy = () -> result;
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
            return lazy.get();
        }
    }
}
