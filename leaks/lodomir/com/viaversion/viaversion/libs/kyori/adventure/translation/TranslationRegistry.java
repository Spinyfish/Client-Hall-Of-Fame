/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.translation;

import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.translation.TranslationRegistryImpl;
import com.viaversion.viaversion.libs.kyori.adventure.translation.Translator;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TranslationRegistry
extends Translator {
    public static final Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("'");

    @NotNull
    public static TranslationRegistry create(Key name) {
        return new TranslationRegistryImpl(Objects.requireNonNull(name, "name"));
    }

    public boolean contains(@NotNull String var1);

    @Override
    @Nullable
    public MessageFormat translate(@NotNull String var1, @NotNull Locale var2);

    public void defaultLocale(@NotNull Locale var1);

    public void register(@NotNull String var1, @NotNull Locale var2, @NotNull MessageFormat var3);

    default public void registerAll(@NotNull Locale locale, @NotNull Map<String, MessageFormat> formats) {
        this.registerAll(locale, formats.keySet(), formats::get);
    }

    default public void registerAll(@NotNull Locale locale, @NotNull Path path, boolean escapeSingleQuotes) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);){
            this.registerAll(locale, new PropertyResourceBundle(reader), escapeSingleQuotes);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    default public void registerAll(@NotNull Locale locale, @NotNull ResourceBundle bundle, boolean escapeSingleQuotes) {
        this.registerAll(locale, bundle.keySet(), key -> {
            String format = bundle.getString((String)key);
            return new MessageFormat(escapeSingleQuotes ? SINGLE_QUOTE_PATTERN.matcher(format).replaceAll("''") : format, locale);
        });
    }

    default public void registerAll(@NotNull Locale locale, @NotNull Set<String> keys, Function<String, MessageFormat> function) {
        LinkedList<IllegalArgumentException> errors = null;
        for (String key : keys) {
            try {
                this.register(key, locale, function.apply(key));
            }
            catch (IllegalArgumentException e) {
                if (errors == null) {
                    errors = new LinkedList<IllegalArgumentException>();
                }
                errors.add(e);
            }
        }
        if (errors != null) {
            int size = errors.size();
            if (size == 1) {
                throw (IllegalArgumentException)errors.get(0);
            }
            if (size > 1) {
                throw new IllegalArgumentException(String.format("Invalid key (and %d more)", size - 1), (Throwable)errors.get(0));
            }
        }
    }

    public void unregister(@NotNull String var1);
}

