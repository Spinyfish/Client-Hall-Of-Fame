package dev.elysium.client.mods.impl.movement;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.*;
import dev.elysium.client.mods.impl.combat.Killaura;
import dev.elysium.client.utils.Timer;
import dev.elysium.client.utils.player.MovementUtil;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.block.BlockLadder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

public class TargetStrafe extends Mod {

    public Killaura killaura;
    public NumberSetting radius = new NumberSetting("Radius", 0,6, 1, 0.1, this);
    public BooleanSetting space = new BooleanSetting("Hold Space", false,this);

    public int direction = 1;
    public float yaw = 0F;
    public double prevBPS;
    public float ticks;
    public boolean strafing;
    public boolean shouldswitch;
    public boolean test;
    public Timer timer = new Timer();


    public TargetStrafe() {
        super("TargetStrafe","Moves around the target", Category.MOVEMENT);
    }


    public void onEnabled() {

        this.strafing = false;
        if(killaura == null)
            killaura = (Killaura) Elysium.getInstance().getModManager().getModByName("Killaura");

    }

    public void onDisable() {

        this.strafing = false;

    }

    @EventTarget
    public void onEventRender3D(EventRender3D e) {
        if(killaura == null)
            killaura = (Killaura) Elysium.getInstance().getModManager().getModByName("Killaura");
        if(Elysium.getInstance().targets.isEmpty()) return;
        EntityLivingBase target = Elysium.getInstance().targets.get(0);

        float red = 0;
        float green = 0;
        float blue = 0;

        boolean MovementModuleEnabled = Elysium.getInstance().getModManager().getModByName("Fly").toggled
                || Elysium.getInstance().getModManager().getModByName("Speed").toggled/**
                || Elysium.getInstance().getModManager().getModByName("Longjump").toggled**/;

        if (killaura.toggled && !Elysium.getInstance().targets.isEmpty() && Elysium.getInstance().targets.get(0) != null && (MovementModuleEnabled && !space.isEnabled() || space.isEnabled() && mc.gameSettings.keyBindJump.pressed && MovementModuleEnabled || Elysium.getInstance().getModManager().getModByName("Fly").toggled && space.isEnabled())) {
            red = 0.5f;
            green = 1f;
            blue = 0.5f;
            strafing = true;
        } else if (!MovementModuleEnabled || space.isEnabled() && !mc.gameSettings.keyBindJump.isPressed()) {
            red = 1f;
            green = 1f;
            blue = 1f;
            strafing = false;
        }

        if (target != null && !target.isDead && killaura.toggled) {
            drawCircle2(target, e.getPartialTicks(), (Double) radius.getValue());
            drawCircle(target, e.getPartialTicks(), (Double) radius.getValue(),red,green,blue);
        }
    }

    @EventTarget
    public void onEventMoveFlying(EventMoveFlying e) {
        if(strafing && !Elysium.getInstance().targets.isEmpty() && Elysium.getInstance().targets.get(0) != null) {
            e.setYaw(this.yaw);
            strafe(MovementUtil.getSpeed());
        }
    }
    
