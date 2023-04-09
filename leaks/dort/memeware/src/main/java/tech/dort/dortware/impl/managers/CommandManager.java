package tech.dort.dortware.impl.managers;

import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.config.ConfigCommand;
import tech.dort.dortware.api.manager.Manager;
import tech.dort.dortware.impl.commands.*;

import java.util.ArrayList;

public class CommandManager extends Manager<Command> {

    public CommandManager() {
        super(new ArrayList<>());
    }

    @Override
    public void onCreated() {
        this.add(new HelpCommand());
        this.add(new ClipCommand());
        this.add(new SearchEngineCommand());
        this.add(new NameProtectCommand());
        this.add(new ConfigCommand());
        this.add(new NameCommand());
        this.add(new SayCommand());
        this.add(new ToggleCommand());
        this.add(new BindCommand());
        this.add(new FriendCommand());
        this.add(new AutoGGCommand());
        this.add(new DiscordCommand());
        this.add(new SpamCommand());
        this.add(new GayRateCommand());
        this.add(new TeleportCommand());
        this.add(new SetSpectatorAltCommand());
//        Client.INSTANCE.getModuleManager().getObjects().forEach(module -> {
//            if (!Client.INSTANCE.getValueManager().hasValues(module))
//                return;
//            this.add(new ModuleCommandBase(new CommandData(module.getModuleData().getName().toLowerCase().replace(" ", "")), module));
//        });
    }
}
