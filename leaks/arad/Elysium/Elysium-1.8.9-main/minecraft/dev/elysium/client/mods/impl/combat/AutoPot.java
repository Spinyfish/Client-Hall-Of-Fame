package dev.elysium.client.mods.impl.combat;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.events.EventPacket;
import dev.elysium.client.utils.Timer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;

import java.util.Iterator;

public class AutoPot extends Mod {

    public int potionSlot;
    public boolean shouldPot;
    public int ogSlot;
    public int stage;
    public Vec3 potPos;
    public Timer timer = new Timer();
    public NumberSetting delay = new NumberSetting("Delay",1,2000,750,10,this);
    public ModeSetting mode = new ModeSetting("Mode",this,"Floor","Jump","Jump Only");
    public NumberSetting health = new NumberSetting("HP",1,20,10,1,this);
    public BooleanSetting regen = new BooleanSetting("Regen",true,this);
    public BooleanSetting speed = new BooleanSetting("Speed",true,this);
    public BooleanSetting jump = new BooleanSetting("Jump",true,this);
    public AutoPot() {
        super("AutoPot","If you read this u lose 30 iq (u know have 0)", Category.COMBAT);
    }

    public void onEnable() {
        shouldPot = false;
        potionSlot = -1;
        ogSlot = -1;
        stage = 0;
    }

    public void onDisable() {
        shouldPot = false;
        potionSlot = -1;
        ogSlot = -1;
        stage = 0;
    }

    @EventTarget
    public void onEventPacket(EventPacket e) {
        if(mode.is("Jump") && stage > 0 && shouldPot && e.packet instanceof C03PacketPlayer)
            e.setCancelled(true);

    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        if(e.isPost()) return;
        getPotions();
        if(shouldPot) {
        switch (mode.getMode()) {
            case "Floor":
                stage++;
                mc.thePlayer.rotationPitchHead = 90;
                switch(stage) {
                    case 1:
                        e.setPitch(90);
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C09PacketHeldItemChange(potionSlot));
                        break;
                    case 2:
                        e.setPitch(90);
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(potionSlot)));
                        break;
                    case 3:
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C09PacketHeldItemChange(ogSlot));
                        stage = 0;
                        shouldPot = false;
                        break;
                }
                break;
            case "Jump":
                if(stage == 0 && !mc.thePlayer.onGround)
                    break;
                stage++;
                mc.thePlayer.rotationPitchHead = -90;
                switch(stage) {
                    case 1:
                        potPos = new Vec3(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ);
                        e.setPitch(-90);
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(potPos.xCoord, potPos.yCoord, potPos.zCoord,e.yaw, -90, e.isOnGround()));
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C09PacketHeldItemChange(potionSlot));
                        break;
                    case 2:
                        e.setPitch(-90);
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(potionSlot)));
                        break;
                    case 3:
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C09PacketHeldItemChange(ogSlot));
                        break;
                    case 5:
                        packetJump(potPos);
                        stage = 0;
                        shouldPot = false;
                        break;
                    case 6:
                        break;
                }
                break;
        }
        } else
            ogSlot = mc.thePlayer.inventory.currentItem;
    }

    public void getPotions() {
        if(shouldPot)
            timer.reset();
        else if(timer.hasTimeElapsed((long) delay.getValue(), false)) {
            if (mc.thePlayer.getHealth() <= health.getValue()) {
                if (!mc.thePlayer.isPotionActive(Potion.regeneration) && regen.isEnabled() && potionSlot(Potion.regeneration) != -1) {
                    shouldPot = true;
                    potionSlot = potionSlot(Potion.regeneration);
                } else if (potionSlot(Potion.heal) != -1) {
                    potionSlot = potionSlot(Potion.heal);
                    shouldPot = true;
                }
            }

            if(speed.isEnabled() && !mc.thePlayer.isPotionActive(Potion.moveSpeed) && potionSlot(Potion.moveSpeed) != -1) {
                potionSlot = potionSlot(Potion.moveSpeed);
                shouldPot = true;
            }

            if(jump.isEnabled() && !mc.thePlayer.isPotionActive(Potion.jump) && potionSlot(Potion.jump) != -1) {
                potionSlot = potionSlot(Potion.jump);
                shouldPot = true;
            }
        }
    }

    public int potionSlot(Potion potion) {
        for(int x = 0; x < 9; x++) {
            ItemStack itemstack = mc.thePlayer.inventory.getStackInSlot(x);
            if (itemstack != null && itemstack.getItem() instanceof ItemPotion) {

                ItemPotion itemPotion = (ItemPotion)itemstack.getItem();

                for (PotionEffect effect : itemPotion.getEffects(itemstack)) {
                    if (effect.getPotionID() == potion.id && ItemPotion.isSplash(itemstack.getItemDamage()))
                        return x;
                }
            }
        }
        return -1;
    }

    public void packetJump(Vec3 vec) {
        double x = vec.xCoord;
        double y = vec.yCoord;
        double z = vec.zCoord;
        mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + .41999998688698, z, false));
        mc.thePlayer.motionY = 0.33319999363422365;
        mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + .7531999805212015, z, false));
        mc.thePlayer.motionY = 0.24813599859094576;
        mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.001335979112147, z, false));
        mc.thePlayer.motionY = 0.16477328182606651;
        mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.166109260938214, z, false));
        mc.thePlayer.motionY = 0.08307781780646721;
        //mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.249187078744681, z, false));
        mc.thePlayer.setPosition(x, y + 1.249187078744681, z);
        mc.thePlayer.motionY = 0.0030162615090425808;

        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
    }

}