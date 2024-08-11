package org.e2immu.testwrite;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessControlContext;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.*;
public class JavaUtilConcurrent {
    public static final String PACKAGE_NAME = "java.util.concurrent";

    class AbstractExecutorService$ {
        AbstractExecutorService$() { }
        <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) { return null; }
        <T> RunnableFuture<T> newTaskFor(Callable<T> callable) { return null; }
        Future<?> submit(Runnable task) { return null; }
        <T> Future<T> submit(Runnable task, T result) { return null; }
        <T> Future<T> submit(Callable<T> task) { return null; }
        <T> T doInvokeAny(Collection<? extends Callable<T>> tasks, boolean timed, long nanos) { return null; }
        <T> T invokeAny(Collection<? extends Callable<T>> tasks) { return null; }
        <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit timeUnit) { return null; }
        <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) { return null; }

        <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit timeUnit) {
            return null;
        }

        static <T> void cancelAll(ArrayList<Future<T>> futures) { }
        static <T> void cancelAll(ArrayList<Future<T>> futures, int j) { }
    }

    class Completion {
        Completion() { }
        CompletableFuture<?> tryFire(int i) { return null; }
        boolean isLive() { return false; }
        void run() { }
        boolean exec() { return false; }
        Void getRawResult() { return null; }
        void setRawResult(Void v) { }
    }

    class AltResult { AltResult(Throwable x) { } }

    class UniApply<T, V> {
        UniApply(
            Executor executor,
            CompletableFuture<V> dep,
            CompletableFuture<T> src,
            Function<? super T, ? extends V> fn) { }
        CompletableFuture<V> tryFire(int mode) { return null; }
    }

    class UniAccept<T> {
        UniAccept(Executor executor, CompletableFuture<Void> dep, CompletableFuture<T> src, Consumer<? super T> fn) { }
        CompletableFuture<Void> tryFire(int mode) { return null; }
    }

    class UniRun<T> {
        UniRun(Executor executor, CompletableFuture<Void> dep, CompletableFuture<T> src, Runnable fn) { }
        CompletableFuture<Void> tryFire(int mode) { return null; }
    }

    class UniWhenComplete<T> {
        UniWhenComplete(
            Executor executor,
            CompletableFuture<T> dep,
            CompletableFuture<T> src,
            BiConsumer<? super T, ? super Throwable> fn) { }
        CompletableFuture<T> tryFire(int mode) { return null; }
    }

    class UniHandle<T, V> {
        UniHandle(
            Executor executor,
            CompletableFuture<V> dep,
            CompletableFuture<T> src,
            BiFunction<? super T, Throwable, ? extends V> fn) { }
        CompletableFuture<V> tryFire(int mode) { return null; }
    }

    class UniExceptionally<T> {
        UniExceptionally(
            Executor executor,
            CompletableFuture<T> dep,
            CompletableFuture<T> src,
            Function<? super Throwable, ? extends T> fn) { }
        CompletableFuture<T> tryFire(int mode) { return null; }
    }

    class UniComposeExceptionally<T> {
        UniComposeExceptionally(
            Executor executor,
            CompletableFuture<T> dep,
            CompletableFuture<T> src,
            Function<Throwable, ? extends CompletionStage<T>> fn) { }
        CompletableFuture<T> tryFire(int mode) { return null; }
    }

    class UniRelay<U, T> {
        UniRelay(CompletableFuture<U> dep, CompletableFuture<T> src) { }
        CompletableFuture<U> tryFire(int mode) { return null; }
    }

    class MinimalStage<T> {
        MinimalStage() { }
        MinimalStage(Object r) { }
        <U> CompletableFuture<U> newIncompleteFuture() { return null; }
        T get() { return null; }
        T get(long timeout, TimeUnit timeUnit) { return null; }
        T getNow(T valueIfAbsent) { return null; }
        T join() { return null; }
        boolean complete(T value) { return false; }
        boolean completeExceptionally(Throwable ex) { return false; }
        boolean cancel(boolean mayInterruptIfRunning) { return false; }
        void obtrudeValue(T value) { }
        void obtrudeException(Throwable ex) { }
        boolean isDone() { return false; }
        boolean isCancelled() { return false; }
        boolean isCompletedExceptionally() { return false; }
        int getNumberOfDependents() { return 0; }
        CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor) { return null; }
        CompletableFuture<T> completeAsync(Supplier<? extends T> supplier) { return null; }
        CompletableFuture<T> orTimeout(long timeout, TimeUnit timeUnit) { return null; }
        CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit timeUnit) { return null; }
        CompletableFuture<T> toCompletableFuture() { return null; }
    }

    class UniCompose<T, V> {
        UniCompose(
            Executor executor,
            CompletableFuture<V> dep,
            CompletableFuture<T> src,
            Function<? super T, ? extends CompletionStage<V>> fn) { }
        CompletableFuture<V> tryFire(int mode) { return null; }
    }

    class CoCompletion {
        CoCompletion(BiCompletion<?, ?, ?> base) { }
        CompletableFuture<?> tryFire(int mode) { return null; }
        boolean isLive() { return false; }
    }

    class BiCompletion<T, U, V> {
        BiCompletion(Executor executor, CompletableFuture<V> dep, CompletableFuture<T> src, CompletableFuture<U> snd) { }
    }

    class BiApply<T, U, V> {
        BiApply(
            Executor executor,
            CompletableFuture<V> dep,
            CompletableFuture<T> src,
            CompletableFuture<U> snd,
            BiFunction<? super T, ? super U, ? extends V> fn) { }
        CompletableFuture<V> tryFire(int mode) { return null; }
    }

    class BiAccept<T, U> {
        BiAccept(
            Executor executor,
            CompletableFuture<Void> dep,
            CompletableFuture<T> src,
            CompletableFuture<U> snd,
            BiConsumer<? super T, ? super U> fn) { }
        CompletableFuture<Void> tryFire(int mode) { return null; }
    }

    class BiRun<T, U> {
        BiRun(
            Executor executor,
            CompletableFuture<Void> dep,
            CompletableFuture<T> src,
            CompletableFuture<U> snd,
            Runnable fn) { }
        CompletableFuture<Void> tryFire(int mode) { return null; }
    }

    class BiRelay<T, U> {
        BiRelay(CompletableFuture<Void> dep, CompletableFuture<T> src, CompletableFuture<U> snd) { }
        CompletableFuture<Void> tryFire(int mode) { return null; }
    }

    class OrApply<T, U, V> {
        OrApply(
            Executor executor,
            CompletableFuture<V> dep,
            CompletableFuture<T> src,
            CompletableFuture<U> snd,
            Function<? super T, ? extends V> fn) { }
        CompletableFuture<V> tryFire(int mode) { return null; }
    }

    class OrAccept<T, U> {
        OrAccept(
            Executor executor,
            CompletableFuture<Void> dep,
            CompletableFuture<T> src,
            CompletableFuture<U> snd,
            Consumer<? super T> fn) { }
        CompletableFuture<Void> tryFire(int mode) { return null; }
    }

    class OrRun<T, U> {
        OrRun(
            Executor executor,
            CompletableFuture<Void> dep,
            CompletableFuture<T> src,
            CompletableFuture<U> snd,
            Runnable fn) { }
        CompletableFuture<Void> tryFire(int mode) { return null; }
    }

    class AsyncSupply<T> {
        AsyncSupply(CompletableFuture<T> dep, Supplier<? extends T> fn) { }
        Void getRawResult() { return null; }
        void setRawResult(Void v) { }
        boolean exec() { return false; }
        void run() { }
    }

    class AsyncRun {
        AsyncRun(CompletableFuture<Void> dep, Runnable fn) { }
        Void getRawResult() { return null; }
        void setRawResult(Void v) { }
        boolean exec() { return false; }
        void run() { }
    }

    class Signaller {
        Signaller(boolean interruptible, long nanos, long l) { }
        CompletableFuture<?> tryFire(int ignore) { return null; }
        boolean isReleasable() { return false; }
        boolean block() { return false; }
        boolean isLive() { return false; }
    }

    class AnyOf {
        AnyOf(CompletableFuture<Object> dep, CompletableFuture<?> src, CompletableFuture<?> [] srcs) { }
        CompletableFuture<Object> tryFire(int mode) { return null; }
        boolean isLive() { return false; }
    }

    class Canceller { Canceller(Future<?> f) { } void accept(Object ignore, Throwable ex) { } }
    class Timeout { Timeout(CompletableFuture<?> f) { } void run() { } }
    class DaemonThreadFactory { DaemonThreadFactory() { } Thread newThread(Runnable r) { return null; } }

    class Delayer {
        Delayer() { }
        static ScheduledFuture<?> delay(Runnable command, long delay, TimeUnit timeUnit) { return null; }
    }

    class DelayedCompleter<U> { DelayedCompleter(CompletableFuture<U> f, U u) { } void run() { } }

    class DelayedExecutor {
        DelayedExecutor(long delay, TimeUnit timeUnit, Executor unit) { }
        void execute(Runnable r) { }
    }

    class ThreadPerTaskExecutor { ThreadPerTaskExecutor() { } void execute(Runnable r) { } }
    class TaskSubmitter { TaskSubmitter(Executor executor, Runnable action) { } void run() { } }

    class UniCompletion<T, V> {
        UniCompletion(Executor executor, CompletableFuture<V> dep, CompletableFuture<T> src) { }
        boolean claim() { return false; }
        boolean isLive() { return false; }
    }

    class AsynchronousCompletionTask { }

    class CompletableFuture$<T> {
        CompletableFuture$() { }
        CompletableFuture$(Object r) { }
        boolean internalComplete(Object r) { return false; }
        boolean tryPushStack(Completion c) { return false; }
        void pushStack(Completion c) { }
        boolean completeNull() { return false; }
        Object encodeValue(T t) { return null; }
        boolean completeValue(T t) { return false; }
        static AltResult encodeThrowable(Throwable x) { return null; }
        boolean completeThrowable(Throwable x) { return false; }
        static Object encodeThrowable(Throwable x, Object r) { return null; }
        boolean completeThrowable(Throwable x, Object r) { return false; }
        Object encodeOutcome(T t, Throwable x) { return null; }
        static Object encodeRelay(Object r) { return null; }
        boolean completeRelay(Object r) { return false; }
        static Object reportGet(Object r) { return null; }
        static Object reportJoin(Object r) { return null; }
        static Executor screenExecutor(Executor e) { return null; }
        void postComplete() { }
        void cleanStack() { }
        void unipush(Completion c) { }
        CompletableFuture<T> postFire(CompletableFuture<?> a, int mode) { return null; }
        <V> CompletableFuture<V> uniApplyStage(Executor e, Function<? super T, ? extends V> f) { return null; }
        <V> CompletableFuture<V> uniApplyNow(Object r, Executor e, Function<? super T, ? extends V> f) { return null; }
        CompletableFuture<Void> uniAcceptStage(Executor e, Consumer<? super T> f) { return null; }
        CompletableFuture<Void> uniAcceptNow(Object r, Executor e, Consumer<? super T> f) { return null; }
        CompletableFuture<Void> uniRunStage(Executor e, Runnable f) { return null; }
        CompletableFuture<Void> uniRunNow(Object r, Executor e, Runnable f) { return null; }

        boolean uniWhenComplete(Object r, BiConsumer<? super T, ? super Throwable> f, UniWhenComplete<T> c) {
            return false;
        }

        CompletableFuture<T> uniWhenCompleteStage(Executor e, BiConsumer<? super T, ? super Throwable> f) {
            return null;
        }

        <S> boolean uniHandle(Object r, BiFunction<? super S, Throwable, ? extends T> f, UniHandle<S, T> c) {
            return false;
        }

        <V> CompletableFuture<V> uniHandleStage(Executor e, BiFunction<? super T, Throwable, ? extends V> f) {
            return null;
        }

        boolean uniExceptionally(Object r, Function<? super Throwable, ? extends T> f, UniExceptionally<T> c) {
            return false;
        }

        CompletableFuture<T> uniExceptionallyStage(Executor e, Function<Throwable, ? extends T> f) { return null; }

        CompletableFuture<T> uniComposeExceptionallyStage(
            Executor e,
            Function<Throwable, ? extends CompletionStage<T>> f) { return null; }

        static <U, T> CompletableFuture<U> uniCopyStage(CompletableFuture<T> src) { return null; }
        MinimalStage<T> uniAsMinimalStage() { return null; }

        <V> CompletableFuture<V> uniComposeStage(Executor e, Function<? super T, ? extends CompletionStage<V>> f) {
            return null;
        }

        void bipush(CompletableFuture<?> b, BiCompletion<?, ?, ?> c) { }
        CompletableFuture<T> postFire(CompletableFuture<?> a, CompletableFuture<?> b, int mode) { return null; }

        <R, S> boolean biApply(Object r, Object s, BiFunction<? super R, ? super S, ? extends T> f, BiApply<R, S, T> c) {
            return false;
        }

        <U, V> CompletableFuture<V> biApplyStage(
            Executor e,
            CompletionStage<U> o,
            BiFunction<? super T, ? super U, ? extends V> f) { return null; }

        <R, S> boolean biAccept(Object r, Object s, BiConsumer<? super R, ? super S> f, BiAccept<R, S> c) {
            return false;
        }

        <U> CompletableFuture<Void> biAcceptStage(Executor e, CompletionStage<U> o, BiConsumer<? super T, ? super U> f) {
            return null;
        }

        boolean biRun(Object r, Object s, Runnable f, BiRun<?, ?> c) { return false; }
        CompletableFuture<Void> biRunStage(Executor e, CompletionStage<?> o, Runnable f) { return null; }
        static CompletableFuture<Void> andTree(CompletableFuture<?> [] cfs, int lo, int hi) { return null; }
        void orpush(CompletableFuture<?> b, BiCompletion<?, ?, ?> c) { }

        <U, V> CompletableFuture<V> orApplyStage(Executor e, CompletionStage<U> o, Function<? super T, ? extends V> f) {
            return null;
        }

        <U> CompletableFuture<Void> orAcceptStage(Executor e, CompletionStage<U> o, Consumer<? super T> f) {
            return null;
        }

        CompletableFuture<Void> orRunStage(Executor e, CompletionStage<?> o, Runnable f) { return null; }
        static <U> CompletableFuture<U> asyncSupplyStage(Executor e, Supplier<U> f) { return null; }
        static CompletableFuture<Void> asyncRunStage(Executor e, Runnable f) { return null; }
        Object waitingGet(boolean interruptible) { return null; }
        Object timedGet(long nanos) { return null; }
        static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) { return null; }
        static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor) { return null; }
        static CompletableFuture<Void> runAsync(Runnable runnable) { return null; }
        static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor) { return null; }
        static <U> CompletableFuture<U> completedFuture(U value) { return null; }
        boolean isDone() { return false; }
        T get() { return null; }
        T get(long timeout, TimeUnit timeUnit) { return null; }
        T join() { return null; }
        T getNow(T valueIfAbsent) { return null; }
        boolean complete(T value) { return false; }
        boolean completeExceptionally(Throwable ex) { return false; }
        <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> fn) { return null; }
        <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) { return null; }
        <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) { return null; }
        CompletableFuture<Void> thenAccept(Consumer<? super T> action) { return null; }
        CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) { return null; }
        CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) { return null; }
        CompletableFuture<Void> thenRun(Runnable action) { return null; }
        CompletableFuture<Void> thenRunAsync(Runnable action) { return null; }
        CompletableFuture<Void> thenRunAsync(Runnable action, Executor executor) { return null; }

        <U, V> CompletableFuture<V> thenCombine(
            CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) { return null; }

        <U, V> CompletableFuture<V> thenCombineAsync(
            CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) { return null; }

        <U, V> CompletableFuture<V> thenCombineAsync(
            CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn,
            Executor executor) { return null; }

        <U> CompletableFuture<Void> thenAcceptBoth(
            CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) { return null; }

        <U> CompletableFuture<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) { return null; }

        <U> CompletableFuture<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action,
            Executor executor) { return null; }

        CompletableFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) { return null; }
        CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) { return null; }

        CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
            return null;
        }

        <U> CompletableFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
            return null;
        }

        <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
            return null;
        }

        <U> CompletableFuture<U> applyToEitherAsync(
            CompletionStage<? extends T> other,
            Function<? super T, U> fn,
            Executor executor) { return null; }

        CompletableFuture<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
            return null;
        }

        CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
            return null;
        }

        CompletableFuture<Void> acceptEitherAsync(
            CompletionStage<? extends T> other,
            Consumer<? super T> action,
            Executor executor) { return null; }

        CompletableFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) { return null; }
        CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) { return null; }

        CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
            return null;
        }

        <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) { return null; }
        <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) { return null; }

        <U> CompletableFuture<U> thenComposeAsync(
            Function<? super T, ? extends CompletionStage<U>> fn,
            Executor executor) { return null; }

        CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) { return null; }
        CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) { return null; }

        CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
            return null;
        }

        <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) { return null; }
        <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) { return null; }

        <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
            return null;
        }

        CompletableFuture<T> toCompletableFuture() { return null; }
        CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) { return null; }
        CompletableFuture<T> exceptionallyAsync(Function<Throwable, ? extends T> fn) { return null; }
        CompletableFuture<T> exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor) { return null; }
        CompletableFuture<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn) { return null; }

        CompletableFuture<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn) {
            return null;
        }

        CompletableFuture<T> exceptionallyComposeAsync(
            Function<Throwable, ? extends CompletionStage<T>> fn,
            Executor executor) { return null; }

        static CompletableFuture<Void> allOf(CompletableFuture<?> ... cfs) { return null; }
        static CompletableFuture<Object> anyOf(CompletableFuture<?> ... cfs) { return null; }
        boolean cancel(boolean mayInterruptIfRunning) { return false; }
        boolean isCancelled() { return false; }
        boolean isCompletedExceptionally() { return false; }
        void obtrudeValue(T value) { }
        void obtrudeException(Throwable ex) { }
        int getNumberOfDependents() { return 0; }
        public String toString() { return null; }
        <U> CompletableFuture<U> newIncompleteFuture() { return null; }
        Executor defaultExecutor() { return null; }
        CompletableFuture<T> copy() { return null; }
        CompletionStage<T> minimalCompletionStage() { return null; }
        CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor) { return null; }
        CompletableFuture<T> completeAsync(Supplier<? extends T> supplier) { return null; }
        CompletableFuture<T> orTimeout(long timeout, TimeUnit timeUnit) { return null; }
        CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit timeUnit) { return null; }
        static Executor delayedExecutor(long delay, TimeUnit timeUnit, Executor unit) { return null; }
        static Executor delayedExecutor(long delay, TimeUnit timeUnit) { return null; }
        static <U> CompletionStage<U> completedStage(U value) { return null; }
        static <U> CompletableFuture<U> failedFuture(Throwable ex) { return null; }
        static <U> CompletionStage<U> failedStage(Throwable ex) { return null; }
    }

    class CompletionStage$<T> {
        <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> function) { return null; }
        <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> function) { return null; }

        <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> function, Executor executor) {
            return null;
        }

        CompletionStage<Void> thenAccept(Consumer<? super T> consumer) { return null; }
        CompletionStage<Void> thenAcceptAsync(Consumer<? super T> consumer) { return null; }
        CompletionStage<Void> thenAcceptAsync(Consumer<? super T> consumer, Executor executor) { return null; }
        CompletionStage<Void> thenRun(Runnable runnable) { return null; }
        CompletionStage<Void> thenRunAsync(Runnable runnable) { return null; }
        CompletionStage<Void> thenRunAsync(Runnable runnable, Executor executor) { return null; }

        <U, V> CompletionStage<V> thenCombine(
            CompletionStage<? extends U> completionStage,
            BiFunction<? super T, ? super U, ? extends V> biFunction) { return null; }

        <U, V> CompletionStage<V> thenCombineAsync(
            CompletionStage<? extends U> completionStage,
            BiFunction<? super T, ? super U, ? extends V> biFunction) { return null; }

        <U, V> CompletionStage<V> thenCombineAsync(
            CompletionStage<? extends U> completionStage,
            BiFunction<? super T, ? super U, ? extends V> biFunction,
            Executor executor) { return null; }

        <U> CompletionStage<Void> thenAcceptBoth(
            CompletionStage<? extends U> completionStage,
            BiConsumer<? super T, ? super U> biConsumer) { return null; }

        <U> CompletionStage<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> completionStage,
            BiConsumer<? super T, ? super U> biConsumer) { return null; }

        <U> CompletionStage<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> completionStage,
            BiConsumer<? super T, ? super U> biConsumer,
            Executor executor) { return null; }

        CompletionStage<Void> runAfterBoth(CompletionStage<?> completionStage, Runnable runnable) { return null; }
        CompletionStage<Void> runAfterBothAsync(CompletionStage<?> completionStage, Runnable runnable) { return null; }

        CompletionStage<Void> runAfterBothAsync(CompletionStage<?> completionStage, Runnable runnable, Executor executor) {
            return null;
        }

        <U> CompletionStage<U> applyToEither(
            CompletionStage<? extends T> completionStage,
            Function<? super T, U> function) { return null; }

        <U> CompletionStage<U> applyToEitherAsync(
            CompletionStage<? extends T> completionStage,
            Function<? super T, U> function) { return null; }

        <U> CompletionStage<U> applyToEitherAsync(
            CompletionStage<? extends T> completionStage,
            Function<? super T, U> function,
            Executor executor) { return null; }

        CompletionStage<Void> acceptEither(CompletionStage<? extends T> completionStage, Consumer<? super T> consumer) {
            return null;
        }

        CompletionStage<Void> acceptEitherAsync(
            CompletionStage<? extends T> completionStage,
            Consumer<? super T> consumer) { return null; }

        CompletionStage<Void> acceptEitherAsync(
            CompletionStage<? extends T> completionStage,
            Consumer<? super T> consumer,
            Executor executor) { return null; }

        CompletionStage<Void> runAfterEither(CompletionStage<?> completionStage, Runnable runnable) { return null; }

        CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> completionStage, Runnable runnable) {
            return null;
        }

        CompletionStage<Void> runAfterEitherAsync(
            CompletionStage<?> completionStage,
            Runnable runnable,
            Executor executor) { return null; }

        <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> function) { return null; }

        <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> function) {
            return null;
        }

        <U> CompletionStage<U> thenComposeAsync(
            Function<? super T, ? extends CompletionStage<U>> function,
            Executor executor) { return null; }

        <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> biFunction) { return null; }
        <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> biFunction) { return null; }

        <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> biFunction, Executor executor) {
            return null;
        }

        CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> biConsumer) { return null; }
        CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> biConsumer) { return null; }

        CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> biConsumer, Executor executor) {
            return null;
        }

        CompletionStage<T> exceptionally(Function<Throwable, ? extends T> function) { return null; }
        CompletionStage<T> exceptionallyAsync(Function<Throwable, ? extends T> fn) { return null; }
        CompletionStage<T> exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor) { return null; }
        CompletionStage<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn) { return null; }

        CompletionStage<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn) {
            return null;
        }

        CompletionStage<T> exceptionallyComposeAsync(
            Function<Throwable, ? extends CompletionStage<T>> fn,
            Executor executor) { return null; }

        CompletableFuture<T> toCompletableFuture() { return null; }
    }

    class Executor$ { void execute(Runnable runnable) { } }

    class ExecutorService$ {
        void shutdown() { }
        List<Runnable> shutdownNow() { return null; }
        boolean isShutdown() { return false; }
        boolean isTerminated() { return false; }
        boolean awaitTermination(long l, TimeUnit timeUnit) { return false; }
        <T> Future<T> submit(Callable<T> callable) { return null; }
        <T> Future<T> submit(Runnable runnable, T t) { return null; }
        Future<?> submit(Runnable runnable) { return null; }
        <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) { return null; }

        <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) {
            return null;
        }

        <T> T invokeAny(Collection<? extends Callable<T>> collection) { return null; }
        <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) { return null; }
    }

    class ForkJoinWorkerThreadFactory { ForkJoinWorkerThread newThread(ForkJoinPool forkJoinPool) { return null; } }

    class WorkQueue {
        WorkQueue(ForkJoinWorkerThread owner, boolean isInnocuous) { }
        WorkQueue(int config) { }
        static ForkJoinTask<?> getSlot(ForkJoinTask<?> [] a, int i) { return null; }
        static ForkJoinTask<?> getAndClearSlot(ForkJoinTask<?> [] a, int i) { return null; }
        static void setSlotVolatile(ForkJoinTask<?> [] a, int i, ForkJoinTask<?> v) { }
        static boolean casSlotToNull(ForkJoinTask<?> [] a, int i, ForkJoinTask<?> c) { return false; }
        boolean tryLock() { return false; }
        void setBaseOpaque(int b) { }
        int getPoolIndex() { return 0; }
        int queueSize() { return 0; }
        boolean isEmpty() { return false; }
        void push(ForkJoinTask<?> task, ForkJoinPool pool) { }
        boolean lockedPush(ForkJoinTask<?> task) { return false; }
        void growArray() { }
        ForkJoinTask<?> pop() { return null; }
        boolean tryUnpush(ForkJoinTask<?> task) { return false; }
        boolean externalTryUnpush(ForkJoinTask<?> task) { return false; }
        boolean tryRemove(ForkJoinTask<?> task, boolean owned) { return false; }
        ForkJoinTask<?> tryPoll() { return null; }
        ForkJoinTask<?> nextLocalTask(int cfg) { return null; }
        ForkJoinTask<?> nextLocalTask() { return null; }
        ForkJoinTask<?> peek() { return null; }
        void topLevelExec(ForkJoinTask<?> task, WorkQueue q) { }
        int helpComplete(ForkJoinTask<?> task, boolean owned, int limit) { return 0; }
        void helpAsyncBlocker(ManagedBlocker blocker) { }
        void initializeInnocuousWorker() { }
        boolean isApparentlyUnblocked() { return false; }
    }

    class ManagedBlocker { boolean block() { return false; } boolean isReleasable() { return false; } }

    class DefaultCommonPoolForkJoinWorkerThreadFactory {
        DefaultCommonPoolForkJoinWorkerThreadFactory() { }
        ForkJoinWorkerThread newThread(ForkJoinPool pool) { return null; }
    }

    class InvokeAnyRoot<E> {
        InvokeAnyRoot(int n, ForkJoinPool p) { }
        void tryComplete(Callable<E> c) { }
        boolean exec() { return false; }
        E getRawResult() { return null; }
        void setRawResult(E v) { }
    }

    class InvokeAnyTask<E> {
        InvokeAnyTask(InvokeAnyRoot<E> root, Callable<E> callable) { }
        boolean exec() { return false; }
        boolean cancel(boolean mayInterruptIfRunning) { return false; }
        void setRawResult(E v) { }
        E getRawResult() { return null; }
    }

    class DefaultForkJoinWorkerThreadFactory {
        DefaultForkJoinWorkerThreadFactory() { }
        ForkJoinWorkerThread newThread(ForkJoinPool pool) { return null; }
    }

    class ForkJoinPool$ {
        static final ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory = null;
        ForkJoinPool$() { }
        ForkJoinPool$(int parallelism) { }

        ForkJoinPool$(
            int parallelism,
            ForkJoinWorkerThreadFactory factory,
            Thread.UncaughtExceptionHandler handler,
            boolean asyncMode) { }

        ForkJoinPool$(
            int parallelism,
            ForkJoinWorkerThreadFactory factory,
            Thread.UncaughtExceptionHandler handler,
            boolean asyncMode,
            int corePoolSize,
            int maximumPoolSize,
            int minimumRunnable,
            Predicate<? super ForkJoinPool> saturate,
            long keepAliveTime,
            TimeUnit timeUnit) { }

        ForkJoinPool$(byte forCommonPoolOnly) { }
        static void checkPermission() { }
        static AccessControlContext contextWithPermissions(Permission... perms) { return null; }
        boolean compareAndSetCtl(long c, long l) { return false; }
        long compareAndExchangeCtl(long c, long l) { return 0; }
        long getAndAddCtl(long v) { return 0; }
        int getAndBitwiseOrMode(int v) { return 0; }
        int getAndAddThreadIds(int x) { return 0; }
        static int getAndAddPoolIds(int x) { return 0; }
        boolean createWorker() { return false; }
        String nextWorkerThreadName() { return null; }
        void registerWorker(WorkQueue w) { }
        void deregisterWorker(ForkJoinWorkerThread wt, Throwable ex) { }
        void signalWork() { }
        void runWorker(WorkQueue w) { }
        int scan(WorkQueue w, int prevSrc, int r) { return 0; }
        int awaitWork(WorkQueue w) { return 0; }
        boolean canStop() { return false; }
        int tryCompensate(long c) { return 0; }
        void uncompensate() { }
        int helpJoin(ForkJoinTask<?> task, WorkQueue w, boolean canHelp) { return 0; }
        int helpComplete(ForkJoinTask<?> task, WorkQueue w, boolean owned) { return 0; }
        ForkJoinTask<?> pollScan(boolean submissionsOnly) { return null; }
        int helpQuiescePool(WorkQueue w, long nanos, boolean b) { return 0; }
        int externalHelpQuiescePool(long nanos, boolean b) { return 0; }
        ForkJoinTask<?> nextTaskFor(WorkQueue w) { return null; }
        WorkQueue submissionQueue() { return null; }
        void externalPush(ForkJoinTask<?> task) { }
        <T> ForkJoinTask<T> externalSubmit(ForkJoinTask<T> task) { return null; }
        static WorkQueue commonQueue() { return null; }
        WorkQueue externalQueue() { return null; }
        static void helpAsyncBlocker(Executor e, ManagedBlocker blocker) { }
        static int getSurplusQueuedTaskCount() { return 0; }
        boolean tryTerminate(boolean now, boolean enable) { return false; }
        static Object newInstanceFromSystemProperty(String property) { return null; }
        static ForkJoinPool commonPool() { return null; }
        <T> T invoke(ForkJoinTask<T> task) { return null; }
        void execute(ForkJoinTask<?> task) { }
        void execute(Runnable task) { }
        <T> ForkJoinTask<T> submit(ForkJoinTask<T> task) { return null; }
        <T> ForkJoinTask<T> submit(Callable<T> task) { return null; }
        <T> ForkJoinTask<T> submit(Runnable task, T result) { return null; }
        ForkJoinTask<?> submit(Runnable task) { return null; }
        <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) { return null; }

        <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit timeUnit) {
            return null;
        }

        <T> T invokeAny(Collection<? extends Callable<T>> tasks) { return null; }
        <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit timeUnit) { return null; }
        ForkJoinWorkerThreadFactory getFactory() { return null; }
        Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() { return null; }
        int getParallelism() { return 0; }
        static int getCommonPoolParallelism() { return 0; }
        int getPoolSize() { return 0; }
        boolean getAsyncMode() { return false; }
        int getRunningThreadCount() { return 0; }
        int getActiveThreadCount() { return 0; }
        boolean isQuiescent() { return false; }
        long getStealCount() { return 0; }
        long getQueuedTaskCount() { return 0; }
        int getQueuedSubmissionCount() { return 0; }
        boolean hasQueuedSubmissions() { return false; }
        ForkJoinTask<?> pollSubmission() { return null; }
        int drainTasksTo(Collection<? super ForkJoinTask<?>> c) { return 0; }
        public String toString() { return null; }
        void shutdown() { }
        List<Runnable> shutdownNow() { return null; }
        boolean isTerminated() { return false; }
        boolean isTerminating() { return false; }
        boolean isShutdown() { return false; }
        boolean awaitTermination(long timeout, TimeUnit timeUnit) { return false; }
        boolean awaitQuiescence(long timeout, TimeUnit timeUnit) { return false; }
        static void managedBlock(ManagedBlocker blocker) { }
        void compensatedBlock(ManagedBlocker blocker) { }
        static void unmanagedBlock(ManagedBlocker blocker) { }
        <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) { return null; }
        <T> RunnableFuture<T> newTaskFor(Callable<T> callable) { return null; }
    }

    class Aux { Aux(Thread thread, Throwable ex) { } boolean casNext(Aux c, Aux v) { return false; } }

    class AdaptedRunnableAction {
        AdaptedRunnableAction(Runnable runnable) { }
        Void getRawResult() { return null; }
        void setRawResult(Void v) { }
        boolean exec() { return false; }
        void run() { }
        public String toString() { return null; }
    }

    class AdaptedRunnable<T> {
        AdaptedRunnable(Runnable runnable, T result) { }
        T getRawResult() { return null; }
        void setRawResult(T v) { }
        boolean exec() { return false; }
        void run() { }
        public String toString() { return null; }
    }

    class AdaptedCallable<T> {
        AdaptedCallable(Callable<? extends T> callable) { }
        T getRawResult() { return null; }
        void setRawResult(T v) { }
        boolean exec() { return false; }
        void run() { }
        public String toString() { return null; }
    }

    class AdaptedInterruptibleCallable<T> {
        AdaptedInterruptibleCallable(Callable<? extends T> callable) { }
        T getRawResult() { return null; }
        void setRawResult(T v) { }
        boolean exec() { return false; }
        void run() { }
        boolean cancel(boolean mayInterruptIfRunning) { return false; }
        public String toString() { return null; }
    }

    class RunnableExecuteAction {
        RunnableExecuteAction(Runnable runnable) { }
        Void getRawResult() { return null; }
        void setRawResult(Void v) { }
        boolean exec() { return false; }
        int trySetException(Throwable ex) { return 0; }
    }

    class ForkJoinTask$<V> {
        ForkJoinTask$() { }
        int getAndBitwiseOrStatus(int v) { return 0; }
        boolean casStatus(int c, int v) { return false; }
        boolean casAux(Aux c, Aux v) { return false; }
        void signalWaiters() { }
        int setDone() { return 0; }
        int trySetCancelled() { return 0; }
        int trySetThrown(Throwable ex) { return 0; }
        int trySetException(Throwable ex) { return 0; }
        static boolean isExceptionalStatus(int s) { return false; }
        int doExec() { return 0; }
        int awaitDone(ForkJoinPool pool, boolean ran, boolean interruptible, boolean timed, long nanos) { return 0; }
        static void cancelIgnoringExceptions(Future<?> t) { }
        Throwable getThrowableException() { return null; }
        Throwable getException(int s) { return null; }
        void reportException(int s) { }
        void reportExecutionException(int s) { }
        static void rethrow(Throwable ex) { }
        static <T> void uncheckedThrow(Throwable t) { }
        ForkJoinTask<V> fork() { return null; }
        V join() { return null; }
        V invoke() { return null; }
        static void invokeAll(ForkJoinTask<?> t1, ForkJoinTask<?> t2) { }
        static void invokeAll(ForkJoinTask<?> ... tasks) { }
        static <T> Collection<T> invokeAll(Collection<T> tasks) { return null; }
        boolean cancel(boolean mayInterruptIfRunning) { return false; }
        boolean isDone() { return false; }
        boolean isCancelled() { return false; }
        boolean isCompletedAbnormally() { return false; }
        boolean isCompletedNormally() { return false; }
        Throwable getException() { return null; }
        void completeExceptionally(Throwable ex) { }
        void complete(V value) { }
        void quietlyComplete() { }
        V get() { return null; }
        V get(long timeout, TimeUnit timeUnit) { return null; }
        void quietlyJoin() { }
        void quietlyInvoke() { }
        void awaitPoolInvoke(ForkJoinPool pool) { }
        void awaitPoolInvoke(ForkJoinPool pool, long nanos) { }
        V joinForPoolInvoke(ForkJoinPool pool) { return null; }
        V getForPoolInvoke(ForkJoinPool pool) { return null; }
        V getForPoolInvoke(ForkJoinPool pool, long nanos) { return null; }
        static void helpQuiesce() { }
        void reinitialize() { }
        static ForkJoinPool getPool() { return null; }
        static boolean inForkJoinPool() { return false; }
        boolean tryUnfork() { return false; }
        static int getQueuedTaskCount() { return 0; }
        static int getSurplusQueuedTaskCount() { return 0; }
        V getRawResult() { return null; }
        void setRawResult(V v) { }
        boolean exec() { return false; }
        static ForkJoinTask<?> peekNextLocalTask() { return null; }
        static ForkJoinTask<?> pollNextLocalTask() { return null; }
        static ForkJoinTask<?> pollTask() { return null; }
        static ForkJoinTask<?> pollSubmission() { return null; }
        short getForkJoinTaskTag() { return 0; }
        short setForkJoinTaskTag(short newValue) { return 0; }
        boolean compareAndSetForkJoinTaskTag(short expect, short update) { return false; }
        static ForkJoinTask<?> adapt(Runnable runnable) { return null; }
        static <T> ForkJoinTask<T> adapt(Runnable runnable, T result) { return null; }
        static <T> ForkJoinTask<T> adapt(Callable<? extends T> callable) { return null; }
        static <T> ForkJoinTask<T> adaptInterruptible(Callable<? extends T> callable) { return null; }
        void writeObject(ObjectOutputStream s) { }
        void readObject(ObjectInputStream s) { }
    }

    class Future$<V> {
        boolean cancel(boolean b) { return false; }
        boolean isCancelled() { return false; }
        boolean isDone() { return false; }
        V get() { return null; }
        V get(long l, TimeUnit timeUnit) { return null; }
    }

    class RunnableFuture$<V> { void run() { } }
    class ThreadFactory$ { Thread newThread(Runnable runnable) { return null; } }
}
