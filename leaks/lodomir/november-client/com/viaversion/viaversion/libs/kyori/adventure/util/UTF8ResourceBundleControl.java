/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NotNull;

public final class UTF8ResourceBundleControl
extends ResourceBundle.Control {
    private static final UTF8ResourceBundleControl INSTANCE = new UTF8ResourceBundleControl();

    public static @NotNull ResourceBundle.Control get() {
        return INSTANCE;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        if (format.equals("java.properties")) {
            InputStream is;
            String bundle = this.toBundleName(baseName, locale);
            String resource = this.toResourceName(bundle, "properties");
            try {
                is = AccessController.doPrivileged(() -> {
                    if (reload) {
                        URLConnection connection;
                        URL url = loader.getResource(resource);
                        if (url != null && (connection = url.openConnection()) != null) {
                            connection.setUseCaches(false);
                            return connection.getInputStream();
                        }
                        return null;
                    }
                    return loader.getResourceAsStream(resource);
                });
            }
            catch (PrivilegedActionException e) {
                throw (IOException)e.getException();
            }
            if (is != null) {
                try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);){
                    PropertyResourceBundle propertyResourceBundle = new PropertyResourceBundle(isr);
                    return propertyResourceBundle;
                }
            }
            return null;
        }
        return super.newBundle(baseName, locale, format, loader, reload);
    }
}

