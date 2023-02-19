package alos.stella.module.modules.visual;

import alos.stella.Stella;
import alos.stella.event.EventTarget;
import alos.stella.event.events.Render2DEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.module.modules.combat.KillAura;
import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.ColorUtils;
import alos.stella.utils.render.DrawUtils;
import alos.stella.value.FloatValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.min;

@ModuleInfo(name = "TargetHUD", description = "undefiend.", category = ModuleCategory.VISUAL)
public class TargetHUD extends Module {
    //lnk
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Simplicity","LiquidSense", "Astolfo", "Novoline","Exhibition"}, "LiquidSense");
    private final FloatValue scale = new FloatValue("Scale", 1.0f, 0.1f, 2.0f);
    private final IntegerValue customX = new IntegerValue("X", 0, -500, 500);
    private final IntegerValue customY = new IntegerValue("Y", 0, -500, 500);
    private final IntegerValue redValue = new IntegerValue("R", 125, 0, 255);
    private final IntegerValue greenValue = new IntegerValue("G", 125, 0, 255);
    private final IntegerValue blueValue = new IntegerValue("B", 225, 0, 255);

    double animation = 0;
    float width = 0;

    final KillAura ka = (KillAura) Stella.moduleManager.getModule(KillAura.class);
    public static boolean chat;

