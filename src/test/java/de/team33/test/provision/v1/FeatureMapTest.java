package de.team33.test.provision.v1;

import de.team33.libs.provision.v1.FeatureMap;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class FeatureMapTest
{

  @Test
  public final void keyToString() {
    final FeatureMap.Key<?> key = new Key<>();
    final String expected = "de.team33.test.provision.v1.FeatureMapTest.keyToString(FeatureMapTest.java:21)";
    assertEquals(expected, key.toString());
  }

  @Test
  public final void get() {
    final FeatureMap.Key<Map<String, List<String>>> key = new Key<>();
    final Map<String, List<String>> feature = new TreeMap<>();
    final FeatureMap features = FeatureMap.builder()
                                          .set(key, feature)
                                          .build();
    final Map<String, List<String>> result = features.get(key);
    assertSame(feature, result);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void getFail() {
    final FeatureMap.Key<Map<String, List<String>>> key = new Key<>();
    final FeatureMap features = FeatureMap.builder().build();
    fail("expected to fail but was " + features.get(key));
  }

  @Test
  public final void getVariable() {
    final FeatureMap.Key<Date> key = new Key<>();
    final FeatureMap.Stage stage = FeatureMap.builder()
                                             .setup(key, Date::new)
                                             .prepare();

    final FeatureMap features = stage.get();
    assertSame(features.get(key), features.get(key));

    final FeatureMap features2 = stage.get();
    assertNotSame(features.get(key), features2.get(key));
  }

  @Test
  public final void getFixed() {
    final FeatureMap.Key<Date> key = new Key<>();
    final FeatureMap.Stage stage = FeatureMap.builder()
                                             .set(key, new Date())
                                             .prepare();

    final FeatureMap features = stage.get();
    assertSame(features.get(key), features.get(key));

    final FeatureMap features2 = stage.get();
    assertSame(features.get(key), features2.get(key));
  }
  
  private static class Key<T> extends de.team33.libs.identification.v1.Key<T> implements FeatureMap.Key<T> {}
}
