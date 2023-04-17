package me.levansj01.verus.check;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.Setter;
import me.levansj01.verus.alert.manager.AlertManager;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.manager.CheckManager;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.config.VerusConfiguration;
import org.bukkit.entity.Player;

@Getter
@Setter
public class Check {

    private int lastViolation;
    protected PlayerData playerData;
    private CheckType type;
    private int maxViolation;
    private boolean logData;
    private String subType;
    private String friendlyName;
    private boolean blocks;
    protected double violations;
    private CheckVersion checkVersion;
    private int priority;
    private boolean heavy;
    private boolean schem;
    private List<ServerVersion> unsupportedServers;
    protected double minViolation;
    private boolean pullback;
    private List<ClientVersion> unsupportedVersions;
    protected Player player;

    public void decreaseVL(double var1) {
        this.violations -= Math.min(this.violations - this.minViolation, var1);
    }

    public boolean supported() {
        VerusConfiguration config = StorageEngine.getInstance().getVerusConfig();
        return !this.unsupportedVersions.contains(this.playerData.getVersion()) && (this.playerData.getVersion() != ClientVersion.VERSION_UNSUPPORTED || this.unsupportedVersions.isEmpty()) && (!this.heavy || config.isHeavyChecks()) && (!this.schem || config.isSchem()) && (!this.blocks || config.isBlockLog()) && (!config.isHardDisable() || !CheckManager.getInstance().isDisabled(this));
    }

    @Deprecated
    public Check(CheckType type, String subType, String friendlyName, CheckVersion checkVersion, ClientVersion... unsupportedVersions) {
        this.maxViolation = 2147483647;
        this.priority = 1;
        this.unsupportedServers = Collections.emptyList();
        this.type = type;
        this.subType = subType;
        this.friendlyName = friendlyName;
        this.checkVersion = checkVersion;
        this.unsupportedVersions = Arrays.asList(unsupportedVersions);
    }

    public void handleBan() {
        this.handleBan(false);
    }

    public Check() {
        this.maxViolation = 20;
        this.priority = 1;
        this.unsupportedServers = Collections.emptyList();
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            CheckInfo checkInfo = this.getClass().getAnnotation(CheckInfo.class);
            this.setType(checkInfo.type());
            this.setSubType(checkInfo.subType());
            this.setFriendlyName(checkInfo.friendlyName());
            if (this.friendlyName.isEmpty()) {
                this.setFriendlyName(this.type.getName());
            }

            this.setCheckVersion(checkInfo.version());
            ClientVersion clientVersion = checkInfo.unsupportedAtleast();
            ClientVersion[] clientVersions = checkInfo.unsupportedVersions();
            this.setUnsupportedVersions(Arrays.asList(maxOf(clientVersion, ClientVersion.NONE, clientVersions, ClientVersion.class)));
            ServerVersion serverVersion = checkInfo.unsupportedServerAtleast();
            ServerVersion[] serverVersions = checkInfo.unsupportedServers();
            this.setUnsupportedServers(maxOf(serverVersion, ServerVersion.NONE, serverVersions, ServerVersion.class));
            this.setMinViolation(checkInfo.minViolations());
            this.setPriority(checkInfo.priority());
            this.setHeavy(checkInfo.heavy());
            this.setSchem(checkInfo.schem());
            this.setBlocks(checkInfo.blocks());
            if (!checkInfo.schem() && !checkInfo.butterfly()) {
                this.setMaxViolation(checkInfo.maxViolations());
            } else {
                VerusConfiguration config = StorageEngine.getInstance().getVerusConfig();
                if (checkInfo.schem() && config.isSchemBans()) {
                    setMaxViolation(checkInfo.maxViolations());
                }

                if (checkInfo.butterfly() && config.isButterflyBans()) {
                    this.setMaxViolation(checkInfo.maxViolations());
                }
            }

            this.setLogData(checkInfo.logData());
        }

    }

    public void setUnsupportedServers(ServerVersion... serverVersions) {
        this.unsupportedServers = Arrays.asList(serverVersions);
    }

    public void handleViolation(String s) {
        this.handleViolation(s, 1.0D);
    }

    public void handleViolation(Supplier<String> var1) {
        this.handleViolation(var1, 1.0D);
    }

    public void handleViolation(String s, double d, boolean b) {
        this.handleViolation(() -> s, d, b);
    }

    protected boolean canEqual(Object o) {
        return o instanceof Check;
    }

    private static <T extends Enum<T>> T[] maxOf(T enum1, T enum2, T[] enums, Class<T> clazz) {
        if (enum1 == enum2) {
            return enums;
        } else {
            Enum[] enumConstants = clazz.getEnumConstants();
            Enum[] newInstance = (Enum[])Array.newInstance(clazz, enumConstants.length - enum1.ordinal() + enums.length);
            int ordinal = enum1.ordinal();
            System.arraycopy(enumConstants, ordinal, newInstance, 0, newInstance.length - enums.length);
            if (enums.length > 0) {
                System.arraycopy(enums, 0, newInstance, newInstance.length - enums.length, enums.length);
            }

            return (T[]) newInstance;
        }
    }

    public void handleViolation() {
        this.handleViolation("");
    }

    public void handleViolation(Supplier<String> stringSupplier, double d) {
        this.handleViolation(stringSupplier, d, false);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Check)) {
            return false;
        }
        Check check = (Check)object;
        if (!check.canEqual(this)) {
            return false;
        }
        CheckType checkType = this.getType();
        CheckType checkType2 = check.getType();
        if (!(checkType == null && checkType2 == null || checkType.equals((Object)checkType2))) {
            return false;
        }
        String string = this.getSubType();
        String string2 = check.getSubType();
        return string == null && string2 == null || string.equals((Object)string2);
    }

    @Deprecated
    public boolean valid() {
        return this.type != null && this.subType != null && this.friendlyName != null && this.checkVersion != null && this.unsupportedVersions != null;
    }

    public String identifier() {
        return this.type.ordinal() + "" + this.subType;
    }

    public void debug(String string) {
        this.debug(() -> string);
    }

    public void setCheckVersion(CheckVersion checkVersion) {
        this.checkVersion = checkVersion;
    }

    public void run(Runnable runnable) {
        NMSManager.getInstance().postToMainThread(() -> {
            if (this.playerData.isEnabled()) {
                runnable.run();
            }

        });
    }

    public void debug(Supplier<String> supplier) {
        this.playerData.debug(supplier);
    }

    public int hashCode() {
        int n;
        int n2;
        int n4 = 1;
        CheckType checkType = this.getType();
        if (checkType == null) {
            n2 = 43;
        } else {
            n2 = checkType.hashCode();
        }
        n4 = n4 * 59 + n2;
        String string = this.getSubType();
        if (string == null) {
            n = 43;
            return n4 * 59 + n;
        }
        n = string.hashCode();
        return n4 * 59 + n;
    }

    public String name() {
        return this.type.getName() + " " + this.subType;
    }

    public void setViolations(double var1) {
        this.violations = var1;
    }

    public void handleViolation(String s, double d) {
        this.handleViolation(s, d,false);
    }

    public void handleBan(boolean b) {
        AlertManager.getInstance().handleBan(this.playerData, this, b);
    }

    public void handleViolation(Supplier<String> supplier, double var2, boolean var4) {
        AlertManager.getInstance().handleViolation(this.playerData, this, supplier, var2, var4);
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public boolean isHeavy() {
        return this.heavy;
    }
}
