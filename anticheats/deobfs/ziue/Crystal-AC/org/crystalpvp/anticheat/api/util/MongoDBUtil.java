package org.crystalpvp.anticheat.api.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class MongoDBUtil {
    public static ClassLoader injectorClassLoader = MongoDBUtil.class.getClassLoader();



    public static void download(File file, String from) throws Exception {
        URL url = new URL(from);
        InputStream stream = url.openStream();
        ReadableByteChannel channel = Channels.newChannel(stream);
        FileOutputStream out = new FileOutputStream(file);
        out.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
    }



    public static void injectURL(URL url) {
        try {
            URLClassLoader systemClassLoader = (URLClassLoader) injectorClassLoader;
            Class<URLClassLoader> classLoaderClass = URLClassLoader.class;

            try {
                Method method = classLoaderClass.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(systemClassLoader, url);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } catch (Exception e) {
        }
    }
}
