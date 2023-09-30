package dev.echo.module.impl.misc;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.network.PacketReceiveEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.utils.server.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public final class NoRotate extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Cancel");
    private final BooleanSetting fakeUpdate = new BooleanSetting("Fake Update", false);

    @Link
    public Listener<PacketReceiveEvent> onPacketReceive = e -> {
        if (mc.thePlayer == null) return;
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            switch (mode.getMode()) {
                case "Normal":
                    packet.setYaw(mc.thePlayer.rotationYaw);
                    packet.setPitch(mc.thePlayer.rotationPitch);
                    break;
                case "Cancel":
                    e.setCancelled(true);
                    break;
            }

            if (fakeUpdate.isEnabled()) {
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                        mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,
                        packet.getYaw(), packet.getPitch(), mc.thePlayer.onGround));
            }
        }
    };

    public NoRotate() {
        super("No Rotate", Category.MISC, "Prevents servers from rotating you");
        this.addSettings(fakeUpdate);
    }

}
