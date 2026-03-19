package util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private final ConcurrentMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public String nextId(String prefix) {
        int nextValue = counters.computeIfAbsent(prefix, key -> new AtomicInteger()).incrementAndGet();
        return String.format("%s-%03d", prefix, nextValue);
    }
}
