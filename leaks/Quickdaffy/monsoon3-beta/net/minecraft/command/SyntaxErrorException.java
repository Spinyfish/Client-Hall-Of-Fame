/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.command.CommandException;

public class SyntaxErrorException
extends CommandException {
    public SyntaxErrorException() {
        this("commands.generic.snytax", new Object[0]);
    }

    public SyntaxErrorException(String message, Object ... replacements) {
        super(message, replacements);
    }
}

