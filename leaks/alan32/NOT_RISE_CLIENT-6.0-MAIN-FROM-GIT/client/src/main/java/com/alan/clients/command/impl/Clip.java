package com.alan.clients.command.impl;

import com.alan.clients.api.Rise;
import com.alan.clients.command.Command;
import com.alan.clients.util.chat.ChatUtil;

/**
 * @author Auth
 * @since 3/02/2022
 */
@Rise
public final class Clip extends Command {

    public Clip() {
        super("command.clip.description", "clip", "vclip", "hclip");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length <= 1 || args[1].isEmpty()) {
            error(".clip <up/down/forward/back/left/right> <amount>");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "vclip": {
                final double amount = Double.parseDouble(args[1]);

                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + amount, mc.thePlayer.posZ);
                ChatUtil.display("Clipped you " + (amount > 0 ? "up" : "down") + " " + Math.abs(amount) + " blocks.");
                break;
            }

            case "hclip": {
                final double amount = Double.parseDouble(args[1]);

                final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                final double x = Math.sin(yaw) * amount;
                final double z = Math.cos(yaw) * amount;

                mc.thePlayer.setPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                ChatUtil.display("Clipped you " + (amount > 0 ? "forward" : "back") + " " + Math.abs(amount) + " blocks.");
                break;
            }

            case "clip": {
                if (args.length <= 2 || args[2].isEmpty()) {
                    error();
                    return;
                }

                switch (args[1]) {
                    case "upward":
                    case "upwards":
                    case "up": {
                        final double amount = Double.parseDouble(args[2]);

                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + amount, mc.thePlayer.posZ);
                        ChatUtil.display("Clipped you up " + amount + " blocks.");
                        break;
                    }

                    case "downward":
                    case "downwards":
                    case "down": {
                        final double amount = Double.parseDouble(args[2]);

                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - amount, mc.thePlayer.posZ);
                        ChatUtil.display("Clipped you down " + amount + " blocks.");
                        break;
                    }

                    case "forwards":
                    case "forward": {
                        final double amount = Double.parseDouble(args[2]);

                        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                        final double x = Math.sin(yaw) * amount;
                        final double z = Math.cos(yaw) * amount;

                        mc.thePlayer.setPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                        ChatUtil.display("Clipped you forward " + amount + " blocks.");
                        break;
                    }

                    case "backwards":
                    case "backward":
                    case "back": {
                        final double amount = Double.parseDouble(args[2]);

                        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                        final double x = Math.sin(yaw) * amount;
                        final double z = Math.cos(yaw) * amount;

                        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ - z);
                        ChatUtil.display("Clipped you back " + amount + " blocks.");
                        break;
                    }

                    case "left": {
                        final double amount = Double.parseDouble(args[2]);

                        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw - 90);
                        final double x = Math.sin(yaw) * amount;
                        final double z = Math.cos(yaw) * amount;

                        mc.thePlayer.setPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                        ChatUtil.display("Clipped you left " + amount + " blocks.");
                        break;
                    }

                    case "right": {
                        final double amount = Double.parseDouble(args[2]);

                        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw + 90);
                        final double x = Math.sin(yaw) * amount;
                        final double z = Math.cos(yaw) * amount;

                        mc.thePlayer.setPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                        ChatUtil.display("Clipped you right " + amount + " blocks.");
                        break;
                    }

                    default: {
                        error();
                        break;
                    }
                }
                break;
            }

            default: {
                error();
                break;
            }
        }
    }
}
