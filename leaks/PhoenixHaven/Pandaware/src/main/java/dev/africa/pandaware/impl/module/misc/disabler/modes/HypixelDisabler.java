package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.game.ServerJoinEvent;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import dev.africa.pandaware.impl.module.render.HUDModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.utils.client.HWIDUtils;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.client.ServerUtils;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.player.MovementUtils;
import javazoom.jl.player.Player;
import lombok.var;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;

public class HypixelDisabler extends ModuleMode<DisablerModule> {
    private final BooleanSetting timer = new BooleanSetting("Timer Disabler", false);

    public HypixelDisabler(String name, DisablerModule parent) {
        super(name, parent);

        this.registerSettings(this.timer);
    }

    private Player player = null;
    private int packets;
    private boolean music;

    @Override
    public void onEnable() {
        this.packets = 0;
        if (Client.getInstance().isKillSwitch()) mc.timer.timerSpeed = 0;
    }

    @EventHandler
    EventCallback<TickEvent> onTick = event -> {
        if ((ServerUtils.isOnServer("mc.hypixel.net") || ServerUtils.isOnServer("hypixel.net")) && !(ServerUtils.compromised)) {
            if (this.packets <= 100) {
                mc.gameSettings.keyBindAttack.pressed = false;
                mc.gameSettings.keyBindUseItem.pressed = false;
            }
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if ((ServerUtils.isOnServer("mc.hypixel.net") || ServerUtils.isOnServer("hypixel.net")) && !(ServerUtils.compromised)) {
            if (event.getPacket() instanceof S01PacketJoinGame) {
                this.packets = 0;
            }

            HUDModule hud = Client.getInstance().getModuleManager().getByClass(HUDModule.class);
            if (this.packets <= 100 && (event.getPacket() instanceof C0FPacketConfirmTransaction ||
                    event.getPacket() instanceof C00PacketKeepAlive ||
                    event.getPacket() instanceof C03PacketPlayer ||
                    event.getPacket() instanceof C09PacketHeldItemChange ||
                    event.getPacket() instanceof C08PacketPlayerBlockPlacement ||
                    event.getPacket() instanceof C0CPacketInput ||
                    event.getPacket() instanceof C13PacketPlayerAbilities ||
                    event.getPacket() instanceof C07PacketPlayerDigging ||
                    event.getPacket() instanceof C0APacketAnimation ||
                    event.getPacket() instanceof C02PacketUseEntity)) {
                mc.thePlayer.inventory.currentItem = 0;
                event.cancel();

                if (event.getPacket() instanceof C03PacketPlayer) {
                    if (this.packets % 8 == 0) {
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX, mc.thePlayer.posY - (1 + (MovementUtils.MODULO_GROUND * 2)), mc.thePlayer.posZ,
                                false
                        ));
                    }

                    this.packets++;
                }
            }
            if (event.getPacket() instanceof C03PacketPlayer && this.timer.getValue()) {
                var c03 = (C03PacketPlayer) event.getPacket();
                if (!(c03.isMoving()) && (!mc.thePlayer.isSwingInProgress || !mc.thePlayer.isUsingItem()) && !c03.getRotating()) {
                    event.cancel();
                }
            }
            if (event.getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = event.getPacket();

                if (packet.getChatComponent().getUnformattedText().contains("Cages opened! FIGHT!")) {
                    if (RandomUtils.nextInt(0, 10000) == 6442) {
                        Printer.chat("congrats you triggered a 1/10000 chance");
                        new Thread(() -> {
                            try {
                                player = new Player(this.getClass().getResourceAsStream("/assets/minecraft/pandaware/funny.mp3"));
                                player.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }
            }
        } else {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event.getPacket() instanceof C03PacketPlayer) event.cancel();
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new CPacketConfirmTeleport(1));
            }
        }
    };

    @EventHandler
    EventCallback<ServerJoinEvent> onJoin = event -> {
        if (!(event.getIp().equals("mc.hypixel.net") || event.getIp().equals("hypixel.net")) && !ServerUtils.compromised) {
            this.getParent().toggle(false);
        }
    };

    @EventHandler
    EventCallback<TickEvent> onTick2 = event -> {
        String hwid = HWIDUtils.getHWID();

        if (hwid.equals("uiJqkrxp6lwMbArixQy2XyN37OGDJ5IIH3R38ZqVkFvWj5XGVzX2Sa8BGdwmWcqjbC6mKC48IbW4Szxrue+FKg==")) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new CPacketConfirmTeleport(1));
        }
    };
}
