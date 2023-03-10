/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonParser
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsFile
extends StatFileWriter {
    private static final Logger logger = LogManager.getLogger();
    private final MinecraftServer mcServer;
    private final File statsFile;
    private final Set<StatBase> field_150888_e = Sets.newHashSet();
    private int field_150885_f = -300;
    private boolean field_150886_g = false;

    public StatisticsFile(MinecraftServer serverIn, File statsFileIn) {
        this.mcServer = serverIn;
        this.statsFile = statsFileIn;
    }

    public void readStatFile() {
        if (this.statsFile.isFile()) {
            try {
                this.statsData.clear();
                this.statsData.putAll(this.parseJson(FileUtils.readFileToString((File)this.statsFile)));
            }
            catch (IOException ioexception) {
                logger.error("Couldn't read statistics file " + this.statsFile, (Throwable)ioexception);
            }
            catch (JsonParseException jsonparseexception) {
                logger.error("Couldn't parse statistics file " + this.statsFile, (Throwable)jsonparseexception);
            }
        }
    }

    public void saveStatFile() {
        try {
            FileUtils.writeStringToFile((File)this.statsFile, (String)StatisticsFile.dumpJson(this.statsData));
        }
        catch (IOException ioexception) {
            logger.error("Couldn't save stats", (Throwable)ioexception);
        }
    }

    @Override
    public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int p_150873_3_) {
        int i = statIn.isAchievement() ? this.readStat(statIn) : 0;
        super.unlockAchievement(playerIn, statIn, p_150873_3_);
        this.field_150888_e.add(statIn);
        if (statIn.isAchievement() && i == 0 && p_150873_3_ > 0) {
            this.field_150886_g = true;
            if (this.mcServer.isAnnouncingPlayerAchievements()) {
                this.mcServer.getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.achievement", playerIn.getDisplayName(), statIn.func_150955_j()));
            }
        }
        if (statIn.isAchievement() && i > 0 && p_150873_3_ == 0) {
            this.field_150886_g = true;
            if (this.mcServer.isAnnouncingPlayerAchievements()) {
                this.mcServer.getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.achievement.taken", playerIn.getDisplayName(), statIn.func_150955_j()));
            }
        }
    }

    public Set<StatBase> func_150878_c() {
        HashSet set = Sets.newHashSet(this.field_150888_e);
        this.field_150888_e.clear();
        this.field_150886_g = false;
        return set;
    }

    public Map<StatBase, TupleIntJsonSerializable> parseJson(String p_150881_1_) {
        JsonElement jsonelement = new JsonParser().parse(p_150881_1_);
        if (!jsonelement.isJsonObject()) {
            return Maps.newHashMap();
        }
        JsonObject jsonobject = jsonelement.getAsJsonObject();
        HashMap map = Maps.newHashMap();
        for (Map.Entry entry : jsonobject.entrySet()) {
            StatBase statbase = StatList.getOneShotStat((String)entry.getKey());
            if (statbase != null) {
                TupleIntJsonSerializable tupleintjsonserializable = new TupleIntJsonSerializable();
                if (((JsonElement)entry.getValue()).isJsonPrimitive() && ((JsonElement)entry.getValue()).getAsJsonPrimitive().isNumber()) {
                    tupleintjsonserializable.setIntegerValue(((JsonElement)entry.getValue()).getAsInt());
                } else if (((JsonElement)entry.getValue()).isJsonObject()) {
                    JsonObject jsonobject1 = ((JsonElement)entry.getValue()).getAsJsonObject();
                    if (jsonobject1.has("value") && jsonobject1.get("value").isJsonPrimitive() && jsonobject1.get("value").getAsJsonPrimitive().isNumber()) {
                        tupleintjsonserializable.setIntegerValue(jsonobject1.getAsJsonPrimitive("value").getAsInt());
                    }
                    if (jsonobject1.has("progress") && statbase.func_150954_l() != null) {
                        try {
                            Constructor<? extends IJsonSerializable> constructor = statbase.func_150954_l().getConstructor(new Class[0]);
                            IJsonSerializable ijsonserializable = constructor.newInstance(new Object[0]);
                            ijsonserializable.fromJson(jsonobject1.get("progress"));
                            tupleintjsonserializable.setJsonSerializableValue(ijsonserializable);
                        }
                        catch (Throwable throwable) {
                            logger.warn("Invalid statistic progress in " + this.statsFile, throwable);
                        }
                    }
                }
                map.put(statbase, tupleintjsonserializable);
                continue;
            }
            logger.warn("Invalid statistic in " + this.statsFile + ": Don't know what " + (String)entry.getKey() + " is");
        }
        return map;
    }

    public static String dumpJson(Map<StatBase, TupleIntJsonSerializable> p_150880_0_) {
        JsonObject jsonobject = new JsonObject();
        for (Map.Entry<StatBase, TupleIntJsonSerializable> entry : p_150880_0_.entrySet()) {
            if (entry.getValue().getJsonSerializableValue() != null) {
                JsonObject jsonobject1 = new JsonObject();
                jsonobject1.addProperty("value", (Number)entry.getValue().getIntegerValue());
                try {
                    jsonobject1.add("progress", entry.getValue().getJsonSerializableValue().getSerializableElement());
                }
                catch (Throwable throwable) {
                    logger.warn("Couldn't save statistic " + entry.getKey().getStatName() + ": error serializing progress", throwable);
                }
                jsonobject.add(entry.getKey().statId, (JsonElement)jsonobject1);
                continue;
            }
            jsonobject.addProperty(entry.getKey().statId, (Number)entry.getValue().getIntegerValue());
        }
        return jsonobject.toString();
    }

    public void func_150877_d() {
        for (StatBase statbase : this.statsData.keySet()) {
            this.field_150888_e.add(statbase);
        }
    }

    public void func_150876_a(EntityPlayerMP p_150876_1_) {
        int i = this.mcServer.getTickCounter();
        HashMap map = Maps.newHashMap();
        if (this.field_150886_g || i - this.field_150885_f > 300) {
            this.field_150885_f = i;
            for (StatBase statbase : this.func_150878_c()) {
                map.put(statbase, this.readStat(statbase));
            }
        }
        p_150876_1_.playerNetServerHandler.sendPacket(new S37PacketStatistics(map));
    }

    public void sendAchievements(EntityPlayerMP player) {
        HashMap map = Maps.newHashMap();
        for (Achievement achievement : AchievementList.achievementList) {
            if (!this.hasAchievementUnlocked(achievement)) continue;
            map.put(achievement, this.readStat(achievement));
            this.field_150888_e.remove(achievement);
        }
        player.playerNetServerHandler.sendPacket(new S37PacketStatistics(map));
    }

    public boolean func_150879_e() {
        return this.field_150886_g;
    }
}

