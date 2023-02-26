/*
+
 * Decompiled with CFR 0_122.
 */
package ProxyManager;

import java.util.ArrayList;
import me.arithmo.gui.altmanager.Alt;

public class SocksProxyManager {
    public static SocksProxy lastProxy;
    public static ArrayList<SocksProxy> registry;

    public ArrayList<SocksProxy> getRegistry() {
        return registry;
    }

    public void setLastProxy(SocksProxy last) {
        lastProxy = last;
    }

    static {
        registry = new ArrayList();
    }
}

