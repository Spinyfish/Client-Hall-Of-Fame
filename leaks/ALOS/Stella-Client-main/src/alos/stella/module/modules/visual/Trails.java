package alos.stella.module.modules.visual;


import alos.stella.Stella;
import alos.stella.event.EventState;
import alos.stella.event.EventTarget;
import alos.stella.event.events.MotionEvent;
import alos.stella.event.events.Render3DEvent;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.utils.ColorUtils;
import alos.stella.utils.render.DrawUtils;
import alos.stella.utils.render.GLUtils;
import alos.stella.value.BoolValue;
import alos.stella.value.FloatValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Trails", description = "Leaves a trail behind you.", category = ModuleCategory.VISUAL)
public class Trails extends Module {

    private final ListValue trailsMode = new ListValue("TrailsMode", new String[]{"Line","Circle"}, "Line");
    public final BoolValue timeoutBool = new BoolValue("Timeout", false);
    public final FloatValue timeout = new FloatValue("Time", 0F, 1F, 10F);

    public static final ListValue colorMode = new ListValue("TrailsColor", new String[]{"Astolfo", "Rainbow", "Fade", "Custom"}, "Astolfo");
    public static final IntegerValue colorRedValue = new IntegerValue("Red", 0, 0, 255);
    public static final IntegerValue colorGreenValue = new IntegerValue("Green", 160, 0, 255);
    public static final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
    public final IntegerValue removeticks = new IntegerValue("RemoveTicks", 100, 1, 500);
    public final IntegerValue alpha = new IntegerValue("AlphaTrails", 255, 1, 255);

    public final BoolValue smoothending = new BoolValue("Smooth Ending", false);

    public static final FloatValue saturationValue = new FloatValue("saturation", 0.5f, 0f, 1f);
    public static final FloatValue brightnessValue = new FloatValue("brightness", 1f, 0f, 1f);

    public final BoolValue onlyMeTrails = new BoolValue("OnlyMe", false);

    List<Vector3d> path = new ArrayList<>();
    ArrayList<Point> points = new ArrayList<Point>();

    @Override
    public void onDisable() {
        points.clear();
        super.onDisable();
    }

    @EventTarget
    public void onPreMotion(MotionEvent event) {
        if (event.getEventState() == EventState.PRE) {
            if (trailsMode.get().equalsIgnoreCase("Circle")) {

                if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
                    path.add(new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
                }

                if (timeoutBool.get())
                    while (path.size() > (int) timeout.get().floatValue()) {
                        path.remove(0);
                    }
            }
        }
    }
    @EventTarget
    public void onRender3DEvent(Render3DEvent event3D) {
        if (trailsMode.get().equalsIgnoreCase("Circle")) {
            DrawUtils.drawRenderBreadCrumbs(path);
        }
    }

    @EventTarget
    public void onRender(Render3DEvent event) {
        if (trailsMode.get().equalsIgnoreCase("Line")) {

            if ((mc.gameSettings.thirdPersonView == 1 || mc.gameSettings.thirdPersonView == 3)) {

                points.removeIf(p -> p.age >= removeticks.get());

                float x = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.getPartialTicks());
                float y = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.getPartialTicks());
                float z = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.getPartialTicks());

                points.add(new Point((float) (x), y, (float) (z)));


                GL11.glPushMatrix();
                GL11.glDisable(GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(GL11.GL_CULL_FACE);
                for (final Point t : points) {
                    if (points.indexOf(t) >= points.size() - 1) continue;

                    Point temp = points.get(points.indexOf(t) + 1);

                    float a = alpha.get();
                    if (smoothending.get())
                        a = alpha.get() * (points.indexOf(t) / (float) points.size());

                    Color color = Color.WHITE;
                    switch (colorMode.get()) {
                        case "Custom":
                            color = new Color(Trails.colorRedValue.get(), Trails.colorGreenValue.get(), Trails.colorBlueValue.get());
                            break;
                        case "Rainbow":
                            color = new Color(alos.stella.utils.render.ColorUtils.getRainbowOpaque(2,Trails.saturationValue.get(), Trails.brightnessValue.get(), 0));
                            break;
                        case "Astolfo":
                            color = alos.stella.utils.render.ColorUtils.skyRainbow(0, Trails.saturationValue.get(), Trails.brightnessValue.get());
                            break;
                        case "Fade":
                            color = ColorUtils.fade(new Color(Trails.colorRedValue.get(), Trails.colorGreenValue.get(), Trails.colorBlueValue.get()), 0, 100);
                            break;
                    }
                    Color c = alos.stella.utils.render.ColorUtils.injectAlpha(color, (int) a);

                    glBegin(GL_QUAD_STRIP);
                    final double x2 = t.x - mc.getRenderManager().renderPosX;
                    final double y2 = t.y - mc.getRenderManager().renderPosY;
                    final double z2 = t.z - mc.getRenderManager().renderPosZ;

                    final double x1 = temp.x - mc.getRenderManager().renderPosX;
                    final double y1 = temp.y - mc.getRenderManager().renderPosY;
                    final double z1 = temp.z - mc.getRenderManager().renderPosZ;

                    alos.stella.utils.render.ColorUtils.color(new Color(c.getRed(), c.getGreen(), c.getBlue(), 0).getRGB());
                    if (PlayerEdit.baby.get() && Stella.moduleManager.getModule(PlayerEdit.class).getState()) {
                        glVertex3d(x2, y2 + mc.thePlayer.height - 0.7 - 0.1, z2);
                    }else{
                        glVertex3d(x2, y2 + mc.thePlayer.height - 0.1, z2);
                    }
                    alos.stella.utils.render.ColorUtils.color(c.getRGB());
                    glVertex3d(x2, y2 + 0.2, z2);
                    if (PlayerEdit.baby.get() && Stella.moduleManager.getModule(PlayerEdit.class).getState()) {
                        glVertex3d(x1, y1 + mc.thePlayer.height - 0.7 - 0.1, z1);
                    }else {
                        glVertex3d(x1, y1 + mc.thePlayer.height - 0.1, z1);
                    }
                    glVertex3d(x1, y1 + 0.2, z1);
                    glEnd();
                    ++t.age;
                }
                GlStateManager.resetColor();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL_CULL_FACE);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }
    class Point {
        public final float x, y, z;

        public float age = 0;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}