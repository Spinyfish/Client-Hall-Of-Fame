package de.fanta.module.impl.movement;

import java.awt.Color;

import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.PlayerUtil;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class NoWeb extends Module {
    public NoWeb() {
        super("NoWeb", 0, Type.Movement, Color.green);

        this.settings.add(new Setting("Modes", new DropdownBox("Vanilla", new String[]{"Vanilla", "Intave", "NCP", "AAC 3.3.13"})));


    }


    @Override
    public void onEvent(Event event) {
        switch (((DropdownBox) this.getSetting("Modes").getSetting()).curOption) {
            case "Vanilla":
                if (mc.thePlayer.isInWeb) {
                    mc.thePlayer.isInWeb = false;
                }
                break;
            case "Intave":
                BlockPos block98 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                BlockPos block = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                if ((mc.theWorld.getBlockState(block98).getBlock() == Blocks.web || mc.theWorld.getBlockState(block).getBlock() == Blocks.web)) {
                    double speed = 0.35D;
                   Speed.setSpeed(speed);
                }
                break;
            case "NCP":
                break;
            case "AAC 3.3.13":
            	  BlockPos block981 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                  BlockPos block1 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 0.5D, mc.thePlayer.posZ);
                  if ((mc.theWorld.getBlockState(block981).getBlock() == Blocks.web || mc.theWorld.getBlockState(block1).getBlock() == Blocks.web)) {
                      double speed = 0.35D;
                      Speed.setSpeed(speed);
                  }
                break;
        }
    }
}
