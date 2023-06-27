package net.minecraft.profiler;

import com.google.common.collect.*;
import java.net.*;
import net.minecraft.util.*;
import java.lang.management.*;
import java.util.*;

public class PlayerUsageSnooper
{
    private final Map<String, Object> field_152773_a;
    private final Map<String, Object> field_152774_b;
    private final String uniqueID;
    private final URL serverUrl;
    private final IPlayerUsage playerStatsCollector;
    private final Timer threadTrigger;
    private final Object syncLock;
    private final long minecraftStartTimeMilis;
    private boolean isRunning;
    private int selfCounter;
    
    public PlayerUsageSnooper(final String p_i1563_1_, final IPlayerUsage playerStatCollector, final long startTime) {
        this.field_152773_a = (Map<String, Object>)Maps.newHashMap();
        this.field_152774_b = (Map<String, Object>)Maps.newHashMap();
        this.uniqueID = UUID.randomUUID().toString();
        this.threadTrigger = new Timer("Snooper Timer", true);
        this.syncLock = new Object();
        try {
            this.serverUrl = new URL("http://snoop.minecraft.net/" + p_i1563_1_ + "?version=" + 2);
        }
        catch (MalformedURLException var6) {
            throw new IllegalArgumentException();
        }
        this.playerStatsCollector = playerStatCollector;
        this.minecraftStartTimeMilis = startTime;
    }
    
    public void startSnooper() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.func_152766_h();
            this.threadTrigger.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (PlayerUsageSnooper.this.playerStatsCollector.isSnooperEnabled()) {
                        final Map<String, Object> map;
                        synchronized (PlayerUsageSnooper.this.syncLock) {
                            map = (Map<String, Object>)Maps.newHashMap(PlayerUsageSnooper.this.field_152774_b);
                            if (PlayerUsageSnooper.this.selfCounter == 0) {
                                map.putAll(PlayerUsageSnooper.this.field_152773_a);
                            }
                            map.put("snooper_count", PlayerUsageSnooper.this.selfCounter++);
                            map.put("snooper_token", PlayerUsageSnooper.this.uniqueID);
                        }
                        HttpUtil.postMap(PlayerUsageSnooper.this.serverUrl, map, true);
                    }
                }
            }, 0L, 900000L);
        }
    }
    
    private void func_152766_h() {
        this.addJvmArgsToSnooper();
        this.addClientStat("snooper_token", this.uniqueID);
        this.addStatToSnooper("snooper_token", this.uniqueID);
        this.addStatToSnooper("os_name", System.getProperty("os.name"));
        this.addStatToSnooper("os_version", System.getProperty("os.version"));
        this.addStatToSnooper("os_architecture", System.getProperty("os.arch"));
        this.addStatToSnooper("java_version", System.getProperty("java.version"));
        this.addClientStat("version", "1.8.8");
        this.playerStatsCollector.addServerTypeToSnooper(this);
    }
    
    private void addJvmArgsToSnooper() {
        final RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
        final List<String> list = runtimemxbean.getInputArguments();
        int i = 0;
        for (final String s : list) {
            if (s.startsWith("-X")) {
                this.addClientStat("jvm_arg[" + i++ + "]", s);
            }
        }
        this.addClientStat("jvm_args", i);
    }
    
    public void addMemoryStatsToSnooper() {
        this.addStatToSnooper("memory_total", Runtime.getRuntime().totalMemory());
        this.addStatToSnooper("memory_max", Runtime.getRuntime().maxMemory());
        this.addStatToSnooper("memory_free", Runtime.getRuntime().freeMemory());
        this.addStatToSnooper("cpu_cores", Runtime.getRuntime().availableProcessors());
        this.playerStatsCollector.addServerStatsToSnooper(this);
    }
    
    public void addClientStat(final String p_152768_1_, final Object p_152768_2_) {
        synchronized (this.syncLock) {
            this.field_152774_b.put(p_152768_1_, p_152768_2_);
        }
    }
    
    public void addStatToSnooper(final String p_152767_1_, final Object p_152767_2_) {
        synchronized (this.syncLock) {
            this.field_152773_a.put(p_152767_1_, p_152767_2_);
        }
    }
    
    public Map<String, String> getCurrentStats() {
        final Map<String, String> map = (Map<String, String>)Maps.newLinkedHashMap();
        synchronized (this.syncLock) {
            this.addMemoryStatsToSnooper();
            for (final Map.Entry<String, Object> entry : this.field_152773_a.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }
            for (final Map.Entry<String, Object> entry2 : this.field_152774_b.entrySet()) {
                map.put(entry2.getKey(), entry2.getValue().toString());
            }
            return map;
        }
    }
    
    public boolean isSnooperRunning() {
        return this.isRunning;
    }
    
    public void stopSnooper() {
        this.threadTrigger.cancel();
    }
    
    public String getUniqueID() {
        return this.uniqueID;
    }
    
    public long getMinecraftStartTimeMillis() {
        return this.minecraftStartTimeMilis;
    }
}
