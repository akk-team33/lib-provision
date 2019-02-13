package de.team33.test.provision.v1;

import de.team33.libs.provision.v1.LazyMap;
import org.junit.Test;

import java.util.Date;
import java.util.function.Function;

import static org.junit.Assert.*;

public class LazyMapTest {

    private static final Key<Date> DATE0 = LazyMapTest::newDate0;

    private final LazyMap<LazyMapTest> lazy = new LazyMap<>(this);
    private final long time0 = System.currentTimeMillis();

    private int time0Count = 0;

    private Date newDate0() {
        time0Count += 1;
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

    @FunctionalInterface
    private interface Key<T> extends Function<LazyMapTest, T> {
    }
}