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
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.Render3DEvent;
import tech.dort.dortware.impl.utils.render.ColorUtil;
import tech.dort.dortware.impl.utils.render.RenderUtils;

import java.awt.*;

public class ESP extends Module {

    public EnumValue<Mode> mode = new EnumValue<>("Mode", this, ESP.Mode.values());
    private final NumberValue red = new NumberValue("Red", this, 255, 0, 255, true);
    private final NumberValue green = new NumberValue("Green", this, 50, 0, 255, true);
    private final NumberValue blue = new NumberValue("Blue", this, 50, 0, 255, true);
    private final NumberValue boxAlpha = new NumberValue("Box Alpha", this, 150, 0, 255, true);
    private final NumberValue width = new NumberValue("Width", this, 3, 1, 5, true);
    private final NumberValue polygonSlices = new NumberValue("Polygon Slices", this, 12, 4, 64, true);
    private final BooleanValue booleanValue = new BooleanValue("Health Color", this, false);
    private final BooleanValue rainbow = new BooleanValue("Rainbow", this, false);
    private final BooleanValue players = new BooleanValue("Players", this, true);
    private final BooleanValue animals = new BooleanValue("Animals", this, false);
    private final BooleanValue neutral = new BooleanValue("Neutral", this, false);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", this, false);
    private final BooleanValue self = new BooleanValue("Self", this, false);
    private final BooleanValue mobs = new BooleanValue("Mobs", this, false);

    public ESP(ModuleData moduleData) {
        super(moduleData);
        register(mode, red, green, blue, boxAlpha, width, polygonSlices, booleanValue, rainbow, players, animals, neutral, invisibles, self, mobs);
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        switch (mode.getValue()) {
            case POLYGON:
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (isValidEntity(entity)) {
                        RenderUtils.entityRenderer(mc, entity, () -> {
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            Cylinder cylinder = new Cylinder();
                            cylinder.setDrawStyle(GLU.GLU_SILHOUETTE);
                            GlStateManager.rotate(90.0F, -90.0F, 0.0F, 1.0F);
                            final Color healthColor = new Color(ColorUtil.getHealthColor((EntityLivingBase) entity));
                            final Color rainbowColor = new Color(ColorUtil.rainbow(-6000, 0));
                            if (!Client.INSTANCE.getFriendManager().getObjects().contains(entity.getName().toLowerCase())) {
                                if (!rainbow.getValue()) {
                                    GL11.glColor3d(booleanValue.getValue() ? healthColor.getRed() / 255f : (float) (red.getValue() / 255f), booleanValue.getValue() ? healthColor.getGreen() / 255f : (float) (green.getValue() / 255f), booleanValue.getValue() ? healthColor.getBlue() / 255f : (float) (blue.getValue() / 255f));
                                } else {
                                    GL11.glColor3d(rainbowColor.getRed() / 255F, rainbowColor.getBlue() / 255F, rainbowColor.getGreen() / 255F);
                                }
                            } else {
                                GL11.glColor3d(0, 0, 1);
                            }
                            GlStateManager.rotate(mc.getRenderManager().playerViewY, 0.0F, 0.0F, -1.0F);
                            GL11.glLineWidth(width.getValue().floatValue());
                            cylinder.draw(entity.width, entity.width, entity.height + 0.2F, polygonSlices.getValue().intValue(), 0);
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                        });
                    }
                }
                break;

            case SIMPLE:
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (isValidEntity(entity)) {
                        RenderUtils.entityRenderer(mc, entity, () -> {
                            GlStateManager.rotate(mc.getRenderManager().playerViewY, 0.0F, -1.0F, 0.0F);
                            GL11.glLineWidth(width.getValue().floatValue());

                            final Color healthColor = new Color(ColorUtil.getHealthColor((EntityLivingBase) entity));
                            final Color rainbowColor = new Color(ColorUtil.rainbow(-6000, 0));
                            if (!Client.INSTANCE.getFriendManager().getObjects().contains(entity.getName().toLowerCase())) {
                                if (!rainbow.getValue()) {
                                    GL11.glColor3d(booleanValue.getValue() ? healthColor.getRed() / 255f : (float) (red.getValue() / 255f), booleanValue.getValue() ? healthColor.getGreen() / 255f : (float) (green.getValue() / 255f), booleanValue.getValue() ? healthColor.getBlue() / 255f : (float) (blue.getValue() / 255f));
                                } else {
                                    GL11.glColor3d(rainbowColor.getRed() / 255F, rainbowColor.getBlue() / 255F, rainbowColor.getGreen() / 255F);
                                }
                            } else {
                                GL11.glColor3d(0, 0, 1);
                            }

                            RenderUtils.drawOutlinedRectNoColor(-entity.width, 0, entity.width, entity.height + 0.2F);

                            GL11.glColor3d(healthColor.getRed() / 255f, healthColor.getGreen() / 255f, healthColor.getBlue() / 255f);
                            RenderUtils.drawRectNoColor(entity.width + 0.06F + mc.thePlayer.getDistanceToEntity(entity) / 1000.0F, 0, entity.width + 0.05F, ((EntityLivingBase) entity).getHealth() / 10.0F);
                        });
                    }
                }
                break;
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

    public enum Mode implements INameable {
        SIMPLE("Simple"), POLYGON("Polygon");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
