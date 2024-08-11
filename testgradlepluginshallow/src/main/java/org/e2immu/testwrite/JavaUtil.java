package org.e2immu.testwrite;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
public class JavaUtil {
    public static final String PACKAGE_NAME = "java.util";

    class AbstractCollection$<E> {
        AbstractCollection$() { }
        Iterator<E> iterator() { return null; }
        int size() { return 0; }
        boolean isEmpty() { return false; }
        boolean contains(Object o) { return false; }
        Object[] toArray() { return null; }
        <T> T[] toArray(T[] a) { return null; }
        static <T> T[] finishToArray(T[] r, Iterator<?> it) { return null; }
        boolean add(E e) { return false; }
        boolean remove(Object o) { return false; }
        boolean containsAll(Collection<?> c) { return false; }
        boolean addAll(Collection<? extends E> c) { return false; }
        boolean removeAll(Collection<?> c) { return false; }
        boolean retainAll(Collection<?> c) { return false; }
        void clear() { }
        public String toString() { return null; }
    }

    class SimpleImmutableEntry<K, V> {
        SimpleImmutableEntry(K key, V value) { }
        SimpleImmutableEntry(Entry<? extends K, ? extends V> entry) { }
        K getKey() { return null; }
        V getValue() { return null; }
        V setValue(V value) { return null; }
        public boolean equals(Object o) { return false; }
        public int hashCode() { return 0; }
        public String toString() { return null; }
    }

    class SimpleEntry<K, V> {
        SimpleEntry(K key, V value) { }
        SimpleEntry(Entry<? extends K, ? extends V> entry) { }
        K getKey() { return null; }
        V getValue() { return null; }
        V setValue(V value) { return null; }
        public boolean equals(Object o) { return false; }
        public int hashCode() { return 0; }
        public String toString() { return null; }
    }

    class AbstractMap$<K, V> {
        AbstractMap$() { }
        int size() { return 0; }
        boolean isEmpty() { return false; }
        boolean containsValue(Object value) { return false; }
        boolean containsKey(Object key) { return false; }
        V get(Object key) { return null; }
        V put(K key, V value) { return null; }
        V remove(Object key) { return null; }
        void putAll(Map<? extends K, ? extends V> m) { }
        void clear() { }
        Set<K> keySet() { return null; }
        Collection<V> values() { return null; }
        Set<Entry<K, V>> entrySet() { return null; }
        public boolean equals(Object o) { return false; }
        public int hashCode() { return 0; }
        public String toString() { return null; }
        protected Object clone() { return null; }
        static boolean eq(Object o1, Object o2) { return false; }
    }

    class AbstractSet$<E> {
        AbstractSet$() { }
        public boolean equals(Object o) { return false; }
        public int hashCode() { return 0; }
        boolean removeAll(Collection<?> c) { return false; }
    }

    class Collection$<E> {
        int size() { return 0; }
        boolean isEmpty() { return false; }
        boolean contains(Object object) { return false; }
        Iterator<E> iterator() { return null; }
        Object[] toArray() { return null; }
        <T> T[] toArray(T[] t) { return null; }
        <T> T[] toArray(IntFunction<T[]> generator) { return null; }
        boolean add(E e) { return false; }
        boolean remove(Object object) { return false; }
        boolean containsAll(Collection<?> collection) { return false; }
        boolean addAll(Collection<? extends E> collection) { return false; }
        boolean removeAll(Collection<?> collection) { return false; }
        boolean removeIf(Predicate<? super E> filter) { return false; }
        boolean retainAll(Collection<?> collection) { return false; }
        void clear() { }
        public boolean equals(Object object) { return false; }
        public int hashCode() { return 0; }
        Spliterator<E> spliterator() { return null; }
        Stream<E> stream() { return null; }
        Stream<E> parallelStream() { return null; }
    }

    class Comparator$<T> {
        int compare(T t, T t1) { return 0; }
        public boolean equals(Object object) { return false; }
        Comparator<T> reversed() { return null; }
        Comparator<T> thenComparing(Comparator<? super T> other) { return null; }

