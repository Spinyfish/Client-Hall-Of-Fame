package byron.Mono.module.impl.movement;

import byron.Mono.Mono;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.Event;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.font.FontUtil;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.TimeUtil;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@ModuleInterface(name = "Scaffold", description = "Block placement.", category = Category.Movement)
public class Scaffold extends Module {
    public boolean rel = false;
    private final List invalidBlocks;
    private final List validBlocks;
    private final BlockPos[] blockPositions;
    private final EnumFacing[] facings;
    private final Random rng;
    private float[] angles;
    private boolean rotating;
    private int slot;
    private TimeUtil timeUtil = new TimeUtil();

    public Scaffold() {
        this.invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web);
        this.validBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava);
        this.blockPositions = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};
        this.facings = new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH};
        this.rng = new Random();
        this.angles = new float[2];
        rSetting(new Setting("Timer Bypass", this, false));
        rSetting(new Setting("Double Bridge", this, false));
    }
    @Override
    public void onEnable() {
        this.slot = mc.thePlayer.inventory.currentItem;
        super.onEnable();
   
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindUseItem.pressed = false;
        super.onDisable();
        mc.timer.timerSpeed = 1.0F;
        timeUtil.reset();
    }


    @Subscribe
    public void onUpdate(EventUpdate e) {

            EntityPlayerSP player = mc.thePlayer;
            WorldClient world = mc.theWorld;
            double yDif = 9.0D;
            BlockData data = null;
            
            if(getSetting("Double Bridge").getValBoolean())
            {
            	for(double posY = player.posY - 1.0D; posY > 0.0D; --posY) {
                    BlockData newData = this.getBlockData(new BlockPos(player.posX, posY, player.posZ));
                    if (newData != null) {
                        yDif = player.posY - posY;
                        if (yDif <= 3.0D) {
                            data = newData;
                            break;
                        }
                    }
            	}
            }
            else
            {
            	  for(double posY = player.posY + 0.1D; posY > 0.0D; --posY) {
                      BlockData newData = this.getBlockData(new BlockPos(player.posX, posY, player.posZ));
                      if (newData != null) {
                          yDif = player.posY - posY;
                          if (yDif <= 3.0D) {
                              data = newData;
                              break;
                          }
                      }
                  }
            }
          

            if (data != null) {
                BlockPos pos = data.pos;
                Block block = world.getBlockState(pos.offset(data.face)).getBlock();
                Vec3 hitVec = this.getVec3(data);
                if(getSetting("Timer Bypass").getValBoolean())
                {
                	
                	if (timeUtil.hasTimePassed(300))
                	{
                		mc.timer.timerSpeed = 0.24F;
                		
                		if (timeUtil.hasTimePassed(1700))
                    	{
                    		mc.timer.timerSpeed = 1.65F;
                    	}
                	}
                	
                	if (timeUtil.hasTimePassed(1900))
                	{
                		timeUtil.reset();
                	}
                	
            mc.playerController.onPlayerRightClick(player, world, player.getCurrentEquippedItem(), pos, data.face, hitVec);
                }
                else
            {
                mc.playerController.onPlayerRightClick(player, world, player.getCurrentEquippedItem(), pos, data.face, hitVec);
            }
            }
            }
            
            
            


    private Vec3 getVec3(BlockData data) {
        BlockPos pos = data.pos;
        EnumFacing face = data.face;
        double x = (double)pos.getX() + 0.5D;
        double y = (double)pos.getY() + 0.5D;
        double z = (double)pos.getZ() + 0.5D;
        x += (double)face.getFrontOffsetX() / 2.0D;
        z += (double)face.getFrontOffsetZ() / 2.0D;
        y += (double)face.getFrontOffsetY() / 2.0D;
        if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
            y += this.randomNumber(0.49D, 0.5D);
        } else {
            x += this.randomNumber(0.3D, -0.3D);
            z += this.randomNumber(0.3D, -0.3D);
        }

        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += this.randomNumber(0.3D, -0.3D);
        }

        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += this.randomNumber(0.3D, -0.3D);
        }

        return new Vec3(x, y, z);
    }

    private double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    private BlockData getBlockData(BlockPos pos) {
        BlockPos[] blockPositions = this.blockPositions;
        EnumFacing[] facings = this.facings;
        WorldClient world = mc.theWorld;
        BlockPos posBelow = new BlockPos(0, -1, 0);
        List validBlocks = this.validBlocks;
        if (!validBlocks.contains(world.getBlockState(pos.add(posBelow)).getBlock())) {
            return new BlockData(pos.add(posBelow), EnumFacing.UP);
        } else {
            int i = 0;

            for(int blockPositionsLength = blockPositions.length; i < blockPositionsLength; ++i) {
                BlockPos blockPos = pos.add(blockPositions[i]);
                if (!validBlocks.contains(world.getBlockState(blockPos).getBlock())) {
                    return new BlockData(blockPos, facings[i]);
                }

                for(int i1 = 0; i1 < blockPositionsLength; ++i1) {
                    BlockPos blockPos1 = pos.add(blockPositions[i1]);
                    BlockPos blockPos2 = blockPos.add(blockPositions[i1]);
                    if (!validBlocks.contains(world.getBlockState(blockPos1).getBlock())) {
                        return new BlockData(blockPos1, facings[i1]);
                    }

                    if (!validBlocks.contains(world.getBlockState(blockPos2).getBlock())) {
                        return new BlockData(blockPos2, facings[i1]);
                    }
                }
            }

            return null;
        }
    }
}
