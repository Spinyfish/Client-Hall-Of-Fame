/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.ReferenceCounted;
import io.netty.util.ThreadDeathWatcher;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ReferenceCountUtil {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountUtil.class);

    public static <T> T retain(T msg) {
        if (msg instanceof ReferenceCounted) {
            return (T)((ReferenceCounted)msg).retain();
        }
        return msg;
    }

    public static <T> T retain(T msg, int increment) {
        if (msg instanceof ReferenceCounted) {
            return (T)((ReferenceCounted)msg).retain(increment);
        }
        return msg;
    }

    public static boolean release(Object msg) {
        if (msg instanceof ReferenceCounted) {
            return ((ReferenceCounted)msg).release();
        }
        return false;
    }

    public static boolean release(Object msg, int decrement) {
        if (msg instanceof ReferenceCounted) {
            return ((ReferenceCounted)msg).release(decrement);
        }
        return false;
    }

    public static void safeRelease(Object msg) {
        try {
            ReferenceCountUtil.release(msg);
        }
        catch (Throwable t) {
            logger.warn("Failed to release a message: {}", msg, (Object)t);
        }
    }

    public static void safeRelease(Object msg, int decrement) {
        block2: {
            try {
                ReferenceCountUtil.release(msg, decrement);
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block2;
                logger.warn("Failed to release a message: {} (decrement: {})", msg, decrement, t);
            }
        }
    }

    public static <T> T releaseLater(T msg) {
        return ReferenceCountUtil.releaseLater(msg, 1);
    }

    public static <T> T releaseLater(T msg, int decrement) {
        if (msg instanceof ReferenceCounted) {
            ThreadDeathWatcher.watch(Thread.currentThread(), new ReleasingTask((ReferenceCounted)msg, decrement));
        }
        return msg;
    }

    private ReferenceCountUtil() {
    }

    private static final class ReleasingTask
    implements Runnable {
        private final ReferenceCounted obj;
        private final int decrement;

        ReleasingTask(ReferenceCounted obj, int decrement) {
            this.obj = obj;
            this.decrement = decrement;
        }

        @Override
        public void run() {
            try {
                if (!this.obj.release(this.decrement)) {
                    logger.warn("Non-zero refCnt: {}", (Object)this);
                } else {
                    logger.debug("Released: {}", (Object)this);
                }
            }
            catch (Exception ex) {
                logger.warn("Failed to release an object: {}", (Object)this.obj, (Object)ex);
            }
        }

        public String toString() {
            return StringUtil.simpleClassName(this.obj) + ".release(" + this.decrement + ") refCnt: " + this.obj.refCnt();
        }
    }
}

