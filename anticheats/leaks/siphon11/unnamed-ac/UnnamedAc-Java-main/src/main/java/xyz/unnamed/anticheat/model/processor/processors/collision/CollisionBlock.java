package xyz.unnamed.anticheat.model.processor.processors.collision;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Stairs;

import java.util.Locale;


@Getter
public class CollisionBlock {

    private final Block block;
    private final CollisionType type;
    private boolean loaded, climbable, water, lava, solid, ice, slime, soulSand, web, piston, fence, fenceGate, wall, door, slab, stairs, carpet;
    private String name;
    private Material material;
    private double x, y, z;
    private Location location;

    public CollisionBlock(Block block, CollisionType type) {
        this.block = block;
        this.type = type;
        loaded = block != null;

        if (loaded) {
            material = block.getType();
            name = material.name();
            x = block.getX();
            y = block.getY();
            z = block.getZ();
            location = new Location(block.getWorld(), x, y, z);

            solid = material.isSolid() || material == Material.SNOW;
            water = material == Material.STATIONARY_WATER
                    || material == Material.WATER;
            lava = material == Material.STATIONARY_LAVA
                    || material == Material.LAVA;
            ice = material == Material.PACKED_ICE
                    || material == Material.ICE;
            slime = material == Material.SLIME_BLOCK;
            soulSand = material == Material.SOUL_SAND;
            web = material == Material.WEB;
            piston = material == Material.PISTON_BASE
                    || material == Material.PISTON_EXTENSION
                    || material == Material.PISTON_MOVING_PIECE
                    || material == Material.PISTON_STICKY_BASE;
            carpet = material == Material.CARPET;
            fence = material == Material.FENCE
                    || material == Material.ACACIA_FENCE
                    || material == Material.BIRCH_FENCE
                    || material == Material.DARK_OAK_FENCE
                    || material == Material.JUNGLE_FENCE
                    || material == Material.NETHER_FENCE
                    || material == Material.SPRUCE_FENCE;
            fenceGate = material == Material.FENCE_GATE
                    || material == Material.ACACIA_FENCE_GATE
                    || material == Material.BIRCH_FENCE_GATE
                    || material == Material.DARK_OAK_FENCE_GATE
                    || material == Material.JUNGLE_FENCE_GATE
                    || material == Material.SPRUCE_FENCE_GATE;
            wall = material == Material.COBBLE_WALL;
            climbable = material == Material.LADDER || material == Material.VINE;
            door = material == Material.ACACIA_DOOR
                    || material == Material.BIRCH_DOOR
                    || material == Material.WOOD_DOOR
                    || material == Material.WOODEN_DOOR
                    || material == Material.SPRUCE_DOOR
                    || material == Material.JUNGLE_DOOR
                    || material == Material.DARK_OAK_DOOR;
            slab = block.toString().toLowerCase(Locale.ROOT).contains("slab");
            stairs = block instanceof Stairs;
        }
    }

    public BlockType get() {
        if (water || lava) return BlockType.LIQUID;
        if (fence) return BlockType.FENCE;
        if (ice) return BlockType.ICE;
        if (slime) return BlockType.SLIME;
        if (soulSand) return BlockType.SOULSAND;
        if (carpet) return BlockType.CARPET;
        if (door) return BlockType.DOOR;
        if (fenceGate) return BlockType.FENCEGATE;
        if(slab) return BlockType.SLAB;
        if(stairs) return BlockType.STAIRS;
        if(wall) return BlockType.WALL;
        if(piston) return BlockType.PISTON;
        if(web) return BlockType.WEB;
        if(climbable) return BlockType.CLIMBABLE;

        return solid ? BlockType.SOLID : BlockType.NOT_SOLID;
    }
}
