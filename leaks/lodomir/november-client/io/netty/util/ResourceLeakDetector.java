/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.ResourceLeak;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ResourceLeakDetector<T> {
    private static final String PROP_LEVEL = "io.netty.leakDetectionLevel";
    private static final Level DEFAULT_LEVEL;
    private static Level level;
    private static final InternalLogger logger;
    private static final int DEFAULT_SAMPLING_INTERVAL = 113;
    private final DefaultResourceLeak head = new DefaultResourceLeak((Object)null);
    private final DefaultResourceLeak tail = new DefaultResourceLeak((Object)null);
    private final ReferenceQueue<Object> refQueue = new ReferenceQueue();
    private final ConcurrentMap<String, Boolean> reportedLeaks = PlatformDependent.newConcurrentHashMap();
    private final String resourceType;
    private final int samplingInterval;
    private final long maxActive;
    private long active;
    private final AtomicBoolean loggedTooManyActive = new AtomicBoolean();
    private long leakCheckCnt;
    private static final String[] STACK_TRACE_ELEMENT_EXCLUSIONS;

    @Deprecated
    public static void setEnabled(boolean enabled) {
        ResourceLeakDetector.setLevel(enabled ? Level.SIMPLE : Level.DISABLED);
    }

    public static boolean isEnabled() {
        return ResourceLeakDetector.getLevel().ordinal() > Level.DISABLED.ordinal();
    }

    public static void setLevel(Level level) {
        if (level == null) {
            throw new NullPointerException("level");
        }
        ResourceLeakDetector.level = level;
    }

    public static Level getLevel() {
        return level;
    }

    public ResourceLeakDetector(Class<?> resourceType) {
        this(StringUtil.simpleClassName(resourceType));
    }

    public ResourceLeakDetector(String resourceType) {
        this(resourceType, 113, Long.MAX_VALUE);
    }

    public ResourceLeakDetector(Class<?> resourceType, int samplingInterval, long maxActive) {
        this(StringUtil.simpleClassName(resourceType), samplingInterval, maxActive);
    }

    public ResourceLeakDetector(String resourceType, int samplingInterval, long maxActive) {
        if (resourceType == null) {
            throw new NullPointerException("resourceType");
        }
        if (samplingInterval <= 0) {
            throw new IllegalArgumentException("samplingInterval: " + samplingInterval + " (expected: 1+)");
        }
        if (maxActive <= 0L) {
            throw new IllegalArgumentException("maxActive: " + maxActive + " (expected: 1+)");
        }
        this.resourceType = resourceType;
        this.samplingInterval = samplingInterval;
        this.maxActive = maxActive;
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    public ResourceLeak open(T obj) {
        Level level = ResourceLeakDetector.level;
        if (level == Level.DISABLED) {
            return null;
        }
        if (level.ordinal() < Level.PARANOID.ordinal()) {
            if (this.leakCheckCnt++ % (long)this.samplingInterval == 0L) {
                this.reportLeak(level);
                return new DefaultResourceLeak(obj);
            }
            return null;
        }
        this.reportLeak(level);
        return new DefaultResourceLeak(obj);
    }

    private void reportLeak(Level level) {
        DefaultResourceLeak ref;
        int samplingInterval;
        if (!logger.isErrorEnabled()) {
            DefaultResourceLeak ref2;
            while ((ref2 = (DefaultResourceLeak)this.refQueue.poll()) != null) {
                ref2.close();
            }
            return;
        }
        int n = samplingInterval = level == Level.PARANOID ? 1 : this.samplingInterval;
        if (this.active * (long)samplingInterval > this.maxActive && this.loggedTooManyActive.compareAndSet(false, true)) {
            logger.error("LEAK: You are creating too many " + this.resourceType + " instances.  " + this.resourceType + " is a shared resource that must be reused across the JVM," + "so that only a few instances are created.");
        }
        while ((ref = (DefaultResourceLeak)this.refQueue.poll()) != null) {
            String records;
            ref.clear();
            if (!ref.close() || this.reportedLeaks.putIfAbsent(records = ref.toString(), Boolean.TRUE) != null) continue;
            if (records.isEmpty()) {
                logger.error("LEAK: {}.release() was not called before it's garbage-collected. Enable advanced leak reporting to find out where the leak occurred. To enable advanced leak reporting, specify the JVM option '-D{}={}' or call {}.setLevel()", this.resourceType, PROP_LEVEL, Level.ADVANCED.name().toLowerCase(), StringUtil.simpleClassName(this));
                continue;
            }
            logger.error("LEAK: {}.release() was not called before it's garbage-collected.{}", (Object)this.resourceType, (Object)records);
        }
    }

    static String newRecord(int recordsToSkip) {
        StackTraceElement[] array;
        StringBuilder buf = new StringBuilder(4096);
        for (StackTraceElement e : array = new Throwable().getStackTrace()) {
            if (recordsToSkip > 0) {
                --recordsToSkip;
                continue;
            }
            String estr = e.toString();
            boolean excluded = false;
            for (String exclusion : STACK_TRACE_ELEMENT_EXCLUSIONS) {
                if (!estr.startsWith(exclusion)) continue;
                excluded = true;
                break;
            }
            if (excluded) continue;
            buf.append('\t');
            buf.append(estr);
            buf.append(StringUtil.NEWLINE);
        }
        return buf.toString();
    }

    static {
        boolean disabled;
        DEFAULT_LEVEL = Level.SIMPLE;
        logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);
        if (SystemPropertyUtil.get("io.netty.noResourceLeakDetection") != null) {
            disabled = SystemPropertyUtil.getBoolean("io.netty.noResourceLeakDetection", false);
            logger.debug("-Dio.netty.noResourceLeakDetection: {}", (Object)disabled);
            logger.warn("-Dio.netty.noResourceLeakDetection is deprecated. Use '-D{}={}' instead.", (Object)PROP_LEVEL, (Object)DEFAULT_LEVEL.name().toLowerCase());
        } else {
            disabled = false;
        }
        Level defaultLevel = disabled ? Level.DISABLED : DEFAULT_LEVEL;
        String levelStr = SystemPropertyUtil.get(PROP_LEVEL, defaultLevel.name()).trim().toUpperCase();
        Level level = DEFAULT_LEVEL;
        for (Level l : EnumSet.allOf(Level.class)) {
            if (!levelStr.equals(l.name()) && !levelStr.equals(String.valueOf(l.ordinal()))) continue;
            level = l;
        }
        ResourceLeakDetector.level = level;
        if (logger.isDebugEnabled()) {
            logger.debug("-D{}: {}", (Object)PROP_LEVEL, (Object)level.name().toLowerCase());
        }
        STACK_TRACE_ELEMENT_EXCLUSIONS = new String[]{"io.netty.buffer.AbstractByteBufAllocator.toLeakAwareBuffer("};
    }

    private final class DefaultResourceLeak
    extends PhantomReference<Object>
    implements ResourceLeak {
        private static final int MAX_RECORDS = 4;
        private final String creationRecord;
        private final Deque<String> lastRecords;
        private final AtomicBoolean freed;
        private DefaultResourceLeak prev;
        private DefaultResourceLeak next;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        DefaultResourceLeak(Object referent) {
            super(referent, referent != null ? ResourceLeakDetector.this.refQueue : null);
            this.lastRecords = new ArrayDeque<String>();
            if (referent != null) {
                Level level = ResourceLeakDetector.getLevel();
                this.creationRecord = level.ordinal() >= Level.ADVANCED.ordinal() ? ResourceLeakDetector.newRecord(3) : null;
                DefaultResourceLeak defaultResourceLeak = ResourceLeakDetector.this.head;
                synchronized (defaultResourceLeak) {
                    this.prev = ResourceLeakDetector.this.head;
                    this.next = ((ResourceLeakDetector)ResourceLeakDetector.this).head.next;
                    ((ResourceLeakDetector)ResourceLeakDetector.this).head.next.prev = this;
                    ((ResourceLeakDetector)ResourceLeakDetector.this).head.next = this;
                    ResourceLeakDetector.this.active++;
                }
                this.freed = new AtomicBoolean();
            } else {
                this.creationRecord = null;
                this.freed = new AtomicBoolean(true);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void record() {
            if (this.creationRecord != null) {
                String value = ResourceLeakDetector.newRecord(2);
                Deque<String> deque = this.lastRecords;
                synchronized (deque) {
                    int size = this.lastRecords.size();
                    if (size == 0 || !this.lastRecords.getLast().equals(value)) {
                        this.lastRecords.add(value);
                    }
                    if (size > 4) {
                        this.lastRecords.removeFirst();
                    }
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean close() {
            if (this.freed.compareAndSet(false, true)) {
                DefaultResourceLeak defaultResourceLeak = ResourceLeakDetector.this.head;
                synchronized (defaultResourceLeak) {
                    ResourceLeakDetector.this.active--;
                    this.prev.next = this.next;
                    this.next.prev = this.prev;
                    this.prev = null;
                    this.next = null;
                }
                return true;
            }
            return false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Object[] array;
            if (this.creationRecord == null) {
                return "";
            }
            Deque<String> deque = this.lastRecords;
            synchronized (deque) {
                array = this.lastRecords.toArray();
            }
            StringBuilder buf = new StringBuilder(16384);
            buf.append(StringUtil.NEWLINE);
            buf.append("Recent access records: ");
            buf.append(array.length);
            buf.append(StringUtil.NEWLINE);
            if (array.length > 0) {
                for (int i = array.length - 1; i >= 0; --i) {
                    buf.append('#');
                    buf.append(i + 1);
                    buf.append(':');
                    buf.append(StringUtil.NEWLINE);
                    buf.append(array[i]);
                }
            }
            buf.append("Created at:");
            buf.append(StringUtil.NEWLINE);
            buf.append(this.creationRecord);
            buf.setLength(buf.length() - StringUtil.NEWLINE.length());
            return buf.toString();
        }
    }

    public static enum Level {
        DISABLED,
        SIMPLE,
        ADVANCED,
        PARANOID;

    }
}

