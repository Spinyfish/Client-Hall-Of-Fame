package dev.africa.pandaware.utils.client;

import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@UtilityClass
public class HWIDUtils {

    public String getHWID() {
        String infos = System.getProperty("os.name") +
                System.getProperty("os.arch") +
                System.getProperty("os.version") +
                System.getProperty("user.name") +
                System.getProperty("user.home") +
                System.getProperty("sun.arch.data.model") +
                Runtime.getRuntime().availableProcessors() +
                System.getenv("PROCESSOR_IDENTIFIER") +
                System.getenv("PROCESSOR_ARCHITECTURE") +
                System.getenv("PROCESSOR_ARCHITEW6432") +
                System.getenv("NUMBER_OF_PROCESSORS") +
                System.getenv("PROCESSOR_LEVEL") +
                System.getenv("PROCESSOR_REVISION");

        MessageDigest sha512;
        try {
            sha512 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            System.exit(4124);
            // lol

            return null;
        }

        Base64.Encoder base64 = Base64.getEncoder();

        byte[] hashed = base64.encode(sha512.digest(infos.getBytes()));

        return new String(hashed);
    }
}
