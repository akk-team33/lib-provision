package de.team33.libs.provision.vX;

import java.util.*;

public class LazySupplyD<C> extends LazySupply<C> {

    private Map<Object, Object> map = Collections.emptyMap();

    public LazySupplyD(final C context) {
        super(context);
    }

    @Override
    public final <R> R get(final Key<? super C, R> key) {
        //noinspection unchecked
        return Optional.ofNullable((R) map.get(key)).orElseGet(() -> {
            try {
                synchronized (key) {
                    synchronized (this) {
                        final R result = key.init(context);
                        final Map<Object, Object> newMap = new CustomMap(map);
                        newMap.put(key, result);
                        map = newMap;
                        return result;
                    }
                }
            } catch (final RuntimeException caught) {
                throw caught;
            } catch (final Exception caught) {
                throw new EnvelopeException(caught);
            }
        });
    }

    private class CustomMap implements Map<Object, Object> {

        private final List<Object> keys = new LinkedList<>();
        private final List<Object> values = new LinkedList<>();

        public CustomMap(final Map<Object, Object> map) {
            map.forEach((k,v) -> {
                keys.add(k);
                values.add(v);
            });
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public boolean containsKey(final Object key) {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public boolean containsValue(final Object value) {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public Object get(final Object key) {
            final int index = keys.indexOf(key);
            if (0 > index) {
                return null;
            } else {
                return values.get(index);
            }
        }

        @Override
        public Object put(final Object key, final Object value) {
            final int index = keys.indexOf(key);
            if (0 > index) {
                keys.add(key);
                values.add(value);
                return null;
            } else {
                final Object result = values.get(index);
                values.set(index, value);
                return result;
            }
        }

        @Override
        public Object remove(final Object key) {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public void putAll(final Map<?, ?> m) {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public Set<Object> keySet() {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public Collection<Object> values() {
            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            return new AbstractSet<Entry<Object, Object>>() {
                @Override
                public Iterator<Entry<Object, Object>> iterator() {
                    return new EntryIterator();
                }

                @Override
                public int size() {
                    return keys.size();
                }
            };
        }

        private class EntryIterator implements Iterator<Entry<Object, Object>> {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < keys.size();
            }

            @Override
            public Entry<Object, Object> next() {
                return next(new AbstractMap.SimpleImmutableEntry<>(keys.get(index), values.get(index)));
            }

            private Entry<Object, Object> next(final Entry<Object, Object> entry) {
                index += 1;
                return entry;
            }
        }
    }
}
