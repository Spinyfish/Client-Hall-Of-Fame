package net.minecraft.stats;

import net.minecraft.server.*;
import org.apache.commons.io.*;
import java.io.*;
import com.google.common.collect.*;
import net.minecraft.util.*;
import com.google.gson.*;
import java.util.*;
import java.lang.reflect.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.*;
import org.apache.logging.log4j.*;

public class StatisticsFile extends StatFileWriter
{
    private static final Logger logger;
    private final MinecraftServer mcServer;
    private final File statsFile;
    private final Set<StatBase> field_150888_e;
    private int field_150885_f;
    private boolean field_150886_g;
    
    public StatisticsFile(final MinecraftServer serverIn, final File statsFileIn) {
        this.field_150888_e = (Set<StatBase>)Sets.newHashSet();
        this.field_150885_f = -300;
        this.field_150886_g = false;
        this.mcServer = serverIn;
        this.statsFile = statsFileIn;
    }
    
    public void readStatFile() {
        if (this.statsFile.isFile()) {
            try {
                this.statsData.clear();
                this.statsData.putAll(this.parseJson(FileUtils.readFileToString(this.statsFile)));
            }
            catch (IOException ioexception) {
                StatisticsFile.logger.error("Couldn't read statistics file " + this.statsFile, (Throwable)ioexception);
            }
            catch (JsonParseException jsonparseexception) {
                StatisticsFile.logger.error("Couldn't parse statistics file " + this.statsFile, (Throwable)jsonparseexception);
            }
        }
    }
    
    public void saveStatFile() {
        try {
            FileUtils.writeStringToFile(this.statsFile, dumpJson(this.statsData));
        }
        catch (IOException ioexception) {
            StatisticsFile.logger.error("Couldn't save stats", (Throwable)ioexception);
        }
    }
    
    @Override
    public void unlockAchievement(final EntityPlayer playerIn, final StatBase statIn, final int p_150873_3_) {
        final int i = statIn.isAchievement() ? this.readStat(statIn) : 0;
        super.unlockAchievement(playerIn, statIn, p_150873_3_);
        this.field_150888_e.add(statIn);
        if (statIn.isAchievement() && i == 0 && p_150873_3_ > 0) {
            this.field_150886_g = true;
            if (this.mcServer.isAnnouncingPlayerAchievements()) {
                this.mcServer.getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.achievement", new Object[] { playerIn.getDisplayName(), statIn.func_150955_j() }));
            }
        }
        if (statIn.isAchievement() && i > 0 && p_150873_3_ == 0) {
            this.field_150886_g = true;
            if (this.mcServer.isAnnouncingPlayerAchievements()) {
                this.mcServer.getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.achievement.taken", new Object[] { playerIn.getDisplayName(), statIn.func_150955_j() }));
            }
        }
    }
    
    public Set<StatBase> func_150878_c() {
        final Set<StatBase> set = (Set<StatBase>)Sets.newHashSet((Iterable)this.field_150888_e);
        this.field_150888_e.clear();
        this.field_150886_g = false;
        return set;
    }
    
    public Map<StatBase, TupleIntJsonSerializable> parseJson(final String p_150881_1_) {
        final JsonElement jsonelement = new JsonParser().parse(p_150881_1_);
        if (!jsonelement.isJsonObject()) {
            return (Map<StatBase, TupleIntJsonSerializable>)Maps.newHashMap();
        }
        final JsonObject jsonobject = jsonelement.getAsJsonObject();
        final Map<StatBase, TupleIntJsonSerializable> map = (Map<StatBase, TupleIntJsonSerializable>)Maps.newHashMap();
        for (final Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            final StatBase statbase = StatList.getOneShotStat(entry.getKey());
            if (statbase != null) {
                final TupleIntJsonSerializable tupleintjsonserializable = new TupleIntJsonSerializable();
                if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isNumber()) {
                    tupleintjsonserializable.setIntegerValue(entry.getValue().getAsInt());
                }
                else if (entry.getValue().isJsonObject()) {
                    final JsonObject jsonobject2 = entry.getValue().getAsJsonObject();
                    if (jsonobject2.has("value") && jsonobject2.get("value").isJsonPrimitive() && jsonobject2.get("value").getAsJsonPrimitive().isNumber()) {
                        tupleintjsonserializable.setIntegerValue(jsonobject2.getAsJsonPrimitive("value").getAsInt());
                    }
                    if (jsonobject2.has("progress") && statbase.func_150954_l() != null) {
                        try {
                            final Constructor<? extends IJsonSerializable> constructor = statbase.func_150954_l().getConstructor((Class<?>[])new Class[0]);
                            final IJsonSerializable ijsonserializable = (IJsonSerializable)constructor.newInstance(new Object[0]);
                            ijsonserializable.fromJson(jsonobject2.get("progress"));
                            tupleintjsonserializable.setJsonSerializableValue(ijsonserializable);
                        }
                        catch (Throwable throwable) {
                            StatisticsFile.logger.warn("Invalid statistic progress in " + this.statsFile, throwable);
                        }
                    }
                }
                map.put(statbase, tupleintjsonserializable);
            }
            else {
                StatisticsFile.logger.warn("Invalid statistic in " + this.statsFile + ": Don't know what " + entry.getKey() + " is");
            }
        }
        return map;
    }
    
    public static String dumpJson(final Map<StatBase, TupleIntJsonSerializable> p_150880_0_) {
        final JsonObject jsonobject = new JsonObject();
        for (final Map.Entry<StatBase, TupleIntJsonSerializable> entry : p_150880_0_.entrySet()) {
            if (entry.getValue().getJsonSerializableValue() != null) {
                final JsonObject jsonobject2 = new JsonObject();
                jsonobject2.addProperty("value", (Number)entry.getValue().getIntegerValue());
                try {
                    jsonobject2.add("progress", entry.getValue().getJsonSerializableValue().getSerializableElement());
                }
                catch (Throwable throwable) {
                    StatisticsFile.logger.warn("Couldn't save statistic " + entry.getKey().getStatName() + ": error serializing progress", throwable);
                }
                jsonobject.add(entry.getKey().statId, (JsonElement)jsonobject2);
            }
            else {
                jsonobject.addProperty(entry.getKey().statId, (Number)entry.getValue().getIntegerValue());
            }
        }
        return jsonobject.toString();
    }
    
    public void func_150877_d() {
        for (final StatBase statbase : this.statsData.keySet()) {
            this.field_150888_e.add(statbase);
        }
    }
    
    public void func_150876_a(final EntityPlayerMP p_150876_1_) {
        final int i = this.mcServer.getTickCounter();
        final Map<StatBase, Integer> map = (Map<StatBase, Integer>)Maps.newHashMap();
        if (this.field_150886_g || i - this.field_150885_f > 300) {
            this.field_150885_f = i;
            for (final StatBase statbase : this.func_150878_c()) {
                map.put(statbase, this.readStat(statbase));
            }
        }
        p_150876_1_.playerNetServerHandler.sendPacket(new S37PacketStatistics(map));
    }
    
    public void sendAchievements(final EntityPlayerMP player) {
        final Map<StatBase, Integer> map = (Map<StatBase, Integer>)Maps.newHashMap();
        for (final Achievement achievement : AchievementList.achievementList) {
            if (this.hasAchievementUnlocked(achievement)) {
                map.put(achievement, this.readStat(achievement));
                this.field_150888_e.remove(achievement);
            }
        }
        player.playerNetServerHandler.sendPacket(new S37PacketStatistics(map));
    }
    
    public boolean func_150879_e() {
        return this.field_150886_g;
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
