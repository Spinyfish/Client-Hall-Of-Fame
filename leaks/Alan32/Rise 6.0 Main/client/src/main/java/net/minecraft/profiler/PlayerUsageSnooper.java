package net.minecraft.profiler;

import com.google.common.collect.Maps;
import net.minecraft.util.HttpUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

public class PlayerUsageSnooper {
    private final Map<String, Object> field_152773_a = Maps.newHashMap();
    private final Map<String, Object> field_152774_b = Maps.newHashMap();
    private final String uniqueID = UUID.randomUUID().toString();

    /**
     * URL of the server to send the report to
     */
    private final URL serverUrl;
    private final IPlayerUsage playerStatsCollector;

    /**
     * set to fire the snooperThread every 15 mins
     */
    private final Timer threadTrigger = new Timer("Snooper Timer", true);
    private final Object syncLock = new Object();
    private final long minecraftStartTimeMilis;
    private boolean isRunning;

    /**
     * incremented on every getSelfCounterFor
     */
    private int selfCounter;

    public PlayerUsageSnooper(final String p_i1563_1_, final IPlayerUsage playerStatCollector, final long startTime) {
        try {
            this.serverUrl = new URL("http://snoop.minecraft.net/" + p_i1563_1_ + "?version=" + 2);
        } catch (final MalformedURLException var6) {
            throw new IllegalArgumentException();
        }

        this.playerStatsCollector = playerStatCollector;
        this.minecraftStartTimeMilis = startTime;
    }

    /**
     * Note issuing start multiple times is not an error.
     */
    public void startSnooper() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.func_152766_h();
            this.threadTrigger.schedule(new TimerTask() {
                public void run() {
                    if (PlayerUsageSnooper.this.playerStatsCollector.isSnooperEnabled()) {
                        final Map<String, Object> map;

                        synchronized (PlayerUsageSnooper.this.syncLock) {
                            map = Maps.newHashMap(PlayerUsageSnooper.this.field_152774_b);

                            if (PlayerUsageSnooper.this.selfCounter == 0) {
                                map.putAll(PlayerUsageSnooper.this.field_152773_a);
                            }

                            map.put("snooper_count", Integer.valueOf(PlayerUsageSnooper.this.selfCounter++));
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
        this.addClientStat("version", "1.8.9");
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

        this.addClientStat("jvm_args", Integer.valueOf(i));
    }

    public void addMemoryStatsToSnooper() {
        this.addStatToSnooper("memory_total", Long.valueOf(Runtime.getRuntime().totalMemory()));
        this.addStatToSnooper("memory_max", Long.valueOf(Runtime.getRuntime().maxMemory()));
        this.addStatToSnooper("memory_free", Long.valueOf(Runtime.getRuntime().freeMemory()));
        this.addStatToSnooper("cpu_cores", Integer.valueOf(Runtime.getRuntime().availableProcessors()));
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
        final Map<String, String> map = Maps.newLinkedHashMap();

        synchronized (this.syncLock) {
            this.addMemoryStatsToSnooper();

            for (final Entry<String, Object> entry : this.field_152773_a.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }

            for (final Entry<String, Object> entry1 : this.field_152774_b.entrySet()) {
                map.put(entry1.getKey(), entry1.getValue().toString());
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

    /**
     * Returns the saved value of System#currentTimeMillis when the game started
     */
    public long getMinecraftStartTimeMillis() {
        return this.minecraftStartTimeMilis;
    }
}
