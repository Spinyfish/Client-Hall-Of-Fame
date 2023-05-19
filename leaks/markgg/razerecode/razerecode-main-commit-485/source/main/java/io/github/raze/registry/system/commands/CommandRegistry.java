package io.github.raze.registry.system.commands;

import io.github.raze.Raze;
import io.github.raze.commands.collection.*;
import io.github.raze.commands.system.BaseCommand;
import io.github.raze.events.collection.game.EventChatMessage;
import io.github.raze.events.collection.game.EventKeyboard;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.utilities.collection.visual.ChatUtil;
import io.github.raze.utilities.system.BaseUtility;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRegistry implements BaseUtility {

    public List<BaseCommand> commands;

    public List<BaseCommand> getCommands() {
        return commands;
    }

    public CommandRegistry() {
        commands = new ArrayList<>();
    }

    public void bootstrap() {
        register(

                // Commands
                new HelpCommand(),
                new PrefixCommand(),
                new VClipCommand(),
                new HClipCommand(),
                new BindCommand(),
                new SayCommand(),
                new IGNCommand(),
                new ToggleCommand(),
                new WaterMarkCommand()

        );
    }

    public void register(BaseCommand... input) {
        commands.addAll(Arrays.asList(input));
    }

    public BaseCommand getCommandByName(String input) {
        for (BaseCommand command : getCommands()) {
            if (
                    command.getName().equalsIgnoreCase(input) ||
                    command.getAliases().contains(input)
            ) {
                return command;
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onChatMessage(EventChatMessage event) {

        String prefix = Raze.INSTANCE.getPrefix();

        if (event.isCancelled()) {
            return;
        }

        String   message   = event.getMessage();
        String[] arguments = message.split(" ");

        if (message.startsWith(prefix)) {
            event.setCancelled(true);
        } else {
            return;
        }

        String name = arguments[0].substring(1);

        BaseCommand command = getCommandByName(name);

        String result = String.format("Could not find command %s.", name);

        if (command != null) {
            result = command.onCommand(arguments, message);
        }

        if (!result.isEmpty()) {
            ChatUtil.addChatMessage(result);
        }
    }

    @SubscribeEvent
    public void onKeyboard(EventKeyboard event) {

        String prefix = Raze.INSTANCE.getPrefix();

        int keyCode = event.getKeyCode();

        if (keyCode == Keyboard.getKeyIndex(prefix)) {
            mc.displayGuiScreen(new GuiChat(prefix));
        }

    }

}