    public EntityLivingBase getTarget () {
        if (mc.currentScreen instanceof GuiChat){
            chat = true;
        }else {
            chat = false;
        }
        if (ka.targetHUD == true || chat) {
            return (EntityLivingBase) ka.closeEntity();
        } else {
            return null;
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        EntityLivingBase entity = getTarget();
        ScaledResolution res = new ScaledResolution(mc);
        int x = res.getScaledWidth() / 2;
        int y = res.getScaledHeight() / 2;

        final KillAura ka = (KillAura) Stella.moduleManager.getModule(KillAura.class);
        EntityPlayer ent = (EntityPlayer) getTarget();
        if (entity != null) {
            // Simlicity
            if (modeValue.get().equalsIgnoreCase("Simplicity")) {
                GlStateManager.pushMatrix();
                double width = 100f;
                width = getIncremental(width, -50);

                GlStateManager.scale(scale.get(), scale.get(), scale.get());
                Fonts.fontSFUI35.drawStringWithShadow("\247l" + entity.getName(), customX.get() + x + 38, customY.get() + y + 2.0f, -1);

                final float health = entity.getHealth();
                final float progress = health / entity.getMaxHealth();

                if (width < 80.0) {
                    width = 80.0;
                }

                if (width > 80.0) {
                    width = 80.0;
                }

                final double healthLocation = width * progress;

                if (animation < healthLocation + 5) {
                    animation++;
                }

                if (animation > healthLocation + 2) {
                    animation--;
                }

                DrawUtils.drawGradientSideways(customX.get() + x + 37.5, customY.get() + y + 11, customX.get() + x + 37.5 + animation, customY.get() + y + 19, ColorUtils.rainbow(5000000000L).getRGB(), ColorUtils.rainbow(500L).getRGB());
                DrawUtils.drawRectAngleBordered(customX.get() + x + 37.0, customY.get() + y + 10.5, customX.get() + x + 38.0 + animation, customY.get() + y + 19.5, 0.5, getColor(0, 0), getColor(0));

                GlStateManager.resetColor();
                GlStateManager.popMatrix();
            }

            // LiquidSense
            if (modeValue.get().equalsIgnoreCase("LiquidSense")) {
                GlStateManager.pushMatrix();
                double width = mc.fontRendererObj.getStringWidth(ent.getName());
                width = getIncremental(width, -50);

                GlStateManager.scale(scale.get(), scale.get(), scale.get());
                DrawUtils.drawTargetHudRect(customX.get() + x, customY.get() + y - 0.5, customX.get() + x + 80 + width, customY.get() + y + 37.0, getColor(0, 0, 0, 120), getColor(200, 255, 255));
                mc.fontRendererObj.drawStringWithShadow("\247l" + ent.getName(), customX.get() + x + 38, customY.get() + y + 3.0f, -1);

                BigDecimal DT = BigDecimal.valueOf(mc.thePlayer.getDistanceToEntity(ent));
                DT = DT.setScale(1, RoundingMode.HALF_UP);
                double Dis = DT.doubleValue();
                double Food = ent.getFoodStats().getFoodLevel();
                double Armor = entity.getTotalArmorValue();

                final float health = entity.getHealth();
                final float[] fractions = {0.0f, 0.5f, 1.0f};
                final Color[] colors = {Color.RED, Color.YELLOW, Color.GREEN};
                final float progress = health / entity.getMaxHealth();
                final Color customColor = (health >= 0.0f) ? alos.stella.utils.render.ColorUtils.blendColors(fractions, colors, progress).brighter() : Color.RED;

                if (width < 80.0) {
                    width = 80.0;
                }

                if (width > 80.0) {
                    width = 80.0;
                }

                final double healthLocation = width * progress;

                if (animation < healthLocation + 5) {
                    animation++;
                }

                if (animation > healthLocation + 2) {
                    animation--;
                }

                DrawUtils.drawGradientSideways(customX.get() + x + 37.5, customY.get() + y + 13.5, customX.get() + x + 37.5 + animation, customY.get() + y + 16.5, new Color(225, 50, 50).getRGB(), customColor.getRGB());
                DrawUtils.drawRectAngleBordered(customX.get() + x + 37.0, customY.get() + y + 13.5, customX.get() + x + 40.0 + width, customY.get() + y + 15.5 + 1, 0.5, getColor(0, 0), getColor(0));
                DrawUtils.drawFace(customX.get() + x + 3, customY.get() + y + 2, 32, (AbstractClientPlayer) entity);

                GlStateManager.scale(0.5, 0.5, 0.5);
                final String str = "Dist>> " + Dis + "   Food>> " + Food;
                mc.fontRendererObj.drawStringWithShadow(str, (customX.get() + x) * 2 + 76.0f, (customY.get() + y) * 2 + 40.0f, -1);
                final String str2 = String.format("Yaw>> %s", (int) entity.rotationYaw + "   Armor>> " + Armor);
                mc.fontRendererObj.drawStringWithShadow(str2, (customX.get() + x) * 2 + 76.0f, (customY.get() + y) * 2 + 55.0f, -1);
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.popMatrix();
            }

            // Astolfo
            if (modeValue.get().equalsIgnoreCase("Astolfo")) {
                int colors = new Color(redValue.get(), greenValue.get(), blueValue.get(), 255).getRGB();
                int colors1 = new Color(redValue.get(), greenValue.get(), blueValue.get(), 150).getRGB();
                int colors2 = new Color(redValue.get(), greenValue.get(), blueValue.get(), 50).getRGB();

                final float health = getHealthes(entity);
                final float n18 = 125.0f * (health / entity.getMaxHealth());
                animation = getAnimationState(animation, n18, 135.0f);
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (res.getScaledWidth() / 2 + customX.get() + 15), (float) (res.getScaledHeight() - customY.get() - 55), 0.0F);
                GlStateManager.color(1, 1, 1);
                GlStateManager.scale(scale.get(), scale.get(), scale.get());
                if (ka.closeEntity() instanceof EntityPlayer) {
                    GuiInventory.drawEntityOnScreen(-18, 47, 30, -180, 0, entity);
                } else {
                    GuiInventory.drawEntityOnScreen(-20, 50, 30, -180, 0, entity);
                }
                DrawUtils.drawRect(-38.0, -14.0, 133, 52.0, getColor(0, 0, 0, 180));
                mc.fontRendererObj.drawStringWithShadow(entity.getName(), 0.0F, -8.0F, new Color(255, 255, 255).getRGB());
                DrawUtils.drawRect(0.0f, 8.0f + Math.round(40.0f), 130f, 40f, colors2);
                if ((entity.getHealth() / 2.0f + entity.getAbsorptionAmount() / 2.0f) > 1.0) {
                    DrawUtils.drawRect(0.0f, 8.0f + Math.round(40.0f), animation + 5f, 40f, colors1);
                }
                DrawUtils.drawRect(0.0f, 8.0f + Math.round(40.0f), animation, 40f, colors);
                GlStateManager.scale(3f, 3f, 3f);
                mc.fontRendererObj.drawStringWithShadow(getHealthes(entity) + " \u2764", 0.0F, 2.5F, colors);
                GlStateManager.popMatrix();
            }

            // Novoline
            if (modeValue.get().equalsIgnoreCase("Novoline")) {
                int colors233 = new Color(redValue.get(), greenValue.get(), blueValue.get(), 255).getRGB();
                final String healthStr = String.valueOf((int) entity.getHealth() / 2.0f);

                double healthwidth;
                double healthamout;

                //player health
                GL11.glPushMatrix();
                double width = Fonts.minecraftFont.getStringWidth(entity.getName()) > 40 ? 20 + mc.fontRendererObj.getStringWidth(entity.getName()) : 50;
                healthwidth = width + 18;
                healthamout = healthwidth * entity.getHealth() / entity.getMaxHealth();
                healthamout = getAnimationState(healthamout, healthwidth * entity.getHealth() / entity.getMaxHealth(), 200);
                GlStateManager.scale(scale.get(), scale.get(), scale.get());
                DrawUtils.drawRectAngleBordered(customX.get() + x - 9, customY.get() + y, customX.get() + x + 52 + width, customY.get() + y + 37, 1f, new Color(40, 40, 40).getRGB(), new Color(40, 40, 40).getRGB());
                //RenderUtil.rectangleBordered(x + 30, y + 11, x + 30 + healthwidth, y + 15, 0.5f, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0).getRGB());
                DrawUtils.drawRectAngle(customX.get() + x + 30, customY.get() + y + 15, customX.get() + x + 30 + healthwidth, customY.get() + y + 24.5, new Color(90, 90, 90).getRGB());
                DrawUtils.drawRectAngleBordered(customX.get() + x + 30, customY.get() + y + 15, customX.get() + x + 30 + healthamout, customY.get() + y + 24.5, 0f, colors233, new Color(0, 0, 0, 0).getRGB());
                DrawUtils.drawFace(customX.get() + x - 7, customY.get() + y + 2, 33, (AbstractClientPlayer) entity);
                Fonts.minecraftFont.drawStringWithShadow(healthStr + "❤", customX.get() + x + 30f, customY.get() + y + 58f - mc.fontRendererObj.FONT_HEIGHT - 22.2F, colors233);
                Fonts.minecraftFont.drawStringWithShadow(entity.getName(), customX.get() + x + 30, customY.get() + y + 4, new Color(255, 255, 255).getRGB());
                GL11.glPopMatrix();
            }
            if (modeValue.get().equalsIgnoreCase("Exhibition")) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(width / 2.0F + 10.0F - 2.5F, res.getScaledHeight() / 2.0f + 53.0f, 0.0f);
                GlStateManager.scale(scale.get(), scale.get(), scale.get());
                DrawUtils.drawTargetHudRect(0.0D, -2.0D, (Fonts.fontTahoma.getStringWidth(entity.getName()) > 70.0F) ? (124.0F + Fonts.fontTahoma.getStringWidth(entity.getName()) - 70.0F) : 124.0D, 38.0D, 1.0D);
                DrawUtils.drawTargetHudRect1(0.0D, -2.0D, 124.0D, 38.0D, 1.0D);
                Fonts.fontTahoma.drawString(entity.getName(), 42.0F, 0F, -1);
                float health = entity.getHealth();
                float totalHealth = entity.getHealth() + entity.getAbsorptionAmount();
                float[] fractions = { 0.0F, 0.5F, 1.0F };
                Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
                float progress = health / entity.getMaxHealth();
                Color customColor = (health >= 0.0F) ? alos.stella.utils.render.ColorUtils.blendColors(fractions, colors, progress).brighter() : Color.RED;
                double width1 = 60.0D;
                double distWidth = width1 / 10;
//            width1 = Colors.getIncremental(width1, 5.0D);
//            if (width1 < 50.0D)
//                width1 = 50.0D;
                double healthLocation = width1 * progress;
                DrawUtils.drawRectAngle(42.0, 9, 2.0 + width1 + 0.5, 12.0, customColor.darker().darker().darker().getRGB()); // health bar nigga
                DrawUtils.drawRectAngle(42.0, 9, 42.0 + healthLocation + 0.5, 12.0, customColor.getRGB());
                double absorptionPercentage = entity.getAbsorptionAmount() / entity.getMaxHealth();
                if (entity.getAbsorptionAmount() > 0.0F)
                    DrawUtils.drawRectAngle(102.0 - min(width1, absorptionPercentage * width1), 8.8, 102.5, 12.0, (new Color(137, 112, 9)).getRGB());
                DrawUtils.drawRectAngleBordered(41.5, 8.300000190734863, 41.5 + width1 + 1.5, 12.5D, 0.5D, getColor(0, 0), getColor(0));
                for (int dist = 1; dist <= 9; dist++) {
                    double dThing = distWidth * dist;
                    DrawUtils.drawRectAngle(42.0 + dThing, 8.3, 42.0 + dThing + 0.5, 12.5, getColor(0));
                }
                int var18 = (int)mc.thePlayer.getDistanceToEntity((Entity)entity);
                String str = "HP: " + (int)totalHealth + " | Dist: " + var18;
                Fonts.fontTahomaSmall.drawString(str, 42F, 15.0F, -1);
                GlStateManager.scale(0.5D, 0.5D, 0.5D);
                GlStateManager.scale(2.0D, 2.0D, 2.0D);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                if (entity instanceof net.minecraft.entity.player.EntityPlayer)
                    drawArmor(28, 19);
                GlStateManager.scale(0.31D, 0.31D, 0.31D);
                GlStateManager.translate(73.0F, 102.0F, 40.0F);
                model(entity.rotationYaw, entity.rotationPitch, entity);
                GlStateManager.popMatrix();
                
            }
        }
    }

    private float getHealthes(final EntityLivingBase entityLivingBase) {
        return (float)(int)((int)Math.ceil(entityLivingBase.getHealth()) + 0.5f);
    }
    public static double getIncremental(final double val, final double inc) {
        final double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }
    public static double getAnimationState(double animation, double finalState, double speed) {
        float add = (float) (0.01 * speed);
        if (animation < finalState) {
            if (animation + add < finalState)
                animation += add;
            else
                animation = finalState;
        } else {
            if (animation - add > finalState)
                animation -= add;
            else
                animation = finalState;
        }
        return animation;
    }
    public static int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        int color1 = color | alpha << 24;
        color1 |= red << 16;
        color1 |= green << 8;
        return color1 |= blue;
    }
    public static int getColor(int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }
    private void model(float yaw, float pitch, EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 50.0F);
        GlStateManager.scale(-50.0F, 50.0F, 50.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float renderYawOffset = entityLivingBase.renderYawOffset;
        float rotationYaw = entityLivingBase.rotationYaw;
        float rotationPitch = entityLivingBase.rotationPitch;
        float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(-Math.atan((pitch / 40.0F)) * 20.0D), 1.0F, 0.0F, 0.0F);
        entityLivingBase.renderYawOffset = yaw - yaw / yaw * 0.4F;
        entityLivingBase.rotationYaw = yaw - yaw / yaw * 0.2F;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0F);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw((Entity)entityLivingBase, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    private void drawArmor(int x, int y) {
        GL11.glPushMatrix();
        List<ItemStack> stuff = new ArrayList<>();
        int split = -3;
        for (int index = 3; index >= 0; index--) {
            ItemStack armer = mc.thePlayer.inventory.armorInventory[index];
            if (armer != null)
                stuff.add(armer);
        }
        if (mc.thePlayer.getCurrentEquippedItem() != null)
            stuff.add(mc.thePlayer.getCurrentEquippedItem());
        for (ItemStack everything : stuff) {
            if (mc.theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                split += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            (mc.getRenderItem()).zLevel = -150.0F;
            mc.getRenderItem().renderItemIntoGUI(everything, split + x, y);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, everything, split + x, y);
            DrawUtils.renderEnchantText(everything, split + x, y);
            (mc.getRenderItem()).zLevel = 0.0F;
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            everything.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }

}


