package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.utils.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;

public class Eagle extends Mod {
    public Timer timer = new Timer();

    public NumberSetting crouchtime = new NumberSetting("Crouch Time",0,2500,125,1,this);
    public BooleanSetting omni = new BooleanSetting("All Direction",false, this);
    public BooleanSetting onlyonground = new BooleanSetting("Only When Grounded",true, this);
    public BooleanSetting stopmove = new BooleanSetting("Stop Move Offground",true, this);

    public Eagle() {
        super("Eagle","Prevents you from falling by crouching", Category.PLAYER);
        stopmove.setPredicate(mod -> onlyonground.isEnabled());
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        BlockPos blockunder = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ);

        if(blockunder.getBlock() instanceof BlockAir && (!onlyonground.isEnabled() || mc.thePlayer.onGround) && (mc.thePlayer.moveForward < 0.1f || omni.isEnabled()))
            timer.reset();

        if(!timer.hasTimeElapsed((long) crouchtime.getValue(), false)) {
            mc.gameSettings.keyBindSneak.pressed = true;
        } else if(!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false;
    }
}
