package byron.Mono;

import byron.Mono.accounts.AltManager;
import byron.Mono.clickgui.Clickgui;
import byron.Mono.clickgui.setting.SettingsManager;
import byron.Mono.command.CommandManager;
import byron.Mono.event.impl.EventChat;
import byron.Mono.event.impl.EventKey;
import byron.Mono.font.FontUtil;
import byron.Mono.interfaces.HUD;
import byron.Mono.module.Module;
import byron.Mono.module.ModuleManager;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public enum Mono {

    INSTANCE;

    private EventBus eventBus;
    private SettingsManager settingsManager;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private Clickgui clickGui;
    private AltManager altManager;
    public static HUD hud = new HUD();
    
    public final void init()
    {
    eventBus = new EventBus();
    settingsManager = new SettingsManager();
    moduleManager = new ModuleManager();
    commandManager = new CommandManager();
    clickGui = new Clickgui();
    hud = new HUD();
   

    FontUtil.bootstrap();

    eventBus.register(this);
    }



    @Subscribe//weirc
    public void onKey(EventKey e)
    {
        for (Module m : moduleManager.getModules())
        {
            if(m.getKey() == e.getKey()){
            m.toggle();
            }
        }
    }

    @Subscribe
    public void onChat(EventChat e)
    {
        commandManager.handleChat(e);
    }
    public final void stop() {
        eventBus.unregister(this);
    }
    public final EventBus getEventBus()
    {
        return eventBus;
    }
    public final ModuleManager getModuleManager() {
        return moduleManager;
    }
    public CommandManager getCommandManager() {
        return commandManager;
    }
    public final void sendMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.BLUE + "Mono" + ChatFormatting.BLACK + " > " + ChatFormatting.WHITE + message));
    }
    
    public final void sendAlert(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "Mono Alert" + ChatFormatting.BLACK + " > " + ChatFormatting.WHITE + message));
    }
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
    public final Clickgui getClickGui() {
        return clickGui;
    }
    public HUD getHud() {
        return hud;
    }
    public AltManager getAltManager()
    {
    return altManager;
    }

}
