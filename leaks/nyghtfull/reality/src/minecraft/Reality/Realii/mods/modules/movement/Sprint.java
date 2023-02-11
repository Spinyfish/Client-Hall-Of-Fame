/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.movement;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.world.Scaffold;
import Reality.Realii.mods.modules.movement.Sprint;
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


public class Sprint
        extends Module {
    private Mode mod = new Mode("Mode", "Mode", new String[]{"Key", "Set"}, "Key");
    private Option<Boolean> omni = new Option<Boolean>("Omni-Directional", "omni", true);

    public Sprint() {
        super("Sprint", ModuleType.Movement);
        this.addValues(this.omni, this.mod);
    }

    @EventHandler
    public void onMove(EventMove e) {
    
    	
        omni.setVisible(mod.getModeAsString().equals("Set"));
        
        if (mod.getModeAsString().equals("Key")) {
        	
            
        }

        if (this.mc.thePlayer.getFoodStats().getFoodLevel() > 6 && this.omni.getValue() != false ? this.mc.thePlayer.moving() : this.mc.thePlayer.moveForward > 0.1f) {
            this.mc.thePlayer.setSprinting(true);
            
            
            
            {
            	}
            	
            	
            	}
            	
            }
            
            
            
            }
        
    


