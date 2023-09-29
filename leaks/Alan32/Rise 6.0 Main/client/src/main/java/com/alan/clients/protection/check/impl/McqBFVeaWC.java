package com.alan.clients.protection.check.impl;

import com.alan.clients.Client;
import com.alan.clients.protection.check.ProtectionCheck;
import com.alan.clients.protection.check.api.McqBFVadWB;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Strikeless
 * @since 25.03.2022
 */
public final class McqBFVeaWC extends ProtectionCheck {

    private static final String[] CLASS_NAMES = {
            "sun.instrument.InstrumentationImpl",
            "java.lang.instrument.Instrumentation",
            "java.lang.instrument.ClassDefinition",
            "java.lang.instrument.ClassFileTransformer",
            "java.lang.instrument.IllegalClassFormatException",
            "java.lang.instrument.UnmodifiableClassException"
    };

    private final Method findLoadedClassMethod;

    @SneakyThrows
    public McqBFVeaWC() {
        super(McqBFVadWB.REPETITIVE, true);

        this.findLoadedClassMethod = ClassLoader.class.getDeclaredMethod("findLoadedClass0", String.class);
    }

    @Override
    public boolean check() throws InvocationTargetException, IllegalAccessException, InterruptedException {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        // Check if any classes related to the java instrumentation API have been loaded.
        // Java agents use the instrumentation API so this check is quite useful.
        for (final String className : CLASS_NAMES) {
            final Object loadedClass = this.findLoadedClassMethod.invoke(classLoader, className);

            if (loadedClass != null) {
                // Try to crash immediately instead of hanging in an infinite loop.
                Client.INSTANCE.getMcqAFVeaWB().crash();
                return true;
            }
        }

        return false;
    }
}
