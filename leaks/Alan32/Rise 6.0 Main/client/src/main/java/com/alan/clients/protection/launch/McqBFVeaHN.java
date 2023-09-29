package com.alan.clients.protection.launch;

import com.alan.clients.Client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class McqBFVeaHN {

    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private static boolean checked;
    public static int value = -2048;

    static {

    }

    public static Object getVariable(final byte[] variable) {
        if (Client.INSTANCE.isValidated() || !McqBFVavWB.BYTES.isEmpty()) { // Checking if the client is already validated even though it hasn't been validated yet.
            return new char['L'];
        }

        final String var = new String(variable);

        switch (var) {
            case "font1": {
                return new String(DECODER.decode("dGV4dHVyZXMvZm9udC9hc2NpaQ=="), StandardCharsets.UTF_8);
            }

            case "font2": {
                return new String(DECODER.decode("dGV4dHVyZXMvZm9udC9hc2NpaV9zZ2E="), StandardCharsets.UTF_8);
            }

            case "depthBits": {
                return 24;
            }

            default: {
                while (true) ;
            }
        }
    }
}