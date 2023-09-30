package dev.africa.pandaware.utils.java.crypt;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomCryptUtils {
    public String decrypt(String toDecrypt, String key) {
        int add = getAdd(key);

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();

        for (char c : toDecrypt.toCharArray()) {
            c += add / 8;
            c -= add / 3;
            c -= add;

            sb1.append(c);
        }

        for (char c : sb1.toString().toCharArray()) {
            c -= add * 8;
            c += add;
            c -= add / 4;

            sb2.append(c);
        }

        for (char c : sb2.toString().toCharArray()) {
            c += add * 8;
            c -= add * 16;
            c += add;

            sb3.append(c);
        }

        return sb3.toString();
    }

    public String encrypt(String toEncrypt, String key) {
        int add = getAdd(key);

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();

        for (char c : toEncrypt.toCharArray()) {
            c -= add / 8;
            c += add / 3;
            c += add;

            sb1.append(c);
        }

        for (char c : sb1.toString().toCharArray()) {
            c += add * 8;
            c -= add;
            c += add / 4;

            sb2.append(c);
        }

        for (char c : sb2.toString().toCharArray()) {
            c -= add * 8;
            c += add * 16;
            c -= add;

            sb3.append(c);
        }

        return sb3.toString();
    }

    public static int getAdd(String key) {
        int uppercase = 0;

        for (int i = 0; i < key.length(); ++i) {
            if (Character.isUpperCase(key.charAt(i))) {
                uppercase++;
            }
        }

        return key.length() + uppercase;
    }
}
