package alos.stella.module.modules.movement;

import alos.stella.event.EventTarget;
import alos.stella.event.events.PacketEvent;
import alos.stella.event.events.UpdateEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.utils.MovementUtils;
import alos.stella.value.BoolValue;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;

@ModuleInfo(name = "Sprint", description = "Sprint", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    public BoolValue allDirectionsValue = new BoolValue("AllDirections", true);
    public BoolValue noPacketPatchValue = new BoolValue("AllDir-NoPacketsPatch", true);
    public BoolValue moveDirPatchValue = new BoolValue("AllDir-MoveDirPatch", false);
    public BoolValue blindnessValue = new BoolValue("Blindness", true);
    public BoolValue foodValue = new BoolValue("Food", true);

    public BoolValue checkServerSide = new BoolValue("CheckServerSide", false);
    public BoolValue checkServerSideGround = new BoolValue("CheckServerSideOnlyGround", false);

    private boolean modified = false;

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (allDirectionsValue.get() && noPacketPatchValue.get()) {
            if (event.getPacket() instanceof C0BPacketEntityAction && (((C0BPacketEntityAction) event.getPacket()).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING || ((C0BPacketEntityAction) event.getPacket()).getAction() == C0BPacketEntityAction.Action.START_SPRINTING)) {
                C0BPacketEntityAction packet = (C0BPacketEntityAction) event.getPacket();
                event.cancelEvent();
            }
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (!MovementUtils.isMoving() || mc.thePlayer.isSneaking() ||
                (blindnessValue.get() && mc.thePlayer.isPotionActive(Potion.blindness)) ||
                (foodValue.get() && !(mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying))) {
            mc.thePlayer.setSprinting(false);
            return;
        }

        if (allDirectionsValue.get() || mc.thePlayer.movementInput.moveForward >= 0.8F)
            mc.thePlayer.setSprinting(true);
    }

}
