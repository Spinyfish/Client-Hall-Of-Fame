package crispy.features.commands;

import crispy.Crispy;
import crispy.features.event.Event;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

@Getter
public abstract class Command {
    private final String name;
    private final String description;
    private final String syntax;
    private final String alias;

    public Command() {
        CommandInfo commandInfo = getCommandInfo();
        name = commandInfo.name();
        description = commandInfo.description();
        alias = commandInfo.alias();
        syntax = commandInfo.syntax();
    }

    public CommandInfo getCommandInfo() {
        if (this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return this.getClass().getAnnotation(CommandInfo.class);
        } else {
            System.err.println("CommandInfo was not set from class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }
    public abstract void onCommand(String arg, String[] args) throws Exception;

    public void message(String message, boolean prefix) {
        String f = prefix ?"\247bCrispy\2477 » " : "";
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(f + "\2477" + message));

    }

    public void onEvent(Event event) { }
}
