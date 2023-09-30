/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.commands.commands;

import Reality.Realii.commands.Command;
import Reality.Realii.utils.cheats.player.Helper;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.MathUtil;
import net.minecraft.util.EnumChatFormatting;

public class VClip
extends Command {
    private TimerUtil timer = new TimerUtil();

    public VClip() {
        super("Vc", new String[]{"Vclip", "clip", "verticalclip", "clip"}, "<distance>", "Teleport down a specific ammount");
    }

    @Override
    public String execute(String[] args) {
        if (!Helper.onServer("enjoytheban")) {
            if (args.length > 0) {
                if (MathUtil.parsable(args[0], (byte)4)) {
                    float distance = Float.parseFloat(args[0]);
                    Helper.mc.thePlayer.setPosition(Helper.mc.thePlayer.posX, Helper.mc.thePlayer.posY + (double)distance, Helper.mc.thePlayer.posZ);
                    Helper.sendMessage("> Vclipped you " + distance + " blocks");
                } else {
                    this.syntaxError((Object)((Object)EnumChatFormatting.GRAY) + args[0] + " is not a valid number");
                }
            } else {
                this.syntaxError((Object)((Object)EnumChatFormatting.GRAY) + "Valid .vclip <number>");
            }
        } else {
            Helper.sendMessage("> You cannot use vclip on the ETB Server.");
        }
        return null;
    }
}

