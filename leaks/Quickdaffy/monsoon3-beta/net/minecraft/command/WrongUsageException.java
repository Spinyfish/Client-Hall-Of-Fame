/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.command.SyntaxErrorException;

public class WrongUsageException
extends SyntaxErrorException {
    public WrongUsageException(String message, Object ... replacements) {
        super(message, replacements);
    }
}

