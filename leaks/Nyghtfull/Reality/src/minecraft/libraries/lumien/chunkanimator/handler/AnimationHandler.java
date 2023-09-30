package libraries.lumien.chunkanimator.handler;

import java.util.WeakHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import Reality.Realii.Client;
import Reality.Realii.mods.modules.render.ChunkAnimator;

public class AnimationHandler {
    WeakHashMap<RenderChunk, AnimationData> timeStamps;

    public AnimationHandler() {
        timeStamps = new WeakHashMap<RenderChunk, AnimationData>();
    }

    public void preRender(RenderChunk renderChunk) {
        if (timeStamps.containsKey(renderChunk)) {
            AnimationData animationData = timeStamps.get(renderChunk);
            long time = animationData.timeStamp;
            int mode = ((ChunkAnimator) (Client.instance.getModuleManager().getModuleByClass(ChunkAnimator.class))).getMode();

//            System.out.println(mode);
            if (time == -1L) {
                time = System.currentTimeMillis();

                animationData.timeStamp = time;

                // Mode 4 Set Chunk Facing
                if (mode == 4) {
                    BlockPos zeroedPlayerPosition = Minecraft.getMinecraft().thePlayer.getPosition();
                    zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);

                    BlockPos zeroedCenteredChunkPos = renderChunk.getPosition().add(8, -renderChunk.getPosition().getY(), 8);

                    Vec3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);

                    int difX = Math.abs(dif.getX());
                    int difZ = Math.abs(dif.getZ());

                    EnumFacing chunkFacing;

                    if (difX > difZ) {
                        if (dif.getX() > 0) {
                            chunkFacing = EnumFacing.EAST;
                        } else {
                            chunkFacing = EnumFacing.WEST;
                        }
                    } else {
                        if (dif.getZ() > 0) {
                            chunkFacing = EnumFacing.SOUTH;
                        } else {
                            chunkFacing = EnumFacing.NORTH;
                        }
                    }

                    animationData.chunkFacing = chunkFacing;
                }
            }

            long timeDif = System.currentTimeMillis() - time;
            int animationDuration = 1000;

            if (timeDif < animationDuration) {
                double chunkY = renderChunk.getPosition().getY();
                double modY;

                if (mode == 2) {
                    if (chunkY < Minecraft.getMinecraft().theWorld.getHorizon()) {
                        mode = 0;
                    } else {
                        mode = 1;
                    }
                }

                if (mode == 4) {
                    mode = 3;
                }

                switch (mode) {
                    case 0:
                        modY = chunkY / (animationDuration) * timeDif;
                        GlStateManager.translate(0, -chunkY + modY, 0);
                        break;
                    case 1:
                        modY = (256D - chunkY) / (animationDuration) * timeDif;
                        GlStateManager.translate(0, 256 - chunkY - modY, 0);
                        break;
                    case 3:
                        EnumFacing chunkFacing = animationData.chunkFacing;

                        if (chunkFacing != null) {
                            Vec3i vec = chunkFacing.getDirectionVec();
                            double mod = -(200D - (200D / animationDuration * timeDif));

                            GlStateManager.translate(vec.getX() * mod, 0, vec.getZ() * mod);
                        }
                        break;
                }
            } else {
                timeStamps.remove(renderChunk);
            }
        }
    }

    public void setPosition(RenderChunk renderChunk, BlockPos position) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            boolean flag = true;
            BlockPos zeroedPlayerPosition = Minecraft.getMinecraft().thePlayer.getPosition();
            zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);
            BlockPos zeroedCenteredChunkPos = position.add(8, -position.getY(), 8);

//			if (ChunkAnimator.INSTANCE.config.disableAroundPlayer())
//			{
//				flag = zeroedPlayerPosition.distanceSq(zeroedCenteredChunkPos) > (64 * 64);
//			}

            if (flag) {
                EnumFacing chunkFacing = null;

                if (((ChunkAnimator) (Client.instance.getModuleManager().getModuleByClass(ChunkAnimator.class))).getMode() == 3) {
                    Vec3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);

                    int difX = Math.abs(dif.getX());
                    int difZ = Math.abs(dif.getZ());

                    if (difX > difZ) {
                        if (dif.getX() > 0) {
                            chunkFacing = EnumFacing.EAST;
                        } else {
                            chunkFacing = EnumFacing.WEST;
                        }
                    } else {
                        if (dif.getZ() > 0) {
                            chunkFacing = EnumFacing.SOUTH;
                        } else {
                            chunkFacing = EnumFacing.NORTH;
                        }
                    }
                }

                AnimationData animationData = new AnimationData(-1L, chunkFacing);
                timeStamps.put(renderChunk, animationData);
            }
        }
    }

    private class AnimationData {
        public long timeStamp;

        public EnumFacing chunkFacing;

        public AnimationData(long timeStamp, EnumFacing chunkFacing) {
            this.timeStamp = timeStamp;
            this.chunkFacing = chunkFacing;
        }
    }
}
