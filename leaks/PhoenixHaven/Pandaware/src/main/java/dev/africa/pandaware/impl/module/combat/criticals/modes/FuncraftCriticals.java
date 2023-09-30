package dev.africa.pandaware.impl.module.combat.criticals.modes;

import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.combat.criticals.CriticalsModule;
import dev.africa.pandaware.impl.module.combat.criticals.ICriticalsMode;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;

public class FuncraftCriticals extends ModuleMode<CriticalsModule> implements ICriticalsMode {
    public FuncraftCriticals(String name, CriticalsModule parent) {
        super(name, parent);
    }

    private int stage;

    @Override
    public void handle(MotionEvent event, int ticksExisted) {
        if (PlayerUtils.isMathGround()) {
            event.setOnGround(false);

            switch (this.stage) {
                case 0:
                    event.setY(event.getY() + 0.0012412948712);
                    break;
                case 1:
                    event.setY(event.getY() + 0.024671798242);
                    break;
                case 2:
                    event.setY(event.getY() + 0.00742167842112);
                    break;
                case 3:
                    event.setY(event.getY() + 0.02124121242);
                    break;
            }

            event.setY(event.getY() + RandomUtils.nextFloat(1.0E-8F, 1.0E-6F));

            if (this.stage++ >= 3) {
                this.stage = 0;

                event.setOnGround(true);
            }
        }
    }

    @Override
    public void entityIsNull() {
        this.stage = 0;
    }
}
