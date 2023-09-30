/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.render;

import net.minecraft.util.ResourceLocation;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRenderCape;
import Reality.Realii.managers.FriendManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class Capes
        extends Module {
    public Capes() {
        super("Capes", ModuleType.Render);
    }

    @EventHandler
    public void onRender(EventRenderCape event) {
        if (this.mc.theWorld != null && FriendManager.isFriend(event.getPlayer().getName())) {
            // TODO: 2021/5/20 FIX CAPE
            mc.thePlayer.setLocationOfCape(new ResourceLocation("client/cape.png"));
//            event.setLocation(Reality.CLIENT_CAPE);
            event.setCancelled(true);
        }
    }
}

