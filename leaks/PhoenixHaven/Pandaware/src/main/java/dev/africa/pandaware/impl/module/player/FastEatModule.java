package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.KeyEvent;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.packet.PacketBalance;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.player.MovementUtils;
import lombok.AllArgsConstructor;
import lombok.var;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(name = "FastEat", description = "become dream", category = Category.PLAYER)
public class FastEatModule extends Module {
    private final EnumSetting<FastEatMode> mode = new EnumSetting<>("Mode", FastEatMode.NCP);
    private final NumberSetting timer = new NumberSetting("Timer", 2, 1.01, 1.2, 0.01,
            () -> this.mode.getValue() == FastEatMode.TIMER);
    private final BooleanSetting stopMoving = new BooleanSetting("Stop moving", false);

    private int packet;
    private boolean fixed;

    private final TimeHelper time = new TimeHelper();

    public FastEatModule() {
        this.registerSettings(
                this.mode,
                this.timer,
                this.stopMoving);
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            if (mc.thePlayer.inventory.getCurrentItem() != null) {
                if ((mc.gameSettings.keyBindUseItem.isKeyDown()) && (!this.stopMoving.getValue() || !MovementUtils.isMoving())) {
                    var heldItem = mc.thePlayer.inventory.getCurrentItem().getItem();
                    if (heldItem instanceof ItemFood || heldItem instanceof ItemPotion) {
                        fixed = false;
                        switch (this.mode.getValue()) {
                            case NCP:
                                if (packet != 18) {
                                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
                                    packet++;
                                } else {
                                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                                }
                                break;
                            case TIMER:
                                mc.timer.timerSpeed = this.timer.getValue().floatValue();
                                break;

                            case VULCAN:
                                if (packet != 16) {
                                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                                        mc.timer.timerSpeed = 0.33f;
                                    } else {
                                        mc.timer.timerSpeed = 1f;
                                    }
                                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
                                    packet++;
                                } else {
                                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                                    mc.gameSettings.keyBindUseItem.pressed = false;
                                }
                                break;

                            case NIKOCADO_AVOCADO:
                                if (time.reach(500)) {
                                    for (int i = 0; i < 32; i++) {
                                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
                                    }
                                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                                    time.reset();
                                }
                                break;

                            case DREAM:
                                if (time.reach(500)) {
                                    for (int i = 0; i < 100; i++) {
                                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
                                    }
                                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                                    time.reset();
                                    if (mc.isSingleplayer()) mc.displayGuiScreen(new GuiWinGame());
                                }
                                mc.thePlayer.motionX = 0;
                                mc.thePlayer.motionZ = 0;
                                break;
                            case HYPIXEL:
                                if (packet != 20) {
                                    mc.timer.timerSpeed = 0.5f;
                                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(true));
                                    packet++;
                                } else {
                                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                                }
                            break;
                        }
                    }
                }
            }

            if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                if (!fixed) {
                    packet = 0;
                    mc.timer.timerSpeed = 1f;
                    fixed = true;
                    time.reset();
                }
            }
        }
    };

    @Override
    public void onDisable() {
        packet = 0;
        mc.timer.timerSpeed = 1f;
        fixed = false;
    }

    @AllArgsConstructor
    private enum FastEatMode {
        NCP("NCP"),
        TIMER("Timer"),
        HYPIXEL("Hypixel"),
        VULCAN("Vulcan"),
        NIKOCADO_AVOCADO("Nikocado Avocado"),
        DREAM("Dream");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
