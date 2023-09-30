package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.render.NameTagsEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.math.vector.Vec2f;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "Name Tags", description = "Phoenix haven is <insert bad here>", category = Category.VISUAL)
public class NameTagsModule extends Module {

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_3D) {
            mc.theWorld.playerEntities.stream().filter(entity -> !Client.getInstance().getIgnoreManager()
                    .isIgnore(entity, false)).forEach(entity -> {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_DEPTH_TEST);

                Vec3 position = this.interpolatePosition(entity);

                double distanceSquared = mc.thePlayer.getDistanceSqToEntity(entity);
                double distance = ApacheMath.sqrt(distanceSquared);
                double scale = ApacheMath.max(distanceSquared / distance, 5);

                double sneakOffset = entity.isSneaking() ? 0.4 : 0;

                Vec2f rotation = new Vec2f(
                        mc.thePlayer.rotationYaw + (mc.gameSettings.thirdPersonView == 2 ? 180 : 0),
                        (mc.gameSettings.thirdPersonView == 2 ? -mc.thePlayer.rotationPitch : mc.thePlayer.rotationPitch)
                );

                GL11.glTranslated(position.getX(), (position.getY() + entity.height - sneakOffset) + 0.8, position.getZ());
                GL11.glRotated(rotation.getX(), 0, -1, 0);
                GL11.glRotated(rotation.getY(), 1, 0, 0);
                GL11.glScaled(-0.03, -0.03, 0);

                if (entity instanceof EntityPlayerSP && mc.gameSettings.thirdPersonView != 0) {
                    GL11.glScaled(0.7, 0.7, 0.7);
                } else {
                    GL11.glScaled(scale *= 0.09, scale, scale);
                }

                this.renderTag(entity);

                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glPopMatrix();
            });
        }
    };

    @EventHandler
    EventCallback<NameTagsEvent> onNametags = event -> {
        if (event.getEntity() instanceof EntityPlayer && !Client.getInstance().getIgnoreManager()
                .isIgnore((EntityPlayer) event.getEntity(), false)) {
            event.cancel();
        }
    };

    void renderTag(EntityPlayer entity) {
        String health = MathUtils.roundToDecimal(entity.getHealth(), 1) + " §c❤";
        String name = entity.getName() + " §7|";

        double nameLength = Fonts.getInstance().getArialBdNormal().getStringWidth(name);
        double healthOffset = Fonts.getInstance().getArialBdNormal().getStringWidth(health);

        RenderUtils.drawRect(
                -(nameLength / 2f) - 2 - (healthOffset / 2f), -2,
                ((nameLength + healthOffset) / 2f) + 5,
                10.5, new Color(0, 0, 0, 120).getRGB()
        );

        Fonts.getInstance().getArialBdNormal().drawCenteredStringWithShadow(name, (float) -(healthOffset / 2f), 0, -1);

        int healthColor = Color.HSBtoRGB(
                ApacheMath.max(0.0F, ApacheMath.min(entity.getHealth(),
                        entity.getMaxHealth()) / entity.getMaxHealth()) / 3.0F,
                1.0F, 0.75F
        ) | 0xFF000000;

        Fonts.getInstance().getArialBdNormal().drawCenteredStringWithShadow(health, (float) ((nameLength / 2f) + 4), 0, healthColor);
    }

    Vec3 interpolatePosition(Entity entity) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;

        x -= RenderManager.renderPosX;
        y -= RenderManager.renderPosY;
        z -= RenderManager.renderPosZ;

        return new Vec3(x, y, z);
    }
}