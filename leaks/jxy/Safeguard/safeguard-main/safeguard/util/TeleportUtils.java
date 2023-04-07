package intentions.util;

import java.util.ArrayList;

import intentions.modules.movement.ClickTP;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class TeleportUtils {

	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static BlockPos getBlockPosRelativeToEntity(Entity en, double d) {
		return new BlockPos(en.posX, en.posY + d, en.posZ);
	}
	public static Block getBlock(BlockPos pos) {
		return mc.theWorld.getBlockState(pos).getBlock();
	}
	public static BlockPos getBlockPos(Vec3 vec) {
		return new BlockPos(vec.xCoord, vec.yCoord, vec.zCoord);
	}
	
	public static TeleportResult pathFinderTeleportTo(Vec3 from, Vec3 to) {
		boolean sneaking = mc.thePlayer.isSneaking();
		ArrayList<Vec3> positions = new ArrayList<Vec3>();
		ArrayList<Node> triedPaths = new ArrayList<Node>();
//		System.out.println(to.toString());
		BlockPos targetBlockPos = new BlockPos(TeleportUtils.getBlockPos(to));
		BlockPos fromBlockPos = TeleportUtils.getBlockPos(from);
		if(!NodeProcessor.isPassable(TeleportUtils.getBlock(fromBlockPos))) {
			
			float angle = (float) Math.toDegrees(Math.atan2(to.zCoord - from.zCoord, to.xCoord - from.xCoord));
			
			angle += 180;
		    if(angle < 0){
		        angle += 360;
		    }
		    from = TeleportUtils.getVec3(fromBlockPos.offset(EnumFacing.fromAngle(TeleportUtils.normalizeAngle(angle))).add(0.5, 0, 0.5));
		}
		
		BlockPos finalBlockPos = targetBlockPos;
		boolean passable = true;
		if(!NodeProcessor.isPassable(TeleportUtils.getBlock(targetBlockPos))) {
			finalBlockPos = targetBlockPos.up();
			boolean lastIsPassable;
			if(!(lastIsPassable = NodeProcessor.isPassable(TeleportUtils.getBlock(targetBlockPos.up())))) {
				finalBlockPos = targetBlockPos.up(2);
				if(!lastIsPassable) {
					passable = false;
				}
			}
		}
		
		if(!passable) {
			float angle = (float) Math.toDegrees(Math.atan2(to.zCoord - finalBlockPos.getZ(), to.xCoord - finalBlockPos.getX()));
			
			angle += 180;
		    if(angle < 0){
		        angle += 360;
		    }
		    finalBlockPos = targetBlockPos.offset(EnumFacing.fromAngle(TeleportUtils.normalizeAngle(angle)));
		}
		
		NodeProcessor processor = new NodeProcessor();
		
		processor.getPath(new BlockPos(from.xCoord, from.yCoord, from.zCoord), finalBlockPos);
		triedPaths = processor.triedPaths;
		if(processor.path == null) {
			return new TeleportResult(positions, null, triedPaths, null, null, false);
		}
		Vec3 lastPos = null;
		if (sneaking) {
			mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
		}
		for(Node node : processor.path) {
			BlockPos pos = node.getBlockpos();
			if(ClickTP.mode.getMode().equalsIgnoreCase("ACD")) {
				for(int i=0;i<100;i++) 
				{
					mc.thePlayer.setPosition((node.getBlockpos().getX() - 1 + (0.01 * i)) + 0.5, node.getBlockpos().getY() + 1, (node.getBlockpos().getZ() - 1 + (0.01 * i)) + 0.5);
				}
			} else {
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, false));
			}
			positions.add((lastPos = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)));
		}
		if (sneaking) {
			mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
		}
		return new TeleportResult(positions, null, triedPaths, processor.path, lastPos, true);
	}
	
	public static float normalizeAngle(float angle) {
		return (angle + 360) % 360;
	}
	
	public static Vec3 getVec3(BlockPos blockPos) {
		return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public static boolean isValidTeleport(BlockPos blockPos) {
		return true;
		//return BlockUtils.getBlockUnderPlayer(mc.thePlayer, 1) == Blocks.air;
	}
	
}
