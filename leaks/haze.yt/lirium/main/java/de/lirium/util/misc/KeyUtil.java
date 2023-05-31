package de.lirium.util.misc;

import org.lwjgl.input.Keyboard;

public class KeyUtil {

    public static String getKeyName(int key) {
        if(key < 0) {
            return "MOUSE" + (100 + key);
        } else {
            return Keyboard.getKeyName(key);
        }
    }
}
