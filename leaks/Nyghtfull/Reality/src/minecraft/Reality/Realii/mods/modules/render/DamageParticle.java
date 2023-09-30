package Reality.Realii.mods.modules.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRender3D;
import Reality.Realii.event.events.world.EventLivingUpdate;
import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.math.AnimationUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.*;

public class DamageParticle extends Module {
    public DamageParticle() {
        super("DamageParticle", ModuleType.Render);
    }

    private HashMap<EntityLivingBase, Float> healthMap = new HashMap<EntityLivingBase, Float>();
    private List<Particles> particles = new ArrayList<Particles>();
    float anim = 1320;
    public AnimationUtils animationUtils = new AnimationUtils();

    @EventHandler
    public void onLivingUpdate(EventLivingUpdate e) {
        EntityLivingBase entity = (EntityLivingBase) e.getEntity();
        if (entity == this.mc.thePlayer) {
            return;
        }
        if (!this.healthMap.containsKey(entity)) {
            this.healthMap.put(entity, ((EntityLivingBase) entity).getHealth());
        }
        anim = moveUD((float) anim, (float) 0.0f, (float) 0.08f, (float) 0.08f);
        //GlStateManager.translate(anim, anim, anim);
        float floatValue = this.healthMap.get(entity);
        float health = entity.getHealth();
        if (floatValue != health) {
            String text;
            if (floatValue - health < 0.0f) {
                text = "\247a" + roundToPlace((floatValue - health) * -1.0f, 1);
            } else {
                text = "\247e" + roundToPlace(floatValue - health, 1);
            }
            Location location = new Location(entity);
            location.setY(entity.getEntityBoundingBox().minY
                    + (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2.0);
            location.setX(location.getX() - 0.5 + new Random(System.currentTimeMillis()).nextInt(5) * 0.1);
            location.setZ(location.getZ() - 0.5
                    + new Random(System.currentTimeMillis() + 1).nextInt(5)
                    * 0.01);
            this.particles.add(new Particles(location, text));
            this.healthMap.remove(entity);
            this.healthMap.put(entity, entity.getHealth());
        }
    }

    @EventHandler
    public void onRender(EventRender3D e) {
        if (particles.size() <= 0)
            return;
        try {
            for (Particles p : this.particles) {
                double x = p.location.getX();
                this.mc.getRenderManager();
                double n = x - mc.getRenderManager().viewerPosX;
                double y = p.location.getY();
                this.mc.getRenderManager();
                double n2 = y - mc.getRenderManager().viewerPosY;
                double z = p.location.getZ();
                this.mc.getRenderManager();
                double n3 = z - mc.getRenderManager().viewerPosZ;
                GlStateManager.pushMatrix();
                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
                GlStateManager.translate((float) n, (float) n2, (float) n3);
                GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                float textY;
                if (this.mc.gameSettings.thirdPersonView == 2) {
                    textY = -1.0f;
                } else {
                    textY = 1.0f;
                }
                GlStateManager.rotate(this.mc.getRenderManager().playerViewX, textY, 0.0f, 0.0f);
                p.location.sizeAnim = p.location.animationUtils.animate(40f, p.location.sizeAnim, 0.04f);
                final double size = p.location.sizeAnim * 0.001f;
                GlStateManager.scale(-size, -size, size);
                enableGL2D();
                disableGL2D();
                GL11.glDepthMask(false);
                this.mc.fontRendererObj.drawStringWithShadow(p.text,
                        (float) (-(this.mc.fontRendererObj.getStringWidth(p.text) / 2)),
                        (float) (-(this.mc.fontRendererObj.FONT_HEIGHT - 1)),
                        new Color(255, 0,
                                0).getRGB());
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glDepthMask(true);
                GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
                GlStateManager.disablePolygonOffset();
                GlStateManager.popMatrix();
            }
        } catch (ConcurrentModificationException exception) {
            // TODO: 2021/7/4 fix it 
           
        }
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static double roundToPlace(double p_roundToPlace_0_, int p_roundToPlace_2_) {
        if (p_roundToPlace_2_ < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(p_roundToPlace_0_).setScale(p_roundToPlace_2_, RoundingMode.HALF_UP).doubleValue();
    }

    @EventHandler
    public void onUpdate(EventPostUpdate eventUpdate) {
        try {
            this.particles.forEach(this::onUpdate);
        } catch (ConcurrentModificationException e) {

        }
    }

    private void onUpdate(Particles update) {
        ++update.ticks;
        if (update.ticks <= 10) {
            update.location.setY(update.location.getY() + update.ticks * 0.005);
        }
        if (update.ticks > 20) {
            this.particles.remove(update);
        }
    }


    public static float mvoeUD(float current, float end, float minSpeed) {
        return moveUD(current, end, 0.125f, minSpeed);
    }

    public static float moveUD(float current, float end, float smoothSpeed, float minSpeed) {
        float movement = (end - current) * smoothSpeed;
        if (movement > 0.0f) {
            movement = Math.max((float) minSpeed, (float) movement);
            movement = Math.min((float) (end - current), (float) movement);
        } else if (movement < 0.0f) {
            movement = Math.min((float) (-minSpeed), (float) movement);
            movement = Math.max((float) (end - current), (float) movement);
        }
        return current + movement;
    }
}

class Particles {
    public int ticks;
    public Location location;
    public String text;

    public Particles(final Location location, final String text) {
        this.location = location;
        this.text = text;
        this.ticks = 0;
    }
}


class Location {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    public final AnimationUtils animationUtils = new AnimationUtils();
    public double sizeAnim = 0.2;


    public Location(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0.0F;
        this.pitch = 0.0F;
    }

    public Location(BlockPos pos) {
        this.x = (double) pos.getX();
        this.y = (double) pos.getY();
        this.z = (double) pos.getZ();
        this.yaw = 0.0F;
        this.pitch = 0.0F;
    }

    public Location(int x, int y, int z) {
        this.x = (double) x;
        this.y = (double) y;
        this.z = (double) z;
        this.yaw = 0.0F;
        this.pitch = 0.0F;
    }

    public Location(EntityLivingBase entity) {
        this.x = entity.posX;
        this.y = entity.posY;
        this.z = entity.posZ;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }


    public Location add(int x, int y, int z) {
        this.x += (double) x;
        this.y += (double) y;
        this.z += (double) z;
        return this;
    }

    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location subtract(int x, int y, int z) {
        this.x -= (double) x;
        this.y -= (double) y;
        this.z -= (double) z;
        return this;
    }

    public Location subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Block getBlock() {
        return Minecraft.getMinecraft().theWorld.getBlockState(this.toBlockPos()).getBlock();
    }

    public double getX() {
        return this.x;
    }

    public Location setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return this.y;
    }

    public Location setY(double y) {
        this.y = y;
        return this;
    }

    public double getZ() {
        return this.z;
    }

    public Location setZ(double z) {
        this.z = z;
        return this;
    }

    public float getYaw() {
        return this.yaw;
    }

    public Location setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Location setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public static Location fromBlockPos(BlockPos blockPos) {
        return new Location(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.getX(), this.getY(), this.getZ());
    }

    public double distanceTo(Location loc) {
        double dx = loc.x - this.x;
        double dz = loc.z - this.z;
        double dy = loc.y - this.y;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distanceToY(Location loc) {
        double dy = loc.y - this.y;
        return Math.sqrt(dy * dy);
    }
}



