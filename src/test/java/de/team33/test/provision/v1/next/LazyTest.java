package de.team33.test.provision.v1.next;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Supplier;

import de.team33.libs.provision.v1.next.Lazy;
import org.junit.Test;


public class LazyTest {

    private final Lazy<Date> loose;
    private final Lazy<Date> strict;
    private final Date first;

    private int counter;

    public LazyTest() {
        final Supplier<Date> initial = () -> {
            try {
                counter += 1;
                Thread.sleep(1);
                return new Date();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        };
        strict = Lazy.strict(initial);
        loose = Lazy.loose(initial);
        first = new Date();
    }

    @Test
    public void get() throws InterruptedException {
        Thread.sleep(1);
        for (final Lazy<Date> lazy : Arrays.asList(loose, strict)) {
            counter = 0;
            assertTrue(first.compareTo(lazy.get()) < 0);
            assertSame(lazy.get(), lazy.get());
            assertEquals(1, counter);
        }
    }

    @Test
    public void looseGetMultiThreaded() throws InterruptedException {
        getMultiThreaded(loose);
        assertTrue(1 < counter);
    }

    @Test
    public void strictGetMultiThreaded() throws InterruptedException {
        getMultiThreaded(strict);
        assertEquals(1, counter);
    }

    private void getMultiThreaded(final Lazy<Date> lazy) throws InterruptedException {
        counter = 0;
        final Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(lazy::get);
        }
        for (final Thread thread : threads) {
            thread.start();
        }
        for (final Thread thread : threads) {
            thread.join();
        }
    }
}
