package dev.africa.pandaware.impl.module.movement;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import lombok.Getter;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;

@Getter
@ModuleInfo(name = "Sprint", category = Category.MOVEMENT)
public class SprintModule extends Module {
    private final BooleanSetting omniSprint = new BooleanSetting("OmniSprint", false);
    private final BooleanSetting applySpeed = new BooleanSetting("Apply Speed", false);
    private final BooleanSetting cancel = new BooleanSetting("Cancel Sprint", false);

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        boolean canSprint = mc.thePlayer != null && PlayerUtils.isMathGround() &&
                !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.getFoodStats().getFoodLevel() > 6 &&
                MovementUtils.isMoving() && !mc.thePlayer.isCollidedHorizontally && !mc.gameSettings.keyBindSneak.pressed;
        if (this.applySpeed.getValue() && MovementUtils.canSprint() && mc.thePlayer != null) {
            MovementUtils.strafe(event, MovementUtils.getBaseMoveSpeed() * 0.97575f);
        }
        if (Client.getInstance().getModuleManager().getByClass(ScaffoldModule.class).getData().isEnabled()) return;
        if (omniSprint.getValue() && MovementUtils.canSprint()) {
            mc.thePlayer.setSprinting(true);
        } else if (mc.thePlayer.moveForward > 0 && MovementUtils.canSprint()) {
            mc.thePlayer.setSprinting(true);
        }
    };

    public SprintModule() {
        this.registerSettings(
                this.omniSprint,
                this.applySpeed,
                this.cancel
        );
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer.isSprinting() && cancel.getValue()) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer,
                    C0BPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C0BPacketEntityAction && cancel.getValue()) {
            event.cancel();
        }
    };
}
