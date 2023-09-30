package Reality.Realii.mods.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRender2D;
import Reality.Realii.event.events.rendering.EventRender3D;
import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.managers.FriendManager;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.ClientSettings;
import Reality.Realii.mods.modules.player.Teams;
import Reality.Realii.mods.modules.world.Scaffold;
import Reality.Realii.utils.cheats.player.PlayerUtils;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.AnimationUtils;
import Reality.Realii.utils.math.MathUtil;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Killaura extends Module {

    public static EntityLivingBase target;
    private Option mob = new Option("Mob", false);
    private Option animals = new Option("Animals", false);
    private Option player = new Option("Player", true);
    
    private Option invisible = new Option("Invisible", false);
    private Option block = new Option("Block", false);
    private Option smart = new Option("SmartSwitch", false);
    private Option noSwing = new Option("NoSwing", false);
    private Option esp = new Option("Esp", true);
    private Option Autusim = new Option("Autism", true);
  


    private Mode mode = new Mode("Mode", "Mode", new String[]{"Switch", "Single", "Multi"}, "Switch");
    private Mode rot = new Mode("RotationMode", "RotationMode", new String[]{"Instant", "Animate", "none","Funny"}, "Animate");
    private Mode priority = new Mode("Priority", "Priority", new String[]{"Distance", "Health", "Direction"}, "Distance");
    private Mode blockhit = new Mode("autoblock", "autoblock", new String[]{"Vannila", "None",}, "None");
    private Mode tracers = new Mode("tracers", "tracers", new String[]{"Tracers", "None",}, "tracers");
    private Mode boxesp12 = new Mode("BoxEsp", "BoxEsp", new String[]{"On", "off",}, "off");
    private Mode prepost = new Mode("KillauraHitEvent", "KillauraHitEvent", new String[]{"OnPre", "OnPost",}, "OnPre");
    private Mode BoxEspColor = new Mode("BoxEspColort", "BoxEspColort", new String[]{"Client", "Black","Green"}, "Client");


    private Numbers<Number> switchDelay = new Numbers<>("SwitchDelay", 150, 10, 2000, 10);
    private Numbers<Number> rotSpeed = new Numbers<>("RotationSpeed", 80, 1, 100, 1);
    private Numbers<Number> Maxcps = new Numbers<>("MaxCPS", 11, 1, 20, 0.1);
    private Numbers<Number> minimumcps = new Numbers<>("MinimumCPS", 11, 1, 18, 0.1);
    private Numbers<Number> range = new Numbers<>("Range", 3.8, 1, 6, 0.1);
    private Numbers<Number> targets = new Numbers<>("Targets", 3, 1, 6, 1);

    static ArrayList<EntityLivingBase> curTargets = new ArrayList<>();

    private TimerUtil timer = new TimerUtil();
    private TimerUtil cpsTimer = new TimerUtil();
    private AnimationUtils animationUtils1 = new AnimationUtils();
    private AnimationUtils animationUtils2 = new AnimationUtils();
    private AnimationUtils espAnimation = new AnimationUtils();
    private float yPos = 0;
    private boolean direction;
    private int cur = 0;
    private Criticals crit;

    public Killaura() {
        super("KillAura", ModuleType.Combat);
        addValues(priority, esp , Maxcps, minimumcps, range, targets, mob, animals, player, invisible, rot, switchDelay, rotSpeed, mode, smart, noSwing, Autusim, blockhit,tracers,prepost, boxesp12, BoxEspColor);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        crit = (Criticals) ModuleManager.getModuleByClass(Criticals.class);
    }

    @EventHandler
    private void onRender3D(EventRender3D e) {
        if (target != null && ((boolean) esp.getValue())) {
            drawShadow(target, e.getPartialTicks(), yPos, direction);
            drawCircle(target, e.getPartialTicks(), yPos);
        }
    }
    TimerUtil timerUtil = new TimerUtil();
    @EventHandler
    private void onRender2D(EventRender2D e) {
        if(timerUtil.delay(20)) {
            if (direction) {
                yPos += 0.03;
                if (2 - yPos < 0.02) {
                    direction = false;
                }
            } else {
                yPos -= 0.03;
                if (yPos < 0.02) {
                    direction = true;
                }
            }
            timerUtil.reset();
        }
    }

    private void drawShadow(Entity entity, float partialTicks, float pos, boolean direction) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel((int) 7425);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY + pos;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;
        GL11.glBegin(GL11.GL_QUAD_STRIP);
        Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
        for (int i = 0; i <= 12; i++) {
            double c1 = i * Math.PI * 2 / 12;
            double c2 = (i + 1) * Math.PI * 2 / 12;
            GL11.glColor4f(c.getRed() / 255f, (float) c.getGreen() / 255f, (float) c.getBlue() / 255f, 0.23f);
            GL11.glVertex3d(x + 0.7 * Math.cos(c1), y, z + 0.7 * Math.sin(c1));
            GL11.glVertex3d(x + 0.7 * Math.cos(c2), y, z + 0.7 * Math.sin(c2));
            GL11.glColor4f(c.getRed() / 255f, (float) c.getGreen() / 255f, (float) c.getBlue() / 255f, 0.0f);

            GL11.glVertex3d(x + 0.7 * Math.cos(c1), y + (direction ? -0.3 : 0.3), z + 0.7 * Math.sin(c1));
            GL11.glVertex3d(x + 0.7 * Math.cos(c2), y + (direction ? -0.3 : 0.3), z + 0.7* Math.sin(c2));


        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel((int) 7424);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void drawCircle(Entity entity, float partialTicks, float pos) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel((int) 7425);
        GL11.glLineWidth(2);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY + pos;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;
        GL11.glBegin(GL11.GL_LINE_STRIP);
        Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
        for (int i = 0; i <= 12; i++) {
            double c1 = i * Math.PI * 2 / 12;
            GL11.glColor4f(c.getRed() / 255f, (float) c.getGreen() / 255f, (float) c.getBlue() / 255f, 1);
// GL11.glVertex3d(x + 0.5 * Math.cos(c1), y, z + 0.5 * Math.sin(c1));


        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel((int) 7424);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    
    @EventHandler
    public void onRender(EventRender3D event) {
    	if (this.boxesp12.getValue().equals("On")) {
    	if (Killaura.target.hurtTime > -1) {
    		
             this.doBoxESP(event);
    		 }
    	 if (Killaura.target != null) {
    		
    		 
    	 }
    	
    	 }  
    	if (this.boxesp12.getValue().equals("Off")) {
    		 
    	 }
    	
         }
    
    
    private void doBoxESP(EventRender3D event) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        for (Object o2 : this.mc.theWorld.loadedEntityList) {
            if (!(o2 instanceof EntityPlayer) || o2 == Killaura.target) continue;
           
            
            if (this.BoxEspColor.getValue().equals("Client")) {
            	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
            	 entityESPBox2(Killaura.target, new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue(), 70), event);
            }
            if (this.BoxEspColor.getValue().equals("Black")) {
            entityESPBox2(Killaura.target, new Color(000, 000, 000, 70), event);
            }
            if (this.BoxEspColor.getValue().equals("Green")) {
            entityESPBox2(Killaura.target, new Color(000, 255, 000, 70), event);
            }

        }
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }
    
    public static void entityESPBox2(Entity e, Color color, EventRender3D event) {
        double posX = Killaura.target.lastTickPosX + (e.posX - e.lastTickPosX) * (double) event.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().renderPosX;
        double posY = Killaura.target.lastTickPosY + (e.posY - e.lastTickPosY) * (double) event.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().renderPosY;
        double posZ = Killaura.target.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) event.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().renderPosZ;
        GL11.glColor4f((float) ((float) color.getRed() / 255.0f), (float) ((float) color.getGreen() / 255.0f), (float) ((float) color.getBlue() / 255.0f), color.getAlpha() / 255.0f);
        RenderUtil.drawBoundingBox((AxisAlignedBB) new AxisAlignedBB(posX + 0.4, posY, posZ - 0.4, posX - 0.4, posY + 1.8, posZ + 0.4));
        RenderUtil.drawOutlinedBoundingBox((AxisAlignedBB) new AxisAlignedBB(posX + 0.4, posY, posZ - 0.4, posX - 0.4, posY + 1.8, posZ + 0.4));
    }
    
    @EventHandler
    private void on3DRender(EventRender3D e) {
    	
        for (Object o : this.mc.theWorld.loadedEntityList) {
            double[] arrd;
            Entity entity = (Entity)o;
            if (!entity.isEntityAlive() || !(entity instanceof EntityPlayer) || entity == this.mc.thePlayer) continue;
            double posX = Killaura.target.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)e.getPartialTicks() - RenderManager.renderPosX;
            double posY = Killaura.target.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)e.getPartialTicks() - RenderManager.renderPosY;
            double posZ = Killaura.target.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)e.getPartialTicks() - RenderManager.renderPosZ;
            
            RenderUtil.startDrawing();

            this.mc.entityRenderer.setupCameraTransform(this.mc.timer.renderPartialTicks, 2);
  
            float color = (float)Math.round(255.0 - this.mc.thePlayer.getDistanceSqToEntity(entity) * 255.0 / MathUtil.square((double)this.mc.gameSettings.renderDistanceChunks * 2.5)) / 255.0f;
            if (FriendManager.isFriend(entity.getName())) {
                double[] arrd2 = new double[3];
                arrd2[0] = 0.0;
                arrd2[1] = 1.0;
                arrd = arrd2;
                arrd2[2] = 1.0;
            } else {
                double[] arrd3 = new double[3];
                arrd3[0] = color;
                arrd3[1] = 1.0f - color;
                arrd = arrd3;
                arrd3[2] = 0.0;
            }
            this.drawLine(Killaura.target, arrd, posX, posY, posZ);
            
            RenderUtil.stopDrawing();
        }
    	}
    

    private void drawLine(Entity entity, double[] color, double x, double y, double z) {
    	if (tracers.getValue().equals("Tracers")) {
        float distance = this.mc.thePlayer.getDistanceToEntity(entity);
        float xD = distance / 48.0f;
        if (xD >= 1.0f) {
            xD = 1.0f;
        }
        boolean entityesp = false;
        GL11.glEnable((int)2848);
        if (color.length >= 4) {
            if (color[3] <= 0.1) {
                return;
            }
            GL11.glColor4d((double)color[0], (double)color[1], (double)color[2], (double)color[3]);
        } else {
            GL11.glColor3d((double)color[0], (double)color[1], (double)color[2]);
        }
        GL11.glLineWidth((float)1.0f);
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)0.0, (double)this.mc.thePlayer.getEyeHeight(), (double)0.0);
        GL11.glVertex3d((double)x, (double)y, (double)z);
        GL11.glEnd();
       
        
    }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        target = null;
        curTargets.clear();
        mc.gameSettings.keyBindUseItem.pressed = false;
    }

    public void filter(List<? extends Entity> entities) {
        curTargets.clear();
        target = null;
        for (Entity entity : entities) {
            if (entity == mc.thePlayer) continue;
            if (!entity.isEntityAlive()) continue;
            if (Teams.isOnSameTeam(entity)) continue;
            if (AntiBots.isServerBot(entity)) continue;
            if (curTargets.size() > targets.getValue().intValue()) continue;
            if (mc.thePlayer.getDistanceToEntity(entity) > range.getValue().doubleValue()) continue;

            if (entity instanceof EntityMob && ((boolean) mob.getValue())) curTargets.add((EntityLivingBase) entity);

            if (entity instanceof EntityAnimal && ((boolean) animals.getValue()))
                curTargets.add((EntityLivingBase) entity);

            if (entity instanceof EntityPlayer && ((boolean) player.getValue())) {
                if (entity.isInvisible() && ((boolean) invisible.getValue()))
                    curTargets.add((EntityLivingBase) entity);
                else if (!entity.isInvisible())
                    curTargets.add((EntityLivingBase) entity);
            }

            
        }
    }

    float cYaw, cPitch;

    @EventHandler
    public void onPre(EventPreUpdate e) {
    	if (this.prepost.getValue().equals("OnPre")) {
    	

        this.setSuffix(this.mode.getModeAsString());
        if (ModuleManager.getModuleByClass(Scaffold.class).isEnabled())
            return;
        filter(mc.theWorld.getLoadedEntityList());
        switchTarget();
        if (curTargets.size() != 0) {
            if (cur > curTargets.size() - 1) {
                cur = 0;
            }
            target = curTargets.get(cur);
            float[] rotations = PlayerUtils.getRotations(target);
            rotate(rotations, e);
        }
        
    	if (this.blockhit.getValue().equals("None")) {

     	   
    	}
        
        if (ModuleManager.getModuleByClass(Scaffold.class).isEnabled())
            return;
        if (cpsTimer.delay(1000 / Maxcps.getValue().floatValue()) && target != null) {
            if (mc.thePlayer.isBlocking())
            crit.doCrit(target);
            if ((boolean) noSwing.getValue()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            } else {
                mc.thePlayer.swingItem();
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        	if (this.blockhit.getValue().equals("Vannila")) {

        	    mc.gameSettings.keyBindUseItem.pressed = true;
        	if (mc.thePlayer.inventory.getCurrentItem() != null)
        	    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());

        	}
            
            
            
            cpsTimer.reset();
        }
        
        if (mc.thePlayer.isBlocking() && target == null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }
    	}
    }


    @EventHandler
    public void onPost(EventPostUpdate e) {
    if (this.prepost.getValue().equals("OnPost")) {
    	

        this.setSuffix(this.mode.getModeAsString());
        if (ModuleManager.getModuleByClass(Scaffold.class).isEnabled())
            return;
        filter(mc.theWorld.getLoadedEntityList());
        switchTarget();
        if (curTargets.size() != 0) {
            if (cur > curTargets.size() - 1) {
                cur = 0;
            }
            target = curTargets.get(cur);
            float[] rotations = PlayerUtils.getRotations(target);
            rotatepost(rotations, e);
        }
        
    	if (this.blockhit.getValue().equals("None")) {

     	   
    	}
        
        if (ModuleManager.getModuleByClass(Scaffold.class).isEnabled())
            return;
        if (cpsTimer.delay(1000 / Maxcps.getValue().floatValue()) && target != null) {
            if (mc.thePlayer.isBlocking())
                
            crit.doCrit(target);
            if ((boolean) noSwing.getValue()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            } else {
                mc.thePlayer.swingItem();
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        	if (this.blockhit.getValue().equals("Vannila")) {

        	    mc.gameSettings.keyBindUseItem.pressed = true;
        	if (mc.thePlayer.inventory.getCurrentItem() != null)
        	    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());

        	}
            
            
            
            cpsTimer.reset();
        }
        
        if (mc.thePlayer.isBlocking() && target == null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }
    	}
    }
    	
    	
    
    

    private boolean canBlock() {
        return ((boolean) block.getValue()) && (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) && target != null;
    }

    private void rotate(float[] rotations, EventPreUpdate e) {
        switch (rot.getModeAsString()) {
            case "Instant":
                cYaw = rotations[0];
                cPitch = rotations[1];
                PlayerUtils.rotate(cYaw, cPitch);
                e.setYaw(cYaw);
                e.setPitch(cPitch);
                break;
            case "Animate":
                if (cYaw == 0 || cPitch == 0) {
                    cYaw = mc.thePlayer.rotationYaw;
                    cPitch = mc.thePlayer.rotationPitch;
                }
                cYaw = animationUtils1.animate(rotations[0], cYaw, rotSpeed.getValue().intValue() / 100f);
                cPitch = animationUtils2.animate(rotations[0], cPitch, rotSpeed.getValue().intValue() / 100f);
                PlayerUtils.rotate(cYaw, cPitch);
                e.setYaw(cYaw);
                e.setPitch(cPitch);
            case "none":
            	
        }
    }
    
    private void rotatepost (float[] rotations, EventPostUpdate e) {
        switch (rot.getModeAsString()) {
            case "Instant":
                cYaw = rotations[0];
                cPitch = rotations[1];
                PlayerUtils.rotate(cYaw, cPitch);
                e.setYaw(cYaw);
                e.setPitch(cPitch);
                break;
            case "Animate":
                if (cYaw == 0 || cPitch == 0) {
                    cYaw = mc.thePlayer.rotationYaw;
                    cPitch = mc.thePlayer.rotationPitch;
                }
                cYaw = animationUtils1.animate(rotations[0], cYaw, rotSpeed.getValue().intValue() / 100f);
                cPitch = animationUtils2.animate(rotations[1], cPitch, rotSpeed.getValue().intValue() / 100f);
                PlayerUtils.rotate(cYaw, cPitch);
                e.setYaw(cYaw);
                e.setPitch(cPitch);
            case "none":
            	
        }
    }

    public float getRotationDis(EntityLivingBase entity) {
        float pitch = PlayerUtils.getRotations(entity)[1];
        return mc.thePlayer.rotationPitch - pitch;
    }

    private void switchTarget() {
        if (mode.isValid("Switch")) {
            if (timer.delay(switchDelay.getValue().intValue())) {
                if (cur < curTargets.size() - 1) {
                    if (((boolean) smart.getValue() && target != null && target.getHealth() < 5)) {
                        timer.reset();
                        return;
                    }
                    cur++;
                } else {
                    cur = 0;
                }
                timer.reset();
            }
        } else if (mode.isValid("Single")) {
            switch (priority.getValue().toString()) {
                case "Distance":
                    curTargets.sort(((o1, o2) -> (int) (o2.getDistanceToEntity(mc.thePlayer) - o1.getDistanceToEntity(mc.thePlayer))));
                    break;
                case "Health":
                    curTargets.sort(((o1, o2) -> (int) (o2.getHealth() - o1.getHealth())));
                    break;
                case "Direction":
                    curTargets.sort(((o1, o2) -> (int) (getRotationDis(o2) - getRotationDis(o1))));
                    break;
            }
            cur = 0;
        } else if (mode.isValid("Multi")) {
            if (timer.delay(80)) {
                if (cur < curTargets.size() - 1) {
                    if (((boolean) smart.getValue() && target != null && target.getHealth() < 5)) {
                        timer.reset();
                        return;
                    }
                    cur++;
                } else {
                    cur = 0;
                }
                timer.reset();
            }
        }
    }
}
