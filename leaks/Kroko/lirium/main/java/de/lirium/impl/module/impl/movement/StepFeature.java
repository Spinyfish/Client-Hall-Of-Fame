package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.StepEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.network.play.client.CPacketPlayer;

@ModuleFeature.Info(name = "Step", description = "Step up blocks", category = ModuleFeature.Category.MOVEMENT)
public class StepFeature extends ModuleFeature {

    @Value(name = "Height")
    private final SliderSetting<Double> stepHeight = new SliderSetting<>(1.0, 0.5, 10.0, 0.5);

    @Value(name = "Mode")
    private final ComboBox<String> mode = (ComboBox<String>) new ComboBox<>("Vanilla", new String[]{"Updated NCP"}).onChange(value -> {
        switch (value.getValue()) {
            case "Vanilla":
                stepHeight.max = 10.0;
                break;
            case "Updated NCP":
                stepHeight.setValue(Math.min(stepHeight.getValue(), 1.5));
                stepHeight.max = 1.5;
                break;
        }
    });

    private boolean sprinting;

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        setSuffix(mode.getValue());
        if (!mc.player.isCollidedHorizontally)
            sprinting = mc.player.isSprinting();
    };

    @EventHandler
    public final Listener<StepEvent> stepEvent = e -> {
        if (e.getEntity() != getPlayer())
            return;
        if (mc.player.groundTicks < 2 || mc.world.containsAnyLiquid(mc.player.getEntityBoundingBox()) || mc.player.isOnLadder() || !mc.player.isCollidedHorizontally)
            return;
        if (e.getState().equals(StepEvent.State.PRE)) {
            if (sprinting)
                mc.player.setSprinting(true);
            e.setStepHeight(stepHeight.getValue());
        }
        if (e.getState().equals(StepEvent.State.ON_STEP)) {
            switch (mode.getValue()) {
                case "Vanilla":
                    break;
                case "Updated NCP":
                    doUpdatedNCP(e);
                    break;
            }
        }
    };

    private void doUpdatedNCP(StepEvent e) {
        if (e.getStepHeight() <= 0.6)
            return;
        sendPacket(new CPacketPlayer.Position(getX(), getY(), getZ(), true));
        sendPacket(new CPacketPlayer.Position(getX(), getY() + 0.42f, getZ(), false));
        sendPacket(new CPacketPlayer.Position(getX(), getY() + 0.753f, getZ(), false));
        if (e.getStepHeight() > 1.0) {
            sendPacket(new CPacketPlayer.Position(getX(), getY() + 0.753f + 0.24813599859, getZ(), false));
            sendPacket(new CPacketPlayer.Position(getX(), getY() + 0.753f + 0.24813599859 - 0.015555072702198913, getZ(), false));
            sendPacket(new CPacketPlayer.Position(getX(), getY() + 0.753f + 0.24813599859 + 0.17078707721880448, getZ(), false));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (getPlayer() != null)
            getPlayer().stepHeight = 0.5f;
    }
}
