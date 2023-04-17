package me.levansj01.verus.util.java;

import java.util.stream.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.*;
import javax.annotation.*;

public class JavaV
{
    public static boolean allMatch(final Predicate predicate, final Iterable iterable) {
        final Iterator<Object> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            if (!predicate.test(iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean anyMatch(final Predicate predicate, final Object... array) {
        while (0 < array.length) {
            if (predicate.test(array[0])) {
                return true;
            }
            int n = 0;
            ++n;
        }
        return false;
    }
    
    public static Object findFirst(final Predicate predicate, final Iterable iterable) {
        for (final Object next : iterable) {
            if (predicate.test(next)) {
                return next;
            }
        }
        return null;
    }
    
    public static Stream stream(final Object... array) {
        return Arrays.stream(array);
    }
    
    public static void shutdownAndAwaitTermination(final ExecutorService executorService, final long n, final TimeUnit timeUnit) {
        executorService.shutdown();
        if (!executorService.awaitTermination(n, timeUnit)) {
            executorService.shutdownNow();
            if (!executorService.awaitTermination(n, timeUnit)) {
                System.err.println("Pool did not terminate");
            }
        }
    }
    
    public static void executeSafely(final Queue queue, final Supplier supplier) {
        Runnable runnable;
        while ((runnable = queue.poll()) != null) {
            runnable.run();
        }
    }
    
    public static List findAll(final Predicate predicate, final Object... array) {
        final LinkedList<Object> list = new LinkedList<Object>();
        while (0 < array.length) {
            final Object o = array[0];
            if (predicate.test(o)) {
                list.add(o);
            }
            int n = 0;
            ++n;
        }
        return list;
    }
    
    public static Object findFirst(final Predicate predicate, final Object... array) {
        while (0 < array.length) {
            final Object o = array[0];
            if (predicate.test(o)) {
                return o;
            }
            int n = 0;
            ++n;
        }
        return null;
    }
    
    public static boolean anyMatch(final Predicate predicate, final Iterable iterable) {
        final Iterator<Object> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            if (predicate.test(iterator.next())) {
                return true;
            }
        }
        return false;
    }
    
    public static Queue trim(final Queue queue, final int n) {
        for (int i = queue.size(); i > n; --i) {
            queue.poll();
        }
        return queue;
    }
    
    public static Object firstNonNull(@Nullable final Object o, @Nullable final Object o2) {
        return (o != null) ? o : o2;
    }
}
