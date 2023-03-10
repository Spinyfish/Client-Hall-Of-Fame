/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import lodomir.dev.November;
import lodomir.dev.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class Teleport
extends Command {
    public Teleport() {
        super("Teleport", "Teleports you to the location or given player", "teleport <location/name>", "tp");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 3) {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            Minecraft.getMinecraft().thePlayer.setPosition(x, y, z);
            November.Log("Teleported to the coordinates " + x + " " + y + " " + z + ".");
        } else {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.playerEntities.stream().filter(entity -> entity.getGameProfile().getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
            if (player != null) {
                double x = player.posX;
                double y = player.posY;
                double z = player.posZ;
                Minecraft.getMinecraft().thePlayer.setPosition(x, y, z);
                November.Log("Successfully teleported to " + player.getName() + ".");
            } else {
                November.Log("Could not locate the player given!");
            }
        }
    }
}

