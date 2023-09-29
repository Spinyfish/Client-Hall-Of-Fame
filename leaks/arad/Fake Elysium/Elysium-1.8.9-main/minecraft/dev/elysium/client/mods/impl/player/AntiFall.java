package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventMotion;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class AntiFall extends Mod {
    public double[] op;
    public ModeSetting mode = new ModeSetting("Mode",this,"Teleport","Packet","Flag","Motion","Ground Spoof","Freeze");
    public NumberSetting maxDist = new NumberSetting("Distance To Trigger",0,20,4,0.1,this);
    public BooleanSetting onlyvoid = new BooleanSetting("Void Only",true,this);
    public NumberSetting maxHeight = new NumberSetting("Trigger when height bigger",0,255,4,0.1,this);
    public int tickCooldown = 0;

    public AntiFall() {
        super("AntiFall","Prevents you from falling", Category.PLAYER);
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        if(e.isPre() && !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -0.05, 0).expand(-0.5, 0, -0.5)).isEmpty())
            op = new double[] {mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};

        switch (mode.getMode()) {
            case "Teleport":
                if(mc.thePlayer.fallDistance > maxDist.getValue() && shouldTrigger()) {
                    mc.thePlayer.setPosition(op[0],op[1],op[2]);
                    mc.thePlayer.motionX = mc.thePlayer.motionZ = mc.thePlayer.motionZ = 0;
                }
                break;
            case "Packet":
                if(tickCooldown-- < 1 && mc.thePlayer.fallDistance > maxDist.getValue() && shouldTrigger()) {
                    mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(op[0],op[1],op[2], true));
                    mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(op[0],op[1] + 0.41999998688698,op[2], false));
                    mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(op[0],op[1] + 0.7531999805212015,op[2], false));
                    mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(op[0],op[1] + 1.001335979112147,op[2], false));
                    mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(op[0],op[1] + 1.166109260938214,op[2], false));
                    mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(op[0],op[1] + 1.249187078744681,op[2], false));
                    tickCooldown = 10;
                }
                break;
            case "Flag":
                if(tickCooldown-- < 1 && mc.thePlayer.fallDistance > maxDist.getValue() && shouldTrigger()) {
                    mc.thePlayer.setPosition(0, 20000, 0);
                    tickCooldown = 20;
                }
            case "Motion":
                if(mc.thePlayer.fallDistance > maxDist.getValue() && shouldTrigger() && mc.thePlayer.posY < op[1]) {
                    if(tickCooldown-- < 1) {
                        tickCooldown = 50;
                        mc.thePlayer.sendQueue.addToSilentSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(op[0], op[1], op[2], true));
                    }
                    mc.thePlayer.motionY = maxDist.getValue()/5;
                    mc.thePlayer.fallDistance = 0;
                }
                break;
            case "Ground Spoof":
                if(mc.thePlayer.fallDistance > maxDist.getValue() && shouldTrigger())
                    e.setOnGround(true);
                break;
        }
    }

    private boolean shouldTrigger() {
        return onlyvoid.isEnabled() ? blocksToGround() == -1 : blocksToGround() > maxHeight.getValue();
    }

    private int blocksToGround() {
        double height = mc.thePlayer.posY + 50;

        BlockPos bu = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

        int btg = 0;

        while (height-- > 0) {
            bu = bu.down();
            if(bu.getBlock() instanceof BlockAir)
                btg++;
            else
                break;
            if(bu.getY() <= 0) {
                return -1;
            }
        }
        return btg;
    }

}
