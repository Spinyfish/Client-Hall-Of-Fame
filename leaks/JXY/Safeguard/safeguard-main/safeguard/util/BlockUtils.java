package intentions.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import optifine.Reflector;
import optifine.ReflectorClass;
import optifine.ReflectorMethod;

public class BlockUtils {
   private List<Block> invalid;
   public static Minecraft mc;
   private static ReflectorClass ForgeBlock = new ReflectorClass(Block.class);
   private static ReflectorMethod ForgeBlock_setLightOpacity;
   private static boolean directAccessValid;

	public static float[] getFacePos(Vec3 vec) {
		double diffX = vec.xCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
		double diffY = vec.yCoord + 0.5
				- (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
		double diffZ = vec.zCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		float p = Minecraft.getMinecraft().thePlayer.rotationPitch
				+ MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch);
		return new float[] {
				Minecraft.getMinecraft().thePlayer.rotationYaw
						+ MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw),
				p};
	}
   
   public Block getBlockByIDorName(String var1) {
      Block var2 = null;

      try {
         var2 = Block.getBlockById(Integer.parseInt(var1));
      } catch (NumberFormatException var8) {
         Block var4 = null;
         Iterator var6 = Block.blockRegistry.iterator();

         while(var6.hasNext()) {
            Object var5 = var6.next();
            var4 = (Block)var5;
            String var7 = var4.getLocalizedName().replace(" ", "");
            if (var7.toLowerCase().startsWith(var1) || var7.toLowerCase().contains(var1)) {
               break;
            }
         }

         if (var4 != null) {
            var2 = var4;
         }
      }

      return var2;
   }

   public static boolean isOnIce() {
      if (mc.thePlayer == null) {
         return false;
      } else {
         boolean var0 = false;
         int var1 = (int)mc.thePlayer.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;

         for(int var2 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); var2 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++var2) {
            for(int var3 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); var3 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++var3) {
               Block var4 = getBlock(var2, var1, var3);
               if (var4 != null && !(var4 instanceof BlockAir)) {
                  if (!(var4 instanceof BlockIce) && !(var4 instanceof BlockPackedIce)) {
                     return false;
                  }

                  var0 = true;
               }
            }
         }

         return var0;
      }
   }

   public static boolean isOnLadder() {
      if (mc.thePlayer == null) {
         return false;
      } else {
         boolean var0 = false;
         int var1 = (int)mc.thePlayer.getEntityBoundingBox().offset(0.0D, 1.0D, 0.0D).minY;

         for(int var2 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); var2 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++var2) {
            for(int var3 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); var3 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++var3) {
               Block var4 = getBlock(var2, var1, var3);
               if (var4 != null && !(var4 instanceof BlockAir)) {
                  if (!(var4 instanceof BlockLadder) && !(var4 instanceof BlockVine)) {
                     return false;
                  }

                  var0 = true;
               }
            }
         }

         return var0 || mc.thePlayer.isOnLadder();
      }
   }

   public static boolean canSeeBlock(float var0, float var1, float var2) {
      return getFacing(new BlockPos((double)var0, (double)var1, (double)var2)) != null;
   }

   public static Block getBlockAtPosC(EntityPlayer var0, double var1, double var3, double var5) {
      return getBlock(new BlockPos(var0.posX - var1, var0.posY - var3, var0.posZ - var5));
   }

   public static BlockUtils.BlockData getBlockData(BlockPos var0, List var1) {
      return !var1.contains(mc.theWorld.getBlockState(var0.add(0, -1, 0)).getBlock()) ? new BlockUtils.BlockData(var0.add(0, -1, 0), EnumFacing.UP) : (!var1.contains(mc.theWorld.getBlockState(var0.add(-1, 0, 0)).getBlock()) ? new BlockUtils.BlockData(var0.add(-1, 0, 0), EnumFacing.EAST) : (!var1.contains(mc.theWorld.getBlockState(var0.add(1, 0, 0)).getBlock()) ? new BlockUtils.BlockData(var0.add(1, 0, 0), EnumFacing.WEST) : (!var1.contains(mc.theWorld.getBlockState(var0.add(0, 0, -1)).getBlock()) ? new BlockUtils.BlockData(var0.add(0, 0, -1), EnumFacing.SOUTH) : (!var1.contains(mc.theWorld.getBlockState(var0.add(0, 0, 1)).getBlock()) ? new BlockUtils.BlockData(var0.add(0, 0, 1), EnumFacing.NORTH) : null))));
   }

   public static EnumFacing getFacing(BlockPos var0) {
      EnumFacing[] var1 = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.DOWN};
      EnumFacing[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing var5 = var2[var4];
         EntitySnowball var6 = new EntitySnowball(mc.theWorld);
         var6.posX = (double)var0.getX() + 0.5D;
         var6.posY = (double)var0.getY() + 0.5D;
         var6.posZ = (double)var0.getZ() + 0.5D;
         var6.posX += (double)var5.getDirectionVec().getX() * 0.5D;
         var6.posY += (double)var5.getDirectionVec().getY() * 0.5D;
         var6.posZ += (double)var5.getDirectionVec().getZ() * 0.5D;
         if (mc.thePlayer.canEntityBeSeen(var6)) {
            return var5;
         }
      }

      return null;
   }

   public BlockUtils.BlockData getBlockData1(BlockPos var1) {
      List var2 = this.invalid;
      Minecraft.getMinecraft();
      if (!var2.contains(mc.theWorld.getBlockState(var1.add(0, -1, 0)).getBlock())) {
         return new BlockUtils.BlockData(var1.add(0, -1, 0), EnumFacing.UP);
      } else {
         Minecraft.getMinecraft();
         if (!var2.contains(mc.theWorld.getBlockState(var1.add(-1, 0, 0)).getBlock())) {
            return new BlockUtils.BlockData(var1.add(-1, 0, 0), EnumFacing.EAST);
         } else {
            Minecraft.getMinecraft();
            if (!var2.contains(mc.theWorld.getBlockState(var1.add(1, 0, 0)).getBlock())) {
               return new BlockUtils.BlockData(var1.add(1, 0, 0), EnumFacing.WEST);
            } else {
               Minecraft.getMinecraft();
               if (!var2.contains(mc.theWorld.getBlockState(var1.add(0, 0, -1)).getBlock())) {
                  return new BlockUtils.BlockData(var1.add(0, 0, -1), EnumFacing.SOUTH);
               } else {
                  Minecraft.getMinecraft();
                  if (!var2.contains(mc.theWorld.getBlockState(var1.add(0, 0, 1)).getBlock())) {
                     return new BlockUtils.BlockData(var1.add(0, 0, 1), EnumFacing.NORTH);
                  } else {
                     BlockPos var3 = var1.add(-1, 0, 0);
                     Minecraft.getMinecraft();
                     if (!var2.contains(mc.theWorld.getBlockState(var3.add(-1, 0, 0)).getBlock())) {
                        return new BlockUtils.BlockData(var3.add(-1, 0, 0), EnumFacing.EAST);
                     } else {
                        Minecraft.getMinecraft();
                        if (!var2.contains(mc.theWorld.getBlockState(var3.add(1, 0, 0)).getBlock())) {
                           return new BlockUtils.BlockData(var3.add(1, 0, 0), EnumFacing.WEST);
                        } else {
                           Minecraft.getMinecraft();
                           if (!var2.contains(mc.theWorld.getBlockState(var3.add(0, 0, -1)).getBlock())) {
                              return new BlockUtils.BlockData(var3.add(0, 0, -1), EnumFacing.SOUTH);
                           } else {
                              Minecraft.getMinecraft();
                              if (!var2.contains(mc.theWorld.getBlockState(var3.add(0, 0, 1)).getBlock())) {
                                 return new BlockUtils.BlockData(var3.add(0, 0, 1), EnumFacing.NORTH);
                              } else {
                                 BlockPos var4 = var1.add(1, 0, 0);
                                 Minecraft.getMinecraft();
                                 if (!var2.contains(mc.theWorld.getBlockState(var4.add(-1, 0, 0)).getBlock())) {
                                    return new BlockUtils.BlockData(var4.add(-1, 0, 0), EnumFacing.EAST);
                                 } else {
                                    Minecraft.getMinecraft();
                                    if (!var2.contains(mc.theWorld.getBlockState(var4.add(1, 0, 0)).getBlock())) {
                                       return new BlockUtils.BlockData(var4.add(1, 0, 0), EnumFacing.WEST);
                                    } else {
                                       Minecraft.getMinecraft();
                                       if (!var2.contains(mc.theWorld.getBlockState(var4.add(0, 0, -1)).getBlock())) {
                                          return new BlockUtils.BlockData(var4.add(0, 0, -1), EnumFacing.SOUTH);
                                       } else {
                                          Minecraft.getMinecraft();
                                          if (!var2.contains(mc.theWorld.getBlockState(var4.add(0, 0, 1)).getBlock())) {
                                             return new BlockUtils.BlockData(var4.add(0, 0, 1), EnumFacing.NORTH);
                                          } else {
                                             BlockPos var5 = var1.add(0, 0, -1);
                                             Minecraft.getMinecraft();
                                             if (!var2.contains(mc.theWorld.getBlockState(var5.add(-1, 0, 0)).getBlock())) {
                                                return new BlockUtils.BlockData(var5.add(-1, 0, 0), EnumFacing.EAST);
                                             } else {
                                                Minecraft.getMinecraft();
                                                if (!var2.contains(mc.theWorld.getBlockState(var5.add(1, 0, 0)).getBlock())) {
                                                   return new BlockUtils.BlockData(var5.add(1, 0, 0), EnumFacing.WEST);
                                                } else {
                                                   Minecraft.getMinecraft();
                                                   if (!var2.contains(mc.theWorld.getBlockState(var5.add(0, 0, -1)).getBlock())) {
                                                      return new BlockUtils.BlockData(var5.add(0, 0, -1), EnumFacing.SOUTH);
                                                   } else {
                                                      Minecraft.getMinecraft();
                                                      if (!var2.contains(mc.theWorld.getBlockState(var5.add(0, 0, 1)).getBlock())) {
                                                         return new BlockUtils.BlockData(var5.add(0, 0, 1), EnumFacing.NORTH);
                                                      } else {
                                                         BlockPos var6 = var1.add(0, 0, 1);
                                                         Minecraft.getMinecraft();
                                                         if (!var2.contains(mc.theWorld.getBlockState(var6.add(-1, 0, 0)).getBlock())) {
                                                            return new BlockUtils.BlockData(var6.add(-1, 0, 0), EnumFacing.EAST);
                                                         } else {
                                                            Minecraft.getMinecraft();
                                                            if (!var2.contains(mc.theWorld.getBlockState(var6.add(1, 0, 0)).getBlock())) {
                                                               return new BlockUtils.BlockData(var6.add(1, 0, 0), EnumFacing.WEST);
                                                            } else {
                                                               Minecraft.getMinecraft();
                                                               if (!var2.contains(mc.theWorld.getBlockState(var6.add(0, 0, -1)).getBlock())) {
                                                                  return new BlockUtils.BlockData(var6.add(0, 0, -1), EnumFacing.SOUTH);
                                                               } else {
                                                                  Minecraft.getMinecraft();
                                                                  if (!var2.contains(mc.theWorld.getBlockState(var6.add(0, 0, 1)).getBlock())) {
                                                                     return new BlockUtils.BlockData(var6.add(0, 0, 1), EnumFacing.NORTH);
                                                                  } else {
                                                                     Object var7 = null;
                                                                     return (BlockUtils.BlockData)var7;
                                                                  }
                                                               }
                                                            }
                                                         }
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static float changeRotation(float var0, float var1, float var2) {
      float var3 = MathHelper.wrapAngleTo180_float(var1 - var0);
      if (var3 > var2) {
         var3 = var2;
      }

      if (var3 < -var2) {
         var3 = -var2;
      }

      return var0 + var3;
   }

   public static float[] getFacingRotations(int var0, int var1, int var2, EnumFacing var3) {
      EntitySnowball var4 = new EntitySnowball(mc.theWorld);
      var4.posX = (double)var0 + 0.5D;
      var4.posY = (double)var1 + 0.5D;
      var4.posZ = (double)var2 + 0.5D;
      var4.posX += (double)var3.getDirectionVec().getX() * 0.25D;
      var4.posY += (double)var3.getDirectionVec().getY() * 0.25D;
      var4.posZ += (double)var3.getDirectionVec().getZ() * 0.25D;
      return faceTarget(var4, 100.0F, 100.0F, false);
   }

   public void onEvent(Event var1) {
      if (var1 instanceof EventMotion) {
         EventMotion var2 = (EventMotion)var1;
      }

   }

   public static Block getBlockAbovePlayer(EntityPlayer var0, double var1) {
      return getBlock(new BlockPos(var0.posX, var0.posY + (double)var0.height + var1, var0.posZ));
   }

   public int getBlockSlot() {
      for(int var1 = 36; var1 < 45; ++var1) {
         Minecraft.getMinecraft();
         ItemStack var2 = mc.thePlayer.inventoryContainer.getSlot(var1).getStack();
         if (var2 != null && var2.getItem() instanceof ItemBlock) {
            return var1 - 36;
         }
      }

      return -1;
   }

   public boolean isInsideBlock() {
      for(int var1 = MathHelper.floor_double(mc.thePlayer.boundingBox.minX); var1 < MathHelper.floor_double(mc.thePlayer.boundingBox.maxX) + 1; ++var1) {
         for(int var2 = MathHelper.floor_double(mc.thePlayer.boundingBox.minY); var2 < MathHelper.floor_double(mc.thePlayer.boundingBox.maxY) + 1; ++var2) {
            for(int var3 = MathHelper.floor_double(mc.thePlayer.boundingBox.minZ); var3 < MathHelper.floor_double(mc.thePlayer.boundingBox.maxZ) + 1; ++var3) {
               Block var4 = mc.theWorld.getBlockState(new BlockPos(var1, var2, var3)).getBlock();
               AxisAlignedBB var5;
               if (var4 != null && !(var4 instanceof BlockAir) && (var5 = var4.getCollisionBoundingBox(mc.theWorld, new BlockPos(var1, var2, var3), mc.theWorld.getBlockState(new BlockPos(var1, var2, var3)))) != null && mc.thePlayer.boundingBox.intersectsWith(var5)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean isOnLiquid() {
      boolean var0 = false;
      if (getBlockAtPosC(mc.thePlayer, 0.30000001192092896D, 0.10000000149011612D, 0.30000001192092896D).getMaterial().isLiquid() && getBlockAtPosC(mc.thePlayer, -0.30000001192092896D, 0.10000000149011612D, -0.30000001192092896D).getMaterial().isLiquid()) {
         var0 = true;
      }

      return var0;
   }

   public static void setLightOpacity(Block var0, int var1) {
      if (directAccessValid) {
         try {
            var0.setLightOpacity(var1);
            return;
         } catch (IllegalAccessError var3) {
            directAccessValid = false;
            if (!ForgeBlock_setLightOpacity.exists()) {
               throw var3;
            }
         }
      }

      Reflector.callVoid(var0, ForgeBlock_setLightOpacity, var1);
   }

   static {
      ForgeBlock_setLightOpacity = new ReflectorMethod(ForgeBlock, "setLightOpacity");
      directAccessValid = true;
   }

   public int getBestSlot() {
      Minecraft.getMinecraft();
      if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
         Minecraft.getMinecraft();
         return mc.thePlayer.inventory.currentItem;
      } else {
         for(int var1 = 0; var1 < 8; ++var1) {
            Minecraft.getMinecraft();
            if (mc.thePlayer.inventory.getStackInSlot(var1) != null) {
               Minecraft.getMinecraft();
               if (mc.thePlayer.inventory.getStackInSlot(var1).getItem() instanceof ItemBlock) {
                  return var1;
               }
            }
         }

         return -1;
      }
   }

   public static Block getBlockUnderPlayer(EntityPlayer var0, double var1) {
      return getBlock(new BlockPos(var0.posX, var0.posY - var1, var0.posZ));
   }

   public static Block getBlock(BlockPos var0) {
      return mc.theWorld.getBlockState(var0).getBlock();
   }

   public static Block getBlock(int var0, int var1, int var2) {
      return mc.theWorld.getBlockState(new BlockPos(var0, var1, var2)).getBlock();
   }

   public static float[] getAngles(EntityPlayerSP var0, BlockPos var1) {
      double var2 = (double)var1.getX() + 0.5D - var0.posX;
      double var4 = (double)var1.getY() - var0.posY + (double)var0.getEyeHeight();
      double var6 = (double)var1.getZ() + 0.5D - var0.posZ;
      double var8 = Math.sqrt(var2 * var2 + var6 * var6);
      float var10 = (float)(Math.atan2(var6, var2) * 180.0D / 3.141592653589793D) - 90.0F;
      float var11 = (float)(-(Math.atan2(var4, var8) * 180.0D / 3.141592653589793D));
      return new float[]{var10, var11};
   }

   public static float[] faceTarget(Entity var0, float var1, float var2, boolean var3) {
      double var6 = var0.posX - mc.thePlayer.posX;
      double var8 = var0.posZ - mc.thePlayer.posZ;
      double var4;
      if (var0 instanceof EntityLivingBase) {
         EntityLivingBase var10 = (EntityLivingBase)var0;
         var4 = var10.posY + (double)var10.getEyeHeight() - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
      } else {
         var4 = (var0.getEntityBoundingBox().minY + var0.getEntityBoundingBox().maxY) / 2.0D - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
      }

      new Random();
      double var11 = (double)MathHelper.sqrt_double(var6 * var6 + var8 * var8);
      float var13 = (float)(Math.atan2(var8, var6) * 180.0D / 3.141592653589793D) - 90.0F;
      float var14 = (float)(-(Math.atan2(var4 - (var0 instanceof EntityPlayer ? 0.25D : 0.0D), var11) * 180.0D / 3.141592653589793D));
      float var15 = changeRotation(mc.thePlayer.rotationPitch, var14, var2);
      float var16 = changeRotation(mc.thePlayer.rotationYaw, var13, var1);
      return new float[]{var16, var15};
   }

   public static boolean isBlockUnderPlayer(Material var0, float var1) {
      return getBlockAtPosC(mc.thePlayer, 0.3100000023841858D, (double)var1, 0.3100000023841858D).getMaterial() == var0 && getBlockAtPosC(mc.thePlayer, -0.3100000023841858D, (double)var1, -0.3100000023841858D).getMaterial() == var0 && getBlockAtPosC(mc.thePlayer, -0.3100000023841858D, (double)var1, 0.3100000023841858D).getMaterial() == var0 && getBlockAtPosC(mc.thePlayer, 0.3100000023841858D, (double)var1, -0.3100000023841858D).getMaterial() == var0;
   }

   public BlockUtils() {
      this.invalid = Arrays.asList(Blocks.air, Blocks.water, Blocks.fire, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.chest, Blocks.anvil, Blocks.enchanting_table);
   }

   public static class BlockData {
      public EnumFacing face;
      public BlockPos position;

      public BlockData(BlockPos var1, EnumFacing var2) {
         this.position = var1;
         this.face = var2;
      }
   }

	public static boolean isLiquidBlock(Block block) {
		return block == Blocks.water || block == Blocks.lava || block == Blocks.flowing_water || block == Blocks.flowing_lava;
	}

	public static boolean isNotSolid(Block b) {
		return b != Blocks.grass && !(b instanceof BlockFlower) && b != Blocks.tallgrass;
	}
	
	public static Vec3 getVec3(BlockPos blockPos) {
		return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public static Block[] getBlocksBelow() {
		Block[] blocks = new Block[9];
		
		double expand = 0.3;
		int runs = 0;
		
		for(double x=-expand;x<=expand;x+=expand) {
			for(double z=-expand;z<=expand;z+=expand) {
				try {
					Block b = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY - 0.5001, mc.thePlayer.posZ + z)).getBlock();
					if(b != null) {
						blocks[runs] = b;
						runs++;
					}
				} catch(NullPointerException e) {
					
				}
			}
		}
		
		return blocks;
	}

	public static Block[] getBlocksBelowEntity(EntityOtherPlayerMP p) {
		Block[] blocks = new Block[9];
		
		double expand = 0.3;
		int runs = 0;
		
		for(double x=-expand;x<=expand;x+=expand) {
			for(double z=-expand;z<=expand;z+=expand) {
				try {
					Block b = mc.theWorld.getBlockState(new BlockPos(p.posX + x, p.posY - 0.5001, p.posZ + z)).getBlock();
					if(b != null) {
						blocks[runs] = b;
						runs++;
					}
				} catch(NullPointerException e) {
					
				}
			}
		}
		
		return blocks;
	}

	public static boolean isIce(Block b) {
		return b.getMaterial() == Material.ice || b.getMaterial() == Material.packedIce;
	}
}