package me.levansj01.verus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipFile;

import lombok.Getter;
import lombok.Setter;
import me.levansj01.launcher.VerusLaunch;
import me.levansj01.launcher.VerusLauncher;
import me.levansj01.verus.api.API;
import me.levansj01.verus.check.manager.CheckManager;
import me.levansj01.verus.command.BaseCommandHelp;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.data.manager.DataManager;
import me.levansj01.verus.discord.DiscordManager;
import me.levansj01.verus.gui.manager.GUIManager;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.listener.DataListener;
import me.levansj01.verus.messaging.MessagingHandler;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.config.VerusConfiguration;
import me.levansj01.verus.task.ReportTask;
import me.levansj01.verus.task.ServerTickTask;
import me.levansj01.verus.type.Loader;
import me.levansj01.verus.type.VerusTypeLoader;
import me.levansj01.verus.util.java.SafeReflection;
import me.levansj01.verus.util.java.WordUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.help.HelpMap;
import org.bukkit.permissions.ServerOperator;

@Getter
@Setter
@SuppressWarnings("ALL")
public class VerusPlugin implements VerusLaunch {
    private VerusTypeLoader typeLoader;
    private HelpMap helpMap;
    private VerusLauncher plugin;
    public static ChatColor COLOR = ChatColor.BLUE;
    private DataListener dataListener;
    private ReportTask reportTask;
    private List<Command> commands;
    private final Server server = Bukkit.getServer();
    private SimpleCommandMap commandMap;
    private boolean serverShutdown;
    @Getter private static String name = "verus";

     public void launch(VerusLauncher launcher) throws UnknownHostException {

        long start = System.currentTimeMillis();
        this.plugin = launcher;
        this.typeLoader = new VerusTypeLoader();
        StorageEngine storageEngine = StorageEngine.getInstance();
        storageEngine.startConfig();
        VerusTypeLoader.loader();

        try {
            NMSManager.getInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
            Bukkit.shutdown();
            return;
        }

        storageEngine.start();
        CheckManager.getInstance().enable(this);
        DataManager.enable(this);
        VerusConfiguration configuration = storageEngine.getVerusConfig();

        if (configuration.isBungeeBans()) {
            MessagingHandler.getInstance().enable(this);
        }

        if (configuration.isDiscordBans() || configuration.isDiscordLogs()) {
            DiscordManager.getInstance().start();
        }

        this.registerListeners();
        this.registerCommands();
        ServerTickTask.getInstance().schedule();

        if (storageEngine.getVerusConfig().isSendStats()) {
            this.reportTask = new ReportTask();
            this.reportTask.start();
        }

        API api = API.getAPI();
        if (api != null) {
            api.enable(this);
        }

        long end = System.currentTimeMillis() - start;

        String consoleMessage = String.format("%s b%s launched successfully in %sms", getNameFormatted(), getBuild(), end);
        Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(onlinePlayer -> {
            onlinePlayer.sendMessage(ChatColor.GREEN + consoleMessage);
        });

        this.plugin.getLogger().info(consoleMessage + "\nBy running this plugin, you agree to be bound by our TOS");
    }
    
    public void shutdown() throws InterruptedException {
        ServerTickTask.getInstance().cancel();
        DataManager.disable();
        this.unregisterListeners();
        DiscordManager.getInstance().stop();
        StorageEngine.getInstance().stopConfig();
        CheckManager.getInstance().disable();
        GUIManager.getInstance().disable();
        StorageEngine.getInstance().stop();
        VerusConfiguration config = StorageEngine.getInstance().getVerusConfig();

        if (config != null && config.isBungeeBans()) {
            MessagingHandler.getInstance().disable();
        }

        API.check();
        API api = API.getAPI();
        if (api != null) {
            api.disable();
        }

        if (this.reportTask != null) {
            this.reportTask.end();
        }

        if (ServerTickTask.getInstance().getBukkitTask() != null) {
            ServerTickTask.getInstance().getBukkitTask().cancel();
        }

        this.unregisterCommands();
        this.plugin.getLogger().info(getNameFormatted() + " shutdown successfully");
        this.typeLoader.setCheckClasses(null);
        this.killClassLoader();
    }
    
    public static void restart() {
        VerusLauncher launcher = VerusLauncher.getPlugin();
        Bukkit.getPluginManager().disablePlugin(launcher);
        Bukkit.getPluginManager().enablePlugin(launcher);
    }

