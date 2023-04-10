package crispy.features.hacks.impl.movement;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.render.Event3D;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.Aura;
import crispy.util.player.SpeedUtils;
import crispy.util.rotation.LookUtils;
import crispy.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.NumberValue;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@HackInfo(name = "TargetStrafe", category = Category.MOVEMENT)
public class TargetStrafe extends Hack {
    private static int direction = -1;
    private final BooleanValue directionKeys = new BooleanValue("Direction-Keys", true);
    static NumberValue<Double> radius = new NumberValue<Double>("Range", 4.0D, 1D, 8D);
    BooleanValue drawCircle = new BooleanValue("Draw Circle", true);

    private final TimeHelper timeHelper = new TimeHelper();
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            if (mc.thePlayer.isCollidedHorizontally && this.timeHelper.hasReached(200L)) {
                direction = -direction;
                this.timeHelper.reset();
            }

        } else if(e instanceof Event3D) {

            if (drawCircle.getObject() && Aura.target != null) {
                drawCircle(Aura.target, ((Event3D) e).getPartialTicks(), radius.getObject());

            }
        }
    }
    public static boolean doStrafeAtSpeed(final double moveSpeed) {
        Minecraft mc = Minecraft.getMinecraft();
        final boolean strafe = canStrafe();

        if (strafe) {
            float[] rotations = LookUtils.getRotations(Aura.target);
            if (mc.thePlayer.getDistanceToEntity(Aura.target) <= radius.getObject()) {
                SpeedUtils.setSpeed(moveSpeed, rotations[0], direction, 0);
            } else {
                SpeedUtils.setSpeed(moveSpeed, rotations[0], direction, 1);
            }
        }
        return strafe;
    }


    private void drawCircle(Entity entity, float partialTicks, double rad) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(1.0f);
        glBegin(GL_LINE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;

        final float r = ((float) 1 / 255) * Color.WHITE.getRed();
        final float g = ((float) 1 / 255) * Color.WHITE.getGreen();
        final float b = ((float) 1 / 255) * Color.WHITE.getBlue();

        final double pix2 = Math.PI * 2.0D;

        for (int i = 0; i <= 90; ++i) {
            glColor3f(r, g, b);
            glVertex3d(x + rad * Math.cos(i * pix2 / 45.0), y, z + rad * Math.sin(i * pix2 / 45.0));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }
    public static boolean canStrafe() {
        return Aura.target != null && Crispy.INSTANCE.getHackManager().getHack(TargetStrafe.class).isEnabled();
    }


}
