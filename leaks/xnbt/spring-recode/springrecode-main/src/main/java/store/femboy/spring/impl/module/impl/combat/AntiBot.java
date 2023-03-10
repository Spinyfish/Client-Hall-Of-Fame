package store.femboy.spring.impl.module.impl.combat;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S14PacketEntity;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.event.impl.EventGetPacket;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;

public final class AntiBot extends Module {
    public AntiBot() {
        super("AntiBot", "No more retard floaty niggers", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler(EventPriority.LOWEST)
    public final Listener<EventGetPacket> onGetPacket = e ->{
        if (e.getPacket() instanceof S14PacketEntity) {
            S14PacketEntity packet = (S14PacketEntity) e.getPacket();
            Entity entity = packet.getEntity(mc.theWorld);

            if (!(entity instanceof EntityLivingBase) || mc.getNetHandler().getPlayerInfo(entity.getUniqueID()) == null) {
                return;
            }

        }
    };
}
