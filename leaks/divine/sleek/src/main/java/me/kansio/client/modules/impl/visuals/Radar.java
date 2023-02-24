package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.utils.render.ColorUtils;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleData(
        name = "Radar",
        category = ModuleCategory.VISUALS,
        description = "Shows player locations on the hud"
)
public class Radar extends Module {

    public BooleanValue players = new BooleanValue("Players", this, true);
    public BooleanValue monsters = new BooleanValue("Monsters", this, false);
    public BooleanValue animals = new BooleanValue("Animals", this, false);
    public BooleanValue invisible = new BooleanValue("Invisibles", this, false);

    @Subscribe
    public void onRenderOverlay(RenderOverlayEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        int miX = 5, miY = 70;
        int maX = miX + 100, maY = miY + 100;
        GL11.glPushMatrix();
        RenderUtils.drawRoundedRect(miX, miY, 100, maY - miY, 3, new Color(0, 0, 0, 100).getRGB());
        // drawing horizontal lines
        RenderUtils.drawRect(miX, miY - 1, 100, 1, ColorUtils.getColorFromHud(3).getRGB()); // top
        // drawing vertical lines
        RenderUtils.draw2DPolygon(maX / 2 + 3, miY + 52, 5f, 3, -1); // self

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scale = new ScaledResolution(mc).getScaleFactor();
        GL11.glScissor(miX * scale, mc.displayHeight - scale * 170, maX * scale - (scale * 5), scale * 100);

        for (Entity en : mc.theWorld.loadedEntityList) {
            if (en instanceof EntityMob && !monsters.getValue()) continue;
            if (en instanceof EntityAnimal && !animals.getValue()) continue;
            if (en instanceof EntityVillager && !animals.getValue()) continue;
            if (en.isInvisible() && !invisible.getValue()) continue;
            if (en == mc.thePlayer) continue;

            double dist_sq = mc.thePlayer.getDistanceSqToEntity(en);
            if (dist_sq > 360) {
                continue;
            }
            double x = en.posX - mc.thePlayer.posX, z = en.posZ - mc.thePlayer.posZ;
            double calc = Math.atan2(x, z) * 57.2957795131f;
            double angle = ((mc.thePlayer.rotationYaw + calc) % 360) * 0.01745329251f;
            double hypotenuse = dist_sq / 5;
            double x_shift = hypotenuse * Math.sin(angle), y_shift = hypotenuse * Math.cos(angle);
            RenderUtils.draw2DPolygon(maX / 2 + 3 - x_shift, miY + 52 - y_shift, 3f, 4, Client.getInstance().getFriendManager().isFriend(en.getName()) ? Color.cyan.getRGB() : Color.red.getRGB());
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

}
