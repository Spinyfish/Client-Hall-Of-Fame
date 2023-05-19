package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.impl.events.SafewalkEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.combat.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Scaffold extends Module
{
	public static BooleanValue swing = new BooleanValue("Swing", true, () -> true);
	private BooleanValue swapToBlocksAutomatically = new BooleanValue("Swap to blocks automatically", true, () -> true);
	private ModeValue rotationModeValue = new ModeValue("Rotation Mode", "Normal", new String[] {"Normal", "Smooth"});
	private BooleanValue realClick = new BooleanValue("Real Click", true, () -> true);
	public static BooleanValue sprint = new BooleanValue("Allow Sprint", false, () -> true);
	public static BooleanValue safeWalk = new BooleanValue("Safe Walk", true, () -> true);
	public static BooleanValue rayCast = new BooleanValue("Ray Cast", true, () -> true);
	private float prevYaw, prevPitch;
	private int previousSlot = 0;
	private BlockPos blockPos;
	public static boolean on;
	
	public Scaffold()
	{
		super("Scaffold", Keyboard.KEY_V, Category.PLAYER);
	}
	
	@Override
	public void onEnable()
	{
		on = true;
		super.onEnable();
		this.prevYaw = mc.thePlayer.rotationYaw;
		this.prevPitch = mc.thePlayer.rotationPitch;
		this.previousSlot = mc.thePlayer.inventory.currentItem;
	}
	
	@Subscribe
	public void onSafeWalk(SafewalkEvent e)
	{
		try
		{
			if (this.safeWalk.isOn())
			{
				e.setCancelled(mc.thePlayer.onGround);
			}
		}
		
		catch (Exception exception)
		{
			;
		}
	}
	
	public MovingObjectPosition rayTrace(float yaw, float pitch)
	{
		Vec3 vec3 = mc.thePlayer.getPositionEyes(1);
		Vec3 vec31 = Entity.getVectorForRotation(pitch, yaw);
		Vec3 vec32 = vec3.addVector(vec31.xCoord * mc.playerController.getBlockReachDistance(), vec31.yCoord * mc.playerController.getBlockReachDistance(), vec31.zCoord * mc.playerController.getBlockReachDistance());
		return mc.theWorld.rayTraceBlocks(vec3, vec32);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		if (!this.sprint.isOn())
		{
			mc.thePlayer.setSprinting(false);
		}
		
		if (this.swapToBlocksAutomatically.isOn() && (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)))
		{
			int item = -1;
			
			for (int i = 0; i < 9; i++)
			{
				if (this.mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock)
				{
					item = i;
					break;
				}
			}
			
			if (item != -1)
			{
				this.mc.thePlayer.inventory.currentItem = item;
			}
		}
		
		BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ);
		
		if (this.isValid(blockPos.add(0, -2, 0)))
		{
			this.place(blockPos.add(0, -1, 0), EnumFacing.UP);
		}
		
		else if (this.isValid(blockPos.add(-1, -1, 0)))
		{
			this.place(blockPos.add(0, -1, 0), EnumFacing.EAST);
		}
		
		else if (this.isValid(blockPos.add(1, -1, 0)))
		{
			this.place(blockPos.add(0, -1, 0), EnumFacing.WEST);
		}
		
		else if (this.isValid(blockPos.add(0, -1, -1)))
		{
			this.place(blockPos.add(0, -1, 0), EnumFacing.SOUTH);
		}
		
		else if (this.isValid(blockPos.add(0, -1, 1)))
		{
			this.place(blockPos.add(0, -1, 0), EnumFacing.NORTH);
		}
		
		else if (this.isValid(blockPos.add(1, -1, 1)))
		{
			if (this.isValid(blockPos.add(0, -1, 1)))
			{
				this.place(blockPos.add(0, -1, 1), EnumFacing.NORTH);
			}
			
			this.place(blockPos.add(1, -1, 1), EnumFacing.EAST);
		}
		
		else if (this.isValid(blockPos.add(-1, -1, 1)))
		{
			if (this.isValid(blockPos.add(-1, -1, 0)))
			{
				this.place(blockPos.add(0, -1, 1), EnumFacing.WEST);
			}
			
			this.place(blockPos.add(-1, -1, -1), EnumFacing.SOUTH);
		}
		
		else if (this.isValid(blockPos.add(-1, -1, -1)))
		{
			if (this.isValid(blockPos.add(0, -1, -1)))
			{
				this.place(blockPos.add(0, -1, 1), EnumFacing.SOUTH);
			}
			
			this.place(blockPos.add(-1, -1, 1), EnumFacing.WEST);
		}
		
		else if (this.isValid(blockPos.add(1, -1, -1)))
		{
			if (this.isValid(blockPos.add(1, -1, 0)))
			{
				this.place(blockPos.add(1, -1, 0), EnumFacing.EAST);
			}
			
			this.place(blockPos.add(1, -1, -1), EnumFacing.NORTH);
		}
		
		if (this.blockPos != null)
		{
			double x = this.blockPos.getX() + 0.5 - this.mc.thePlayer.posX;
	        double y = this.blockPos.getY() - 2 - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight());
	        double z = this.blockPos.getZ() + 0.5 - this.mc.thePlayer.posZ;
	        double distance = MathHelper.sqrt_double((x * x) + (z * z));
	        float yaw = (float) (MathHelper.atan2(z, x) * 180 / Math.PI) - 90;
	        float pitch = (float) (-(MathHelper.atan2(y, distance) * 180 / Math.PI));
			
			if (this.rotationModeValue.mode.equals("Smooth"))
			{
				yaw = this.getSmoothYaw(yaw);
				pitch = this.getSmoothPitch(pitch);
				float[] rotations = RotationUtils.getFixedGCD(yaw, pitch);
				e.yaw = rotations[0];
				e.pitch = rotations[1];
				this.mc.thePlayer.renderYawOffset = rotations[0];
			}
			
			else
			{
				this.prevYaw = yaw;
				this.prevPitch = pitch;
				float[] rotations = RotationUtils.getFixedGCD(yaw, pitch);
				e.yaw = rotations[0];
				e.pitch = rotations[1];
				this.mc.thePlayer.renderYawOffset = rotations[0];
			}
		}
	}
	
	private void place(BlockPos blockPos, EnumFacing enumFacing)
	{
		if (this.rayCast.isOn() && this.rayTrace(this.prevYaw, this.prevPitch) == null)
		{
			return;
		}
		
		switch (enumFacing)
		{
			case UP:
			{
				blockPos = blockPos.add(0, -1, 0);
				break;
			}
			
			case NORTH:
			{
				blockPos = blockPos.add(0, 0, 1);
				break;
			}
			
			case EAST:
			{
				blockPos = blockPos.add(-1, 0, 0);
				break;
			}
			
			case SOUTH:
			{
				blockPos = blockPos.add(0, 0, -1);
				break;
			}
			
			case WEST:
			{
				blockPos = blockPos.add(1, 0, 0);
				break;
			}
			
			default:
			{
				break;
			}
		}
		
		this.blockPos = blockPos;
		
		if (this.realClick.isOn())
		{
			if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)
			{
				mc.rightClickMouse(this.blockPos, new Vec3(0.5, 0.5, 0.5), enumFacing);
			}
			
			else
			{
				this.blockPos = null;
			}
		}
		
		else
		{
			if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)
			{	
				if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockPos, enumFacing, new Vec3(0.5, 0.5, 0.5)))
				{
					if (this.swing.isOn())
					{
						mc.thePlayer.swingItem();
					}
					
					else
					{
						mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
					}
				}
			}
			
			else
			{
				this.blockPos = null;
			}
		}
	}
	
	private float getSmoothYaw(float yaw)
	{
		if (this.prevYaw < yaw)
		{
			this.prevYaw += (yaw - this.prevYaw) / 4;
		}
		
		if (yaw < this.prevYaw)
		{
			this.prevYaw -= (this.prevYaw - yaw) / 4;
		}
		
		return this.prevYaw;
	}
	
	private float getSmoothPitch(float pitch)
	{
		if (this.prevPitch < pitch)
		{
			this.prevPitch += (pitch - this.prevPitch) / 4;
		}
		
		if (pitch < this.prevPitch)
		{
			this.prevPitch -= (this.prevPitch - pitch) / 4;
		}
		
		return this.prevPitch;
	}
	
	private boolean isValid(BlockPos blockPos)
	{
		Block block = mc.theWorld.getBlockAt(blockPos);
		return !(block instanceof BlockLiquid) && block.getMaterial() != Material.air;
	}
	
	@Override
	public void onDisable()
	{
		on = false;
		super.onDisable();
		
		if (this.swapToBlocksAutomatically.isOn())
		{
			mc.thePlayer.inventory.currentItem = this.previousSlot;
		}
	}
}