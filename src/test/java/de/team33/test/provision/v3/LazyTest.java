package de.team33.test.provision.v3;

import de.team33.libs.exceptional.v3.RuntimeWrapper;
import de.team33.libs.provision.v3.Lazy;
import org.junit.Test;

import java.util.Date;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class LazyTest {

    private static final RuntimeWrapper<IllegalStateException> WRAPPER =
            new RuntimeWrapper<>(IllegalStateException::new);

    private int counter = 0;
    private Supplier<Date> lazy = new Lazy<>(WRAPPER.supplier(() -> {
        counter += 1;
        Thread.sleep(1);
        return new Date();
    }), this::getLazy, this::setLazy);

    private void setLazy(final Supplier<Date> supplier) {
        lazy = supplier;
    }

    private Supplier<Date> getLazy() {
        return lazy;
    }

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
