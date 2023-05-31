package de.lirium.impl.command;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.event.EventListener;
import de.lirium.base.feature.Manager;
import de.lirium.impl.command.impl.*;
import de.lirium.impl.events.ChatEvent;
import de.lirium.util.string.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager extends EventListener implements Manager<CommandFeature> {

    final ArrayList<CommandFeature> commands = new ArrayList<>();
    public String prefix = ".";

    public CommandManager() {
        init();
        commands.addAll(Arrays.asList(new BindFeature(), new ToggleFeature(), new ReloadFeature(), new VClipFeature(), new ConfigFeature(), new PluginsFeature(), new FriendFeature(), new IngameNameFeature(), new ChangeSkinFeature()));
    }

    @Override
    public ArrayList<CommandFeature> getFeatures() {
        return commands;
    }

    @Override
    public <U extends CommandFeature> U get(Class<U> clazz) {
        final CommandFeature feature = this.getFeatures().stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
        if (feature == null) return null;
        return clazz.cast(feature);
    }

    @Override
    public CommandFeature get(Type type) {
        return this.getFeatures().stream().filter(m -> m.getClass().getName().equals(type.getTypeName())).findFirst().orElse(null);
    }

    @EventHandler
    public final Listener<ChatEvent> chatEventListener = e -> {
        if (e.message.startsWith(prefix)) {
            e.setCancelled(true);
            final String[] split = StringUtil.getArguments(e.message.substring(prefix.length()));

            this.getFeatures().forEach(command -> {
                if (split[0].equalsIgnoreCase(command.getName()) || Arrays.stream(command.getAlias()).anyMatch(s -> split[0].equalsIgnoreCase(s))) {
                    if (!command.execute(Arrays.copyOfRange(split, 1, split.length))) {
                        if (command.getArguments() != null) {
                            for (String argument : command.getArguments())
                                Minecraft.getMinecraft().player.addChatMessage(new TextComponentString(Client.PREFIX + "§e" + prefix + command.getName().toLowerCase() + " §7" + argument));
                        } else
                            System.out.println("Error " + command.getName() + " has no arguments and returning false");
                    }
                }
            });
        }
    };
}