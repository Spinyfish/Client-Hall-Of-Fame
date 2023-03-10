package store.femboy.spring.impl.module.impl.player;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.event.impl.EventGetPacket;
import store.femboy.spring.impl.event.impl.EventUpdate;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;

public final class Velocity extends Module {
    public Velocity() {
        super("Velocity", "No more kb", Keyboard.KEY_U, Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    double velocityX = 0;
    double velocityY = 0;
    double velocityZ = 0;

    @EventHandler(EventPriority.LOWEST)
    public final Listener<EventGetPacket> onGetPacket = e ->{

        if(e.getPacket() instanceof S12PacketEntityVelocity){

            S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();

            velocityX = (s12.getMotionX() / 8000D) * 0;
            velocityY = (s12.getMotionY() / 8000D) * 0;
            velocityZ = (s12.getMotionZ() / 8000D) * 0;

            e.setCancelled(true);
        }else if(e.getPacket() instanceof S27PacketExplosion){
            S27PacketExplosion s27 = (S27PacketExplosion) e.getPacket();
            velocityX = (s27.getX() / 8000D) * 0;
            velocityY = (s27.getY() / 8000D) * 0;
            velocityZ = (s27.getZ() / 8000D) * 0;

            e.setCancelled(true);
        }
    };

    public final Listener<EventUpdate> onUpdate = e ->{
        mc.thePlayer.addVelocity(velocityX, velocityY, velocityZ);
    };
}
