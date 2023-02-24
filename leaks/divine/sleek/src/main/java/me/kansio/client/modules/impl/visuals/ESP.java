package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ModuleData(
        name = "ESP",
        category = ModuleCategory.VISUALS,
        description = "Shows player locations"
)
public class ESP extends Module {

    public final BooleanValue outline = new BooleanValue("Outline", this, true);
    public final ModeValue boxMode = new ModeValue("Box Type", this, "Corners", "Box");
    public final BooleanValue fillInside = new BooleanValue("Fill Box", this, false);
    public final BooleanValue tag = new BooleanValue("Name", this, true);
    public final BooleanValue healthBar = new BooleanValue("Health Bar", this, true);
    public final BooleanValue armorBar = new BooleanValue("Armor Bar", this, true);
    public final BooleanValue localPlayer = new BooleanValue("Local", this, true);
    public final BooleanValue players = new BooleanValue("Players", this, true);
    public final BooleanValue invisibles = new BooleanValue("Invisibles", this, false);
    public final BooleanValue mobs = new BooleanValue("Monsters", this, true);
    public final BooleanValue animals = new BooleanValue("Animals", this, false);
    public final BooleanValue chests = new BooleanValue("Chests", this, false);
    public final BooleanValue droppedItems = new BooleanValue("Dropped Items", this, false);
    public final List collectedEntities;
    private final IntBuffer viewport;
    private final FloatBuffer modelview;
    private final FloatBuffer projection;
    private final FloatBuffer vector;
    private final int color;
    private final int backgroundColor;
    private final int black;

    public ESP() {
        this.collectedEntities = new ArrayList();
        this.viewport = GLAllocation.createDirectIntBuffer(16);
        this.modelview = GLAllocation.createDirectFloatBuffer(16);
        this.projection = GLAllocation.createDirectFloatBuffer(16);
        this.vector = GLAllocation.createDirectFloatBuffer(4);
        this.color = Color.WHITE.getRGB();
        this.backgroundColor = (new Color(0, 0, 0, 120)).getRGB();
        this.black = Color.BLACK.getRGB();
    }

