package de.team33.test.provision.v1;

import de.team33.libs.identification.v1.Unique;
import de.team33.libs.provision.v1.Features;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class FeaturesTest {

    @Test
    public final void keyToString() {
        final Features.Key<?> key = new Key<>();
        final String expected = "de.team33.test.provision.v1.FeaturesTest.keyToString(FeaturesTest.java:22)";
        assertEquals(expected, key.toString());
    }

    @Test
    public final void get() {
        final Features.Key<Map<String, List<String>>> key = new Key<>();
        final Map<String, List<String>> feature = new TreeMap<>();
        final Features features = Features.builder()
                .set(key, feature)
                .build();
        final Map<String, List<String>> result = features.get(key);
        assertSame(feature, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void getFail() {
        final Features.Key<Map<String, List<String>>> key = new Key<>();
        final Features features = Features.builder().build();
        fail("expected to fail but was " + features.get(key));
    }

    @Test
    public final void getVariable() {
        final Features.Key<Date> key = new Key<>();
        final Supplier<Features> stage = Features.builder()
                .setup(key, Date::new)
                .prepare();

        final Features features = stage.get();
        assertSame(features.get(key), features.get(key));

        final Features features2 = stage.get();
        assertNotSame(features.get(key), features2.get(key));
    }

    @Test
    public final void getFixed() {
        final Features.Key<Date> key = new Key<>();
        final Supplier<Features> stage = Features.builder()
                .set(key, new Date())
                .prepare();

        final Features features = stage.get();
        assertSame(features.get(key), features.get(key));

        final Features features2 = stage.get();
        assertSame(features.get(key), features2.get(key));
    }

    private static class Key<T> extends Unique implements Features.Key<T> {
    }
}
