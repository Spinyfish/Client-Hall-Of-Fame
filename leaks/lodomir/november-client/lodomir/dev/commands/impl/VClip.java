/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import lodomir.dev.November;
import lodomir.dev.commands.Command;
import net.minecraft.client.Minecraft;

public class VClip
extends Command {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public VClip() {
        super("VClip", "Clips you on the vertical axis.", "vclip <distance>", "vclip");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length > 0) {
            double dist = Double.parseDouble(args[0]);
            String direction = dist > 0.0 ? "up" : "down";
            VClip.mc.thePlayer.setPosition(VClip.mc.thePlayer.posX, VClip.mc.thePlayer.posY + dist, VClip.mc.thePlayer.posZ);
            November.Log("VClipped you " + direction + " " + Math.abs(dist) + " blocks.");
        }
    }
}

