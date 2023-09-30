/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.commands.commands;

import Reality.Realii.Client;
import Reality.Realii.commands.Command;
import Reality.Realii.mods.Module;
import Reality.Realii.utils.cheats.player.Helper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Option;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.PlayerUtils;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.RotationUtil;
import Reality.Realii.mods.modules.movement.Sprint;
import Reality.Realii.mods.modules.world.Scaffold;
import Reality.Realii.Client;
import Reality.Realii.commands.Command;
import Reality.Realii.mods.Module;
import Reality.Realii.utils.cheats.player.Helper;
import net.minecraft.util.EnumChatFormatting;
import Reality.Realii.mods.modules.movement.Sprint;
import Reality.Realii.mods.modules.world.Scaffold;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Option;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.PlayerUtils;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.RotationUtil;
import Reality.Realii.mods.modules.movement.Sprint;
import Reality.Realii.mods.modules.world.Scaffold;
import Reality.Realii.Client;
import Reality.Realii.commands.Command;
import Reality.Realii.mods.Module;
import Reality.Realii.utils.cheats.player.Helper;
import net.minecraft.util.EnumChatFormatting;


public class Toggle
extends Command {
    public Toggle() {
        super("t", new String[]{"toggle", "togl", "turnon", "enable"}, "<module>", "Toggles a specified Module");
    }

    @Override
    public String execute(String[] args) {
        String modName = "";
        if (args.length > 1) {
            modName = args[1];
        } else if (args.length < 1) {
            Helper.sendMessageWithoutPrefix("\u00a7bCorrect usage:\u00a77 .t <module>");
        }
        boolean found = false;
        Module m = Client.instance.getModuleManager().getModuleByName(args[0].replaceAll(" ",""));
        if (m != null) {
        m.setEnabled(!m.isEnabled());
            
        
        

            
            
        
        	
        	 
        
            found = true;
            if (m.isEnabled()) {
                Helper.sendMessage("> " + m.getName() + (Object)((Object)EnumChatFormatting.GRAY) + " was" + (Object)((Object)EnumChatFormatting.GREEN) + " enabled");
            } else {
                Helper.sendMessage("> " + m.getName() + (Object)((Object)EnumChatFormatting.GRAY) + " was" + (Object)((Object)EnumChatFormatting.RED) + " disabled");
            }
        }
        if (!found) {
            Helper.sendMessage("> Module name " + (Object)((Object)EnumChatFormatting.RED) + args[0] + (Object)((Object)EnumChatFormatting.GRAY) + " is invalid");
        }
        return null;
    }
}

