package de.lirium.util.interfaces;

import de.lirium.Client;

public interface Logger {

    static void print(String text) {
        System.out.println(IColor.ANSI_WHITE + "[" + IColor.ANSI_GREEN + Client.NAME + IColor.ANSI_WHITE + "] " + IColor.ANSI_RESET + " : " + text);
    }

}