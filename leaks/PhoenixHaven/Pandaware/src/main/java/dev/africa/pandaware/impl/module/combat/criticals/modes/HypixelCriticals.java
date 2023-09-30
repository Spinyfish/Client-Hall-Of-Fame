package dev.africa.pandaware.impl.module.combat.criticals.modes;

import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.combat.criticals.CriticalsModule;
import dev.africa.pandaware.impl.module.combat.criticals.ICriticalsMode;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.utils.client.ServerUtils;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.player.block.BlockUtils;
import lombok.AllArgsConstructor;
import net.minecraft.util.BlockPos;

public class HypixelCriticals extends ModuleMode<CriticalsModule> implements ICriticalsMode {
    private final EnumSetting<HypixelMode> mode = new EnumSetting<>("Hypixel Mode", HypixelMode.SAFE);

    public HypixelCriticals(String name, CriticalsModule parent) {
        super(name, parent);

        this.registerSettings(this.mode);
    }

    private int stage;

    @Override
    public void handle(MotionEvent event, int ticksExisted) {

        boolean yGround = mc.thePlayer.posY % 0.015625 == 0;
        boolean isSolidGround = (mc.thePlayer.posY == Math.round(mc.thePlayer.posY));
        boolean isSlab = (mc.thePlayer.posY - Math.round(mc.thePlayer.posY - 1) == 0.5);

        String block = BlockUtils.getBlockAtPos(new BlockPos(
                mc.thePlayer.posX,
                mc.thePlayer.posY - 0.1,
                mc.thePlayer.posZ
        )).getUnlocalizedName();

        if ((ServerUtils.isOnServer("mc.hypixel.net") || ServerUtils.isOnServer("hypixel.net")) && !(ServerUtils.compromised)) {
            if (yGround && mc.thePlayer.onGround && ticksExisted % 4 != 0 &&
                    (isSolidGround || isSlab && block.toLowerCase().contains("slab"))) {
                event.setOnGround(false);
                this.stage++;

                switch (this.mode.getValue()) {
                    case SAFE:
                        if (this.stage == 0) {
                            event.setY(event.getY() + .20200003768373);
                        } else if (this.stage == 1) {
                            event.setY(event.getY() + 0.001635979112147);
                        } else if (this.stage == 2) {
                            event.setY(event.getY() + 0.13150004615783);
                        } else if (this.stage == 3) {
                            event.setY(event.getY() + 0.01855504270223);
                            this.stage = 0;
                        }
                        break;

                    case MELT:
                        switch (this.stage) {
                            case 0:
                                event.setY(event.getY() + 0.02412948712);
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
                        break;
                }

                event.setY(event.getY() + RandomUtils.nextDouble(1.2E-9, 1.2E-7));
            }
        }
    }

    @Override
    public void entityIsNull() {
        this.stage = 0;
    }

    @AllArgsConstructor
    private enum HypixelMode {
        SAFE("Safe"),
        MELT("Melt");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
