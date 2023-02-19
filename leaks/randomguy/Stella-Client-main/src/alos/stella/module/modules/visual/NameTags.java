package alos.stella.module.modules.visual;

import alos.stella.event.EventTarget;
import alos.stella.event.events.Render2DEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.render.DrawUtils;
import alos.stella.utils.render.GLUtils;
import alos.stella.value.BoolValue;
import alos.stella.value.FontValue;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "NameTags", description = "=/", category = ModuleCategory.VISUAL)
public class NameTags extends Module {
    private final BoolValue player = new BoolValue("Player", true);
    private final BoolValue mob = new BoolValue("Mob", false);
    private final BoolValue animal = new BoolValue("Animal", false);
    private final BoolValue invisible = new BoolValue("Invisible", false);
    private final BoolValue health = new BoolValue("Health",true);
    private final BoolValue distance = new BoolValue("Distance",false);
    private final FontValue font = new FontValue("Font", Fonts.minecraftFont);

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        mc.theWorld.loadedEntityList.forEach(entity -> {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase ent = (EntityLivingBase) entity;
                if (isValid(ent) && DrawUtils.drawIsInViewFrustrum(ent) && entity.getUniqueID() != mc.thePlayer.getUniqueID()) {
                    double posX = DrawUtils.drawInterpolate(entity.posX, entity.lastTickPosX, event.getPartialTicks());
                    double posY = DrawUtils.drawInterpolate(entity.posY, entity.lastTickPosY, event.getPartialTicks());
                    double posZ = DrawUtils.drawInterpolate(entity.posZ, entity.lastTickPosZ, event.getPartialTicks());

                    double width = entity.width / 1.5;
                    double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);

                    AxisAlignedBB aabb = new AxisAlignedBB(posX - width, posY, posZ - width, posX + width, posY + height + 0.05, posZ + width);
                    List<Vector3d> vectors = Arrays.asList(
                            new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
                            new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
                            new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
                            new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
                            new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
                            new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
                            new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
                            new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)
                    );

                    mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
                    Vector4d position = null;

                    for (Vector3d vector : vectors) {
                        vector = GLUtils.glProject(vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
                        if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                            if (position == null) {
                                position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                            }
                            position.x = Math.min(vector.x, position.x);
                            position.y = Math.min(vector.y, position.y);
                            position.z = Math.max(vector.x, position.z);
                            position.w = Math.max(vector.y, position.w);
                        }
                    }

                    mc.entityRenderer.setupOverlayRendering();
                    if (position != null) {
                        GL11.glPushMatrix();

                        final float x = (float) position.x;
                        final float x2 = (float) position.z;
                        final float y = (float) position.y - 1;

                        final String healthText = (ent.getHealth() >= 16 ? ChatFormatting.GREEN : ent.getHealth() >= 12 ? ChatFormatting.YELLOW : ent.getHealth() >= 8 ? ChatFormatting.RED : ChatFormatting.DARK_RED) + " " + (int) ent.getHealth();

                        final String nameText = (
                                distance.get() ? "(" + Math.round(mc.thePlayer.getDistance(ent.posX, ent.posY, ent.posZ)) + "m) " : "")
                                + ent.getDisplayName().getUnformattedText()
                                + (health.get() ? healthText : "");


                        font.get().drawStringWithShadow(nameText, (int) ((x + ((x2 - x) / 2)) - (font.get().getStringWidth(nameText) / 2f)), (int) (y - font.get().FONT_HEIGHT - 2), getNameColor(ent));

                        GL11.glPopMatrix();
                    }
                }
            }
        });
    }

    private boolean isValid(EntityLivingBase entity) {
        return mc.thePlayer != entity && entity.getEntityId() != -1488 && isValidType(entity) && entity.isEntityAlive() && (!entity.isInvisible() || invisible.get());
    }

    private boolean isValidType(EntityLivingBase entity) {
        return (player.get() && entity instanceof EntityPlayer) || ((mob.get() && (entity instanceof EntityMob || entity instanceof EntitySlime)) || (animal.get() && entity instanceof EntityAnimal) || (invisible.get() && (entity instanceof EntityVillager || entity instanceof EntityGolem)));
    }

    private int getNameColor(EntityLivingBase entity) {
        if (entity.getName().equals(mc.thePlayer.getName())) {
            return new Color(100, 200, 100).getRGB();
        } else
            return new Color(255, 255, 255).getRGB();
    }
}
