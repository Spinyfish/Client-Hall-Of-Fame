package dev.echo.module.impl.combat;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.AttackEvent;
import dev.echo.listener.event.impl.player.UpdateEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.player.AlwaysUtil;
import dev.echo.utils.player.ChatUtil;
import dev.echo.utils.server.PacketUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.MathHelper;

public final class ExtraKB extends Module {
    public BooleanSetting smart = new BooleanSetting("Smart WTap", false);
    public NumberSetting time1 = new NumberSetting("Stop Sprint Delay", 150, 500, 10, 10);
    public NumberSetting time2 = new NumberSetting("Start Sprint Delay", 150, 500, 10, 10);

    public ExtraKB() {
        super("ExtraKB", Category.COMBAT, "Makes the player your attacking take extra knockback");
        this.addSettings(smart, time1, time2);
    }

    TimerUtil clickTimer = new TimerUtil();
    TimerUtil clickTimer2 = new TimerUtil();
    public static boolean attacked = false;
    public static EntityLivingBase target;

    @Override
    public void onEnable() {
        target = null;
        attacked = false;
        clickTimer.reset();
        clickTimer2.reset();
        super.onEnable();
    }

    @Link
    public Listener<AttackEvent> getEventAttackListener = event -> {
        attacked = true;
    };
    @Link
    public Listener<UpdateEvent> updateEventListener = event -> {
        if (AlwaysUtil.isPlayerInGame()) {
            mc.theWorld.getLoadedEntityList();
            if (KillAura.target instanceof EntityPlayer) {
                target = KillAura.target;
            } else {
                target = null;
            }
            if (target != null) {
                if (smart.isEnabled()) {
                    if (mc.thePlayer.getDistanceToEntity(target) < 3 && attacked == true) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
                        attacked = false;
                    } else {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindForward));
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindBack));
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindRight));
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindLeft));
                    }
                    return;
                }
                if (attacked == true) {
                    if (clickTimer.hasTimeElapsed(MathUtils.getRandomInRange(time1.getValue() - 10, time1.getValue() + 10))) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
                        clickTimer.reset();
                        if (clickTimer2.hasTimeElapsed(MathUtils.getRandomInRange(time2.getValue() - 10, time2.getValue() + 10))) {
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
                            clickTimer2.reset();
                        }
                    }
                    attacked = false;
                }
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            }
        }
    };
}
