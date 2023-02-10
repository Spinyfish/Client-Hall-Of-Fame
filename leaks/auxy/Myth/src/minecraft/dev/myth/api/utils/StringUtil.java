/**
 * @project Myth
 * @author CodeMan
 * @at 25.08.22, 18:56
 */
package dev.myth.api.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class StringUtil {

    public String removeFormatting(String string) {
        StringBuilder builder = new StringBuilder();
        boolean skipNext = false;
        for (char c : string.toCharArray()) {
            if (c == '§') {
                skipNext = true;
                continue;
            }
            if(skipNext) {
                skipNext = false;
                continue;
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public String generateRandomString(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++)
            sb.append(chars.charAt((int) (Math.random() * chars.length())));

        return sb.toString();
    }

    public String formatTime(long time) {
        time /= 1000;
        int seconds = (int) (time % 60);
        int minutes = (int) (time / 60 % 60);
        int hours = (int) (time / 60 / 60 % 60);
        StringBuilder builder = new StringBuilder();
        if(hours > 0) builder.append(hours).append("h ");
        if(minutes > 0) builder.append(minutes).append("m ");
        if(seconds > 0) builder.append(seconds).append("s");
        return builder.toString();
    }

}
