/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import java.util.Arrays;
import java.util.List;
import lodomir.dev.November;
import lodomir.dev.commands.Command;
import lodomir.dev.ui.HUD;

public class ClientName
extends Command {
    private final List chars = Arrays.asList(Character.valueOf('0'), Character.valueOf('1'), Character.valueOf('2'), Character.valueOf('3'), Character.valueOf('4'), Character.valueOf('5'), Character.valueOf('6'), Character.valueOf('7'), Character.valueOf('8'), Character.valueOf('9'), Character.valueOf('a'), Character.valueOf('b'), Character.valueOf('c'), Character.valueOf('d'), Character.valueOf('e'), Character.valueOf('k'), Character.valueOf('m'), Character.valueOf('o'), Character.valueOf('l'), Character.valueOf('n'), Character.valueOf('r'));

    public ClientName() {
        super("ClientName", "Changes text displayed as watermark", "clientname <name>", "clientname");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length >= 0) {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < args.length; ++i) {
                String tempString = args[i];
                tempString = tempString.replace('&', '\u00a7');
                string.append(tempString).append(" ");
            }
            November.Log(String.format("Client name was changed to: %s\u00a77", string.toString().trim(), HUD.clientName));
            HUD.clientName = string.toString().trim();
        }
    }
}

