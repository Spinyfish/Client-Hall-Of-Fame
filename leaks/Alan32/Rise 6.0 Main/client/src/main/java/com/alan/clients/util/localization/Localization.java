package com.alan.clients.util.localization;

import com.alan.clients.Client;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Hazsi
 * @since 10/31/2022
 */
public class Localization {
    static {
        populate();
    }

    private static boolean populated = false;

    /**
     * Gets a translated string using the Client's current locale
     * @param key The key of the string to translate
     * @return The translated string of the key, using the Client's current locale
     */
    public static String get(String key) {
        return get(key, Client.INSTANCE.getLocale());
    }

    /**
     * Gets a translated string using a specified locale
     * @param key The key of the string to translate
     * @param locale The locale to use when translating the given key
     * @return The translated string of the key, using the specified locale. If the key is not found in the specified
     * locale's strings, en_US is used a fallback. If the string is missing in en_US, the key itself is used as a
     * final fallback.
     */
    public static String get(String key, Locale locale) {
        if (!populated) populate();
        String translated = locale.getStrings().get(key);
        if (translated == null) translated = Locale.EN_US.getStrings().get(key);
        return translated == null ? key : translated;
    }

    /**
     * Called on client startup. Populates the maps for each locale by reading and parsing their properties files
     * located in rise/text
     */
    @SneakyThrows
    public static void populate() {
        for (Locale locale : Locale.values()) {
            ResourceLocation resourceLocation = new ResourceLocation("rise/text/" + locale.getFile() + ".properties");

            try (InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    locale.getStrings().put(line.split("=")[0], line.split("=")[1]);
                }
            }
        }
        populated = true;
    }
}