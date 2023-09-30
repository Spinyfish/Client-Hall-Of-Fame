package felix.module.impl.movement;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.events.render.EventRender3D;
import felix.module.Module;
import felix.module.impl.combat.KillAura;
import felix.util.visual.RenderUtil;
import felix.value.impl.BooleanValue;
import felix.value.impl.NumberValue;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public final class TargetStrafe extends Module {
	
	public TargetStrafe() {
		super("TargetStrafe", 0, ModuleCategory.MOVEMENT);
		setHidden(true);
		addValues(radius, holdspace, render);
	}

	public final NumberValue<Double> radius = new NumberValue<>("Radius", 2.0D, 0.1D, 4.0D, 0.1D);
    public final BooleanValue holdspace = new BooleanValue("Hold Space", true);
    private final BooleanValue render = new BooleanValue("Render", true);

    private final List<Vector3f> points = new ArrayList<>();

    public byte direction;

    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
        if (event.isPre()) {
            if (mc.thePlayer.isCollidedHorizontally) {
                direction = (byte) -direction;
                return;
            }

            if (mc.gameSettings.keyBindLeft.isKeyDown()) {
                direction = 1;
                return;
            }

            if (mc.gameSettings.keyBindRight.isKeyDown())
                direction = -1;
        }
    }
    
    public void onEnable() {
    	super.onEnable();
    }
    
    public void onDisable() {
    	super.onDisable();
    }
    
    public static void drawLinesAroundPlayer(Entity entity, RenderManager renderManager, double radius, float partialTicks, int points, float width, int color) {
    	glPushMatrix();
    	glDisable(GL_TEXTURE_2D);
    	glEnable(GL_LINE_SMOOTH);
    	glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
    	glDisable(GL_DEPTH_TEST);
    	glLineWidth(width);
    	glEnable(GL_BLEND);
    	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    	glDisable(GL_DEPTH_TEST);
    	glBegin(GL_LINE_STRIP);
    	final double x = RenderUtil.interpolate(entity.prevPosX, entity.posX, partialTicks) - renderManager.viewerPosX;
    	final double y = RenderUtil.interpolate(entity.prevPosY, entity.posY, partialTicks) - renderManager.viewerPosY;
    	final double z = RenderUtil.interpolate(entity.prevPosZ, entity.posZ, partialTicks) - renderManager.viewerPosZ;
    	RenderUtil.glColor(color);
    	for (int i = 0; i <= points; i++)
    		glVertex3d(x + radius * Math.cos(i * Math.PI * 2 / points), y, z + radius * Math.sin(i * Math.PI * 2 / points));
    	glEnd();
    	glDepthMask(true);
    	glDisable(GL_BLEND);
    	glEnable(GL_DEPTH_TEST);
    	glDisable(GL_LINE_SMOOTH);
    	glEnable(GL_DEPTH_TEST);
    	glEnable(GL_TEXTURE_2D);
    	glPopMatrix();
    }
    
    @Handler
    public void onRender3D(final EventRender3D event) {
    	final KillAura killAura = (KillAura) Client.INSTANCE.getModuleManager().getModule(KillAura.class);
    	final Speed speed = (Speed) Client.INSTANCE.getModuleManager().getModule(Speed.class);
    	final Flight flight = (Flight) Client.INSTANCE.getModuleManager().getModule(Flight.class);
    	for (Entity entity : mc.theWorld.getLoadedEntityList()) {
    		boolean colorchange = speed.isEnabled() || flight.isEnabled();
    		int color = 0;
        	if (killAura.target == entity && colorchange && !this.holdspace.isEnabled()) {
        		color = Color.green.getRGB();
        	}
        	else if (killAura.target == entity && colorchange && this.holdspace.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown()) {
        		color = Color.green.getRGB();
        	} else {
        		color = Color.white.getRGB();
        	}
	    	if (render.isEnabled() && entity != null && entity instanceof EntityPlayer && entity != mc.thePlayer && killAura.target == entity) {
	    		drawLinesAroundPlayer(entity, mc.getRenderManager(), radius.getValue(), event.getPartialTicks(), 12, 3f, Color.black.getRGB());
	    		drawLinesAroundPlayer(entity, mc.getRenderManager(), radius.getValue(), event.getPartialTicks(), 12, 2, color);
	        }
    	}
    }
}
