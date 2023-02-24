package me.kansio.client.modules.impl.movement;


import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.kansio.client.Client;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@ModuleData(
        name = "Sprint",
        category = ModuleCategory.MOVEMENT,
        description = "Automatically sprints"
)
public class Sprint extends Module {

    private ModeValue mode = new ModeValue("Mode", this, "Legit", "Omni");

    private boolean skip;

    @Getter
    private final BooleanValue keepSprint = new BooleanValue("Keep Sprint", this, true); //Handled in NetHandlerPlayerClient at "processEntityAction" and EntityPlayerSP at "setSprinting"

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.isSneaking()) return;

        switch (mode.getValue()) {
            case "Legit":
                if (!skip) {
                    mc.thePlayer.setSprinting(
                            !mc.thePlayer.isCollidedHorizontally
                            && !mc.thePlayer.isSneaking()
                            && mc.thePlayer.getFoodStats().getFoodLevel() > 5
                            && mc.gameSettings.keyBindForward.pressed
                    );
                } else {
                    skip = false;
                }
                break;
            case "Omni":
                if (mc.thePlayer.isMoving() && !mc.thePlayer.isSprinting())
                    mc.thePlayer.setSprinting(true);
                break;
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacketDirection().name().equalsIgnoreCase("INBOUND") && !(event.getPacket() instanceof C03PacketPlayer))
        if (keepSprint.getValue() && event.getPacket() instanceof C0BPacketEntityAction) {
            C0BPacketEntityAction packet = event.getPacket();
            if (((C0BPacketEntityAction) event.getPacket()).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.setSprinting(false);
    }

}
