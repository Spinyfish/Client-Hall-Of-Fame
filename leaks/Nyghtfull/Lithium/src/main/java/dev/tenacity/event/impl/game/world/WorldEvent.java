package dev.tenacity.event.impl.game.world;

import dev.tenacity.event.Event;
import net.minecraft.world.World;

/**
 * WorldEvent is fired when an event involving the world occurs.
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.
 * {@link #world} contains the World this event is occuring in.
 **/
public class WorldEvent extends Event {
    public final World world;

    public WorldEvent(World world) {
        this.world = world;
    }

    /**
     * WorldEvent.Load is fired when Minecraft loads a world.
     * This event is fired when a world is loaded in
     * WorldClient#WorldClient(NetHandlerPlayClient, WorldSettings, int, EnumDifficulty, Profiler),
     * MinecraftServer#loadAllWorlds(String, String, long, WorldType, String),
     * and DimensionManager#initDimension(int).
     **/
    public static class Load extends WorldEvent {
        public Load(World world) {
            super(world);
        }
    }

    /**
     * WorldEvent.Unload is fired when Minecraft unloads a world
     * This event is fired when a world is unloaded in
     * Minecraft#loadWorld(WorldClient, String),
     * MinecraftServer#deleteWorldAndStopServer(),
     * and MinecraftServer#stopServer().
     **/
    public static class Unload extends WorldEvent {
        public Unload(World world) {
            super(world);
        }
    }

}
