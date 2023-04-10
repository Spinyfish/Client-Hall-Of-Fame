package crispy.features.hacks;

import lombok.Getter;
import net.minecraft.client.Minecraft;

@Getter
public class HackMode<T extends Hack>{
    protected Minecraft mc = Minecraft.getMinecraft();
    protected final String name;
    protected final T parent;

    public HackMode(String name, T parent) {
        this.name = name;
        this.parent = parent;
    }

}