        <U> Comparator<T> thenComparing(
            Function<? super T, ? extends U> keyExtractor,
            Comparator<? super U> keyComparator) { return null; }

        <U> Comparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor) { return null; }
        Comparator<T> thenComparingInt(ToIntFunction<? super T> keyExtractor) { return null; }
        Comparator<T> thenComparingLong(ToLongFunction<? super T> keyExtractor) { return null; }
        Comparator<T> thenComparingDouble(ToDoubleFunction<? super T> keyExtractor) { return null; }
        static <T> Comparator<T> reverseOrder() { return null; }
        static <T> Comparator<T> naturalOrder() { return null; }
        static <T> Comparator<T> nullsFirst(Comparator<? super T> comparator) { return null; }
        static <T> Comparator<T> nullsLast(Comparator<? super T> comparator) { return null; }

        static <T, U> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor,
            Comparator<? super U> keyComparator) { return null; }

        static <T, U> Comparator<T> comparing(Function<? super T, ? extends U> keyExtractor) { return null; }
        static <T> Comparator<T> comparingInt(ToIntFunction<? super T> keyExtractor) { return null; }
        static <T> Comparator<T> comparingLong(ToLongFunction<? super T> keyExtractor) { return null; }
        static <T> Comparator<T> comparingDouble(ToDoubleFunction<? super T> keyExtractor) { return null; }
    }

    class Enumeration$<E> {
        boolean hasMoreElements() { return false; }
        E nextElement() { return null; }
        Iterator<E> asIterator() { return null; }
    }

    class Entry<K, V> {
        K getKey() { return null; }
        V getValue() { return null; }
        V setValue(V v) { return null; }
        public boolean equals(Object object) { return false; }
        public int hashCode() { return 0; }
        static <K, V> Comparator<Entry<K, V>> comparingByKey() { return null; }
        static <K, V> Comparator<Entry<K, V>> comparingByValue() { return null; }
        static <K, V> Comparator<Entry<K, V>> comparingByKey(Comparator<? super K> cmp) { return null; }
        static <K, V> Comparator<Entry<K, V>> comparingByValue(Comparator<? super V> cmp) { return null; }
        static <K, V> Entry<K, V> copyOf(Entry<? extends K, ? extends V> e) { return null; }
    }

    class Map$<K, V> {
        int size() { return 0; }
        boolean isEmpty() { return false; }
        boolean containsKey(Object object) { return false; }
        boolean containsValue(Object object) { return false; }
        V get(Object object) { return null; }
        V put(K k, V v) { return null; }
        V remove(Object object) { return null; }
        void putAll(Map<? extends K, ? extends V> map) { }
        void clear() { }
        Set<K> keySet() { return null; }
        Collection<V> values() { return null; }
        Set<Entry<K, V>> entrySet() { return null; }
        public boolean equals(Object object) { return false; }
        public int hashCode() { return 0; }
        V getOrDefault(Object key, V defaultValue) { return null; }
        void forEach(BiConsumer<? super K, ? super V> action) { }
        void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) { }
        V putIfAbsent(K key, V value) { return null; }
        boolean remove(Object key, Object value) { return false; }
        boolean replace(K key, V oldValue, V newValue) { return false; }
        V replace(K key, V value) { return null; }
        V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) { return null; }
        V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) { return null; }
        V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) { return null; }
        V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) { return null; }
        static <K, V> Map<K, V> of() { return null; }
        static <K, V> Map<K, V> of(K k1, V v1) { return null; }
        static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) { return null; }
        static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) { return null; }
        static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) { return null; }
        static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) { return null; }

        static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
            return null;
        }

        static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
            return null;
        }

        static <K, V> Map<K, V> of(
            K k1,
            V v1,
            K k2,
            V v2,
            K k3,
            V v3,
            K k4,
            V v4,
            K k5,
            V v5,
            K k6,
            V v6,
            K k7,
            V v7,
            K k8,
            V v8) { return null; }

        static <K, V> Map<K, V> of(
            K k1,
            V v1,
            K k2,
            V v2,
            K k3,
            V v3,
            K k4,
            V v4,
            K k5,
            V v5,
            K k6,
            V v6,
            K k7,
            V v7,
            K k8,
            V v8,
            K k9,
            V v9) { return null; }

        static <K, V> Map<K, V> of(
            K k1,
            V v1,
            K k2,
            V v2,
            K k3,
            V v3,
            K k4,
            V v4,
            K k5,
            V v5,
            K k6,
            V v6,
            K k7,
            V v7,
            K k8,
            V v8,
            K k9,
            V v9,
            K k10,
            V v10) { return null; }

        static <K, V> Map<K, V> ofEntries(Entry<? extends K, ? extends V> ... entries) { return null; }
        static <K, V> Entry<K, V> entry(K k, V v) { return null; }
        static <K, V> Map<K, V> copyOf(Map<? extends K, ? extends V> map) { return null; }
    }

    class Set$<E> {
        int size() { return 0; }
        boolean isEmpty() { return false; }
        boolean contains(Object object) { return false; }
        Iterator<E> iterator() { return null; }
        Object[] toArray() { return null; }
        <T> T[] toArray(T[] t) { return null; }
        boolean add(E e) { return false; }
        boolean remove(Object object) { return false; }
        boolean containsAll(Collection<?> collection) { return false; }
        boolean addAll(Collection<? extends E> collection) { return false; }
        boolean retainAll(Collection<?> collection) { return false; }
        boolean removeAll(Collection<?> collection) { return false; }
        void clear() { }
        public boolean equals(Object object) { return false; }
        public int hashCode() { return 0; }
        Spliterator<E> spliterator() { return null; }
        static <E> Set<E> of() { return null; }
        static <E> Set<E> of(E e1) { return null; }
        static <E> Set<E> of(E e1, E e2) { return null; }
        static <E> Set<E> of(E e1, E e2, E e3) { return null; }
        static <E> Set<E> of(E e1, E e2, E e3, E e4) { return null; }
        static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5) { return null; }
        static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6) { return null; }
        static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) { return null; }
        static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) { return null; }
        static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) { return null; }
        static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) { return null; }
        static <E> Set<E> of(E... elements) { return null; }
        static <E> Set<E> copyOf(Collection<? extends E> coll) { return null; }
    }

    class OfDouble {
        OfDouble trySplit() { return null; }
        boolean tryAdvance(DoubleConsumer doubleConsumer) { return false; }
        void forEachRemaining(DoubleConsumer action) { }
        boolean tryAdvance(Consumer<? super Double> action) { return false; }
        void forEachRemaining(Consumer<? super Double> action) { }
    }

    class OfLong {
        OfLong trySplit() { return null; }
        boolean tryAdvance(LongConsumer longConsumer) { return false; }
        void forEachRemaining(LongConsumer action) { }
        boolean tryAdvance(Consumer<? super Long> action) { return false; }
        void forEachRemaining(Consumer<? super Long> action) { }
    }

    class OfInt {
        OfInt trySplit() { return null; }
        boolean tryAdvance(IntConsumer intConsumer) { return false; }
        void forEachRemaining(IntConsumer action) { }
        boolean tryAdvance(Consumer<? super Integer> action) { return false; }
        void forEachRemaining(Consumer<? super Integer> action) { }
    }

    class OfPrimitive<T, T_CONS, T_SPLITR> {
        T_SPLITR trySplit() { return null; }
        boolean tryAdvance(T_CONS t_CONS) { return false; }
        void forEachRemaining(T_CONS action) { }
    }

    class Spliterator$<T> {
        static final int ORDERED = 0;
        static final int DISTINCT = 0;
        static final int SORTED = 0;
        static final int SIZED = 0;
        static final int NONNULL = 0;
        static final int IMMUTABLE = 0;
        static final int CONCURRENT = 0;
        static final int SUBSIZED = 0;
        boolean tryAdvance(Consumer<? super T> consumer) { return false; }
        void forEachRemaining(Consumer<? super T> action) { }
        Spliterator<T> trySplit() { return null; }
        long estimateSize() { return 0; }
        long getExactSizeIfKnown() { return 0; }
        int characteristics() { return 0; }
        boolean hasCharacteristics(int characteristics) { return false; }
        Comparator<? super T> getComparator() { return null; }
    }
}
