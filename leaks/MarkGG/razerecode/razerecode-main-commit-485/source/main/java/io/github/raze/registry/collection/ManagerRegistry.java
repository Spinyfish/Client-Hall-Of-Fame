package io.github.raze.registry.collection;

import io.github.raze.registry.system.commands.CommandRegistry;
import io.github.raze.registry.system.events.EventRegistry;
import io.github.raze.registry.system.settings.SettingRegistry;
import io.github.raze.registry.system.modules.ModuleRegistry;
import io.github.raze.registry.system.themes.ThemeRegistry;

public class ManagerRegistry {

    public EventRegistry EVENT_REGISTRY = new EventRegistry();
    public SettingRegistry SETTING_REGISTRY = new SettingRegistry();
    public ModuleRegistry MODULE_REGISTRY = new ModuleRegistry();
    public CommandRegistry COMMAND_REGISTRY = new CommandRegistry();
    public ThemeRegistry THEME_REGISTRY = new ThemeRegistry();

}
