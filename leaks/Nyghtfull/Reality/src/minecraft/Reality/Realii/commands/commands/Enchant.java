/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.commands.commands;

import Reality.Realii.commands.Command;
import Reality.Realii.utils.cheats.player.Helper;
import net.minecraft.client.Minecraft;

public class Enchant
extends Command {
    public Enchant() {
        super("Enchant", new String[]{"e"}, "", "get an enchanthed sword");
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/give " + Minecraft.getMinecraft().thePlayer.getName() + " diamond_sword 1 0 {ench:[{id:16,lvl:127}]}");
        } else {
            Helper.sendMessage("invalid syntax Valid .enchant");
        }
        return null;
    }
}

