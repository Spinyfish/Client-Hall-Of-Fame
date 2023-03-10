/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import lodomir.dev.November;
import lodomir.dev.commands.Command;
import net.minecraft.client.Minecraft;

public class HClip
extends Command {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public HClip() {
        super("HClip", "Clips you on the horizontal axis.", "hclip <distance>", "hclip");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length > 0) {
            double dist = Double.parseDouble(args[0]);
            String direction = dist > 0.0 ? "forward" : "back";
            double rotation = Math.toRadians(HClip.mc.thePlayer.rotationYaw);
            double x = Math.sin(rotation) * dist;
            double z = Math.cos(rotation) * dist;
            HClip.mc.thePlayer.setPosition(HClip.mc.thePlayer.posX - x, HClip.mc.thePlayer.posY, HClip.mc.thePlayer.posZ + z);
            November.Log("HClipped you " + direction + " " + dist + " blocks.");
        }
    }
}

