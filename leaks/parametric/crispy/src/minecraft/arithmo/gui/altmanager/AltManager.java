/*
 * Decompiled with CFR 0_122.
 */
package arithmo.gui.altmanager;

import java.util.ArrayList;

public class AltManager {
    public static Alt lastAlt;
    public static ArrayList<Alt> registry;

    public ArrayList<Alt> getRegistry() {
        return registry;
    }

    public void setLastAlt(Alt alt) {
        lastAlt = alt;
    }

    static {
        registry = new ArrayList();
    }
}
