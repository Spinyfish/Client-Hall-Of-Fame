package me.eldodebug.soar.utils.culling;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import com.logisticscraft.occlusionculling.util.Vec3d;

import me.eldodebug.soar.utils.interfaces.ICullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;

public class CullTask implements Runnable {

	public static boolean requestCull = false;

	private static final Logger LOGGER = LogManager.getLogger();
	private final OcclusionCullingInstance culling;
	private final Minecraft mc = Minecraft.getMinecraft();
	private final int hitboxLimit = 15;
	public long lastTime = 0;

	private Vec3d lastPos = new Vec3d(0, 0, 0);
	private Vec3d aabbMin = new Vec3d(0, 0, 0);
	private Vec3d aabbMax = new Vec3d(0, 0, 0);

	public CullTask(OcclusionCullingInstance culling) {
		this.culling = culling;
	}

	@Override
	public void run() {
		try {
			if(mc.thePlayer != null && mc.getRenderViewEntity() != null) {
				Vec3 cameraMC = ActiveRenderInfo.projectViewFromEntity(mc.getRenderViewEntity(), 1);

				if(requestCull || !(cameraMC.xCoord == lastPos.x && cameraMC.yCoord == lastPos.y
						&& cameraMC.zCoord == lastPos.z)) {
					long start = System.currentTimeMillis();
					requestCull = false;
					lastPos.set(cameraMC.xCoord, cameraMC.yCoord, cameraMC.zCoord);
					Vec3d camera = lastPos;
					culling.resetCache();
					boolean spectator = mc.thePlayer.isSpectator();

					for(int x = -8; x <= 8; x++) {
						for(int z = -8; z <= 8; z++) {
							Chunk chunk = mc.theWorld.getChunkFromChunkCoords(mc.thePlayer.chunkCoordX + x,
									mc.thePlayer.chunkCoordZ + z);
							Iterator<Entry<BlockPos, TileEntity>> iterator = chunk.getTileEntityMap().entrySet()
									.iterator();
							Entry<BlockPos, TileEntity> entry;
							while (iterator.hasNext()) {
								try {
									entry = iterator.next();
								}
								catch(NullPointerException | ConcurrentModificationException ex) {
									break;
								}

								if(entry.getValue().getBlockType() == Blocks.beacon) {
									continue;
								}

								TileEntity tile = entry.getValue();

								if(spectator) {
									((ICullable) tile).setCulled(false);
									continue;
								}

								BlockPos pos = entry.getKey();

								if(pos.distanceSq(cameraMC.xCoord, cameraMC.yCoord,
										cameraMC.zCoord) < 4096.0D) {
									aabbMin.set(pos.getX(), pos.getY(), pos.getZ());
									aabbMax.set(pos.getX() + 1d, pos.getY() + 1d, pos.getZ() + 1d);

									boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
									((ICullable) tile).setCulled(!visible);
								}
							}

						}
					}

					Entity entity = null;
					Iterator<Entity> iterable = mc.theWorld.loadedEntityList.iterator();

					while(iterable.hasNext()) {
						try {
							entity = iterable.next();
						}
						catch(NullPointerException | ConcurrentModificationException ex) {
							break;
						}

						if(spectator || isSkippableArmorstand(entity)) {
							((ICullable) entity).setCulled(false);
							continue;
						}

						if(!(entity.getPositionVector().distanceTo(cameraMC) < 128)) {
							((ICullable) entity).setCulled(false);
							continue;
						}

						AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
						if(boundingBox.maxX - boundingBox.minX > hitboxLimit
								|| boundingBox.maxY - boundingBox.minY > hitboxLimit
								|| boundingBox.maxZ - boundingBox.minZ > hitboxLimit) {
							((ICullable) entity).setCulled(false);
							continue;
						}

						aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
						aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);

						boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
						((ICullable) entity).setCulled(!visible);
					}
					lastTime = (System.currentTimeMillis() - start);
				}
			}
		}
		catch(Exception error) {
			LOGGER.error("Error culling", error);
		}
	}

	private boolean isSkippableArmorstand(Entity entity) {
		return entity instanceof EntityArmorStand && ((EntityArmorStand) entity).hasMarker();
	}

}