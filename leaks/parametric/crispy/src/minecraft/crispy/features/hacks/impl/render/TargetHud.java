package crispy.features.hacks.impl.render;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.event.impl.render.EventRenderGui;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.Aura;
import crispy.fonts.decentfont.FontUtil;
import crispy.fonts.greatfont.TTFFontRenderer;
import crispy.util.animation.AnimationUtils;
import crispy.util.render.ColorUtils;
import crispy.util.render.RenderUtils;
import crispy.util.render.gui.RenderUtil;
import crispy.util.rotation.LookUtils;
import crispy.util.time.Stopwatch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.superblaubeere27.valuesystem.ModeValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.glEnable;

@HackInfo(name = "TargetHUD", category = Category.RENDER)
public class TargetHud extends Hack {

    ModeValue targetMode = new ModeValue("Mode", "New", "Crispy", "Moon", "Exhibition", "New");

    private static final Color COLOR = new Color(0, 0, 0, 180);

    private final Stopwatch animationStopwatch = new Stopwatch();
    private EntityPlayer target;
    private long lastAura;
    private double healthBarWidth;
    private double hudHeight;
    private double otherHealthBarWidth;
    public static double alternateHealth;

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRenderGui) {
            switch (targetMode.getMode()) {
                case "New": {

                    if(target != null && mc.thePlayer != null && Math.abs(System.currentTimeMillis() - lastAura) <= 3000 && target.isEntityAlive()) {
                        float scaledWidth = (float) ScaledResolution.getScaledWidth();
                        float scaledHeight = (float) ScaledResolution.getScaledHeight();
                        float width = 140.0F;
                        float height = 40.0F;
                        float xOffset = 40.0F;
                        float x = scaledWidth / 2.0F - 70.0F;
                        float y = scaledHeight / 2.0F + 80.0F;

                        float health = this.target.getHealth();
                        float armor = this.target.getTotalArmorValue();
                        if(health == 1) {
                            health = (float) alternateHealth;
                        }
                        double armorPercentage = armor / 20;
                        double hpPercentage = health / this.target.getMaxHealth();

                        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                        armorPercentage = MathHelper.clamp_double(armorPercentage, 0.0D, 1.0D);
                        int healthColor = ColorUtils.getHealthColor(health, this.target.getMaxHealth()).getRGB();
                        int otherHealthColor = ColorUtils.getHealthColor(health, this.target.getMaxHealth()).darker().getRGB();
                        RenderUtil.drawRoundedRect(x, y, x + 140.0F, y + 40.0F, 5,new Color(0, 0,0, 100).getRGB());
                        RenderUtil.drawRoundedRect(x, y, x + 140, y + 2, 2, HUD.followColors.getRGB());
                        ResourceLocation resourceLocation = getPlayerSkin(target.getCommandSenderName());

                        double hpWidth = 100 * hpPercentage;
                        double armorWidth = 100 * armorPercentage;
                        if(resourceLocation != null) {
                            GL11.glEnable(GL11.GL_SCISSOR_TEST);
                            RenderUtil.prepareScissorBox(x + 5, y, x + 140, y + 40);
                            mc.getTextureManager().bindTexture(resourceLocation);
                            Gui.drawModalRectWithCustomSizedTexture(x+3, y+6, 30, 30, 30, 30, 30*8, 30*8);
                            GL11.glDisable(GL11.GL_SCISSOR_TEST);
                        }
                        if (this.animationStopwatch.elapsed(15L)) {
                            this.healthBarWidth = hpWidth;
                            this.otherHealthBarWidth = AnimationUtils.animate(hpWidth, this.otherHealthBarWidth, 0.059999852180481D);
                            this.hudHeight = AnimationUtils.animate(40.0D, this.hudHeight, 0.45000000149011612D);
                            this.animationStopwatch.reset();
                        }


                        TTFFontRenderer bold = Crispy.INSTANCE.getFontManager().getFont("karla-bold 26");
                        TTFFontRenderer fr = Crispy.INSTANCE.getFontManager().getFont("clean 16");
                        bold.drawStringWithShadow(target.getCommandSenderName(), x + 35, y + 3, -1);
                        fr.drawStringWithShadow(LookUtils.round(health, 2) + " HP", x + 35, y + 15, -1);
                        RenderUtil.drawRoundedRect(x + 35, y + 33, x + 135, y + 35, 2, new Color(0, 0, 0, 130).getRGB());
                        RenderUtil.drawRoundedRect(x + 35, y + 23, x + 135, y + 25, 2, new Color(0, 0, 0, 130).getRGB());
                        RenderUtil.drawRoundedRect(x + 35, y + 23, x + 35 + otherHealthBarWidth, y + 25, 2, otherHealthColor);
                        RenderUtil.drawRoundedRect(x + 35, y + 23, x + 35 + this.healthBarWidth, y + 25, 2, healthColor);
                        if(armorWidth > 0) {
                            RenderUtil.drawRoundedRect(x + 35, y + 33, x + 35 + armorWidth, y + 35, 2, new Color(33,136,255,255).getRGB());
                        }


                        break;
                    }
                }
                case "Crispy": {
                    if (Aura.target != null && mc.thePlayer != null) {
                        if (Aura.target instanceof EntityPlayer) {
                            float scaledWidth = (float) ScaledResolution.getScaledWidth();
                            float scaledHeight = (float) ScaledResolution.getScaledHeight();
                            this.target = (EntityPlayer) Aura.target;
                            float width = 140.0F;
                            float height = 40.0F;
                            float xOffset = 40.0F;
                            float x = scaledWidth / 2.0F - 70.0F;
                            float y = scaledHeight / 2.0F + 80.0F;
                            float health = this.target.getHealth();
                            double hpPercentage = health / this.target.getMaxHealth();
                            hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                            double hpWidth = 100 * hpPercentage;
                            int healthColor = ColorUtils.getHealthColor(this.target.getHealth(), this.target.getMaxHealth()).getRGB();
                            int otherHealthColor = ColorUtils.getHealthColor(this.target.getHealth(), this.target.getMaxHealth()).darker().getRGB();
                            String healthStr = String.valueOf((float) ((int) this.target.getHealth()) / 2.0F);
                            if (this.animationStopwatch.elapsed(15L)) {
                                this.healthBarWidth = hpWidth;
                                this.otherHealthBarWidth = AnimationUtils.animate(hpWidth, this.otherHealthBarWidth, 0.059999852180481D);
                                this.hudHeight = AnimationUtils.animate(40.0D, this.hudHeight, 0.45000000149011612D);
                                this.animationStopwatch.reset();
                            }

                            glEnable(3089);
                            RenderUtils.prepareScissorBox(x, y, x + 140.0F, (float) ((double) y + this.hudHeight));
                            Gui.drawRect(x, y, x + 140.0F, y + 40.0F, COLOR.getRGB());

                            String status = "Losing";
                            int points = 1;
                            points = (int) (mc.thePlayer.getHealth() - target.getHealth()) - (mc.thePlayer.getTotalArmorValue() - target.getTotalArmorValue());
                            if (points >= 0) {
                                status = "Winning";
                            }
                            Gui.drawRect(x + 40, y + 40.8F, (double) (x + 40) + this.otherHealthBarWidth, y + 38.0F, otherHealthColor);
                            Gui.drawRect(x + 40, y + 40.8F, (double) (x + 40) + this.healthBarWidth, y + 38.0F, healthColor);

                            FontUtil.cleanSmall.drawStringWithShadow("Name: " + this.target.getCommandSenderName(), x + 40.0F, y + 2.0F, -1);
                            FontUtil.cleanSmall.drawStringWithShadow("Distance: " + Math.round(target.getDistanceToEntity(mc.thePlayer)), x + 40.0F, y + 10.0F, -1);
                            FontUtil.cleanSmall.drawStringWithShadow(status, x + 40.0F, y + 20.0F, -1);
                            GuiInventory.drawEntityOnScreen((int) (x + 13.333333F), (int) (y + 40.0F), 20, this.target.rotationYaw, this.target.rotationPitch, this.target);
                            GL11.glDisable(3089);


                        } else {
                            this.healthBarWidth = 100;
                            this.hudHeight = 0.0D;
                            this.target = null;
                        }
                    } else {
                        this.healthBarWidth = 100;
                        this.hudHeight = 0.0D;
                        this.target = null;
                    }
                    break;
                }
                case "Moon": {
                    if (Aura.target != null && mc.thePlayer != null) {
                        if (Aura.target instanceof EntityPlayer) {
                            target = (EntityPlayer) Aura.target;
                            float scaledWidth = (float) ScaledResolution.getScaledWidth();
                            float scaledHeight = (float) ScaledResolution.getScaledHeight();
                            float x = scaledWidth / 2.0F - 70.0F;
                            float y = scaledHeight / 2.0F + 80.0F;
                            float health = this.target.getHealth();
                            glEnable(3089);
                            hudHeight = 40.0D;
                            RenderUtils.prepareScissorBox(x, y, x + 140.0F, (float) ((double) y + this.hudHeight));
                            Gui.drawRect(x, y, x + 140.0F, y + 40.0F, COLOR.getRGB());
                            Gui.drawRect(x, y, x + 140.0F, y + 2, getStaticRainbow(3000, 1));

                            double hpPercentage = health / this.target.getMaxHealth();
                            hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                            double hpWidth = 100 * hpPercentage;

                            double playerPercentage = mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth();
                            double clampedPercent = MathHelper.clamp_double(playerPercentage, 0.0D, 1.0D);
                            double playerWidth = 100 * clampedPercent;
                            FontUtil.cleanSmall.drawStringWithShadow("Name: " + this.target.getCommandSenderName(), x + 40.0F, y + 4.0F, -1);
                            GuiInventory.drawEntityOnScreen((int) (x + 13.333333F), (int) (y + 40.0F), 20, this.target.rotationYaw, this.target.rotationPitch, this.target);
                            int healthColor = ColorUtils.getHealthColor(this.target.getHealth(), this.target.getMaxHealth()).getRGB();
                            Gui.drawRect(x + 40, y + 12, (double) (x + 20) + hpWidth, y + 14, healthColor);

                            Gui.drawRect(x + 40, y + 22, (double) (x + 20) + playerWidth, y + 24, new Color(55, 125, 156).getRGB());
                            GL11.glDisable(3089);
                        }
                    }
                    break;
                }
                case "Exhibition": {
                    if (Aura.target != null) {
                        if (Aura.target instanceof EntityPlayer) {
                            float startX = 20;
                            float scaledWidth = (float) ScaledResolution.getScaledWidth();
                            float scaledHeight = (float) ScaledResolution.getScaledHeight();
                            float renderX = (ScaledResolution.getScaledWidth() / 2) + startX;
                            float renderY = (ScaledResolution.getScaledHeight() / 2) + 10;
                            int maxX2 = 30;
                            if (Aura.target.getCurrentArmor(3) != null) {
                                maxX2 += 15;
                            }
                            if (Aura.target.getCurrentArmor(2) != null) {
                                maxX2 += 15;
                            }
                            if (Aura.target.getCurrentArmor(1) != null) {
                                maxX2 += 15;
                            }
                            if (Aura.target.getCurrentArmor(0) != null) {
                                maxX2 += 15;
                            }
                            if (Aura.target.getHeldItem() != null) {
                                maxX2 += 15;
                            }
                            this.target = (EntityOtherPlayerMP) Aura.target;
                            final float width = 140.0f;
                            final float height = 40.0f;
                            final float xOffset = 40.0f;
                            final float x = scaledWidth / 2.0f + 30.0f;
                            final float y = scaledHeight / 2.0f + 30.0f;
                            final float health = this.target.getHealth();
                            double hpPercentage = health / this.target.getMaxHealth();
                            hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0, 1.0);
                            final double hpWidth = 60.0 * hpPercentage;
                            final int healthColor = ColorUtils.getHealthColor(this.target.getHealth(), this.target.getMaxHealth())
                                    .getRGB();
                            final String healthStr = String.valueOf((int) this.target.getHealth() / 1.0f);
                            int xAdd = 0;
                            double multiplier = 0.85;
                            GlStateManager.pushMatrix();
                            GlStateManager.scale(multiplier, multiplier, multiplier);
                            if (Aura.target.getCurrentArmor(3) != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getCurrentArmor(3), (int) ((((ScaledResolution.getScaledWidth() / 2) + startX + 33) + xAdd) / multiplier), (int) (((ScaledResolution.getScaledHeight() / 2) + 56) / multiplier));
                                xAdd += 15;
                            }
                            if (Aura.target.getCurrentArmor(2) != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getCurrentArmor(2), (int) ((((ScaledResolution.getScaledWidth() / 2) + startX + 33) + xAdd) / multiplier), (int) (((ScaledResolution.getScaledHeight() / 2) + 56) / multiplier));
                                xAdd += 15;
                            }
                            if (Aura.target.getCurrentArmor(1) != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getCurrentArmor(1), (int) ((((ScaledResolution.getScaledWidth() / 2) + startX + 33) + xAdd) / multiplier), (int) (((ScaledResolution.getScaledHeight() / 2) + 56) / multiplier));
                                xAdd += 15;
                            }
                            if (Aura.target.getCurrentArmor(0) != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getCurrentArmor(0), (int) ((((ScaledResolution.getScaledWidth() / 2) + startX + 33) + xAdd) / multiplier), (int) (((ScaledResolution.getScaledHeight() / 2) + 56) / multiplier));
                                xAdd += 15;
                            }
                            if (Aura.target.getHeldItem() != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getHeldItem(), (int) ((((ScaledResolution.getScaledWidth() / 2) + startX + 33) + xAdd) / multiplier), (int) (((ScaledResolution.getScaledHeight() / 2) + 56) / multiplier));
                            }
                            GlStateManager.popMatrix();
                            this.healthBarWidth = AnimationUtils.animate(hpWidth, this.healthBarWidth, 0.1);
                            Gui.drawGradientRect(x - 3.5, y - 3.5, x + 105.5f, y + 42.4f, new Color(10, 10, 10, 255).getRGB(), new Color(10, 10, 10, 255).getRGB());
                            // RenderUtils.prepareScissorBox(x, y, x + 140.0f, (float) (y + this.hudHeight));
                            Gui.drawGradientRect(x - 3, y - 3.2, x + 104.8f, y + 41.8f, new Color(40, 40, 40, 255).getRGB(), new Color(40, 40, 40, 255).getRGB());
                            Gui.drawGradientRect(x - 1.4, y - 1.5, x + 103.5f, y + 40.5f, new Color(74, 74, 74, 255).getRGB(), new Color(74, 74, 74, 255).getRGB());
                            Gui.drawGradientRect(x - 1, y - 1, x + 103.0f, y + 40.0f, new Color(32, 32, 32, 255).getRGB(), new Color(10, 10, 10, 255).getRGB());
                            Gui.drawRect(x + 25.0f, y + 11.0f, x + 87f, y + 14.29f, new Color(105, 105, 105, 40).getRGB());
                            Gui.drawRect(x + 25.0f, y + 11.0f, x + 27f + this.healthBarWidth, y + 14.29f, ColorUtils.getHealthColor(this.target.getHealth(), this.target.getMaxHealth()).getRGB());
                            FontUtil.cleanSmall.drawStringWithShadow(this.target.getCommandSenderName(), x + 24.8f, y + 1.9f, new Color(255, 255, 255).getRGB());
                            FontUtil.cleanSmall.drawStringWithShadow("l   " + "l   " + "l   " + "l   " + "l   " + "l   " + "l   " + "l   ", x + 30.0f, y + 10.2f, new Color(50, 50, 50).getRGB());
                            FontUtil.cleanSmall.drawStringWithShadow("HP:" + healthStr + "  l   " + "Dist:" + ((int) target.getDistanceToEntity(mc.thePlayer)), x - 11.2f + 44.0f - Crispy.INSTANCE.getFontManager().getFont("clean 15").getWidth(healthStr) / 2.0f, y + 17.0f, -1);
                            GuiInventory.drawEntityOnScreen((int) (x + 12f), (int) (y + 34.0f), 15, this.target.rotationYaw, this.target.rotationPitch, this.target);
                        } else {
                            healthBarWidth = 92;
                            this.hudHeight = 0;
                            this.target = null;
                        }
                    } else {
                        healthBarWidth = 92;
                        this.hudHeight = 0;
                        this.target = null;
                    }
                    break;
                }


            }
        } else if(e instanceof EventUpdate) {
            if (Aura.target != null && Aura.target instanceof EntityPlayer) {
                target = (EntityPlayer) Aura.target;
                lastAura = System.currentTimeMillis();
            }
        }
    }


    public ResourceLocation getPlayerSkin(String username) {
        if (mc.thePlayer != null) {
            for (Object playerInfo : mc.thePlayer.sendQueue.getPlayerInfoMap()) {
                try {
                    NetworkPlayerInfo info = (NetworkPlayerInfo) playerInfo;
                    if (username.equals(info.getGameProfile().getName())) {
                        return info.getLocationSkin();
                    }
                } catch (Exception e) {

                }
            }
        }
        return null;
    }

    private int getStaticRainbow(int speed, int offset) {
        float hue = 5000 + (System.currentTimeMillis() + offset) % speed;
        hue /= 5000;
        return Color.getHSBColor(hue, 0.65f, (float) .9).getRGB();
    }
}
