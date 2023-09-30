package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import lombok.var;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

@ModuleInfo(name = "Anti Server", category = Category.PLAYER)
public class AntiServerModule extends Module {
    private final BooleanSetting noRotate = new BooleanSetting("NoRotate", true);
    private final BooleanSetting noCloseGUI = new BooleanSetting("Close window", false);
    private final BooleanSetting noOpenGUI = new BooleanSetting("Open window", false);

    public AntiServerModule() {
        this.registerSettings(
                this.noRotate,
                this.noCloseGUI,
                this.noOpenGUI
        );
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (mc.thePlayer != null && event.getPacket() instanceof S08PacketPlayerPosLook && this.noRotate.getValue()) {
            var packet = (S08PacketPlayerPosLook) event.getPacket();

            packet.setYaw(mc.thePlayer.rotationYaw);
            packet.setPitch(mc.thePlayer.rotationPitch);

            event.setPacket(packet);
        }
        if (mc.thePlayer != null && this.noCloseGUI.getValue() && event.getPacket() instanceof S2EPacketCloseWindow)
            event.cancel();
        if (mc.thePlayer != null && this.noOpenGUI.getValue() && event.getPacket() instanceof S2DPacketOpenWindow)
            event.cancel();
    };
}
