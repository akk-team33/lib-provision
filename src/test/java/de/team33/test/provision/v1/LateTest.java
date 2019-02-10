package de.team33.test.provision.v1;

import de.team33.libs.identification.v1.Key;
import de.team33.libs.provision.v1.Late;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LateTest {

    private static final Key<Date> DATE1 = new Key<>("DATE1");
    private static final Key<Date> DATE2 = new Key<>("DATE2");
    private static final Key<UUID> UUID_KEY = new Key<>("UUID_KEY");
    private final Date first = new Date();
    private int counter = 0;
    private final Late late = Late.builder()
            .add(DATE1, this::newDate)
            .add(DATE2, this::newDate)
            .build();

    private Date newDate() {
        try {
            counter += 1;
            Thread.sleep(1);
            return new Date();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Test
    public void get() throws InterruptedException {
        Thread.sleep(1);
        assertTrue(first.compareTo(late.get(DATE1)) < 0);
        assertSame(late.get(DATE1), late.get(DATE1));
        assertEquals(1, counter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIllegal() {
        late.get(UUID_KEY);
    }

    @Ignore
    @Test
    public void getMultiThreaded() throws InterruptedException {
        final Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> {
                late.get(DATE2);
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