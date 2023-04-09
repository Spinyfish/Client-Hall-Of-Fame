package tech.dort.dortware.impl.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.Render3DEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.modules.movement.Flight;
import tech.dort.dortware.impl.modules.movement.LongJump;
import tech.dort.dortware.impl.utils.movement.MotionUtils;

import static org.lwjgl.opengl.GL11.*;

// Credits: Helium
public class TargetStrafe extends Module {

    public static double dir = -1;

    public final NumberValue range = new NumberValue("Range", this, 3, 0.1, 6, SliderUnit.BLOCKS);
    private final BooleanValue render = new BooleanValue("Render", this, true);
    public final NumberValue width = new NumberValue("Width", this, 3, 1, 5, true, render);
    private final BooleanValue controllable = new BooleanValue("Controllable", this, true);
    private final BooleanValue jumpOnly = new BooleanValue("Jump Only", this, false);
    private final BooleanValue always = new BooleanValue("Always", this, true);

    public TargetStrafe(ModuleData moduleData) {
        super(moduleData);
        register(range, width, controllable, jumpOnly, always, render);
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (canStrafe() && render.getValue()) {
            drawCircle(KillAura.currentTarget, mc.timer.renderPartialTicks);
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (event.isPre()) {
            if (canStrafe() && always.getValue()) {
                MotionUtils.setMotion(MotionUtils.getSpeed());
            }

            if (controllable.getValue()) {
                if (mc.gameSettings.keyBindLeft.getIsKeyPressed()) {
                    dir = 1;
                } else if (mc.gameSettings.keyBindRight.getIsKeyPressed()) {
                    dir = -1;
                }
            }

            if (mc.thePlayer.isCollidedHorizontally || (!MotionUtils.isBlockUnder() && !Client.INSTANCE.getModuleManager().get(Flight.class).isToggled())) {
                invertStrafe();
            }
        }
    }

    private void invertStrafe() {
        dir = -dir;
    }

    private void drawCircle(Entity entity, float partialTicks) {
        glPushMatrix();
        glColor3d(255, 255, 255);
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
            glVertex3d(x + (range.getValue() - 0.5) * Math.cos(i * pix2 / 45), y, z + (range.getValue() - 0.5) * Math.sin(i * pix2 / 45));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static boolean canStrafe() {
        TargetStrafe targetStrafe = Client.INSTANCE.getModuleManager().get(TargetStrafe.class);
        return !targetStrafe.jumpOnly.getValue() ? KillAura.currentTarget != null && Client.INSTANCE.getModuleManager().get(TargetStrafe.class).isToggled() /*!Client.INSTANCE.getModuleManager().get(LongJump.class).isToggled()*/ : KillAura.currentTarget != null && Client.INSTANCE.getModuleManager().get(TargetStrafe.class).isToggled() && !Client.INSTANCE.getModuleManager().get(LongJump.class).isToggled() && mc.gameSettings.keyBindJump.getIsKeyPressed();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        dir = -1;
    }
}
