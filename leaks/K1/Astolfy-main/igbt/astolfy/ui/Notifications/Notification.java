package igbt.astolfy.ui.Notifications;

import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;

import igbt.astolfy.Astolfy;
import igbt.astolfy.module.visuals.Hud;
import igbt.astolfy.utils.AnimationUtils;
import igbt.astolfy.utils.AnimationUtils.AnimationType;
import igbt.astolfy.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class Notification {

    private long start;
    private long end;
    private NotificationType type;
    private String title;
    private String desc;
    private double duration;
    private double scale = 0;
    private double lastY = 0;
    private boolean init = true;
    private String originalDesc;

    private AnimationUtils animationUtil = new AnimationUtils(AnimationType.SCALE);
    
    public Notification(NotificationType type,String title, String desc, double duration) {
        this.type = type;
        this.title = title;
        this.desc = desc;
        this.duration = duration;
        this.originalDesc = desc;
    }
    public double getTime(){ return end - System.currentTimeMillis(); }
    public void showNotification() {
        start = System.currentTimeMillis();
        end = System.currentTimeMillis() + (int)(duration * 1000L);
    }

    public void renderNotification(int count){
    	if(Minecraft.getMinecraft().theWorld != null) {
    		if(originalDesc.contains("Loading World..... L1")) {
    			CopyOnWriteArrayList<Entity> ent = new CopyOnWriteArrayList<>();
    			for(Entity e : Minecraft.getMinecraft().theWorld.loadedEntityList) {
    				if(e instanceof EntityPlayer && e != Minecraft.getMinecraft().thePlayer) {
    					ent.add(e);
    				}
    			}
    			desc = "Loaded Players: " + ent.size();
    		}
    	}
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int sizeX = 200;
        sizeX = Math.max(sizeX, mc.customFont.getStringWidth(desc) + 24 + 30);
        int sizeY = 45;
        int x = sr.getScaledWidth() - 20 - sizeX;
        int y = sr.getScaledHeight() - 5 - 65 - (count * 50);
        switch(Hud.notificationType.getCurrentValue()){
        case "Top":
            x = (sr.getScaledWidth() - sizeX) / 2;
            y = 5 + (count * 50);
            break;
        case "Left":
            x = 5;
            if(mc.currentScreen instanceof GuiChat)
            	y -= 200;
            break;
    }
        if(init) {
        	lastY = y;
        	init = false;
        }
        if(hasExpired()){
            NotificationManager.removeNotification(this);
        }
        GlStateManager.pushMatrix();
        if(getTime() > duration * 1000 - 100) {
        	double add =  1- ((getTime() - ((duration * 1000) - 100)) / 100);
        	animationUtil.Render(add, x + (sizeX / 2), y + (sizeY / 2));
        }
        if(getTime() < 100) {
        	double add =  ((getTime()) / 100);
        	animationUtil.Render(add, x + (sizeX / 2), y + (sizeY / 2));
        }

        ResourceLocation icon;
        switch(type){
            case SUCCESS:
                icon = new ResourceLocation("Images/Notification-Icons/Success.png");
                break;
            case ERROR:
                icon = new ResourceLocation("Images/Notification-Icons/Error.png");
                break;
            case INFORMATION:
                icon = new ResourceLocation("Images/Notification-Icons/Information.png");
                break;
            case WARNING:
                icon = new ResourceLocation("Images/Notification-Icons/Warning.png");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        long downloaded = System.currentTimeMillis() - start;
        long total = end - start;
        long percent = (sizeX * downloaded) / total;

        Gui.drawRect(x,y,x + sizeX,y + sizeY,0x80000000);
        RenderUtils.gradientSideways(x, y + sizeY - 2, percent,2, new Color(Hud.getColor(0)), new Color(Hud.getColor(10)));

        int iconSize = 24;

        Gui.drawImage(mc,x + 10,y + (sizeY - iconSize) / 2,iconSize,icon);

        mc.customFont.drawString(title,x + iconSize + 20,y + 10,-1);
        mc.customFont.drawString(desc, x + iconSize + 20,y + 25,-1);
        GlStateManager.popMatrix();
    }

    public boolean hasExpired(){
        return System.currentTimeMillis() > end;
    }
}
