/*
 * Decompiled with CFR 0.152.
 */
package org.yaml.snakeyaml.util;

public class PlatformFeatureDetector {
    private Boolean isRunningOnAndroid = null;

    public boolean isRunningOnAndroid() {
        if (this.isRunningOnAndroid == null) {
            String name = System.getProperty("java.runtime.name");
            this.isRunningOnAndroid = name != null && name.startsWith("Android Runtime");
        }
        return this.isRunningOnAndroid;
    }
}

