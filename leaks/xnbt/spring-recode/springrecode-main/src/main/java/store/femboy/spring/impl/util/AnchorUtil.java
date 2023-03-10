package store.femboy.spring.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AnchorUtil {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public static PlaceData getData() {
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ);
        BlockPos normalPos = playerPos.add(0, -1, 0);
        EnumFacing currentFacing;
        if(!mc.theWorld.isAirBlock(playerPos.add(0, -2, 0))) {
            currentFacing = EnumFacing.UP;
        } else if(!mc.theWorld.isAirBlock(playerPos.add(-1, -1, 0))) {
            currentFacing = EnumFacing.EAST;
        } else if(!mc.theWorld.isAirBlock(playerPos.add(1, -1, 0))) {
            currentFacing = EnumFacing.WEST;
        } else if(!mc.theWorld.isAirBlock(playerPos.add(0, -1, 1))) {
            currentFacing = EnumFacing.NORTH;
        } else if(!mc.theWorld.isAirBlock(playerPos.add(0, -1, -1))) {
            currentFacing = EnumFacing.SOUTH;
        } else if(!mc.theWorld.isAirBlock(playerPos.add(1, -1, 1))) {
            currentFacing = EnumFacing.WEST;
            normalPos = playerPos.add(0, -1, 1);
        } else if(!mc.theWorld.isAirBlock(playerPos.add(-1, -1, -1))) {
            currentFacing = EnumFacing.EAST;
            normalPos = playerPos.add(0, -1, -1);
        } else if(!mc.theWorld.isAirBlock(playerPos.add(-1, -1, 1))) {
            currentFacing = EnumFacing.NORTH;
            normalPos = playerPos.add(-1, -1, 0);
        } else if(!mc.theWorld.isAirBlock(playerPos.add(1, -1, -1))) {
            currentFacing = EnumFacing.SOUTH;
            normalPos = playerPos.add(1, -1, 0);
        } else {
            currentFacing = EnumFacing.DOWN;
        }
        BlockPos currentPos = anchorPosition(normalPos, currentFacing, -1);
        if(currentPos == null) {
            if(!mc.theWorld.isAirBlock(playerPos.add(0, -3, 0))) {
                currentFacing = EnumFacing.UP;
            } else if(!mc.theWorld.isAirBlock(playerPos.add(-1, -2, 0))) {
                currentFacing = EnumFacing.EAST;
            } else if(!mc.theWorld.isAirBlock(playerPos.add(1, -2, 0))) {
                currentFacing = EnumFacing.WEST;
            } else if(!mc.theWorld.isAirBlock(playerPos.add(0, -2, 1))) {
                currentFacing = EnumFacing.NORTH;
            } else if(!mc.theWorld.isAirBlock(playerPos.add(0, -2, -1))) {
                currentFacing = EnumFacing.SOUTH;
            } else if(!mc.theWorld.isAirBlock(playerPos.add(1, -2, 1))) {
                currentFacing = EnumFacing.WEST;
                normalPos = playerPos.add(0, -2, 1);
            } else if(!mc.theWorld.isAirBlock(playerPos.add(-1, -2, -1))) {
                currentFacing = EnumFacing.EAST;
                normalPos = playerPos.add(0, -2, -1);
            } else if(!mc.theWorld.isAirBlock(playerPos.add(-1, -2, 1))) {
                currentFacing = EnumFacing.NORTH;
                normalPos = playerPos.add(-1, -2, 0);
            } else if(!mc.theWorld.isAirBlock(playerPos.add(1, -2, -1))) {
                currentFacing = EnumFacing.SOUTH;
                normalPos = playerPos.add(1, -2, 0);
            } else {
                currentFacing = EnumFacing.DOWN;
            }
            currentPos = anchorPosition(normalPos.add(0, -1, 0), currentFacing, -1);
            if(currentPos == null) {
                if(!mc.theWorld.isAirBlock(playerPos.add(0, -4, 0))) {
                    currentFacing = EnumFacing.UP;
                } else if(!mc.theWorld.isAirBlock(playerPos.add(-1, -3, 0))) {
                    currentFacing = EnumFacing.EAST;
                } else if(!mc.theWorld.isAirBlock(playerPos.add(1, -3, 0))) {
                    currentFacing = EnumFacing.WEST;
                } else if(!mc.theWorld.isAirBlock(playerPos.add(0, -3, 1))) {
                    currentFacing = EnumFacing.NORTH;
                } else if(!mc.theWorld.isAirBlock(playerPos.add(0, -3, -1))) {
                    currentFacing = EnumFacing.SOUTH;
                } else if(!mc.theWorld.isAirBlock(playerPos.add(1, -3, 1))) {
                    currentFacing = EnumFacing.WEST;
                    normalPos = playerPos.add(0, -3, 1);
                } else if(!mc.theWorld.isAirBlock(playerPos.add(-1, -3, -1))) {
                    currentFacing = EnumFacing.EAST;
                    normalPos = playerPos.add(0, -3, -1);
                } else if(!mc.theWorld.isAirBlock(playerPos.add(-1, -3, 1))) {
                    currentFacing = EnumFacing.NORTH;
                    normalPos = playerPos.add(-1, -3, 0);
                } else if(!mc.theWorld.isAirBlock(playerPos.add(1, -3, -1))) {
                    currentFacing = EnumFacing.SOUTH;
                    normalPos = playerPos.add(1, -3, 0);
                } else {
                    currentFacing = EnumFacing.DOWN;
                }
                currentPos = anchorPosition(normalPos.add(0, -2, 0), currentFacing, -1);
            }
        }
        if (currentPos == null) {
            currentPos = anchorPosition(normalPos.add(0, -1, 0), currentFacing, -1);
        }
        return new PlaceData(currentPos, currentFacing);
    }

    public static BlockPos anchorPosition(BlockPos normalPos, EnumFacing facing, double maxExtend) {
        BlockPos currentPos = null;
        for(double d = -1; d <= maxExtend; d++) {
            if(facing == EnumFacing.NORTH) {
                currentPos = normalPos.add(0, 0, -d);
            } else if(facing == EnumFacing.SOUTH) {
                currentPos = normalPos.add(0, 0, d);
            } else if(facing == EnumFacing.EAST) {
                currentPos = normalPos.add(d, 0, 0);
            } else if(facing == EnumFacing.WEST) {
                currentPos = normalPos.add(-d, 0, 0);
            } else if(facing == EnumFacing.UP) {
                currentPos = normalPos.add(0, d, 0);
            }
        }
        return currentPos;
    }

    public static class PlaceData {
        private BlockPos pos;
        private EnumFacing facing;

        public PlaceData(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }

        public BlockPos getPos() {
            return pos;
        }

        public EnumFacing getFacing() {
            return facing;
        }

    }


}
