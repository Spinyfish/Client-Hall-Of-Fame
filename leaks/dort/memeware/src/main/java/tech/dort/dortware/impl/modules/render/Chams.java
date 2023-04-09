package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.EntityRenderEvent;
import tech.dort.dortware.impl.utils.render.ColorUtil;

import java.awt.*;

/**
 * @author Aidan
 */

public class Chams extends Module {

    private final EnumValue<Chams.Mode> mode = new EnumValue<>("Mode", this, Chams.Mode.values());
    private final BooleanValue booleanValue = new BooleanValue("Health Color", this, false);
    private final NumberValue red = new NumberValue("Red", this, 255, 0, 255, true);
    private final NumberValue green = new NumberValue("Green", this, 50, 0, 255, true);
    private final NumberValue blue = new NumberValue("Blue", this, 50, 0, 255, true);
    private final NumberValue alpha = new NumberValue("Alpha", this, 125, 0, 255, true);
    private final BooleanValue lighting = new BooleanValue("Lighting", this, true);
    private final BooleanValue rainbow = new BooleanValue("Rainbow", this, false);
    private final BooleanValue players = new BooleanValue("Players", this, true);
    private final BooleanValue animals = new BooleanValue("Animals", this, false);
    private final BooleanValue neutral = new BooleanValue("Neutral", this, false);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", this, false);
    private final BooleanValue self = new BooleanValue("Self", this, false);
    private final BooleanValue mobs = new BooleanValue("Mobs", this, false);

    public Chams(ModuleData moduleData) {
        super(moduleData);
        register(mode, red, green, blue, alpha, lighting, rainbow, booleanValue, players, animals, neutral, invisibles, self, mobs);
    }

    @Subscribe
    public void onEvent(EntityRenderEvent event) {
        if (isValidEntity(event.getEntity())) {
            final float red1 = red.getValue().floatValue();
            final float green1 = green.getValue().floatValue();
            final float blue1 = blue.getValue().floatValue();
            final float alpha1 = alpha.getValue().floatValue();

            final HurtColor hurtColor = Client.INSTANCE.getModuleManager().get(HurtColor.class);
            final float hurtRed = hurtColor.isToggled() ? hurtColor.red.getValue().floatValue() : 1.0F;
            final float hurtGreen = hurtColor.isToggled() ? hurtColor.green.getValue().floatValue() : 0.0F;
            final float hurtBlue = hurtColor.isToggled() ? hurtColor.blue.getValue().floatValue() : 0.0F;

            switch (mode.getValue()) {
                case COLORED:
                    if (event.isPre()) {
                        if (lighting.getValue())
                            GlStateManager.disableLighting();
                        GlStateManager.disableTexture2D();
                        GlStateManager.enableBlend();
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        if (!Client.INSTANCE.getFriendManager().getObjects().contains(event.getEntity().getName().toLowerCase())) {
                            if (!booleanValue.getValue()) {
                                if (!rainbow.getValue()) {
                                    GlStateManager.color(event.getEntity().hurtResistantTime > 10 ? hurtRed / 255.0F : red1 / 255.0F, event.getEntity().hurtResistantTime > 10 ? hurtGreen / 255.0F : green1 / 255.0F, event.getEntity().hurtResistantTime > 10 ? hurtBlue / 255.0F : blue1 / 255.0F, alpha1 / 255.0F);
                                } else {
                                    final Color color = new Color(ColorUtil.rainbow(-6000, 0));
                                    GlStateManager.color(event.getEntity().hurtResistantTime > 10 ? hurtRed / 255.0F : color.getRed() / 255.0F, event.getEntity().hurtResistantTime > 10 ? hurtGreen / 255.0F : color.getGreen() / 255.0F, event.getEntity().hurtResistantTime > 10 ? hurtBlue / 255.0F : color.getBlue() / 255.0F, alpha1 / 255.0F);
                                }
                            } else {
                                final Color color = new Color(ColorUtil.getHealthColor((EntityLivingBase) event.getEntity()));
                                GlStateManager.color(event.getEntity().hurtResistantTime > 10 ? hurtRed / 255.0F : color.getRed() / 255.0F, event.getEntity().hurtResistantTime > 10 ? hurtGreen / 255.0F : color.getGreen() / 255.0F, event.getEntity().hurtResistantTime > 10 ? hurtBlue / 255.0F : color.getBlue() / 255.0F, alpha1 / 255.0F);
                            }
                        } else {
                            GlStateManager.color(event.getEntity().hurtResistantTime > 10 ? hurtRed / 255.0F : 0, event.getEntity().hurtResistantTime > 10 ? hurtGreen / 255.0F : 0, event.getEntity().hurtResistantTime > 10 ? hurtBlue / 255.0F : 1, alpha1 / 255.0F);
                        }
                    } else {
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        if (lighting.getValue())
                            GlStateManager.enableLighting();
                        GlStateManager.enableTexture2D();
                        GlStateManager.color(1, 1, 1);
                    }
                    break;

                case NORMAL:
                    if (event.isPre()) {
                        if (lighting.getValue())
                            GlStateManager.disableLighting();
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                    } else {
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        if (lighting.getValue())
                            GlStateManager.enableLighting();
                    }
                    break;
            }
        }
    }

    private boolean isValidEntity(Entity entity) {
        if (entity == mc.thePlayer) return self.getValue() && players.getValue();
        if (entity.isInvisible() && !invisibles.getValue()) return false;
        if (entity instanceof EntityPlayer && players.getValue()) return true;
        if (entity instanceof EntityAnimal && animals.getValue()) return true;
        if (entity instanceof EntityVillager && neutral.getValue()) return true;
        return entity instanceof EntityMob && mobs.getValue();
    }

    private enum Mode implements INameable {
        COLORED("Colored"), NORMAL("Normal");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name;
        }
    }
}
