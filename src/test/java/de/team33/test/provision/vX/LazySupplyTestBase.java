package de.team33.test.provision.vX;

import com.google.common.collect.ImmutableMap;
import de.team33.libs.provision.vX.LazySupply;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class LazySupplyTestBase {

    private int to_map = 0;
    private int to_hash_code = 0;
    private int to_string = 0;

    private static final class Basics {
        public static final Path SOURCE_PATH =
                Paths.get("src/test/java/de/team33/test/provision/vX/LazySupplyTestBase.java");
    }

    protected interface Key<R> extends LazySupply.Key<LazySupplyTestBase, R> {

        Key<Map<String, Object>> TO_MAP = context -> {
            context.to_map += 1;
            return ImmutableMap.<String, Object>builder()
                    .put("identityHashCode", System.identityHashCode(context))
                    .put("toString", context.toString())
                    .put("source", String.join("\n", Files.readAllLines(Basics.SOURCE_PATH)))
                    .build();
        };
        Key<Integer> TO_HASH_CODE = context -> {
            context.to_hash_code += 1;
            return context.getSubject().get(TO_MAP).hashCode();
        };
        Key<String> TO_STRING = context -> {
            context.to_string += 1;
            return context.getSubject().get(TO_MAP).toString();
        };
    }

    private static final int COUNT_A = 100000;
    private static final int COUNT_B = 200000;
    private static final int COUNT_C = 300000;
    private static final int COUNT_D = 400000;
    private static final int COUNT_E = 500000;
    private static final int COUNT_F = 600000;

    protected abstract LazySupply<LazySupplyTestBase> getSubject();

    @Test
    public void testA() {
        test(COUNT_A);
    }

    @Test
    public void testB() {
        test(COUNT_B);
    }

    @Test
    public void testC() {
        test(COUNT_C);
    }

    @Test
    public void testD() {
        test(COUNT_D);
    }

    @Test
    public void testE() {
        test(COUNT_E);
    }

    @Test
    public void testF() {
        test(COUNT_F);
    }

    //@Ignore
    @Test
    public void getMultiThreaded() throws InterruptedException {
        final Thread[] threads = new Thread[500];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> getSubject().get(Key.TO_MAP));
        }

        assertEquals(0, to_map);

        for (final Thread thread : threads) {
            thread.start();
        }
        for (final Thread thread : threads) {
            thread.join();
        }

        assertEquals(1, to_map);
    }

    private <R> void test(final int count) {
        test(count, Key.TO_MAP);
        test(count, Key.TO_HASH_CODE);
        test(count, Key.TO_STRING);
    }

    private <R> void test(final int count, final LazySupply.Key<LazySupplyTestBase, R> key) {
        final List<R> results =
                Stream.generate(() -> (R)null).limit(27).collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < count; ++i) {
            results.set(i % 27, getSubject().get(key));
        }
        if (key == Key.TO_MAP)
            Assert.assertEquals(1, to_map);
        if (key == Key.TO_HASH_CODE)
            Assert.assertEquals(1, to_hash_code);
        if (key == Key.TO_STRING)
            Assert.assertEquals(1, to_string);
        System.out.println(results.get(0));
    }
}
