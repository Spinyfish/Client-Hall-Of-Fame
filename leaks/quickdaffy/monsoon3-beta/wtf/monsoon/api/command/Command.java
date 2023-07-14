/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.command;

import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;

public abstract class Command {
    private final String name;
    public Minecraft mc = Wrapper.getMinecraft();

    public Command(String name) {
        this.name = name;
    }

    public abstract void execute(String[] var1);

    public String getName() {
        return this.name;
    }
}

