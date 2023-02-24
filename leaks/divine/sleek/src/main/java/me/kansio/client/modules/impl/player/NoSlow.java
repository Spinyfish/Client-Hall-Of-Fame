package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.NoSlowEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;

@ModuleData(
        name = "No Slow",
        category = ModuleCategory.PLAYER,
        description = "Stops you from getting slowed down"
)
public class NoSlow extends Module {

    public ModeValue mode = new ModeValue("Mode", this, "Vanilla");
    public BooleanValue item = new BooleanValue("Item", this, true);
    public BooleanValue water = new BooleanValue("Water", this, false);
    public BooleanValue soulsand = new BooleanValue("SoulSand", this, false);

    @Subscribe
    public void onNoSlow(NoSlowEvent event) {
        switch (event.getType()) {
            case ITEM:
                event.setCancelled(item.getValue());
                break;
            case WATER:
                event.setCancelled(water.getValue());
                break;
            case SOULSAND:
                event.setCancelled(soulsand.getValue());
                break;
        }
    }

}
