package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.SprintEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.modules.player.Scaffold;

/**
 * @author Auth
 */

public class Sprint extends Module {

    private final BooleanValue legit = new BooleanValue("Legit", this, true);
    private final BooleanValue cancel = new BooleanValue("Cancel", this, false);

    public Sprint(ModuleData moduleData) {
        super(moduleData);
        register(legit, cancel);
    }

    @Subscribe
    public void onSprint(SprintEvent event) {
        event.setSprinting(canSprint());
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.setSprinting(canSprint());
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C0BPacketEntityAction && cancel.getValue()) {
            C0BPacketEntityAction packetEntityAction = event.getPacket();
            if (packetEntityAction.func_180764_b() == C0BPacketEntityAction.Action.STOP_SPRINTING || packetEntityAction.func_180764_b() == C0BPacketEntityAction.Action.START_SPRINTING) {
                event.setCancelled(true);
            }
        }
    }

    private boolean canSprint() {
        final Scaffold scaffold = Client.INSTANCE.getModuleManager().get(Scaffold.class);
        if (scaffold.isToggled() && scaffold.sprintCheck.getValue()) {
            return false;
        }

        if (!legit.getValue()) {
            return mc.thePlayer.isMoving();
        }

        if (Client.INSTANCE.getModuleManager().get(NoSlow.class).isToggled()) {
            return (mc.thePlayer.getFoodStats().getFoodLevel() > 6 || mc.thePlayer.capabilities.isCreativeMode) && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && mc.thePlayer.movementInput.moveForward > 0 && !mc.thePlayer.isPotionActive(Potion.blindness);
        }

        return (mc.thePlayer.getFoodStats().getFoodLevel() > 6 || mc.thePlayer.capabilities.isCreativeMode) && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && !mc.thePlayer.isUsingItem() && mc.thePlayer.movementInput.moveForward > 0 && !mc.thePlayer.isPotionActive(Potion.blindness);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.getIsKeyPressed());
    }
}