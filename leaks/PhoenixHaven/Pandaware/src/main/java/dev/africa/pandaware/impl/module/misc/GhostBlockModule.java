package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;

@ModuleInfo(name = "Ghost Block", description = "does funny")
public class GhostBlockModule extends Module {
    private final BooleanSetting blockBreak = new BooleanSetting("Breaking", false);
    private final BooleanSetting blockPlace = new BooleanSetting("Placing", false);
    private final BooleanSetting cancelSwing = new BooleanSetting("Cancel Swings", false);

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C07PacketPlayerDigging && this.blockBreak.getValue()
                || event.getPacket() instanceof C08PacketPlayerBlockPlacement && this.blockPlace.getValue()
                || event.getPacket() instanceof C0APacketAnimation && this.cancelSwing.getValue() && mc.gameSettings.keyBindAttack.isKeyDown()) {
            event.cancel();
        }
    };

    public GhostBlockModule() {
        this.registerSettings(this.blockBreak, this.blockPlace, this.cancelSwing);
    }
}
