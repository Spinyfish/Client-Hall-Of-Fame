package club.pulsive.impl.util.client;

import club.pulsive.impl.util.math.apache.ApacheMath;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.ThreadLocalRandom;

public class BlockPosUtil {
    public static double RANDOM = RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    public BlockPos hypixelBlockPos() {
        return new BlockPos(ApacheMath.random() * ThreadLocalRandom.current().nextDouble(-RANDOM, RANDOM),
                ApacheMath.random() * ThreadLocalRandom.current().nextDouble(-RANDOM, RANDOM), ApacheMath.random() * ThreadLocalRandom.current().nextDouble(-RANDOM, RANDOM));
    }
}
