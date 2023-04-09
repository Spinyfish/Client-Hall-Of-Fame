package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.Render3DEvent;
import tech.dort.dortware.impl.events.RenderPlayerTagEvent;
import tech.dort.dortware.impl.gui.click.GuiUtils;
import tech.dort.dortware.impl.utils.render.ColorUtil;
import tech.dort.dortware.impl.utils.render.RenderUtils;

import java.awt.*;
import java.text.DecimalFormat;

public class Tags extends Module {

    private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
    private final NumberValue red = new NumberValue("Red", this, 255, 0, 255, true);
    private final NumberValue green = new NumberValue("Green", this, 50, 0, 255, true);
    private final NumberValue blue = new NumberValue("Blue", this, 50, 0, 255, true);
    private final NumberValue alpha = new NumberValue("Alpha", this, 125, 0, 255, true);
    private final BooleanValue unformatted = new BooleanValue("Unformatted", this, false);
    private final BooleanValue background = new BooleanValue("Background", this, false);
    private final BooleanValue rainbow = new BooleanValue("Rainbow", this, false);
    private final BooleanValue remove = new BooleanValue("Remove", this, true);
    private final BooleanValue showHealth = new BooleanValue("Health", this, true);
    private final BooleanValue players = new BooleanValue("Players", this, true);
    private final BooleanValue animals = new BooleanValue("Animals", this, false);
    private final BooleanValue neutral = new BooleanValue("Neutral", this, false);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", this, false);
    private final BooleanValue self = new BooleanValue("Self", this, false);
    private final BooleanValue mobs = new BooleanValue("Mobs", this, false);

    public Tags(ModuleData moduleData) {
        super(moduleData);
        register(red, green, blue, alpha, unformatted, background, rainbow, remove, showHealth, players, animals, neutral, invisibles, self, mobs);
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        final CustomFontRenderer font = Client.INSTANCE.getFontManager().getFont("Tahoma").getRenderer();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (isValidEntity(entity)) {

                RenderUtils.entityRenderer(mc, entity, () -> {
                    String entityName = unformatted.getValue() ? entity.getName() : entity.getDisplayName().getUnformattedText();

                    final double health = ((EntityLivingBase) entity).getHealth() / 2;
                    final double maxHealth = ((EntityLivingBase) entity).getMaxHealth() / 2;
                    final double percentage = 100 * (health / maxHealth);
                    final String healthColor = percentage > 75 ? "2" : percentage > 50 ? "e" : percentage > 25 ? "6" : "4";

                    final String healthDisplay = decimalFormat.format(health * 2);
                    entityName = showHealth.getValue() ? String.format("%s \247%s%s", entityName, healthColor, healthDisplay) : entityName;

                    final float distance = mc.thePlayer.getDistanceToEntity(entity);
                    final float var13 = (distance / 5 <= 2 ? 2.0F : distance / 5);
                    final float var14 = 0.016666668F * var13;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0, entity.height + 0.5F, 0);
                    if (mc.gameSettings.thirdPersonView == 2) {
                        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(mc.getRenderManager().playerViewX, -1.0F, 0.0F, 0.0F);
                    } else {
                        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                    }
                    GlStateManager.scale(-var14 / 3, -var14 / 3, var14 / 3);

                    int heightOffset = 0;
                    if (entity.isSneaking()) {
                        heightOffset += 4;
                    }

                    heightOffset -= distance / 5;
                    if (heightOffset < -8) {
                        heightOffset = -8;
                    }

                    final int width = (int) (font.getWidth(entityName) / 2) + 2;
                    final EntityLivingBase e = (EntityLivingBase) entity;
                    final float xSpeed = 75F / (Minecraft.func_175610_ah() * 1.05F);
                    if ((float) width < e.animation || (float) width > e.animation) {
                        if (Math.abs((float) width - e.animation) <= xSpeed) {
                            e.animation = (float) width;
                        } else {
                            e.animation += (e.animation < (float) width ? xSpeed * 3 : -xSpeed);
                        }
                    }

                    Color color = !Client.INSTANCE.getFriendManager().getObjects().contains(entity.getName().toLowerCase()) ? new Color(red.getValue().floatValue() / 255f, green.getValue().floatValue() / 255f, blue.getValue().floatValue() / 255f, 1) : new Color(0F, 0F, 1F);

                    if (background.getValue()) {
                        Gui.drawRect((-width) - 2, heightOffset + 17, width, heightOffset, new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
                        GuiUtils.drawRect1((-width) - 2, heightOffset - 2, (width + e.animation) + 2, 2F, rainbow.getValue() && !Client.INSTANCE.getFriendManager().getObjects().contains(entity.getName().toLowerCase()) ? ColorUtil.rainbow(-6000, 0) : color.getRGB());
                    }

                    font.drawStringWithShadow(entityName, -width, heightOffset + 2, rainbow.getValue() ? ColorUtil.rainbow(-6000, 0) : color.getRGB());
                    GL11.glPopMatrix();
                });
            }
        }
    }

    @Subscribe
    public void onNameTagRender(RenderPlayerTagEvent event) {
        event.forceCancel(remove.getValue());
    }

    private boolean isValidEntity(Entity entity) {
        if (entity == mc.thePlayer) return self.getValue() && players.getValue();
        if (entity.isInvisible() && !invisibles.getValue()) return false;
        if (entity instanceof EntityPlayer && players.getValue()) return true;
        if (entity instanceof EntityAnimal && animals.getValue()) return true;
        if (entity instanceof EntityVillager && neutral.getValue()) return true;
        return entity instanceof EntityMob && mobs.getValue();
    }
}
