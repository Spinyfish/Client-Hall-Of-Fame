package me.kansio.client.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import net.minecraft.entity.player.EntityPlayer;

@ModuleData(
        name = "Anti Bot",
        category = ModuleCategory.COMBAT,
        description = "Hides bots"
)
public class AntiBot extends Module {

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        for (EntityPlayer p : mc.theWorld.playerEntities) {
            if (p != null) {
                if (p.isInvisible() && p != mc.thePlayer) {
                    mc.theWorld.removeEntity(p);
                }
            }
        }
    }

}
