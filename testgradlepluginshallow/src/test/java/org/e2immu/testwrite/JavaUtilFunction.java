package org.e2immu.testwrite;
import java.util.Comparator;
import java.util.function.*;
public class JavaUtilFunction {
    public static final String PACKAGE_NAME = "java.util.function";

    class BiConsumer$<T, U> {
        void accept(T t, U u) { }
        BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after) { return null; }
    }

    class BiFunction$<T, U, R> {
        R apply(T t, U u) { return null; }
        <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) { return null; }
    }

    class BiPredicate$<T, U> {
        boolean test(T t, U u) { return false; }
        BiPredicate<T, U> and(BiPredicate<? super T, ? super U> other) { return null; }
        BiPredicate<T, U> negate() { return null; }
        BiPredicate<T, U> or(BiPredicate<? super T, ? super U> other) { return null; }
    }

    class BinaryOperator$<T> {
        static <T> BinaryOperator<T> minBy(Comparator<? super T> comparator) { return null; }
        static <T> BinaryOperator<T> maxBy(Comparator<? super T> comparator) { return null; }
    }

    class BooleanSupplier$ { boolean getAsBoolean() { return false; } }
    class Consumer$<T> { void accept(T t) { } Consumer<T> andThen(Consumer<? super T> after) { return null; } }
    class DoubleBinaryOperator$ { double applyAsDouble(double d, double d1) { return 0.0; } }
    class DoubleConsumer$ { void accept(double d) { } DoubleConsumer andThen(DoubleConsumer after) { return null; } }
    class DoubleFunction$<R> { R apply(double d) { return null; } }

    class DoublePredicate$ {
        boolean test(double d) { return false; }
        DoublePredicate and(DoublePredicate other) { return null; }
        DoublePredicate negate() { return null; }
        DoublePredicate or(DoublePredicate other) { return null; }
    }

    class DoubleSupplier$ { double getAsDouble() { return 0.0; } }
    class DoubleToIntFunction$ { int applyAsInt(double d) { return 0; } }
    class DoubleToLongFunction$ { long applyAsLong(double d) { return 0; } }

    class DoubleUnaryOperator$ {
        double applyAsDouble(double d) { return 0.0; }
        DoubleUnaryOperator compose(DoubleUnaryOperator before) { return null; }
        DoubleUnaryOperator andThen(DoubleUnaryOperator after) { return null; }
        static DoubleUnaryOperator identity() { return null; }
    }

    class Function$<T, R> {
        R apply(T t) { return null; }
        <V> Function<V, R> compose(Function<? super V, ? extends T> before) { return null; }
        <V> Function<T, V> andThen(Function<? super R, ? extends V> after) { return null; }
        static <T> Function<T, T> identity() { return null; }
    }

    class IntBinaryOperator$ { int applyAsInt(int i, int i1) { return 0; } }
    class IntConsumer$ { void accept(int i) { } IntConsumer andThen(IntConsumer after) { return null; } }
    class IntFunction$<R> { R apply(int i) { return null; } }

    class IntPredicate$ {
        boolean test(int i) { return false; }
        IntPredicate and(IntPredicate other) { return null; }
        IntPredicate negate() { return null; }
        IntPredicate or(IntPredicate other) { return null; }
    }

    class IntSupplier$ { int getAsInt() { return 0; } }
    class IntToDoubleFunction$ { double applyAsDouble(int i) { return 0.0; } }
    class IntToLongFunction$ { long applyAsLong(int i) { return 0; } }

    class IntUnaryOperator$ {
        int applyAsInt(int i) { return 0; }
        IntUnaryOperator compose(IntUnaryOperator before) { return null; }
        IntUnaryOperator andThen(IntUnaryOperator after) { return null; }
        static IntUnaryOperator identity() { return null; }
    }

    class LongBinaryOperator$ { long applyAsLong(long l, long l1) { return 0; } }
    class LongConsumer$ { void accept(long l) { } LongConsumer andThen(LongConsumer after) { return null; } }
    class LongFunction$<R> { R apply(long l) { return null; } }

    class LongPredicate$ {
        boolean test(long l) { return false; }
        LongPredicate and(LongPredicate other) { return null; }
        LongPredicate negate() { return null; }
        LongPredicate or(LongPredicate other) { return null; }
    }

    class LongSupplier$ { long getAsLong() { return 0; } }
    class LongToDoubleFunction$ { double applyAsDouble(long l) { return 0.0; } }
    class LongToIntFunction$ { int applyAsInt(long l) { return 0; } }

    class LongUnaryOperator$ {
        long applyAsLong(long l) { return 0; }
        LongUnaryOperator compose(LongUnaryOperator before) { return null; }
        LongUnaryOperator andThen(LongUnaryOperator after) { return null; }
        static LongUnaryOperator identity() { return null; }
    }

    class ObjDoubleConsumer$<T> { void accept(T t, double d) { } }
    class ObjIntConsumer$<T> { void accept(T t, int i) { } }
    class ObjLongConsumer$<T> { void accept(T t, long l) { } }

    class Predicate$<T> {
        boolean test(T t) { return false; }
        Predicate<T> and(Predicate<? super T> other) { return null; }
        Predicate<T> negate() { return null; }
        Predicate<T> or(Predicate<? super T> other) { return null; }
        static <T> Predicate<T> isEqual(Object targetRef) { return null; }
        static <T> Predicate<T> not(Predicate<? super T> target) { return null; }
    }

    class Supplier$<T> { T get() { return null; } }
    class ToDoubleBiFunction$<T, U> { double applyAsDouble(T t, U u) { return 0.0; } }
    class ToDoubleFunction$<T> { double applyAsDouble(T t) { return 0.0; } }
    class ToIntBiFunction$<T, U> { int applyAsInt(T t, U u) { return 0; } }
    class ToIntFunction$<T> { int applyAsInt(T t) { return 0; } }
    class ToLongBiFunction$<T, U> { long applyAsLong(T t, U u) { return 0; } }
    class ToLongFunction$<T> { long applyAsLong(T t) { return 0; } }
    class UnaryOperator$<T> { static <T> UnaryOperator<T> identity() { return null; } }
}
