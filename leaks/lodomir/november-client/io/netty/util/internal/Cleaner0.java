/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  sun.misc.Cleaner
 */
package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import sun.misc.Cleaner;

final class Cleaner0 {
    private static final long CLEANER_FIELD_OFFSET;
    private static final InternalLogger logger;

    static void freeDirectBuffer(ByteBuffer buffer) {
        if (CLEANER_FIELD_OFFSET == -1L || !buffer.isDirect()) {
            return;
        }
        try {
            Cleaner cleaner = (Cleaner)PlatformDependent0.getObject(buffer, CLEANER_FIELD_OFFSET);
            if (cleaner != null) {
                cleaner.clean();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private Cleaner0() {
    }

    static {
        logger = InternalLoggerFactory.getInstance(Cleaner0.class);
        ByteBuffer direct = ByteBuffer.allocateDirect(1);
        long fieldOffset = -1L;
        if (PlatformDependent0.hasUnsafe()) {
            try {
                Field cleanerField = direct.getClass().getDeclaredField("cleaner");
                cleanerField.setAccessible(true);
                Cleaner cleaner = (Cleaner)cleanerField.get(direct);
                cleaner.clean();
                fieldOffset = PlatformDependent0.objectFieldOffset(cleanerField);
            }
            catch (Throwable t) {
                fieldOffset = -1L;
            }
        }
        logger.debug("java.nio.ByteBuffer.cleaner(): {}", (Object)(fieldOffset != -1L ? "available" : "unavailable"));
        CLEANER_FIELD_OFFSET = fieldOffset;
        Cleaner0.freeDirectBuffer(direct);
    }
}

