package me.kansio.client.modules.impl.visuals;

import me.kansio.client.Client;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.ModeValue;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

@ModuleData(
        name = "Cape",
        category = ModuleCategory.VISUALS,
        description = "Custom client capes"
)
public class Cape extends Module {

    private ModeValue capemode = new ModeValue("Cape", this,"Sleek");

    public ResourceLocation getCape() {
        switch (capemode.getValue()) {
            case "Sleek": {
                return new ResourceLocation("sleek/images/capes/sleekcape.png");
            }

            default: {
                throw new IllegalStateException("Unexpected value: " + capemode.getValue());
            }
        }
    }

    public boolean canRender(AbstractClientPlayer player) {
        return player == mc.thePlayer || Client.getInstance().getFriendManager().isFriend(player.getName()) || Client.getInstance().getUsers().containsKey(player.getName());
    }

}
