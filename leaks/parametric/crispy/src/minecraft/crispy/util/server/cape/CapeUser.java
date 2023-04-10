package crispy.util.server.cape;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.ResourceLocation;

@Getter
@Setter
public class CapeUser {

    public ResourceLocation resourceLocation;
    public String player;

    public CapeUser(String player, ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        this.player = player;
    }
}
