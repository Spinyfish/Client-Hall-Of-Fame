package club.pulsive.impl.module.impl.visual.waveycapes;

import club.pulsive.impl.module.impl.visual.WaveyCapes;
import club.pulsive.impl.module.impl.visual.waveycapes.sim.StickSimulation;
import club.pulsive.impl.util.math.apache.ApacheMath;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public interface CapeHolder {
    StickSimulation getSimulation();

    default void updateSimulation(EntityPlayer abstractClientPlayer, int partCount) {
        StickSimulation simulation = this.getSimulation();
        boolean dirty = false;

        if (simulation.points.size() != partCount) {
            simulation.points.clear();
            simulation.sticks.clear();

            for (int i = 0; i < partCount; i++) {
                StickSimulation.Point point = new StickSimulation.Point();
                point.position.y = -i;
                point.locked = i == 0;
                simulation.points.add(point);
                if (i > 0) {
                    simulation.sticks.add(new StickSimulation.Stick(simulation.points.get(i - 1), point, 1f));
                }
            }

            dirty = true;
        }

        if (dirty) {
            for (int i = 0; i < 10; i++) {
                this.simulate(abstractClientPlayer);
            }
        }
    }

    default void simulate(EntityPlayer abstractClientPlayer) {
        StickSimulation simulation = getSimulation();

        if(!simulation.points.isEmpty() && simulation != null) {
            simulation.points.get(0).prevPosition.copy(simulation.points.get(0).position);
            double d0 = abstractClientPlayer.prevChasingPosX + (abstractClientPlayer.chasingPosX - abstractClientPlayer.prevChasingPosX) * (double) Minecraft.getMinecraft().timer.renderPartialTicks - (abstractClientPlayer.prevPosX + (abstractClientPlayer.posX - abstractClientPlayer.prevPosX) * (double) Minecraft.getMinecraft().timer.renderPartialTicks);
            double d2 = abstractClientPlayer.prevChasingPosZ + (abstractClientPlayer.chasingPosZ - abstractClientPlayer.prevChasingPosZ) * (double) Minecraft.getMinecraft().timer.renderPartialTicks - (abstractClientPlayer.prevPosZ + (abstractClientPlayer.posZ - abstractClientPlayer.prevPosZ) * (double) Minecraft.getMinecraft().timer.renderPartialTicks);

            float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset
                    - abstractClientPlayer.prevRenderYawOffset;
            double o = ApacheMath.sin(n * 0.017453292F);
            double p = -ApacheMath.cos(n * 0.017453292F);

            WaveyCapes waveyCapes = WaveyCapes.getInstance();

            float heightMul = waveyCapes.heightMultiplier.getValue().floatValue();

            double fallHack = MathHelper.clamp_double((simulation.points.get(0).position.y -
                    (abstractClientPlayer.posY * heightMul)), 0d, 1d);
            simulation.points.get(0).position.x += ((d0 * o + d2 * p) * 1.8) + fallHack;
            simulation.points.get(0).position.y = (float) (abstractClientPlayer.posY * heightMul +
                    (abstractClientPlayer.isSneaking() ? -4 : 0));

            simulation.simulate();
        }
    }
}