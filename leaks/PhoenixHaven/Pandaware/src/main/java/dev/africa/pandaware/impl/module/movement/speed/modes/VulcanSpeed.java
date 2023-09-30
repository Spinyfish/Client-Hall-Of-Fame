package dev.africa.pandaware.impl.module.movement.speed.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.combat.KillAuraModule;
import dev.africa.pandaware.impl.module.movement.ScaffoldModule;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import lombok.AllArgsConstructor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;

public class VulcanSpeed extends ModuleMode<SpeedModule> {
    private final EnumSetting<VulcanMode> mode = new EnumSetting<>("Mode", VulcanMode.FASTFALL);
    private final BooleanSetting timerSpeed = new BooleanSetting("Timer", false);

    long lastTime;
    int tick = 0, oldSlot, newSlot;

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            if (this.mode.getValue() == VulcanMode.YPORT) {
                if (Client.getInstance().getModuleManager().getByClass(ScaffoldModule.class).getData().isEnabled())
                    return;
                KillAuraModule killAuraModule = Client.getInstance().getModuleManager().getByClass(KillAuraModule.class);
                if (killAuraModule.getData().isEnabled() && killAuraModule.target != null) return;
                if (mc.thePlayer.isUsingItem()) return;
                if (System.currentTimeMillis() - lastTime > 1200L) {
                    if (getBlockStack() == -1) {
                        Client.getInstance().getNotificationManager().addNotification(Notification.Type.WARNING, "You need to have blocks in your hotbar!", 1);
                        lastTime = System.currentTimeMillis();
                        newSlot = getBlockStack();
                        oldSlot = mc.thePlayer.inventory.currentItem;
                        tick = 0;
                        this.getParent().toggle(false);
                        return;
                    }

                    switch (tick) {
                        case 0:
                            newSlot = getBlockStack();
                            oldSlot = mc.thePlayer.inventory.currentItem;
                            if (oldSlot != newSlot)
                                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(newSlot));
                            event.setPitch(90);

                            tick++;
                            break;
                        case 1:
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(newSlot)));
                            event.setPitch(90);

                            tick++;
                            break;
                        case 2:
                            if (newSlot != oldSlot)
                                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(oldSlot));
                            lastTime = System.currentTimeMillis();
                            tick = 0;
                            break;
                    }
                }
            }
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        switch (this.mode.getValue()) {
            case FASTFALL:
                if (MovementUtils.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        if (this.timerSpeed.getValue()) {
                            mc.timer.timerSpeed = 0.9f;
                        }
                        mc.thePlayer.jump();
                        event.y = mc.thePlayer.motionY = 0.42f;
                        MovementUtils.strafe(event, MovementUtils.getBaseMoveSpeed() * (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.9 : 2.1));
                    } else if (mc.thePlayer.getAirTicks() == 5) {
                        if (this.timerSpeed.getValue() && mc.thePlayer.fallDistance < 1) {
                            mc.timer.timerSpeed = 1.25f;
                        }
                        event.y = mc.thePlayer.motionY = -0.42f;
                    } else {
                        if (mc.timer.timerSpeed == 1.3f && mc.thePlayer.fallDistance > 1) {
                            mc.timer.timerSpeed = 1;
                        }
                    }
                }
                break;
            case YPORT:
                if (Client.getInstance().getModuleManager().getByClass(ScaffoldModule.class).getData().isEnabled()) return;
                KillAuraModule killAuraModule = Client.getInstance().getModuleManager().getByClass(KillAuraModule.class);
                if (killAuraModule.getData().isEnabled() && killAuraModule.target != null) return;
                if (mc.thePlayer.isUsingItem()) return;
                if (MovementUtils.isMoving() && this.getBlockStack() != -1) {
                    if (PlayerUtils.isMathGround()) {
                        if (this.timerSpeed.getValue()) {
                            mc.timer.timerSpeed = 1.8f;
                        }
                        event.y = mc.thePlayer.motionY = 0.42f;
                        MovementUtils.strafe(event, MovementUtils.getBaseMoveSpeed() * (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.9 : 2.1));
                    } else if (mc.thePlayer.getAirTicks() == 1) {
                        if (this.timerSpeed.getValue()) {
                            mc.timer.timerSpeed = 0.9f;
                        }
                        MovementUtils.strafe(event, MovementUtils.getSpeed());
                        mc.thePlayer.motionY = -0.0784000015258789;
                    } else if (mc.thePlayer.fallDistance > 0.5) {
                        mc.timer.timerSpeed = 1f;
                    }
                }
        }
    };

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindJump.pressed = false;
        tick = 0;
    }

    public VulcanSpeed(String name, SpeedModule parent) {
        super(name, parent);

        this.registerSettings(
                this.mode,
                this.timerSpeed
        );
    }

    @AllArgsConstructor
    private enum VulcanMode {
        FASTFALL("Fast Fall"),
        YPORT("Yport");

        private final String label;
        @Override
        public String toString() {
            return label;
        }
    }

    private int getBlockStack() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                slot = i;
                break;
            }
        }
        return slot;
    }
}
