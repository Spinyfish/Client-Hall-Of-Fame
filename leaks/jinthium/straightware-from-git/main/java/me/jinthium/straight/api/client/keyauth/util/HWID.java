package me.jinthium.straight.api.client.keyauth.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HWID {
    private static String textToSHA1(String text) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance("SHA-1").digest(text.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String getHWID() {
        String processor = System.getenv("PROCESSOR_IDENTIFIER");
        String computerName = System.getenv("COMPUTERNAME");
        String userName = System.getenv("USERNAME");

        try {
            return textToSHA1(processor + computerName + userName);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
