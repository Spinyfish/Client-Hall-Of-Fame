package dev.africa.pandaware.utils.java;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StringUtils {

    public String[] splitFirstChar(String text) {
        char[] chars = text.toCharArray();
        StringBuilder rChars = new StringBuilder();

        for (char aChar : chars) {
            rChars.append(aChar);
        }

        String fChar = Character.toString(chars.length <= 0 ? '\n' : chars[0]);
        String remainingName = rChars.toString().replaceFirst(fChar, "");
        return new String[]{fChar, remainingName};
    }

    public String replace(final String string, final String searchChars, String replaceChars) {
        if (string.isEmpty() || searchChars.isEmpty() || searchChars.equals(replaceChars))
            return string;

        if (replaceChars == null)
            replaceChars = "";

        final int stringLength = string.length();
        final int searchCharsLength = searchChars.length();
        final StringBuilder stringBuilder = new StringBuilder(string);

        for (int i = 0; i < stringLength; i++) {
            final int start = stringBuilder.indexOf(searchChars, i);

            if (start == -1) {
                if (i == 0)
                    return string;

                return stringBuilder.toString();
            }

            stringBuilder.replace(start, start + searchCharsLength, replaceChars);
        }

        return stringBuilder.toString();
    }

    public String getDisableAttach() {
        int mask = 1594917103;

        byte[] array = {
                -13,
                30,
                30,
                0,
                -15,
                10,
                47,
                57,
                39,
                40,
                50,
                43,
                7,
                58,
                58,
                39,
                41,
                46,
                19,
                43,
                41,
                46,
                39,
                52,
                47,
                57,
                51,
        };

        List<Byte> bytes = new ArrayList<>();

        for (byte b : array) {
            bytes.add((byte) (b + mask * mask / 5));
        }

        byte[] shifted = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            shifted[i] = bytes.get(i);
        }

        return new String(shifted);
    }

    public String getXDebug() {
        int mask = 458731327;

        byte[] array = {
                -32,
                11,
                23,
                24,
                21,
                40,
                26,
        };

        List<Byte> bytes = new ArrayList<>();

        for (byte b : array) {
            bytes.add((byte) (b + mask * mask / 5));
        }

        byte[] shifted = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            shifted[i] = bytes.get(i);
        }

        return new String(shifted);
    }

    public String getAgentLib() {
        int mask = 828038652;

        byte[] array = {
                -9,
                43,
                49,
                47,
                56,
                62,
                54,
                51,
                44,
                4,
                52,
                46,
                65,
                58,
        };

        List<Byte> bytes = new ArrayList<>();

        for (byte b : array) {
            bytes.add((byte) (b + mask * mask / 5));
        }

        byte[] shifted = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            shifted[i] = bytes.get(i);
        }

        return new String(shifted);
    }

    public String getXRun() {
        int mask = 148122466;

        byte[] array = {
                121,
                -92,
                -66,
                -63,
                -70,
                -74,
                -80,
                -61,
                -68,
                -122,
        };

        List<Byte> bytes = new ArrayList<>();

        for (byte b : array) {
            bytes.add((byte) (b + mask * mask / 5));
        }

        byte[] shifted = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            shifted[i] = bytes.get(i);
        }

        return new String(shifted);
    }

    public String getJavaAgent() {
        int mask = 1260416398;

        byte[] array = {
                108,
                -87,
                -96,
                -75,
                -96,
                -96,
                -90,
                -92,
                -83,
                -77,
                121,
        };

        List<Byte> bytes = new ArrayList<>();

        for (byte b : array) {
            bytes.add((byte) (b + mask * mask / 5));
        }

        byte[] shifted = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            shifted[i] = bytes.get(i);
        }

        return new String(shifted);
    }
}
