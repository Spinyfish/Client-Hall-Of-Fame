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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.event.EventRender3D;

public class ChinaHat
extends Module {
    private final Setting<Color> topColour = new Setting<Color>("TopColour", new Color(0, 140, 255)).describedBy("The color of the top of the hat");
    private final Setting<Color> bottomColour = new Setting<Color>("BottomColour", new Color(0, 140, 255).darker()).describedBy("The color of the bottom of the hat");
    private final Setting<Boolean> others = new Setting<Boolean>("Others", true).describedBy("Whether to render hats other players");
    @EventLink
    public final Listener<EventRender3D> eventRender3DListener = event -> this.mc.theWorld.playerEntities.forEach(player -> {
        if (player == this.mc.thePlayer && this.mc.gameSettings.thirdPersonView == 0 || !this.others.getValue().booleanValue() && player != this.mc.thePlayer) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2832);
        GL11.glEnable((int)3042);
        GL11.glShadeModel((int)7425);
        GlStateManager.disableCull();
        GL11.glBegin((int)5);
        Vec3 vec = EntityUtil.getInterpolatedPosition(player).add(new Vec3(-this.mc.getRenderManager().viewerPosX, -this.mc.getRenderManager().viewerPosY + (double)player.getEyeHeight() + 0.41 + (player.isSneaking() ? -0.2 : 0.0), -this.mc.getRenderManager().viewerPosZ));
        for (double i = 0.0; i < Math.PI * 2; i += 0.09817477042468103) {
            ColorUtil.glColor(this.bottomColour.getValue().getRGB());
            GL11.glVertex3d((double)(vec.x + 0.65 * Math.cos(i)), (double)(vec.y - 0.25), (double)(vec.z + 0.65 * Math.sin(i)));
            ColorUtil.glColor(this.topColour.getValue().getRGB());
            GL11.glVertex3d((double)vec.x, (double)vec.y, (double)vec.z);
        }
        GL11.glEnd();
        GL11.glShadeModel((int)7424);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2848);
        GlStateManager.enableCull();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2832);
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
    });

    public ChinaHat() {
        super("ChinaHat", "ching chong -69420 social credit!", Category.VISUAL);
    }
}

