/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.alt;

import java.util.ArrayList;
import lodomir.dev.alt.Alt;

public class AltManager {
    public static Alt lastAlt;
    public static ArrayList<Alt> registry;

    public ArrayList<Alt> getRegistry() {
        return registry;
    }

    public void setLastAlt(Alt alt2) {
        lastAlt = alt2;
    }

    static {
        registry = new ArrayList();
    }
}

