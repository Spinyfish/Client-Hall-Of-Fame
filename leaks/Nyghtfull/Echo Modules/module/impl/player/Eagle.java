package dev.echo.module.impl.player;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class Eagle extends Module {

    private final TimerUtil timerend = new TimerUtil();
    private final TimerUtil timestart = new TimerUtil();

    private final BooleanSetting smart = new BooleanSetting("Smart", false);
    private final NumberSetting startmin = new NumberSetting("Start Min", 50, 500, 10, 10);
    private final NumberSetting startmax = new NumberSetting("Start Max", 50, 500, 10, 10);

    private final NumberSetting endtmin = new NumberSetting("End Min", 50, 500, 10, 10);
    private final NumberSetting endmax = new NumberSetting("End Max", 50, 500, 10, 10);
    private boolean sneaking;


    public Eagle() {
        super("Eagle", Category.MISC, "FUCKING NIGGER");
        addSettings(smart,startmin,startmax,endtmin,endmax);
    }

    @Override
    public void onDisable() {
        timerend.reset();
        timestart.reset();
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.pressed = false;
        }
        super.onDisable();
    }

    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        if(smart.isEnabled() && mc.thePlayer.rotationPitch > 45) {
            return;
        }
        if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir
                && mc.thePlayer.onGround) {
           // if(mc.thePlayer.ticksExisted % 2 == 0) {
            if(timestart.hasTimeElapsed(MathHelper.randomFloatClamp(new Random(),startmin.getValue().floatValue(),startmax.getValue().floatValue()))) {
                sneaking = true;
                timestart.reset();
                mc.gameSettings.keyBindSneak.pressed = true;
            }
          //  }
        } else {
            if (sneaking) {
                if(timestart.hasTimeElapsed(MathHelper.randomFloatClamp(new Random(),endtmin.getValue().floatValue(),endmax.getValue().floatValue()))) {
                    // if(mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.gameSettings.keyBindSneak.pressed = false;
                    timerend.reset();
                    sneaking = false;
                }
             //   }
            } //else{
              //  timerend.reset();
          //  }
        }
    };



}
