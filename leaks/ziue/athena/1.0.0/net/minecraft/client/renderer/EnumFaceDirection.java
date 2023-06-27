package net.minecraft.client.renderer;

import net.minecraft.util.*;

public enum EnumFaceDirection
{
    DOWN(new VertexInformation[] { new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX) }), 
    UP(new VertexInformation[] { new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX) }), 
    NORTH(new VertexInformation[] { new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX) }), 
    SOUTH(new VertexInformation[] { new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX) }), 
    WEST(new VertexInformation[] { new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.WEST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX) }), 
    EAST(new VertexInformation[] { new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.SOUTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.DOWN_INDEX, Constants.NORTH_INDEX), new VertexInformation(Constants.EAST_INDEX, Constants.UP_INDEX, Constants.NORTH_INDEX) });
    
    private static final EnumFaceDirection[] facings;
    private final VertexInformation[] vertexInfos;
    
    public static EnumFaceDirection getFacing(final EnumFacing facing) {
        return EnumFaceDirection.facings[facing.getIndex()];
    }
    
    private EnumFaceDirection(final VertexInformation[] vertexInfosIn) {
        this.vertexInfos = vertexInfosIn;
    }
    
    public VertexInformation func_179025_a(final int p_179025_1_) {
        return this.vertexInfos[p_179025_1_];
    }
    
    static {
        (facings = new EnumFaceDirection[6])[Constants.DOWN_INDEX] = EnumFaceDirection.DOWN;
        EnumFaceDirection.facings[Constants.UP_INDEX] = EnumFaceDirection.UP;
        EnumFaceDirection.facings[Constants.NORTH_INDEX] = EnumFaceDirection.NORTH;
        EnumFaceDirection.facings[Constants.SOUTH_INDEX] = EnumFaceDirection.SOUTH;
        EnumFaceDirection.facings[Constants.WEST_INDEX] = EnumFaceDirection.WEST;
        EnumFaceDirection.facings[Constants.EAST_INDEX] = EnumFaceDirection.EAST;
    }
    
    public static final class Constants
    {
        public static final int SOUTH_INDEX;
        public static final int UP_INDEX;
        public static final int EAST_INDEX;
        public static final int NORTH_INDEX;
        public static final int DOWN_INDEX;
        public static final int WEST_INDEX;
        
        static {
            SOUTH_INDEX = EnumFacing.SOUTH.getIndex();
            UP_INDEX = EnumFacing.UP.getIndex();
            EAST_INDEX = EnumFacing.EAST.getIndex();
            NORTH_INDEX = EnumFacing.NORTH.getIndex();
            DOWN_INDEX = EnumFacing.DOWN.getIndex();
            WEST_INDEX = EnumFacing.WEST.getIndex();
        }
    }
    
    public static class VertexInformation
    {
        public final int field_179184_a;
        public final int field_179182_b;
        public final int field_179183_c;
        
        private VertexInformation(final int p_i46270_1_, final int p_i46270_2_, final int p_i46270_3_) {
            this.field_179184_a = p_i46270_1_;
            this.field_179182_b = p_i46270_2_;
            this.field_179183_c = p_i46270_3_;
        }
    }
}
