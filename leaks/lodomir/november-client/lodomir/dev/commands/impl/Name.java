/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import lodomir.dev.November;
import lodomir.dev.commands.Command;
import lodomir.dev.utils.player.MovementUtils;

public class Name
extends Command {
    public Name() {
        super("Name", "Copies your username to clipboard", "name", "name");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 1) {
            return;
        }
        StringSelection string = new StringSelection(MovementUtils.mc.thePlayer.getName());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(string, null);
        November.Log("Copied your name to clipboard");
    }
}

