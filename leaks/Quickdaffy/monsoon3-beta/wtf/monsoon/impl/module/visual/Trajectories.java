/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.builder.BoxRenderMode;
import wtf.monsoon.api.util.render.builder.RenderBuilder;
import wtf.monsoon.impl.event.EventRender3D;
import wtf.monsoon.impl.module.visual.Accent;

public class Trajectories
extends Module {
    public static Setting<Boolean> box = new Setting<Boolean>("Box", true).describedBy("Draw the box");
    public static Setting<Boolean> line = new Setting<Boolean>("Line", true).describedBy("Draw the line");
    public static Setting<Float> lineWidth = new Setting<Float>("Width", Float.valueOf(1.0f)).minimum(Float.valueOf(1.0f)).maximum(Float.valueOf(0.1f)).incrementation(Float.valueOf(2.0f)).describedBy("How thick the line is").childOf(line);
    public static Setting<Float> maxVertexCount = new Setting<Float>("MaxVertexCount", Float.valueOf(1000.0f)).minimum(Float.valueOf(100.0f)).maximum(Float.valueOf(3000.0f)).incrementation(Float.valueOf(1.0f)).describedBy("The maximum amount of vertices to draw").childOf(line);
    @EventLink
    private final Listener<EventRender3D> render3DListener = event -> {
        ItemStack stack = this.mc.thePlayer.getHeldItem();
        if (stack == null) {
            return;
        }
        if (stack.getItem() instanceof ItemBow && this.mc.thePlayer.getItemInUse() != null || stack.getItem() instanceof ItemSnowball || stack.getItem() instanceof ItemEgg) {
            Accent.EnumAccents enumeration;
            Vec3 position = new Vec3(this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * (double)this.mc.getTimer().renderPartialTicks - Math.cos(Math.toRadians(this.mc.thePlayer.rotationYaw)) * (double)0.16f, this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * (double)this.mc.getTimer().renderPartialTicks + (double)this.mc.thePlayer.getEyeHeight() - 0.15, this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * (double)this.mc.getTimer().renderPartialTicks - Math.sin(Math.toRadians(this.mc.thePlayer.rotationYaw)) * (double)0.16f);
            Vec3 velocity = new Vec3(-Math.sin(Math.toRadians(this.mc.thePlayer.rotationYaw)) * Math.cos(Math.toRadians(this.mc.thePlayer.rotationPitch)) * (double)(stack.getItem() instanceof ItemBow ? 1.0f : 0.4f), -Math.sin(Math.toRadians(this.mc.thePlayer.rotationPitch)) * (double)(stack.getItem() instanceof ItemBow ? 1.0f : 0.4f), Math.cos(Math.toRadians(this.mc.thePlayer.rotationYaw)) * Math.cos(Math.toRadians(this.mc.thePlayer.rotationPitch)) * (double)(stack.getItem() instanceof ItemBow ? 1.0f : 0.4f));
            double motion = Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y + velocity.z * velocity.z);
            velocity = new Vec3(velocity.x / motion, velocity.y / motion, velocity.z / motion);
            double power = stack.getItem() instanceof ItemBow ? MathHelper.clamp_double((float)(72000 - this.mc.thePlayer.getItemInUseCount()) / 20.0f * ((float)(72000 - this.mc.thePlayer.getItemInUseCount()) / 20.0f) + (float)(72000 - this.mc.thePlayer.getItemInUseCount()) / 20.0f * 2.0f, 0.0, 1.0) * 3.0 : 1.5;
            velocity = new Vec3(velocity.x * power, velocity.y * power, velocity.z * power);
            int vertexCount = 0;
            GL11.glPushMatrix();
            GL11.glDisable((int)3553);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glDisable((int)2929);
            GL11.glEnable((int)2848);
            GL11.glLineWidth((float)lineWidth.getValue().floatValue());
            GL11.glBegin((int)3);
            int i = 0;
            while ((float)i < maxVertexCount.getValue().floatValue()) {
                Color combined = ColorUtil.fadeBetween(3, i * 15, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
                enumeration = Wrapper.getModule(Accent.class).accents.getValue();
                if (enumeration.equals((Object)Accent.EnumAccents.ASTOLFO)) {
                    combined = ColorUtil.astolfoColorsC(i * 5, i * 20);
                } else if (enumeration.equals((Object)Accent.EnumAccents.RAINBOW)) {
                    combined = ColorUtil.rainbow((long)i * 300L);
                }
                if (line.getValue().booleanValue()) {
                    ColorUtil.glColor(combined.getRGB());
                    GL11.glVertex3d((double)(position.x - this.mc.getRenderManager().viewerPosX), (double)(position.y - this.mc.getRenderManager().viewerPosY), (double)(position.z - this.mc.getRenderManager().viewerPosZ));
                }
                position = new Vec3(position.x + velocity.x * 0.1, position.y + velocity.y * 0.1, position.z + velocity.z * 0.1);
                velocity = new Vec3(velocity.x, velocity.y - (stack.getItem() instanceof ItemBow ? 0.05 : (stack.getItem() instanceof ItemPotion ? 0.4 : (stack.getItem() instanceof ItemExpBottle ? 0.1 : 0.03))) * 0.1, velocity.z);
                vertexCount = i++;
                MovingObjectPosition result = this.mc.theWorld.rayTraceBlocks(EntityUtil.getInterpolatedPosition(this.mc.thePlayer).add(new Vec3(0.0, this.mc.thePlayer.getEyeHeight(), 0.0)), new Vec3(position.x, position.y, position.z));
                if (result != null) break;
            }
            GL11.glEnd();
            GL11.glDisable((int)3042);
            GL11.glEnable((int)3553);
            GL11.glEnable((int)2929);
            GL11.glDepthMask((boolean)true);
            GL11.glDisable((int)2848);
            GL11.glPopMatrix();
            if (box.getValue().booleanValue()) {
                AxisAlignedBB bb = new AxisAlignedBB(position.x - this.mc.getRenderManager().viewerPosX - 0.25, position.y - this.mc.getRenderManager().viewerPosY - 0.25, position.z - this.mc.getRenderManager().viewerPosZ - 0.25, position.x - this.mc.getRenderManager().viewerPosX + 0.25, position.y - this.mc.getRenderManager().viewerPosY + 0.25, position.z - this.mc.getRenderManager().viewerPosZ + 0.25);
                Color colour = ColorUtil.fadeBetween(3, vertexCount * 15, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
                enumeration = Wrapper.getModule(Accent.class).accents.getValue();
                if (enumeration.equals((Object)Accent.EnumAccents.ASTOLFO)) {
                    colour = ColorUtil.astolfoColorsC(vertexCount * 5, vertexCount * 20);
                } else if (enumeration.equals((Object)Accent.EnumAccents.RAINBOW)) {
                    colour = ColorUtil.rainbow((long)vertexCount * 300L);
                }
                new RenderBuilder().boundingBox(bb).innerColour(ColorUtil.integrateAlpha(colour, 100.0f)).outerColour(ColorUtil.integrateAlpha(colour, 255.0f)).type(BoxRenderMode.FILL).start().blend().depth().texture().build(false);
            }
        }
    };

    public Trajectories() {
        super("Trajectories", "uhhhh yeah uhhh draws lines when ur holding a bow", Category.VISUAL);
    }
}

