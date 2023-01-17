package com.alan.clients.protection.check.api;

import com.alan.clients.protection.check.ProtectionCheck;
import com.alan.clients.Client;
import com.alan.clients.protection.check.impl.*;
import lombok.Getter;
import sun.misc.Unsafe;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Strikeless
 * @since 25.03.2022
 */
@Getter
@SuppressWarnings("all") // we doing goofy shit
public final class McqBFVbnWB {

    private List<String> jvmArguments;

    private ProtectionCheck[] checks;

    private Thread repetitiveHandlerThread;

    private boolean initialized;

    public void init() {
        try {
            this.jvmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

            this.checks = new ProtectionCheck[]{
//                    new McqBFVeaWB(),
//                    new McqBFVeaWC(),
//                    new McqBFVsdWB(),
//                    new MhdBFVeaWB(),
//                    new McqBFVehFK(),
//                    new McqBGGeaWB(),
            };
            this.run(McqBFVadWB.INITIALIZE);

            this.repetitiveHandlerThread = new Thread(() -> {
                while (true) {
                    try {
                        run(McqBFVadWB.REPETITIVE);
                        Thread.sleep(1000L);
                    } catch (final Throwable throwable) {
                        crash();
                    }
                }
            });

            this.repetitiveHandlerThread.start();

            if (this.initialized) {
                crash();
            } else {
                this.initialized = true;
            }

            this.run(McqBFVadWB.POST_INITIALIZE);
        } catch (final Throwable ignored) {
            crash();
        }
    }

    public void run(final McqBFVadWB trigger) {
        try {
            for (final ProtectionCheck module : checks) {
                if (module.getTrigger() == trigger && !(Client.DEVELOPMENT_SWITCH && module.isExemptDev())) {
                    if (module.check()) {
                        System.out.println(module.check());
                        hang();
                    }
                }
            }
        } catch (final Throwable ignored) {
            crash();
        }
    }

    public void hang() {
        while (true) ;
    }

    public void crash() {
        for (; ; ) {
            try {
                final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);

                final Unsafe unsafe = (Unsafe) unsafeField.get(null);

                for (long address = 0; true; ++address) {
                    unsafe.setMemory(address, Long.MAX_VALUE, Byte.MIN_VALUE);
                }
            } catch (final Throwable t) {
                crash();
            }

            hang();
        }
    }
}
