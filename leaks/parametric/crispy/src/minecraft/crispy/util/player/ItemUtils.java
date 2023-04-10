package crispy.util.player;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ItemUtils {
    public static Random random = new Random();
    public static Minecraft mc = Minecraft.getMinecraft();
    public static RenderItem renderItem;
    public static long tick;
    public static double rotation;
    public static final ResourceLocation RES_ITEM_GLINT;

    static {
        renderItem = mc.getRenderItem();
        RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    }

    public static void doRender(Entity par1Entity, double x, double y, double z, float par8, float par9) {
        rotation = (double)(System.nanoTime() - tick) / 3000000.0D * 1.0D;
        if (!mc.inGameHasFocus) {
            rotation = 0.0D;
        }

        EntityItem item = (EntityItem)par1Entity;
        ItemStack itemstack = item.getEntityItem();
        if (itemstack.getItem() != null) {
            random.setSeed(187L);
            boolean flag = false;
            if (TextureMap.locationBlocksTexture != null) {
                mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
                flag = true;
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            IBakedModel ibakedmodel = renderItem.getItemModelMesher().getItemModel(itemstack);
            int i = func_177077_a(item, x, y, z, par9, ibakedmodel);
            BlockPos pos = new BlockPos(item);
            if (item.rotationPitch > 360.0F) {
                item.rotationPitch = 0.0F;
            }

            if (item != null && !Double.isNaN((double)item.getAge()) && !Double.isNaN((double)item.getAir()) && !Double.isNaN((double)item.getEntityId()) && item.getPosition() != null) {
                if (item.onGround) {
                    if (item.rotationPitch != 0.0F && item.rotationPitch != 90.0F && item.rotationPitch != 180.0F && item.rotationPitch != 270.0F) {
                        double Abstand0 = formPositiv(item.rotationPitch);
                        double Abstand90 = formPositiv(item.rotationPitch - 90.0F);
                        double Abstand180 = formPositiv(item.rotationPitch - 180.0F);
                        double Abstand270 = formPositiv(item.rotationPitch - 270.0F);
                        if (Abstand0 <= Abstand90 && Abstand0 <= Abstand180 && Abstand0 <= Abstand270) {
                            if (item.rotationPitch < 0.0F) {
                                item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                            } else {
                                item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                            }
                        }

                        if (Abstand90 < Abstand0 && Abstand90 <= Abstand180 && Abstand90 <= Abstand270) {
                            if (item.rotationPitch - 90.0F < 0.0F) {
                                item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                            } else {
                                item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                            }
                        }

                        if (Abstand180 < Abstand90 && Abstand180 < Abstand0 && Abstand180 <= Abstand270) {
                            if (item.rotationPitch - 180.0F < 0.0F) {
                                item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                            } else {
                                item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                            }
                        }

                        if (Abstand270 < Abstand90 && Abstand270 < Abstand180 && Abstand270 < Abstand0) {
                            if (item.rotationPitch - 270.0F < 0.0F) {
                                item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                            } else {
                                item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                            }
                        }
                    }
                } else {
                    BlockPos posUp = new BlockPos(item);
                    posUp.add(0, 1, 0);
                    Material m1 = item.worldObj.getBlockState(posUp).getBlock().getMaterial();
                    Material m2 = item.worldObj.getBlockState(pos).getBlock().getMaterial();
                    boolean m3 = item.isInsideOfMaterial(Material.water);
                    boolean m4 = item.isInWater();
                    if (m3 | m1 == Material.water | m2 == Material.water | m4) {
                        item.rotationPitch = (float)((double)item.rotationPitch + rotation / 4.0D);
                    } else {
                        item.rotationPitch = (float)((double)item.rotationPitch + rotation * 2.0D);
                    }
                }
            }

            GL11.glRotatef(item.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(item.rotationPitch + 90.0F, 1.0F, 0.0F, 0.0F);

            for(int j = 0; j < i; ++j) {
                if (ibakedmodel.isAmbientOcclusion()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    renderItem.renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();
                    if (j > 0 && shouldSpreadItems()) {
                        GlStateManager.translate(0.0F, 0.0F, 0.046875F * (float)j);
                    }

                    renderItem.renderItem(itemstack, ibakedmodel);
                    if (!shouldSpreadItems()) {
                        GlStateManager.translate(0.0F, 0.0F, 0.046875F);
                    }

                    GlStateManager.popMatrix();
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            if (flag) {
                mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
            }
        }

    }

    public static int func_177077_a(EntityItem item, double x, double y, double z, float p_177077_8_, IBakedModel p_177077_9_) {
        ItemStack itemstack = item.getEntityItem();
        Item item2 = itemstack.getItem();
        if (item2 == null) {
            return 0;
        } else {
            boolean flag = p_177077_9_.isAmbientOcclusion();
            int i = func_177078_a(itemstack);
            float f1 = 0.25F;
            float f2 = 0.0F;
            GlStateManager.translate((float)x, (float)y + f2 + 0.03F, (float)z);
            float f3 = 0.0F;
            if (flag || mc.getRenderManager().renderEngine != null && mc.gameSettings.fancyGraphics) {
                GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            }

            if (!flag) {
                f3 = -0.0F * (float)(i - 1) * 0.5F;
                float f4 = -0.0F * (float)(i - 1) * 0.5F;
                float f5 = -0.046875F * (float)(i - 1) * 0.5F;
                GlStateManager.translate(f3, f4, f5);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }

    public static boolean shouldSpreadItems() {
        return true;
    }

    public static double formPositiv(float rotationPitch) {
        return rotationPitch > 0.0F ? (double)rotationPitch : (double)(-rotationPitch);
    }

    public static int func_177078_a(ItemStack stack) {
        byte b0 = 1;
        if (stack.animationsToGo > 48) {
            b0 = 5;
        } else if (stack.animationsToGo > 32) {
            b0 = 4;
        } else if (stack.animationsToGo > 16) {
            b0 = 3;
        } else if (stack.animationsToGo > 1) {
            b0 = 2;
        }

        return b0;
    }

    public static byte getMiniBlockCount(ItemStack stack, byte original) {
        return original;
    }

    public static byte getMiniItemCount(ItemStack stack, byte original) {
        return original;
    }
}