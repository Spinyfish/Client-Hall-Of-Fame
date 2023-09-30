package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.utils.client.Printer;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C18PacketSpectate;

@ModuleInfo(name = "Entity Desync", category = Category.PLAYER)
public class EntityDesyncModule extends Module {
    private Entity riding;

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.thePlayer == null) {
            this.riding = null;
            this.toggle();
            return;
        }

        if (!mc.thePlayer.isRiding()) {
            Printer.chat("You are not riding an entity.");
            this.riding = null;
            this.toggle();
            return;
        }

        this.riding = mc.thePlayer.ridingEntity;

        mc.thePlayer.dismountEntity(this.riding);
        mc.theWorld.removeEntity(this.riding);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.riding != null) {
            this.riding.isDead = false;
            if (!mc.thePlayer.isRiding()) {
                mc.theWorld.spawnEntityInWorld(this.riding);
                mc.thePlayer.mountEntity(this.riding);
            }

            this.riding = null;
            Printer.chat("Forced a remount.");
        }
    }

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (this.riding == null || mc.thePlayer.isRiding())
                return;

            this.riding.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof C18PacketSpectate) event.cancel();
    };
}
