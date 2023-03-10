/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import net.minecraft.util.EnumFacing;

public enum EnumFaceDirection {
    DOWN(new VertexInformation[]{new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX)}),
    UP(new VertexInformation[]{new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX)}),
    NORTH(new VertexInformation[]{new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX)}),
    SOUTH(new VertexInformation[]{new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX)}),
    WEST(new VertexInformation[]{new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX)}),
    EAST(new VertexInformation[]{new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX)});

    private static final EnumFaceDirection[] facings;
    private final VertexInformation[] vertexInfos;

    public static EnumFaceDirection getFacing(EnumFacing facing) {
        return facings[facing.getIndex()];
    }

    private EnumFaceDirection(VertexInformation[] vertexInfosIn) {
        this.vertexInfos = vertexInfosIn;
    }

    public VertexInformation func_179025_a(int p_179025_1_) {
        return this.vertexInfos[p_179025_1_];
    }

    static {
        facings = new EnumFaceDirection[6];
        EnumFaceDirection.facings[Constants.DOWN_INDEX] = DOWN;
        EnumFaceDirection.facings[Constants.UP_INDEX] = UP;
        EnumFaceDirection.facings[Constants.NORTH_INDEX] = NORTH;
        EnumFaceDirection.facings[Constants.SOUTH_INDEX] = SOUTH;
        EnumFaceDirection.facings[Constants.WEST_INDEX] = WEST;
        EnumFaceDirection.facings[Constants.EAST_INDEX] = EAST;
    }

    public static class VertexInformation {
        public final int field_179184_a;
        public final int field_179182_b;
        public final int field_179183_c;

        private VertexInformation(int p_i46270_1_, int p_i46270_2_, int p_i46270_3_) {
            this.field_179184_a = p_i46270_1_;
            this.field_179182_b = p_i46270_2_;
            this.field_179183_c = p_i46270_3_;
        }
    }

    public static final class Constants {
        public static final int SOUTH_INDEX = EnumFacing.SOUTH.getIndex();
        public static final int UP_INDEX = EnumFacing.UP.getIndex();
        public static final int EAST_INDEX = EnumFacing.EAST.getIndex();
        public static final int NORTH_INDEX = EnumFacing.NORTH.getIndex();
        public static final int DOWN_INDEX = EnumFacing.DOWN.getIndex();
        public static final int WEST_INDEX = EnumFacing.WEST.getIndex();
    }
}

