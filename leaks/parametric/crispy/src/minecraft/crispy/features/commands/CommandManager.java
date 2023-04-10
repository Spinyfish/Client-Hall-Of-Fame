package crispy.features.commands;

import crispy.Crispy;
import crispy.features.commands.impl.*;

import crispy.features.hacks.Hack;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;
import net.superblaubeere27.valuesystem.Value;

import java.util.ArrayList;

@Getter
public class CommandManager {

    private final ArrayList<Command> commands;

    public CommandManager() {
        commands = new ArrayList<>();
        addCommand(new BindCommand());
        addCommand(new VclipCommand());
        addCommand(new ClickPatternCommand());
        addCommand(new TToggleCommand());
        addCommand(new ConfigCommand());
        addCommand(new ScriptCommand());
        addCommand(new FriendCommand());
        addCommand(new CapeCommand());
        addCommand(new HelpCommand());
        addCommand(new PluginCommand());
        addCommand(new SayCommand());
        addCommand(new TeleportCommand());
        addCommand(new ListCommand());
        addCommand(new JoinCommand());
        addCommand(new InfoCommand());
    }

    public void addCommand(Command c){
        commands.add(c);
        System.out.println("Registered command " + c.getClass());
    }

    public Command getCommandsByName(String name) {
        return commands.stream().filter(module -> module.getAlias().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    public <T extends Command> T getCommand(Class<T> clazz) {
        return (T) commands.stream().filter(command -> command.getClass() == clazz).findFirst().orElse(null);
    }

    public void callCommand(String input){
        String[] split = input.split(" ");
        String command = split[0];
        String args = input.substring(command.length()).trim();
        changeValue(args, input.split(" "));
        for(Command c: getCommands()){
            if(c.getAlias().equalsIgnoreCase(command)){
                try{
                    c.onCommand(args, args.split(" "));
                }catch(Exception ignored){

                }
                return;
            }
        }


    }

    public void changeValue(String arg, String[] args) {
        Hack selectedHack = null;
        for(Hack hack : Crispy.INSTANCE.getHackManager().getHacks()) {
            if(args[0].equalsIgnoreCase(hack.getName())) {
                selectedHack = hack;
            }
        }
        Value selectedValue = null;
        if(selectedHack != null) {
            if(args.length > 1) {
                for (Value value : Crispy.INSTANCE.getValueManager().getAllValuesFrom(selectedHack.getName())) {
                    if (value.getName().equalsIgnoreCase(args[1])) {
                        selectedValue = value;
                    }
                }
            } else {
                Crispy.addChatMessage("Value not found");
                for(Value value : Crispy.INSTANCE.getValueManager().getAllValuesFrom(selectedHack.getName())) {
                    Crispy.addChatMessage(value.getName());
                }
            }
            if(selectedValue != null) {
                if(args.length > 2) {
                    if(selectedValue instanceof BooleanValue) {
                        boolean value = Boolean.parseBoolean(args[2]);
                        selectedValue.setObject(value);
                    } else if(selectedValue instanceof ModeValue) {
                        ModeValue modeValue = (ModeValue) selectedValue;
                        boolean valid = false;
                        for(String modeValues : modeValue.getModes()) {
                            if(modeValues.equalsIgnoreCase(args[2])) {
                                valid = true;
                            }
                        }
                        if(valid) {
                            Crispy.addChatMessage("Changed mode to " + args[2]);
                            modeValue.setObject(args[2]);
                        } else {
                            for(String modes : modeValue.getModes()) {
                                Crispy.addChatMessage(modes);
                            }
                        }
                    } else if(selectedValue instanceof NumberValue) {
                        NumberValue numberValue = (NumberValue) selectedValue;
                        double number = Double.parseDouble(args[2]);
                        numberValue.setObject(number);
                    }
                } else {
                    Crispy.addChatMessage("Needs a value");
                }
            } else {
                Crispy.addChatMessage("Value not found");
                for(Value value : Crispy.INSTANCE.getValueManager().getAllValuesFrom(selectedHack.getName())) {
                    Crispy.addChatMessage(value.getName());
                }
            }
        }
    }
    public void message(String message, boolean prefix) {
        String f = prefix ?"\247bCrispy\2477 » " : "";
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(f + "\2477" + message));

    }

}