    private void kill(Field[] fields, ClassLoader classLoader) {
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                ClassLoader classLoader1 = null;
                if (!Modifier.isStatic(field.getModifiers())) {
                    classLoader1 = classLoader;
                }

                field.set(classLoader1, null);
                field.setAccessible(false);
            } catch (Exception exception) {
                break;
            }
        }

    }

    private void killClassLoader() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            ClassLoader parentClassLoader = classLoader.getParent();
            Class<?> parentClazz = parentClassLoader.getClass();
            boolean running;
            try {
                running = NMSManager.getInstance().isRunning();
            } catch (Exception exception) {
                this.getServer().getLogger().log(Level.SEVERE, "Please report this issue! " + exception.getMessage());
                return;
            }

            if (running) {
                (new Thread(() -> {
                    try {
                        Thread.sleep(25L);
                    } catch (Exception exception) {
                        return;
                    }
                    try {
                        Field jarField = parentClazz.getDeclaredField("jar");
                        jarField.setAccessible(true);
                        JarFile jarFile = (JarFile) jarField.get(parentClassLoader);
                        Field field = ZipFile.class.getDeclaredField("closeRequested");
                        field.setAccessible(false);
                        if (jarFile != null) {
                            boolean b = field.getBoolean(jarFile);

                            field.setAccessible(true);
                            if (b) {
                                try {
                                    jarFile = new JarFile(jarFile.getName());
                                    jarField.set(parentClassLoader, jarFile);
                                    System.out.println("[Verus] Injected new jar file, plugin should now restart correctly");
                                } catch (Exception exception) {
                                    if (VerusTypeLoader.isDev()) {
                                        exception.printStackTrace();
                                    }
                                }
                            }
                        }

                        jarField.setAccessible(false);
                    } catch (Exception exception) {
                        if (VerusTypeLoader.isDev()) {
                            exception.printStackTrace();
                        }
                    }
                })).start();
            }

            try {
                Field classes = parentClazz.getDeclaredField("classes");
                classes.setAccessible(true);
                if (classes.get(parentClassLoader) instanceof Map) {
                    ((Map<String, ?>) classes.get(parentClassLoader)).keySet().removeIf((string) -> string.startsWith("me.levansj01.verus"));
                }
                classes.setAccessible(false);
            } catch (IllegalAccessException | NoSuchFieldException exception) {
                return;
            }
        } catch (Exception exception) {
            return;
        }

        Class<?> clazz = classLoader.getClass();
        this.kill(clazz.getDeclaredFields(), classLoader);
        this.kill(clazz.getFields(), classLoader);
    }
  
    private void unregisterCommands() {
        if (this.commandMap != null) {
            Map<String, Command> map = SafeReflection.getKnownCommands(this.commandMap);
            map.values().removeAll(this.commands);

            for (Command command : this.commands) {
                command.unregister(this.commandMap);
            }
        }

        if (this.helpMap != null) {
            this.helpMap.getHelpTopics().removeIf(helpTopic -> helpTopic instanceof BaseCommandHelp);
        }

    }

    private void registerCommands() {
        this.commands = this.typeLoader.getCommands();
        this.commands.addAll(this.typeLoader.getBaseCommands());
        if (!StorageEngine.getInstance().getVerusConfig().isPingCommand()) {
            this.commands.removeIf(command -> command.getName().equals(Loader.getPingCommand()));
        }
        this.commands.forEach(command -> command.setPermissionMessage(EnumMessage.COMMAND_PERMISSION.get()));
        this.commandMap = SafeReflection.getCommandMap();
        this.helpMap = this.server.getHelpMap();
        Map<String, Command> knownCommands = SafeReflection.getKnownCommands(this.commandMap);
        this.commandMap.registerAll(name, this.commands);
        this.commands.forEach(command -> {
            knownCommands.put(command.getName(), command);
            this.helpMap.addTopic(new BaseCommandHelp(command));
        });
        NMSManager.getInstance().syncCommands(this.commands);
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(this.dataListener = new DataListener(), this.plugin);
    }

    private void unregisterListeners() {
        if (this.dataListener != null) {
            HandlerList.unregisterAll(this.dataListener);
        }

        HandlerList.unregisterAll(VerusLauncher.getPlugin());
    }

    public static String getNameFormatted() {
        return WordUtils.capitalize(name);
    }
    
    public static int getType() {
        return 1;
    }

    public static long getBuild() {
        return 3809L;
    }
}
