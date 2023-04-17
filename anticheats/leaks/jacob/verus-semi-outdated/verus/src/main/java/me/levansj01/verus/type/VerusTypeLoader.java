package me.levansj01.verus.type;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import me.levansj01.launcher.VerusLauncher;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.util.java.SafeReflection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

public class VerusTypeLoader {
    private static final VerusType verusType = VerusType.values()[VerusPlugin.getType()];
    private Class<?>[] classes = null;
    private Class<? extends Check>[] checkClasses = null;
    private static Class<?>[] _classLoaded = null;

    public List<BaseCommand> getBaseCommands() {
        List<BaseCommand> baseCommands = new ArrayList<>();
        Class<?>[] classInfos = this.getClassInfos();
        for (Class<?> clazz : classInfos) {
            if (BaseCommand.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                try {
                    baseCommands.add(clazz.asSubclass(BaseCommand.class).newInstance());
                } catch (Exception exception) {
                    if (isDev()) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        return baseCommands;
    }

    private synchronized Class<? extends Check>[] getCheckClasses() {
        if (this.checkClasses == null) {
            Class<?>[] classInfos = this.getClassInfos();
            List<Class<? extends Check>> classList = new ArrayList<>(classInfos.length);
            for (Class<?> clazz : classInfos) {
                try {
                    if (Check.class.isAssignableFrom(clazz) && ! Modifier.isAbstract(clazz.getModifiers())) {
                        Class<? extends Check> checkClass = clazz.asSubclass(Check.class);
                        Check check = checkClass.newInstance();
                        if (check.valid() && !check.getUnsupportedServers().contains(NMSManager.getInstance().getServerVersion())) {
                            classList.add(checkClass);
                        }
                    }
                } catch (Exception exception) {
                    if (isDev()) {
                        VerusLauncher.getPlugin().getLogger().log(Level.WARNING, "Failed to load " + clazz.getName() + ": ", exception);
                    }
                }
            }
            this.checkClasses = classList.toArray(new Class[0]);
        }
        return this.checkClasses;
    }

    public List<Command> getCommands() {
        List<Command> commands = new ArrayList<>();
        Class<?>[] classInfos = this.getClassInfos();
        for (Class<?> clazz : classInfos) {
            if (Command.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                try {
                    commands.add(clazz.asSubclass(Command.class).newInstance());
                } catch (Exception exception) {
                    if (isDev()) {
                        VerusLauncher.getPlugin().getLogger().log(Level.WARNING, "Failed to load " + clazz.getName() + ": ", exception);
                    }
                }
            }
        }
        return commands;
    }

    public void setCheckClasses(Class<? extends Check>[] var1) {
        this.checkClasses = var1;
    }

    public static boolean isCustom() {
        return verusType.afterOrEq(VerusType.CUSTOM);
    }

    public static void loader() {
        try {
            Class.forName("me.levansj01.verus.type.VerusPremiumLoader").asSubclass(Loader.class).newInstance().load();
        } catch (Exception exception) {
            if (!(exception instanceof ClassNotFoundException)) {
                exception.printStackTrace();
            }
        }
    }

    public Check[] loadChecks() {
        Class<?>[] checkClasses = this.getCheckClasses();
        List<Check> list = new ArrayList<>(checkClasses.length);
        for (Class<? extends Check> checkClass : this.getCheckClasses()) {
            try {
                list.add(checkClass.newInstance());
            } catch (Exception exception) {
                if (isDev()) {
                    exception.printStackTrace();
                }
            }
        }
        return list.toArray(new Check[0]);
    }

    public static boolean isDebug() {
        if (verusType == VerusType.ENTERPRISE && Loader.getUsername().equals("PGxPO")) {
            int port = Bukkit.getPort();
            return port == 27979;
        }
        return false;
    }

    private synchronized Class<?>[] getClassInfos() {
        if (this.classes == null) {
            if (_classLoaded == null) {
                try {
                    ClassLoader classLoader = this.getClass().getClassLoader();
                    Vector<?> vector = (Vector<?>) SafeReflection.getLocalField(ClassLoader.class, classLoader, "classes");
                    this.classes = (Class<?>[])vector.toArray(new Object[0]);
                } catch (Exception exception) {
                    throw new RuntimeException("Backup classloader method has failed, JVM unsupported.", exception);
                }
            } else {
                this.classes = _classLoaded;
            }
            _classLoaded = null;
        }
        return this.classes;
    }

    public void setClasses(Class<?>[] var1) {
        this.classes = var1;
    }

    public static boolean isEnterprise() {
        return verusType.afterOrEq(VerusType.ENTERPRISE);
    }

    public static boolean isDev() {
        return verusType == VerusType.DEV;
    }

    public static VerusType getVerusType() {
        return verusType;
    }
}
