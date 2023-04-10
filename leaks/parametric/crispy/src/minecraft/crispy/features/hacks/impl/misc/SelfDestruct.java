package crispy.features.hacks.impl.misc;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.server.CapeManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

@HackInfo(name = "SelfDestruct", category = Category.MISC)
public class SelfDestruct extends Hack {

    public static boolean selfDestruct;

    @Override
    public void onEnable() {
        Crispy.INSTANCE.stop();
        for(Hack hack : Crispy.INSTANCE.getHackManager().getHacks()) {
            hack.setKey(0);
            if(hack.isEnabled() && !hack.equals(this)) hack.setState(false);
        }
        selfDestruct = true;
        Display.setTitle("Minecraft 1.8");
        Minecraft.getMinecraft().setMinecraftIcon();
        CapeManager.capeUsers.clear();
        CapeManager.coolGuy.clear();
        CapeManager.ignoreUsers.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {

    }
}
