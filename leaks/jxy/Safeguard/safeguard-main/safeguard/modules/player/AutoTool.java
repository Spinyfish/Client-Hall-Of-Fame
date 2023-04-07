package intentions.modules.player;

import org.lwjgl.input.Mouse;

import intentions.Client;
import intentions.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class AutoTool extends Module {

	private int oldSlot;
	private boolean isActive = false;

	public AutoTool() {
		super("AutoTool", 0, Category.PLAYER, "Automatically selects the tool to break blocks the fastest", true);
	}
	
	public void onUpdate()
	{
		if(!this.toggled)return;
		if(Mouse.isButtonDown(0)) {
            onLeftClick();
        }
		if(!mc.gameSettings.keyBindAttack.pressed && isActive)
		{
			isActive = false;
			mc.thePlayer.inventory.currentItem = oldSlot;
		}else if(isActive && mc.objectMouseOver != null
			&& mc.objectMouseOver.getBlockPos() != null
			&& mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos())
				.getBlock().getMaterial() != Material.air)
			setSlot(mc.objectMouseOver.getBlockPos());
	}
	
	@Override
	public void onDisable()
	{
		isActive = false;
		mc.thePlayer.inventory.currentItem = oldSlot;
	}
	
	public void onLeftClick()
	{
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		if(mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()
			.getMaterial() != Material.air)
		{
			isActive = true;
			oldSlot = mc.thePlayer.inventory.currentItem;
			setSlot(mc.objectMouseOver.getBlockPos());
		}
	}
	
	public void setSlot(BlockPos blockPos)
	{
		float bestSpeed = 1F;
		int bestSlot = -1;
		Block block = mc.theWorld.getBlockState(blockPos).getBlock();
		for(int i = 0; i < 9; i++)
		{
			ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
			if(item == null)
				continue;
			float speed = item.getStrVsBlock(block);
			if(speed > bestSpeed)
			{
				bestSpeed = speed;
				bestSlot = i;
			}
		}
		if(bestSlot != -1)
			mc.thePlayer.inventory.currentItem = bestSlot;
	}
	
}
