package rip.athena.client.modules.impl.fpssettings;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;

public class OptimizerMod extends Module
{
    @ConfigValue.Boolean(name = "Culling fix", description = "Fixes a bug which can cause chunks to not sometimes render, this can affect fps negatively.")
    public boolean CULLING_FIX;
    public boolean ENTITY_CULLING;
    public int ENTITY_CULLING_INTERVAL;
    public boolean SMART_ENTITY_CULLING;
    public boolean DONT_CULL_PLAYER_NAMETAGS;
    public boolean DONT_CULL_ENTITY_NAMETAGS;
    public boolean DONT_CULL_ARMOR_STANDS_NAMETAGS;
    @ConfigValue.Boolean(name = "Static particle color", description = "Makes particles render at full brightness.")
    public boolean STATIC_PARTICLE_COLOR;
    @ConfigValue.Boolean(name = "Better skin loading", description = "Makes so skin loading should be less stuttery when you join servers.")
    public boolean BETTER_SKIN_LOADING;
    @ConfigValue.Boolean(name = "Remove light calculation", description = "Removes light calculations, rendering the game in full brightness.")
    public boolean LIGHT_CALCULATION_REMOVAL;
    @ConfigValue.Boolean(name = "Chunk update limiter", description = "Enable a chunk update limiter to improve performance")
    public boolean CHUNK_UPDATE_LIMITE_ENABLED;
    @ConfigValue.Integer(name = "Chunk updates per second", description = "Lower value, better fps.", min = 1, max = 250)
    public int CHUNK_UPDATE_LIMITER;
    @ConfigValue.Boolean(name = "Remove mob spawner entity", description = "Removes the spinning entity inside of mob spawners.")
    public boolean REMOVE_ENTITY_SPAWNER;
    @ConfigValue.Boolean(name = "Static drops", description = "Items on ground no longer rotate.")
    public boolean STATIC_DROPS;
    public boolean FAST_WORLD_LOADING;
    @ConfigValue.Boolean(name = "Custom Cane Renderer", description = "Only render cane in a certain radius to help FPS")
    public boolean noLagCane;
    @ConfigValue.Boolean(name = "Remove Water", description = "Removes the render of water")
    public static boolean noLagClearWater;
    @ConfigValue.Integer(name = "Tile Entity Render Distance", min = 1, max = 64)
    public int noLagBlockDistance;
    @ConfigValue.Integer(name = "Entity Render Distance", min = 1, max = 64)
    public int noLagEntityDistance;
    @ConfigValue.Boolean(name = "Better Chests", description = "Disable render of knob/lid/reduce size of chests")
    public boolean noBetterChests;
    
    public OptimizerMod() {
        super("Optimizer", Category.FPS_SETTINGS);
        this.CULLING_FIX = true;
        this.ENTITY_CULLING = true;
        this.ENTITY_CULLING_INTERVAL = 0;
        this.SMART_ENTITY_CULLING = true;
        this.DONT_CULL_PLAYER_NAMETAGS = true;
        this.DONT_CULL_ENTITY_NAMETAGS = true;
        this.DONT_CULL_ARMOR_STANDS_NAMETAGS = true;
        this.STATIC_PARTICLE_COLOR = true;
        this.BETTER_SKIN_LOADING = true;
        this.LIGHT_CALCULATION_REMOVAL = true;
        this.CHUNK_UPDATE_LIMITE_ENABLED = true;
        this.CHUNK_UPDATE_LIMITER = 50;
        this.REMOVE_ENTITY_SPAWNER = true;
        this.STATIC_DROPS = true;
        this.FAST_WORLD_LOADING = true;
        this.noLagCane = false;
        this.noLagBlockDistance = 32;
        this.noLagEntityDistance = 32;
        this.noBetterChests = false;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    static {
        OptimizerMod.noLagClearWater = false;
    }
}