    @Subscribe
    public void onRender(RenderOverlayEvent event) {
        GL11.glPushMatrix();
        this.collectEntities();
        float partialTicks = mc.timer.renderPartialTicks;
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int scaleFactor = scaledResolution.getScaleFactor();
        double scaling = (double) scaleFactor / Math.pow((double) scaleFactor, 2.0D);
        GL11.glScaled(scaling, scaling, scaling);
        int black = this.black;
        int color = this.color;
        int background = this.backgroundColor;
        float scale = 0.65F;
        float upscale = 1.0F / scale;
        FontRenderer fr = mc.fontRendererObj;
        RenderManager renderMng = mc.getRenderManager();
        EntityRenderer entityRenderer = mc.entityRenderer;
        boolean tag = this.tag.getValue();
        boolean outline = this.outline.getValue();
        boolean health = this.healthBar.getValue();
        boolean armor = this.armorBar.getValue();
        boolean box = this.boxMode.getValue().equalsIgnoreCase("box");
        List collectedEntities = this.collectedEntities;
        int i = 0;

        for (int collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; ++i) {
            Entity entity = (Entity) collectedEntities.get(i);
            if (this.isValid(entity) && RenderUtils.isInViewFrustrum(entity)) {
                double x = RenderUtils.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                double y = RenderUtils.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                double z = RenderUtils.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                double width = (double) entity.width / 1.5D;
                double height = (double) entity.height + (entity.isSneaking() ? -0.3D : 0.2D);
                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                entityRenderer.setupCameraTransform(partialTicks, 0);
                Vector4d position = null;

                for (Vector3d o : vectors) {
                    Vector3d vector = o;
                    vector = this.project2D(scaleFactor, vector.x - renderMng.viewerPosX, vector.y - renderMng.viewerPosY, vector.z - renderMng.viewerPosZ);
                    if (vector != null && vector.z >= 0.0D && vector.z < 1.0D) {
                        if (position == null) {
                            position = new Vector4d(vector.x, vector.y, vector.z, 0.0D);
                        }

                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }

                if (position != null) {
                    entityRenderer.setupOverlayRendering();
                    double posX = position.x;
                    double posY = position.y;
                    double endPosX = position.z;
                    double endPosY = position.w;
                    if (outline) {
                        if (Objects.equals(boxMode.getValue(), "Box")) {
                            if (fillInside.getValue())
                                Gui.drawRect(posX - 1.0D, posY, endPosX, endPosY, new Color(0, 0, 0, 100).getRGB());

                            Gui.drawRect(posX - 1.0D, posY, posX + 0.5D, endPosY + 0.5D, black);
                            Gui.drawRect(posX - 1.0D, posY - 0.5D, endPosX + 0.5D, posY + 0.5D + 0.5D, black);
                            Gui.drawRect(endPosX - 0.5D - 0.5D, posY, endPosX + 0.5D, endPosY + 0.5D, black);
                            Gui.drawRect(posX - 1.0D, endPosY - 0.5D - 0.5D, endPosX + 0.5D, endPosY + 0.5D, black);
                            Gui.drawRect(posX - 0.5D, posY, posX + 0.5D - 0.5D, endPosY, color);
                            Gui.drawRect(posX, endPosY - 0.5D, endPosX, endPosY, color);
                            Gui.drawRect(posX - 0.5D, posY, endPosX, posY + 0.5D, color);
                            Gui.drawRect(endPosX - 0.5D, posY, endPosX, endPosY, color);
                        } else {
                            if (fillInside.getValue())
                                Gui.drawRect(posX - 1.0D, posY, endPosX, endPosY, new Color(0, 0, 0, 100).getRGB());

                            Gui.drawRect(posX + 0.5D, posY, posX - 1.0D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                            Gui.drawRect(posX - 1.0D, endPosY, posX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                            Gui.drawRect(posX - 1.0D, posY - 0.5D, posX + (endPosX - posX) / 3.0D + 0.5D, posY + 1.0D, black);
                            Gui.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, posY - 0.5D, endPosX, posY + 1.0D, black);
                            Gui.drawRect(endPosX - 1.0D, posY, endPosX + 0.5D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                            Gui.drawRect(endPosX - 1.0D, endPosY, endPosX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                            Gui.drawRect(posX - 1.0D, endPosY - 1.0D, posX + (endPosX - posX) / 3.0D + 0.5D, endPosY + 0.5D, black);
                            Gui.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, endPosY - 1.0D, endPosX + 0.5D, endPosY + 0.5D, black);
                            Gui.drawRect(posX, posY, posX - 0.5D, posY + (endPosY - posY) / 4.0D, color);
                            Gui.drawRect(posX, endPosY, posX - 0.5D, endPosY - (endPosY - posY) / 4.0D, color);
                            Gui.drawRect(posX - 0.5D, posY, posX + (endPosX - posX) / 3.0D, posY + 0.5D, color);
                            Gui.drawRect(endPosX - (endPosX - posX) / 3.0D, posY, endPosX, posY + 0.5D, color);
                            Gui.drawRect(endPosX - 0.5D, posY, endPosX, posY + (endPosY - posY) / 4.0D, color);
                            Gui.drawRect(endPosX - 0.5D, endPosY, endPosX, endPosY - (endPosY - posY) / 4.0D, color);
                            Gui.drawRect(posX, endPosY - 0.5D, posX + (endPosX - posX) / 3.0D, endPosY, color);
                            Gui.drawRect(endPosX - (endPosX - posX) / 3.0D, endPosY - 0.5D, endPosX - 0.5D, endPosY, color);
                        }
                    }

                    boolean living = entity instanceof EntityLivingBase;
                    EntityLivingBase entityLivingBase;
                    float armorValue;
                    float itemDurability;
                    double durabilityWidth;
                    double textWidth;
                    float tagY;
                    if (living) {
                        entityLivingBase = (EntityLivingBase) entity;
                        if (health) {
                            armorValue = entityLivingBase.getHealth();
                            itemDurability = entityLivingBase.getMaxHealth();
                            if (armorValue > itemDurability) {
                                armorValue = itemDurability;
                            }

                            durabilityWidth = (double) (armorValue / itemDurability);
                            textWidth = (endPosY - posY) * durabilityWidth;
                            Gui.drawRect(posX - 3.5D, posY - 0.5D, posX - 1.5D, endPosY + 0.5D, background);
                            if (armorValue > 0.0F) {
                                int healthColor = new Color(73, 255, 12).getRGB();
                                Gui.drawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - textWidth, healthColor);
                                tagY = entityLivingBase.getAbsorptionAmount();
                                if (tagY > 0.0F) {
                                    Gui.drawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - (endPosY - posY) / 6.0D * (double) tagY / 2.0D, (new Color(Potion.absorption.getLiquidColor())).getRGB());
                                }
                            }
                        }
                    }

                    if (tag) {
                        float scaledHeight = 10.0F;
                        String name = Client.getInstance().getFriendManager().isFriend(entity.getName()) ? Client.getInstance().getFriendManager().getFriend(entity.getName()).getDisplayName() : (Client.getInstance().getUsers().containsKey(entity.getName()) ? Client.getInstance().getUsers().get(entity.getName()) : entity.getName());


                        if (entity instanceof EntityItem) {
                            name = ((EntityItem) entity).getEntityItem().getDisplayName();
                        }

                        String prefix = "";
                        if (entity instanceof EntityPlayer) {
                            prefix = this.isFriendly((EntityPlayer) entity) ? "§b" : Client.getInstance().getUsers().containsKey(entity.getName()) ? "§a" : "§c";
                        }

                        if (entity instanceof EntityPlayer) {
                            if (Client.getInstance().getTargetManager().isTarget((EntityPlayer) entity)) {
                                prefix = "§4";
                            }
                        }

                        durabilityWidth = (endPosX - posX) / 2.0D;
                        textWidth = (double) ((float) fr.getStringWidth(name) * scale);
                        float tagX = (float) ((posX + durabilityWidth - textWidth / 2.0D) * (double) upscale);
                        tagY = (float) (posY * (double) upscale) - scaledHeight;
                        GL11.glPushMatrix();
                        GL11.glScalef(scale, scale, scale);
                        if (living) {
                            Gui.drawRect((double) (tagX - 2.0F), (double) (tagY - 2.0F), (double) tagX + textWidth * (double) upscale + 2.0D, (double) (tagY + 9.0F), (new Color(0, 0, 0, 140)).getRGB());
                        }

                        fr.drawStringWithShadow(prefix + name, tagX, tagY, -1);
                        GL11.glPopMatrix();
                    }

                    if (armor) {
                        if (living) {
                            entityLivingBase = (EntityLivingBase) entity;
                            armorValue = (float) entityLivingBase.getTotalArmorValue();
                            double armorWidth = (endPosX - posX) * (double) armorValue / 20.0D;
                            Gui.drawRect(posX - 0.5D, endPosY + 1.5D, posX - 0.5D + endPosX - posX + 1.0D, endPosY + 1.5D + 2.0D, background);
                            if (armorValue > 0.0F) {
                                Gui.drawRect(posX, endPosY + 2.0D, posX + armorWidth, endPosY + 3.0D, 16777215);
                            }
                        } else if (entity instanceof EntityItem) {
                            ItemStack itemStack = ((EntityItem) entity).getEntityItem();
                            if (itemStack.isItemStackDamageable()) {
                                int maxDamage = itemStack.getMaxDamage();
                                itemDurability = (float) (maxDamage - itemStack.getItemDamage());
                                durabilityWidth = (endPosX - posX) * (double) itemDurability / (double) maxDamage;
                                Gui.drawRect(posX - 0.5D, endPosY + 1.5D, posX - 0.5D + endPosX - posX + 1.0D, endPosY + 1.5D + 2.0D, background);
                                Gui.drawRect(posX, endPosY + 2.0D, posX + durabilityWidth, endPosY + 3.0D, new Color(0, 200, 208, 255).getRGB());
                            }
                        }
                    }
                }
            }
        }

        GL11.glPopMatrix();
        GlStateManager.enableBlend();
        entityRenderer.setupOverlayRendering();
    }

    private boolean isFriendly(EntityPlayer player) {
        return Client.getInstance().getFriendManager().isFriend(player.getName());
    }

    private void collectEntities() {
        this.collectedEntities.clear();
        List playerEntities = mc.theWorld.loadedEntityList;
        int i = 0;

        for (int playerEntitiesSize = playerEntities.size(); i < playerEntitiesSize; ++i) {
            Entity entity = (Entity) playerEntities.get(i);
            if (this.isValid(entity)) {
                this.collectedEntities.add(entity);
            }
        }

    }

    private Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelview);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);
        return GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.vector) ? new Vector3d((double) (this.vector.get(0) / (float) scaleFactor), (double) (((float) Display.getHeight() - this.vector.get(1)) / (float) scaleFactor), (double) this.vector.get(2)) : null;
    }

    private boolean isValid(Entity entity) {
        if (entity != mc.thePlayer || this.localPlayer.getValue() && mc.gameSettings.thirdPersonView != 0) {
            if (entity.isDead) {
                return false;
            } else if (!this.invisibles.getValue() && entity.isInvisible()) {
                return false;
            } else if (this.droppedItems.getValue() && entity instanceof EntityItem && mc.thePlayer.getDistanceToEntity(entity) < 10.0F) {
                return true;
            } else if (this.animals.getValue() && entity instanceof EntityAnimal) {
                return true;
            } else if (this.players.getValue() && entity instanceof EntityPlayer) {
                return true;
            } else {
                return this.mobs.getValue() && (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityDragon || entity instanceof EntityGolem);
            }
        } else {
            return false;
        }
    }
}