/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.util.glu.GLU
 *  org.lwjgl.util.vector.Vector3f
 *  org.lwjgl.util.vector.Vector4f
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.DrawUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.event.EventBlur;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.event.EventRender3D;

public class ChestESP
extends Module {
    public static ChestESP INSTANCE;
    public Setting<Mode> mode = new Setting<Mode>("Mode", Mode.HOLLOW).describedBy("The mode of ESP");
    public Setting<Boolean> blur = new Setting<Boolean>("Blur", true).describedBy("Whether to draw blurred rect");
    public Setting<Boolean> glow = new Setting<Boolean>("Glow", true).describedBy("Whether to render the glow.");
    public Setting<Boolean> outline = new Setting<Boolean>("Outlines", true).describedBy("Whether to draw the outlines");
    public Setting<EnumColor> color = new Setting<EnumColor>("Color", EnumColor.MONSOON).describedBy("The color of the ESP");
    public Setting<Color> customColor = new Setting<Color>("CustomColor", new Color(0, 140, 255)).describedBy("The color of the ESP").visibleWhen(() -> this.color.getValue() == EnumColor.CUSTOM);
    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer((int)4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final Map<TileEntity, float[]> entityPosMap = new HashMap<TileEntity, float[]>();
    private static final Map<TileEntity, float[][]> entities;
    @EventLink
    public final Listener<EventRender3D> eventRender3D = e -> {
        if (this.mode.getValue() == Mode.FILLED || this.mode.getValue() == Mode.HOLLOW || this.mode.getValue() == Mode.BOTH) {
            ScaledResolution sr = new ScaledResolution(this.mc);
            entities.keySet().removeIf(player -> !this.mc.theWorld.loadedTileEntityList.contains(player));
            if (!this.entityPosMap.isEmpty()) {
                this.entityPosMap.clear();
            }
            int scaleFactor = sr.getScaleFactor();
            for (Object fuck : this.mc.theWorld.loadedTileEntityList) {
                Vector3f projection;
                Vector4f position;
                double[][] vectors;
                AxisAlignedBB bb;
                float posZ;
                float posY;
                float posX;
                Vec3 vec3;
                TileEntity player2;
                if (fuck instanceof TileEntityChest) {
                    player2 = (TileEntityChest)fuck;
                    GlStateManager.pushMatrix();
                    vec3 = this.getVec3(player2);
                    posX = (float)(vec3.x - this.mc.getRenderManager().viewerPosX);
                    posY = (float)(vec3.y - this.mc.getRenderManager().viewerPosY);
                    posZ = (float)(vec3.z - this.mc.getRenderManager().viewerPosZ);
                    bb = new AxisAlignedBB(posX + 1.0f, posY + 1.0f, posZ + 1.0f, posX, posY, posZ);
                    vectors = new double[][]{{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ}, {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ}, {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
                    position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0f, -1.0f);
                    for (double[] vec : vectors) {
                        projection = this.project2D((float)vec[0], (float)vec[1], (float)vec[2], scaleFactor);
                        if (projection == null || !(projection.z >= 0.0f) || !(projection.z < 1.0f)) continue;
                        position.x = Math.min(position.x, projection.x);
                        position.y = Math.min(position.y, projection.y);
                        position.z = Math.max(position.z, projection.x);
                        position.w = Math.max(position.w, projection.y);
                    }
                    this.entityPosMap.put(player2, new float[]{position.x, position.z, position.y, position.w});
                    GlStateManager.popMatrix();
                    continue;
                }
                if (!(fuck instanceof TileEntityEnderChest)) continue;
                player2 = (TileEntityEnderChest)fuck;
                GlStateManager.pushMatrix();
                vec3 = this.getVec3(player2);
                posX = (float)(vec3.x - this.mc.getRenderManager().viewerPosX);
                posY = (float)(vec3.y - this.mc.getRenderManager().viewerPosY);
                posZ = (float)(vec3.z - this.mc.getRenderManager().viewerPosZ);
                bb = new AxisAlignedBB(posX + 1.0f, posY + 1.0f, posZ + 1.0f, posX, posY, posZ);
                vectors = new double[][]{{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ}, {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ}, {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
                position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0f, -1.0f);
                for (double[] vec : vectors) {
                    projection = this.project2D((float)vec[0], (float)vec[1], (float)vec[2], scaleFactor);
                    if (projection == null || !(projection.z >= 0.0f) || !(projection.z < 1.0f)) continue;
                    position.x = Math.min(position.x, projection.x);
                    position.y = Math.min(position.y, projection.y);
                    position.z = Math.max(position.z, projection.x);
                    position.w = Math.max(position.w, projection.y);
                }
                this.entityPosMap.put(player2, new float[]{position.x, position.z, position.y, position.w});
                GlStateManager.popMatrix();
            }
        }
    };
    @EventLink
    public final Listener<EventBlur> eventBlur = e -> {
        if (this.blur.getValue().booleanValue()) {
            for (TileEntity player : this.entityPosMap.keySet()) {
                GL11.glPushMatrix();
                float[] positions = this.entityPosMap.get(player);
                float x = positions[0];
                float x2 = positions[1];
                float y = positions[2];
                float y2 = positions[3];
                Gui.drawRect(x, y, x2, y2, -1);
                GL11.glPopMatrix();
            }
        }
    };
    @EventLink
    public final Listener<EventRender2D> eventRender2D = e -> {
        Color color = ColorUtil.fadeBetween(20, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        Color alphaColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
        int colorInt = color.getRGB();
        if (this.mode.getValue() == Mode.FILLED || this.mode.getValue() == Mode.HOLLOW || this.mode.getValue() == Mode.BOTH) {
            for (TileEntity player : this.entityPosMap.keySet()) {
                if (this.color.getValue().equals((Object)EnumColor.CHEST_TYPE)) {
                    colorInt = player instanceof TileEntityEnderChest ? new Color(211, 3, 252).getRGB() : new Color(252, 248, 3).getRGB();
                }
                GL11.glPushMatrix();
                float[] positions = this.entityPosMap.get(player);
                float x = positions[0];
                float x2 = positions[1];
                float y = positions[2];
                float y2 = positions[3];
                switch (this.mode.getValue()) {
                    case FILLED: {
                        Color shit1 = new Color(colorInt);
                        Color shit2 = new Color(shit1.getRed(), shit1.getGreen(), shit1.getBlue(), 100);
                        Gui.drawRect(x, y, x2, y2, shit2.getRGB());
                        break;
                    }
                    case HOLLOW: {
                        GL11.glDisable((int)3553);
                        ChestESP.enableAlpha();
                        ChestESP.disableAlpha();
                        if (this.outline.getValue().booleanValue()) {
                            DrawUtil.drawHollowRectDefineWidth(x - 0.5f, y - 0.5f, x2 - 0.5f, y2 - 0.5f, 0.5f, -1778384896);
                            DrawUtil.drawHollowRectDefineWidth(x + 0.5f, y + 0.5f, x2 + 0.5f, y2 + 0.5f, 0.5f, -1778384896);
                        }
                        DrawUtil.drawHollowRectDefineWidth(x, y, x2, y2, 0.5f, colorInt);
                        GL11.glEnable((int)3553);
                        break;
                    }
                    case BOTH: {
                        Color shit3 = new Color(colorInt);
                        Color shit4 = new Color(shit3.getRed(), shit3.getGreen(), shit3.getBlue(), 100);
                        Gui.drawRect(x, y, x2, y2, shit4.getRGB());
                        GL11.glDisable((int)3553);
                        ChestESP.enableAlpha();
                        ChestESP.disableAlpha();
                        if (this.outline.getValue().booleanValue()) {
                            DrawUtil.drawHollowRectDefineWidth(x - 0.5f, y - 0.5f, x2 - 0.5f, y2 - 0.5f, 0.5f, -1778384896);
                            DrawUtil.drawHollowRectDefineWidth(x + 0.5f, y + 0.5f, x2 + 0.5f, y2 + 0.5f, 0.5f, -1778384896);
                        }
                        DrawUtil.drawHollowRectDefineWidth(x, y, x2, y2, 0.5f, colorInt);
                        GL11.glEnable((int)3553);
                    }
                }
                float width = x2 - x;
                float height = y2 - y;
                float g = width / 5.0f;
                float h = height / 5.0f;
                if (this.glow.getValue().booleanValue()) {
                    RoundedUtils.shadow(x, y, width, height, 0.0f, 10.0f, new Color(colorInt));
                }
                GL11.glPopMatrix();
            }
        }
    };

    public ChestESP() {
        super("Chest ESP", "Helps you see chests in the world.", Category.VISUAL);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static int getColorFromPercentage(float current, float max) {
        float percentage = current / max / 3.0f;
        return Color.HSBtoRGB(percentage, 1.0f, 1.0f);
    }

    private Vector3f project2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat((int)2982, (FloatBuffer)this.modelMatrix);
        GL11.glGetFloat((int)2983, (FloatBuffer)this.projectionMatrix);
        GL11.glGetInteger((int)2978, (IntBuffer)this.viewport);
        if (GLU.gluProject((float)x, (float)y, (float)z, (FloatBuffer)this.modelMatrix, (FloatBuffer)this.projectionMatrix, (IntBuffer)this.viewport, (FloatBuffer)this.windowPosition)) {
            return new Vector3f(this.windowPosition.get(0) / (float)scaleFactor, ((float)this.mc.displayHeight - this.windowPosition.get(1)) / (float)scaleFactor, this.windowPosition.get(2));
        }
        return null;
    }

    public static void enableAlpha() {
        GL11.glEnable((int)3042);
        GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
    }

    public static void disableAlpha() {
        GL11.glDisable((int)3042);
    }

    private Vec3 getVec3(TileEntity var0) {
        float timer = this.mc.getTimer().renderPartialTicks;
        double x = (float)var0.getPos().getX() + 0.0f * timer;
        double y = (float)var0.getPos().getY() + 0.0f * timer;
        double z = (float)var0.getPos().getZ() + 0.0f * timer;
        return new Vec3(x, y, z);
    }

    static {
        entities = new HashMap<TileEntity, float[][]>();
    }

    static enum EnumColor {
        CUSTOM(() -> ChestESP.INSTANCE.customColor.getValue().getRGB()),
        ASTOLFO(() -> ColorUtil.astolfoColors(14, 14)),
        MONSOON(() -> ColorUtil.fadeBetween(new Color(0, 140, 255).getRGB(), new Color(0, 255, 255).getRGB(), (float)(System.currentTimeMillis() % 1500L) / 750.0f)),
        RAINBOW(() -> ColorUtil.rainbow(4)),
        CHEST_TYPE(() -> -1);

        private final Supplier<Integer> colour;

        private EnumColor(Supplier<Integer> colour) {
            this.colour = colour;
        }

        public int getColor() {
            return this.colour.get();
        }
    }

    static enum Mode {
        HOLLOW,
        FILLED,
        BOTH;

    }
}

