package de.team33.test.provision.v1;

import static de.team33.libs.exceptional.v2.Wrapper.function;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

import de.team33.libs.exceptional.v2.Inspector;
import de.team33.libs.provision.v1.Cache;
import org.junit.Test;


public class CacheTest
{

  private static final long LONG_A = 123456789L;

  private int count = 0;

  private Date newDate(final long time)
  {
    count += 1;
    return new Date(time);
  }

  @Test
  public void getSame()
  {
    final Cache<Long, Long, Date> cache = new Cache<>(value -> value, this::newDate);
    assertEquals(0, count);
    assertSame(cache.get(LONG_A), cache.get(LONG_A));
    assertSame(cache.get(LONG_A), cache.get(LONG_A));
    assertEquals(1, count);
  }

  @Test
  public void getExceptional()
  {
    final XCache cache = new XCache();
    final Path path = Paths.get("no", "where", "any.file").toAbsolutePath().normalize();
    try {
      final byte[] result1 = cache.get(path);
      fail("should fail but was: " + Arrays.toString(result1));
    } catch (final IOException caught1) {
      try {
        final byte[] result2 = cache.get(path);
        fail("should fail but was: " + Arrays.toString(result2));
      } catch (final IOException caught2) {
        assertEquals(caught1.toString(), caught2.toString());
      }
    }
  }

  private static class XCache {

    private static final Inspector<IOException> INSPECTOR = Inspector.expect(IOException.class);

    private final Cache<Path, String, byte[]> backing = new Cache<>(Path::toString,
                                                                    function(Files::readAllBytes));

    byte[] get(final Path path) throws IOException
    {
      return INSPECTOR.get(() ->backing.get(path));
    }
  }
}
