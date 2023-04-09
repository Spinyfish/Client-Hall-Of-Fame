package tech.dort.dortware.impl.modules.player;

import org.lwjgl.opengl.Display;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.modules.misc.Commands;
import tech.dort.dortware.impl.modules.movement.Sprint;
import tech.dort.dortware.impl.modules.render.Hud;
import tech.dort.dortware.impl.utils.player.ChatUtil;

public class Panic extends Module {

    public Panic(ModuleData moduleData) {
        super(moduleData);
    }

    public boolean panic;

    @Override
    public void onEnable() {
        if (!panic) {
            Display.setTitle("Minecraft 1.8");
            ChatUtil.displayChatMessage("Disabled everything, enable the module again to unpanic.");
            Client.INSTANCE.getModuleManager().getObjects().stream().filter(Module::isToggled).forEach(Module::toggle);
            panic = true;
        } else {
            Client.INSTANCE.getModuleManager().get(Commands.class).toggle();
            Client.INSTANCE.getModuleManager().get(Sprint.class).toggle();
            Client.INSTANCE.getModuleManager().get(Hud.class).toggle();
            ChatUtil.displayChatMessage("Enabled some modules, enable the module again to panic.");
            Display.setTitle("Dortware");
            this.toggle();
            panic = false;
        }
    }
}
