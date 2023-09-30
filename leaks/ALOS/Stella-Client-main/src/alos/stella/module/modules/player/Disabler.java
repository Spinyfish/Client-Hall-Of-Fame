package alos.stella.module.modules.player;

import alos.stella.Stella;
import alos.stella.event.EventState;
import alos.stella.event.EventTarget;
import alos.stella.event.events.*;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.module.modules.combat.KillAura;
import alos.stella.module.modules.movement.Speed;
import alos.stella.utils.BlockUtils;
import alos.stella.utils.ClientUtils;
import alos.stella.utils.MovementUtils;
import alos.stella.utils.PacketUtils;
import alos.stella.value.BoolValue;
import alos.stella.value.ListValue;
import net.minecraft.block.BlockCarpet;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.apache.http.concurrent.Cancellable;

@ModuleInfo(name = "Disabler", description = "ac===rip", category = ModuleCategory.PLAYER)
public class Disabler extends Module {

    public final ListValue modeValue = new ListValue("Mode", new String[]{"MushMC", "Test"}, "MushMC");
    public void onDisable(){
        mc.timer.timerSpeed = 1.0f;
    }


    @EventTarget
    public void onUpdate(final UpdateEvent event)  {

    }

    @EventTarget
    public void onMove(final MoveEvent event){
    }
    @EventTarget
    public void onMotion(final MotionEvent event){
    }
    @EventTarget
    public void onPacket(PacketEvent event) {
        if (modeValue.get().equalsIgnoreCase("Test")) {
        	if (event.getPacket() instanceof S42PacketCombatEvent) {
        		event.cancelEvent(true);
                ClientUtils.displayChatMessage("Test3");
        	}
        }
        if (modeValue.get().equalsIgnoreCase("MushMC")) {
            if (event.getPacket() instanceof C16PacketClientStatus) {
                event.cancelEvent(true);
            }
            if (event.getPacket() instanceof C0APacketAnimation) {
                event.cancelEvent(true);
            }
            if (event.getPacket() instanceof S42PacketCombatEvent) {
                event.cancelEvent(true);
            }
            if (event.getPacket() instanceof S3EPacketTeams) {
                event.cancelEvent(true);
            }
            mc.thePlayer.capabilities.isCreativeMode=true;
        }
    }

    @EventTarget
    public void strafe(StrafeEvent event) {
    }
}