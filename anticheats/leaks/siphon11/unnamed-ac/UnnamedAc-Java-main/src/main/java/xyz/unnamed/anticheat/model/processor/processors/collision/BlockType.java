package xyz.unnamed.anticheat.model.processor.processors.collision;

import lombok.Getter;

public enum BlockType {

    LIQUID(false),
    ICE(true),
    SLIME(true),
    SOULSAND(true),
    WEB(false),
    PISTON(true),
    CARPET(true),
    FENCE(true),
    SOLID(true),
    NOT_SOLID(false),
    FENCEGATE(false),
    WALL(true),
    DOOR(false),
    SLAB(true),
    CLIMBABLE(true),
    STAIRS(true)

    ;

    @Getter
    private final boolean solid;

    BlockType(boolean solid) {
        this.solid = solid;
    }
}
