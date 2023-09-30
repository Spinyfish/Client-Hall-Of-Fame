package net.minecraft.client.renderer;

import net.minecraft.util.EnumFacing;

public enum EnumFaceDirection
{
    DOWN(new VertexInformation[]{new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX)}),
    UP(new VertexInformation[]{new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX)}),
    NORTH(new VertexInformation[]{new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX)}),
    SOUTH(new VertexInformation[]{new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX)}),
    WEST(new VertexInformation[]{new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX)}),
    EAST(new VertexInformation[]{new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX)});

    private static final EnumFaceDirection[] facings = new EnumFaceDirection[6];
    private final VertexInformation[] vertexInfos;

    public static EnumFaceDirection getFacing(EnumFacing facing)
    {
        return facings[facing.getIndex()];
    }

    private EnumFaceDirection(VertexInformation[] vertexInfosIn)
    {
        this.vertexInfos = vertexInfosIn;
    }

    public VertexInformation getVertexInformation(int index)
    {
        return this.vertexInfos[index];
    }

    static {
        facings[Constants.DOWN_INDEX] = DOWN;
        facings[Constants.UP_INDEX] = UP;
        facings[Constants.NORTH_INDEX] = NORTH;
        facings[Constants.SOUTH_INDEX] = SOUTH;
        facings[Constants.WEST_INDEX] = WEST;
        facings[Constants.EAST_INDEX] = EAST;
    }

    public static final class Constants {
        public static final int SOUTH_INDEX = EnumFacing.SOUTH.getIndex();
        public static final int UP_INDEX = EnumFacing.UP.getIndex();
        public static final int EAST_INDEX = EnumFacing.EAST.getIndex();
        public static final int NORTH_INDEX = EnumFacing.NORTH.getIndex();
        public static final int DOWN_INDEX = EnumFacing.DOWN.getIndex();
        public static final int WEST_INDEX = EnumFacing.WEST.getIndex();
    }

    public static class VertexInformation {
        public final int xIndex;
        public final int yIndex;
        public final int zIndex;

        private VertexInformation(int xIndexIn, int yIndexIn, int zIndexIn)
        {
            this.xIndex = xIndexIn;
            this.yIndex = yIndexIn;
            this.zIndex = zIndexIn;
        }
    }
}
