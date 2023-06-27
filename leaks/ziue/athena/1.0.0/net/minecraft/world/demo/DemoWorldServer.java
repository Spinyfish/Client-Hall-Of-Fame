package net.minecraft.world.demo;

import net.minecraft.server.*;
import net.minecraft.world.storage.*;
import net.minecraft.profiler.*;
import net.minecraft.world.*;

public class DemoWorldServer extends WorldServer
{
    private static final long demoWorldSeed;
    public static final WorldSettings demoWorldSettings;
    
    public DemoWorldServer(final MinecraftServer server, final ISaveHandler saveHandlerIn, final WorldInfo worldInfoIn, final int dimensionId, final Profiler profilerIn) {
        super(server, saveHandlerIn, worldInfoIn, dimensionId, profilerIn);
        this.worldInfo.populateFromWorldSettings(DemoWorldServer.demoWorldSettings);
    }
    
    static {
        demoWorldSeed = "North Carolina".hashCode();
        demoWorldSettings = new WorldSettings(DemoWorldServer.demoWorldSeed, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT).enableBonusChest();
    }
}
