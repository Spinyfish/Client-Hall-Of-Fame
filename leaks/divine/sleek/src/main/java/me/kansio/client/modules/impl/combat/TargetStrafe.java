package me.kansio.client.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.Render3DEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.NumberValue;
import net.minecraft.entity.Entity;

import static org.lwjgl.opengl.GL11.*;

@ModuleData(
        name = "Target Strafe",
        category = ModuleCategory.COMBAT,
        description = "Automatically strafes around the killaura target"
)
public class TargetStrafe extends Module {

    public static double dir = -1;

    public BooleanValue autoF5 = new BooleanValue("Auto F5", this, false);
    public BooleanValue jump = new BooleanValue("On Jump", this, false);
    public BooleanValue control = new BooleanValue("Controllable", this, false);
    public BooleanValue render = new BooleanValue("Render", this, false);
    public NumberValue<Double> radius = new NumberValue<>("Radius", this, 3d, 0.1d, 5d, 0.1);
    public NumberValue<Double> width = new NumberValue<>("Width", this, 1d, 0.1d, 5d, 0.1, render);
    public NumberValue<Double> red = new NumberValue<>("Red", this, 100d, 0d, 255d, 1d, render);
    public NumberValue<Double> green = new NumberValue<>("Green", this, 100d, 0d, 255d, 1d, render);
    public NumberValue<Double> blue = new NumberValue<>("Blue", this, 100d, 0d, 255d, 1d, render);

    @Override
    public void onDisable() {
        dir = -1;
    }

    @Subscribe
    public void onMotion(UpdateEvent event) {
        if (canStrafe() && autoF5.getValue()) {
            mc.gameSettings.thirdPersonView = 1;
        } else if (!canStrafe() && autoF5.getValue()) {
            mc.gameSettings.thirdPersonView = 0;
        }

        if (event.isPre()) {
            if (control.getValue()) {
                if (mc.gameSettings.keyBindLeft.isPressed()) {
                    dir = 1;
                } else if (mc.gameSettings.keyBindRight.isPressed()) {
                    dir = -1;
                }
            }

            if (mc.thePlayer.isCollidedHorizontally) {
                invertStrafe();
            }
        }
    }

    private void invertStrafe() {
        dir = -dir;
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (canStrafe() && render.getValue()) {
            drawCircle(KillAura.target, mc.timer.renderPartialTicks);
        }
    }

    public boolean canStrafe() {
        if (!Client.getInstance().getModuleManager().getModuleByName("KillAura").isToggled()) {
            return false;
        }

        if (!this.isToggled()) {
            return false;
        }

        if (jump.getValue()) {
            if (mc.gameSettings.keyBindJump.isKeyDown())
                return KillAura.target != null;
            else
                return false;
        } else {
            return KillAura.target != null;
        }
    }

    private void drawCircle(Entity entity, float partialTicks) {
        glPushMatrix();
        glColor3d(red.getValue(), green.getValue(), blue.getValue());
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(width.getValue().floatValue());
        glBegin(GL_LINE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;


        final double pix2 = Math.PI * 2.0D;
        for (int i = 0; i <= 90; ++i) {
            glVertex3d(x + (radius.getValue() - 0.5) * Math.cos(i * pix2 / 45), y, z + (radius.getValue() - 0.5) * Math.sin(i * pix2 / 45));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }
}
