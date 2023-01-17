package com.alan.clients.launcher.util;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public final class ShifterUtil {

    public static String shift(final String input) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            builder.append(Character.reverseBytes(input.charAt(i)));
        }

        return new String(Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8)));
    }
}
