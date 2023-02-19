package alos.stella.module.modules.combat;

import alos.stella.Stella;
import alos.stella.event.EventTarget;
import alos.stella.event.events.PacketEvent;
import alos.stella.event.events.UpdateEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.module.modules.movement.Speed;
import alos.stella.utils.timer.MSTimer;
import alos.stella.value.BoolValue;
import alos.stella.value.FloatValue;
import alos.stella.value.ListValue;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(name = "Velocity", description = "Reduce knockback", category = ModuleCategory.COMBAT)
public class Velocity extends Module {
    public static final ListValue modeValue = new ListValue("Mode", new String[]{"Simple", "Cancel", "Cancel2" ,"AAC"}, "Simple");

    public static FloatValue horizontal = new FloatValue("Horizontal", 0f, -100f, 100f,"%");
    public static FloatValue vertical = new FloatValue("Vertical", 0f, 0, 100f, "%");
    private static FloatValue aacPushXZReducerValue = new FloatValue("AACPushXZReducer", 2F, 1F, 3F, "x");
    private static BoolValue aacPushYReducerValue = new BoolValue("AACPushYReducer", true);

    MSTimer velocityTimer = new MSTimer();
    boolean velocityInput = false;
    private boolean jump = false;
    @EventTarget
    public void onUpdate(UpdateEvent event){
        if (modeValue.get().equalsIgnoreCase("AAC")){
            if (jump) {
                if (mc.thePlayer.onGround)
                    jump = false;
            } else {
                // Strafe
                if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0)
                    mc.thePlayer.onGround = true;

                // Reduce Y
                if (mc.thePlayer.hurtResistantTime > 0 && aacPushYReducerValue.get() && Stella.moduleManager.getModule(Speed.class).getState()) mc.thePlayer.motionY -= 0.014999993;
            }

            // Reduce XZ
            if (mc.thePlayer.hurtResistantTime >= 19) {
                float reduce = aacPushXZReducerValue.get();

                mc.thePlayer.motionX /= reduce;
                mc.thePlayer.motionZ /= reduce;
            }
        }
    }
    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity && modeValue.get().equalsIgnoreCase("Simple")) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();

            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                    event.cancelEvent();
                } else {
                    packet.motionX = ((int)(packet.getMotionX() * horizontal.getValue() / 100.0));
                    packet.motionY = ((int)(packet.getMotionX() * vertical.getValue() / 100.0));
                    packet.motionZ = ((int)(packet.getMotionX() * horizontal.getValue() / 100.0));
                }
            }
        }
        if (modeValue.get().equalsIgnoreCase("Cancel")) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
            	event.cancelEvent();
            }
        }
        if (modeValue.get().equalsIgnoreCase("Cancel2")) {
	            if (event.getPacket() instanceof S12PacketEntityVelocity) {
	            	if (mc.thePlayer.hurtTime == 1) {
		            	event.cancelEvent();
		            	mc.thePlayer.isCollidedVertically=false;
	            }
        	}
        }
    }
    @Override
    public String getTag() {
        return modeValue.get();
    }
}