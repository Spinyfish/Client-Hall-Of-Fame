package frapppyz.cutefurry.pics.modules.impl.combat;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Motion;
import frapppyz.cutefurry.pics.event.impl.Move;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Boolean;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.modules.settings.impl.Number;
import frapppyz.cutefurry.pics.util.ColorUtil;
import frapppyz.cutefurry.pics.util.MoveUtil;
import frapppyz.cutefurry.pics.util.RotUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Mouse;
import viamcp.ViaMCP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Killaura extends Mod {

    public static EntityLivingBase entity;
    private Mode rots = new Mode("Rotations", "Normal", "Normal", "Randomised", "Randomised2", "Movement", "None");
    private Mode reach = new Mode("Reach", "3.0", "3.0", "3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9", "4.0", "4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9", "5.0", "5.1", "5.2", "5.3", "5.4", "5.5", "5.6", "5.7", "5.8", "5.9", "6.0");

    private Mode ticks = new Mode("Tick Delay", "1", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private Mode keepsprint = new Mode("Keepsprint", "Vanilla", "Vanilla", "Drag", "Sexy", "Sexy2", "Off");
    public static Mode autoblock = new Mode("Autoblock", "Fake", "Fake", "Vanilla", "RightClick", "NCP", "Hypixel", "Off");

    public static boolean Attacking;
    private Boolean viadelay = new Boolean("1.9 Delay", false);
    public Killaura() {
        super("Killaura", "Attacks nearby entities.", 0, Category.COMBAT);
        addSettings(rots, reach, ticks, keepsprint, autoblock, viadelay);
    }

    public void onEnable(){
        Attacking = false;
        entity=null;
    }
    public void onDisable(){
        Attacking = false;
        entity=null;
        if(autoblock.is("RightClick")){
            mc.gameSettings.keyBindDrop.pressed = Mouse.isButtonDown(1);
        }
    }

    private List<EntityLivingBase> getValidTargets() {
        List<EntityLivingBase> targets = new ArrayList<>(mc.theWorld.loadedEntityList).stream()
                .filter(EntityLivingBase.class::isInstance)
                .map(entity -> (EntityLivingBase) entity).sorted(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer))).collect(Collectors.toList());

        targets.removeIf(t -> t == mc.thePlayer || mc.thePlayer.getDistanceToEntity(t) >= Double.parseDouble(reach.getMode()) || !(t instanceof EntityPlayer) || Antibot.bots.contains(t) && Wrapper.getModManager().getModByName("Antibot").isToggled());
        return targets;
    }

    private EntityLivingBase getNearest() {
        return getValidTargets().stream().findFirst().orElse(null);
    }

    public void onRender(){
        ticks.setShowed(!viadelay.isToggled());
    }

    public void onEvent(Event e){
        if(e instanceof Render){
            this.setSuffix(rots.getMode());

            if(entity != null){
                ScaledResolution sr = new ScaledResolution(this.mc);
                String name = StringUtils.stripControlCodes(entity.getName());
                float startX = 20;
                float renderX = (sr.getScaledWidth() / 2f) + startX;
                float renderY  = (sr.getScaledHeight() / 2f) + 10;
                int maxX2 = 30;
                float healthPercentage = ((EntityLivingBase) entity).getHealth() / ((EntityLivingBase) entity).getMaxHealth();

                float maxX = 100;
                Gui.drawRect(renderX, renderY, renderX + maxX, renderY + 40, 1342177280);
                Gui.drawRect(renderX, renderY + 37, renderX + (maxX * healthPercentage), renderY + 40, ColorUtil.getAstolfoColors(1));
                mc.fontRendererObj.drawStringWithShadow(name, renderX + 25, renderY + 7, -1);
                mc.fontRendererObj.drawStringWithShadow(Math.round((entity).getHealth()) + " ❤", renderX + 25, renderY + 20, ColorUtil.getAstolfoColors(1));
                int xAdd = 0;

                GuiInventory.drawEntityOnScreen((int)renderX + 12, (int)renderY + 36, 15, entity.rotationYaw, entity.rotationPitch, entity);
            }



        }

        if(e instanceof Update){
            if(mc.thePlayer.ticksExisted % 5 == 0) {
                entity = getNearest();
            }
            
            if(entity != null){
                if(autoblock.is("Vanilla")){
                    mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 5);
                }else if(autoblock.is("RightClick")){
                    mc.gameSettings.keyBindDrop.pressed = true;
                }
            }else if(autoblock.is("RightClick")){
                mc.gameSettings.keyBindDrop.pressed = Mouse.isButtonDown(1);
            }

            Attacking = (entity != null);

            if(entity != null && (viadelay.isToggled() ? (mc.thePlayer.getCurrentEquippedItem()!=null ? mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemAxe || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSpade ? mc.thePlayer.ticksExisted % 20 == 0  : mc.thePlayer.ticksExisted % 12 == 0 : mc.thePlayer.ticksExisted % 5 == 0) : mc.thePlayer.ticksExisted % Integer.parseInt(ticks.getMode()) == 0 && (Math.random() >= 0.3 || keepsprint.is("Sexy") || keepsprint.is("Sexy2") || Wrapper.getModManager().getModByName("HvH").isToggled()))){
                    if(ViaMCP.getInstance().getVersion() <= 47){
                        if(keepsprint.is("Sexy") || keepsprint.is("Sexy2")){
                            if(entity.hurtTime < 4){
                                mc.thePlayer.swingItem();
                            }

                        }else{
                            mc.thePlayer.swingItem();
                        }

                    }
                        if(keepsprint.is("Vanilla"))
                            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                        else if(keepsprint.is("Off")) {
                            mc.playerController.attackEntity(mc.thePlayer, entity);
                        }else if(keepsprint.is("Drag")){
                                for(int i = 0; i < 4; i++){
                                    mc.playerController.attackEntity(mc.thePlayer, entity);
                                }

                        }else if(keepsprint.is("Sexy") || keepsprint.is("Sexy2")){
                            if(entity.hurtTime < 2){
                                if(keepsprint.is("Sexy")) mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK)); else mc.playerController.attackEntity(mc.thePlayer, entity);
                            }
                        }

                if(ViaMCP.getInstance().getVersion() > 47){
                        mc.thePlayer.swingItem();
                    }
                }

        }
        if(e instanceof Motion){
            if(entity != null) {
                if(rots.is("Normal")){
                    RotUtil.renderRots(RotUtil.getRots(entity)[0], RotUtil.getRots(entity)[1]);
                    ((Motion) e).setYaw(RotUtil.getRots(entity)[0]);
                    ((Motion) e).setPitch(RotUtil.getRots(entity)[1]);
                }else if(rots.is("Randomised")){
                    RotUtil.renderRots(RotUtil.getRandomisedRots(entity)[0], RotUtil.getRots(entity)[1]);
                    ((Motion) e).setYaw(RotUtil.getRandomisedRots(entity)[0]);
                    ((Motion) e).setPitch(RotUtil.getRandomisedRots(entity)[1]);
                }else if(rots.is("Randomised2")){
                    RotUtil.renderRots(RotUtil.getRandomisedRots2(entity)[0], RotUtil.getRandomisedRots2(entity)[1]);
                    ((Motion) e).setYaw(RotUtil.getRandomisedRots2(entity)[0]);
                    ((Motion) e).setPitch(RotUtil.getRandomisedRots2(entity)[1]);
                }else if(rots.is("Movement")){
                    RotUtil.renderRots(RotUtil.getRandomisedRots2(entity)[0], RotUtil.getRandomisedRots2(entity)[1]);
                    ((Motion) e).setYaw(RotUtil.getRandomisedRots2(entity)[0]);
                    ((Motion) e).setPitch(RotUtil.getRandomisedRots2(entity)[1]);
                }


            }

        }

        if(e instanceof Move){
            if(rots.is("Movement") && mc.thePlayer.onGround){
                MoveUtil.setSpeed((Move) e, MoveUtil.getSpeed());
            }
        }

    }
}
