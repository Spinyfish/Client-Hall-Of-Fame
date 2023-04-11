package byron.Mono.module.impl.combat;

import byron.Mono.event.impl.Event3D;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.module.impl.movement.Speed;
import byron.Mono.utils.*;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

@ModuleInterface(name = "TargetStrafe", description = "no way it works.", category = Category.Combat)
public class TargetStrafe extends Module {

    private Killaura aura;
    private AuraUtil auraUtil;
    private RotationUtil rotationUtil;
    private int direction = -1;
    public double radius = 2.0D;
    private float distance;
    private boolean reset;


    public void setup()
    {
        super.setup();
        this.aura = new Killaura();
        this.auraUtil = new AuraUtil();
        this.rotationUtil = new RotationUtil();
    }

    public TargetStrafe()
    {
        this.aura = new Killaura();
        this.auraUtil = new AuraUtil();
    }

    private void switchDirection() {
        if (this.direction == 1) {
        	
            this.direction = -1;
        } else {
            this.direction = 1;
        }

    }
    private float getYaw() {
        final double x = (Killaura.target.posX - (Killaura.target.lastTickPosX - Killaura.target.posX)) - mc.thePlayer.posX;
        final double z = (Killaura.target.posZ - (Killaura.target.lastTickPosZ - Killaura.target.posZ)) - mc.thePlayer.posZ;

        return (float) (Math.toDegrees(Math.atan2(z, x)) - 90.0F);
    }
    public void onUpdate(EventUpdate e) {
      
        
        if (mc.thePlayer.isCollidedHorizontally) {
            this.switchDirection();
        }

        if (mc.gameSettings.keyBindLeft.isPressed()) {
            this.direction = 1;
        }

        if (mc.gameSettings.keyBindRight.isPressed()) {
            this.direction = -2;
        }

    }

    @Subscribe
    public void on3D(Event3D event) {
        if (Killaura.target == null) return;

        final Color theme = new Color(255,255,255);
        final Color color = new Color(theme.getRed(), theme.getGreen(), theme.getBlue(), 62);

        
        if (mc.gameSettings.keyBindJump.isKeyDown())
        		{
        		circle(Killaura.target, radius - 0.5, color);
        		}
         
    }

    @Subscribe
    public void strafe(EventUpdate e) {
        if(Killaura.toggled == true) {
        	
            double radius = 2.0D;
            EntityLivingBase target = Killaura.target;
            float[] rotations = rotationUtil.getRotations(target);
         
            if ((double) mc.thePlayer.getDistanceToEntity(target) <= Double.valueOf(radius)) {
                MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed(), rotations[0], (double) radius, 0D);
            } else {
                MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed(), rotations[0], (double) radius, 1D);

            }

        }

    }
    
    private void circle(final Entity entity, final double rad, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY) + 0.01;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        for (int i = 0; i <= 90; ++i) {
            RenderUtil.color(color);

            GL11.glVertex3d(x + rad * Math.cos(i * 6.283185307179586 / 45.0), y, z + rad * Math.sin(i * 6.283185307179586 / 45.0));
        }

        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }
    


    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
