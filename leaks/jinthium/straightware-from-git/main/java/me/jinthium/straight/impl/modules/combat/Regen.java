package me.jinthium.straight.impl.modules.combat;

import com.google.gson.annotations.Expose;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends Module {

    private final NumberSetting health = new NumberSetting("Minimum Health", 15, 1, 20, 1);
    private final NumberSetting packets = new NumberSetting("Speed", 20, 1, 100, 1);
    
    public Regen(){
        super("Regen", Category.COMBAT);
        this.addSettings(health, packets);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if(event.isPre()){
            if (mc.thePlayer.getHealth() < this.health.getValue().floatValue()) {
                for (int i = 0; i < this.packets.getValue().intValue(); i++) {
                    PacketUtil.sendPacket(new C03PacketPlayer(event.isOnGround()));
                }
            }
        }
    };
}
