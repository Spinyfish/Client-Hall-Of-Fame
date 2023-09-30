package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.utils.client.Printer;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "Middle Click Friend")
public class MiddleClickFriendModule extends Module {
    private boolean wasButtonDown;

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        boolean isButtonDown = Mouse.isButtonDown(2);

        if (!this.wasButtonDown && isButtonDown) {
            Client.getInstance().getExecutor().execute(() -> {
                EntityPlayer entity = (EntityPlayer) mc.objectMouseOver.entityHit;

                if (Client.getInstance().getIgnoreManager().isIgnore(entity, true)) {
                    Client.getInstance().getIgnoreManager().remove(entity, true);
                    Printer.chat("§cRemoved friend §7" + entity.getName());
                } else {
                    Client.getInstance().getIgnoreManager().add(entity, true);
                    Printer.chat("§aAdded friend §7" + entity.getName());
                }
            });
        }

        this.wasButtonDown = isButtonDown;
    };
}