    @EventTarget
    public void onEventJump(EventJump e) {
        if(strafing && !Elysium.getInstance().targets.isEmpty() && Elysium.getInstance().targets.get(0) != null) {
            strafe(MovementUtil.getSpeed());
            e.setYaw(this.yaw);
            e.setSprinting(true);
        }
    }
    
    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        if (!killaura.toggled || !Elysium.getInstance().targets.isEmpty() && Elysium.getInstance().targets.get(0) == null) {
            strafing = false;
        } else if(strafing) {
            mc.gameSettings.keyBindLeft.pressed = false;
            mc.gameSettings.keyBindRight.pressed = false;
        }
    }
    
    @EventTarget
    public void onEventMotion(EventMotion e) {
        if(strafing && !Elysium.getInstance().targets.isEmpty()) {
            EntityLivingBase target = Elysium.getInstance().targets.get(0);

            if(target == null || !killaura.toggled) return;

            if (!target.isDead && mc.thePlayer.isMoving()) {

                if (!mc.thePlayer.isCollidedHorizontally && shouldswitch) test = true;

                BlockPos blockunderpos;
                if (shouldswitch && test && (mc.thePlayer.isCollidedHorizontally && !space.isEnabled() || mc.thePlayer.isCollidedHorizontally && mc.gameSettings.keyBindJump.isPressed() && space.isEnabled())) {
                    mc.thePlayer.motionX *= -1;
                    mc.thePlayer.motionZ *= -1;

                    switchDirection();
                    test = false;
                }

                blockunderpos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);

                boolean goingtoground = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -2.6, 0).expand(-0.3, -2, -0.3)).isEmpty();

                if (!shouldswitch && !goingtoground && !blockunderpos.getBlock().getMaterial().isLiquid() && !(blockunderpos.getBlock() instanceof BlockLadder)) {
                    shouldswitch = true;
                }

                if (goingtoground || blockunderpos.getBlock().getMaterial().isLiquid() || blockunderpos.getBlock() instanceof BlockLadder) {
                    if (shouldswitch && !Elysium.getInstance().getModManager().getModByName("Fly").toggled) {
                        mc.thePlayer.motionX *= -1;
                        mc.thePlayer.motionZ *= -1;
                        switchDirection();
                        shouldswitch = false;
                    }
                }

                float ty = target.rotationYaw;
            }
        }
    }

    private void switchDirection() {
        direction *= -1;
        EntityLivingBase target = Elysium.getInstance().targets.get(0);
        float dir = (float) Math.toRadians(ticks);
        double deltaX = mc.thePlayer.posX + (target.posX - target.lastTickPosX) - target.posX;
        double deltaY = mc.thePlayer.posY - 3.5 + target.getEyeHeight() * 1.3185 - target.posY + mc.thePlayer.getEyeHeight() - 0.25592;
        double deltaZ = mc.thePlayer.posZ + (target.posZ - target.lastTickPosZ) - target.posZ;
        deltaZ -=  (Math.cos(dir) * (radius.getValue()+0.35+MovementUtil.getSpeed()));
        deltaX += (Math.sin(dir) * (radius.getValue()+0.35+MovementUtil.getSpeed()));
        if(mc.thePlayer.getDistance(deltaX + mc.thePlayer.posX, deltaZ + mc.thePlayer.posZ) <= radius.getValue()/radius.getValue()*radius.getValue()) ticks += ((radius.max+(radius.max+-radius.getValue())-radius.getValue())*(radius.getValue()/radius.getValue()))*direction;

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ));
        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = (float)(90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = (float)(-90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }

        ticks = yaw;

    }

    public void strafe(float moveSpeed) {
        double prevradius = radius.getValue();
        EntityLivingBase target = Elysium.getInstance().targets.get(0);
        float dir = (float) Math.toRadians(ticks);
        double bps = target.getDistance(target.lastTickPosX, target.posY,target.lastTickPosZ) * (20 * mc.timer.timerSpeed);
        double prevBPS = this.prevBPS;
        this.prevBPS = bps;
        if(prevBPS > bps) bps = prevBPS;
        double deltaX = target.posX + (target.posX - target.lastTickPosX) * bps - mc.thePlayer.posX;
        double deltaZ = target.posZ + (target.posZ - target.lastTickPosZ) * bps - mc.thePlayer.posZ;
        deltaZ -=  (Math.cos(dir) * (prevradius*1.6));
        deltaX += (Math.sin(dir) * (prevradius*1.6));

        if(mc.thePlayer.getDistance(deltaX + mc.thePlayer.posX, deltaZ + mc.thePlayer.posZ) -
                MovementUtil.getSpeed() <= prevradius)
            ticks +=
                    (20*Math.sqrt(prevradius))*direction;

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ));
        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = (float)(90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = (float)(-90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }

        this.yaw = yaw;
        MovementUtil.strafe(moveSpeed, yaw);
    }

    private void drawCircle(EntityLivingBase entity, float partialTicks, double rad, float red, float green, float blue) {
        float circlerads = 6;

        double difx = mc.getRenderManager().viewerPosX - entity.posX;
        double difZ = mc.getRenderManager().viewerPosZ - entity.posZ;

        start();
        mc.entityRenderer.disableLightmap();
        GL11.glLineWidth(3F);
        GL11.glBegin(3);

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;

        RenderUtils.glColor(0xFF7dff90);

        for (int i = 0; i <= 90; ++i) {
            GL11.glColor3f(red, green, blue);
            GL11.glVertex3d(x + rad * Math.cos((double) i * Math.PI / circlerads), y, z + rad * Math.sin((double) i * Math.PI / circlerads));
        }

        end();
    }
    private void drawCircle2(EntityLivingBase entity, float partialTicks, double rad) {
        float circlerads = 6;

        double difx = mc.getRenderManager().viewerPosX - entity.posX;
        double difZ = mc.getRenderManager().viewerPosZ - entity.posZ;
        start();

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;

        GL11.glLineWidth(5F);
        mc.entityRenderer.disableLightmap();
        GL11.glBegin(3);
        RenderUtils.glColor(0xFF050505);

        for (int i = 0; i <= 90; ++i) {
            GL11.glVertex3d(x + rad * Math.cos((double) i * Math.PI / circlerads), y, z + rad * Math.sin((double) i * Math.PI / circlerads));
        }
        end();
        start();
        GL11.glLineWidth(5F);
        mc.entityRenderer.disableLightmap();
        GL11.glBegin(3);
        RenderUtils.glColor(0x22222222);
        for (int i = 0; i <= 90; ++i) {
            GL11.glVertex3d(x + rad * Math.cos((double) i * Math.PI / circlerads), y, z + rad * Math.sin((double) i * Math.PI / circlerads));
        }
        end();
        start();
        GL11.glLineWidth(10F);
        mc.entityRenderer.disableLightmap();
        GL11.glBegin(3);
        RenderUtils.glColor(0x11222222);
        for (int i = 0; i <= 90; ++i) {
            GL11.glVertex3d(x + rad * Math.cos((double) i * Math.PI / circlerads), y, z + rad * Math.sin((double) i * Math.PI / circlerads));
        }

        end();
    }

    public void start() {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glBlendFunc(770, 771);
    }

    public void end() {
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
        GL11.glEnd();
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }
}
