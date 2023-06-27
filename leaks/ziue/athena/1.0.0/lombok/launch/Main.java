package lombok.launch;

import java.util.*;
import java.lang.reflect.*;

class Main
{
    private static ShadowClassLoader classLoader;
    
    static synchronized ClassLoader getShadowClassLoader() {
        if (Main.classLoader == null) {
            Main.classLoader = new ShadowClassLoader(Main.class.getClassLoader(), "lombok", null, Arrays.asList(new String[0]), Arrays.asList("lombok.patcher.Symbols"));
        }
        return Main.classLoader;
    }
    
    static synchronized void prependClassLoader(final ClassLoader loader) {
        getShadowClassLoader();
        Main.classLoader.prependParent(loader);
    }
    
    public static void main(final String[] args) throws Throwable {
        final ClassLoader cl = getShadowClassLoader();
        final Class<?> mc = cl.loadClass("lombok.core.Main");
        try {
            mc.getMethod("main", String[].class).invoke(null, args);
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
