package club.pulsive.impl.util.render;

import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.player.BlockUtils;
import club.pulsive.impl.util.player.PlayerUtil;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.Vec3;

@Getter
public class Particle {

    private final TimerUtil removeTimer = new TimerUtil();

    public final Vec3 position;
    private final Vec3 delta;

    public Particle(final Vec3 position) {
        this.position = position;
        this.delta = new Vec3((ApacheMath.random() * 0.5 - 0.25) * 0.01, (ApacheMath.random() * 0.25) * 0.01, (ApacheMath.random() * 0.5 - 0.25) * 0.01);
        this.removeTimer.reset();
    }

    public Particle(final Vec3 position, final Vec3 velocity) {
        this.position = position;
        this.delta = new Vec3(velocity.xCoord * 0.01, velocity.yCoord * 0.01, velocity.zCoord * 0.01);
        this.removeTimer.reset();
    }

    public void update() {
        final Block block1 = BlockUtils.getBlock(this.position.xCoord, this.position.yCoord, this.position.zCoord + this.delta.zCoord);
        if (!(block1 instanceof BlockAir || block1 instanceof BlockBush || block1 instanceof BlockLiquid))
            this.delta.zCoord *= -0.8;

        final Block block2 = BlockUtils.getBlock(this.position.xCoord, this.position.yCoord + this.delta.yCoord, this.position.zCoord);
        if (!(block2 instanceof BlockAir || block2 instanceof BlockBush || block2 instanceof BlockLiquid)) {
            this.delta.xCoord *= 0.999F;
            this.delta.zCoord *= 0.999F;

            this.delta.yCoord *= -0.6;
        }

        final Block block3 = BlockUtils.getBlock(this.position.xCoord + this.delta.xCoord, this.position.yCoord, this.position.zCoord);
        if (!(block3 instanceof BlockAir || block3 instanceof BlockBush || block3 instanceof BlockLiquid))
            this.delta.xCoord *= -0.8;

        this.updateWithoutPhysics();
    }

    public void updateWithoutPhysics() {
        this.position.xCoord += this.delta.xCoord;
        this.position.yCoord += this.delta.yCoord;
        this.position.zCoord += this.delta.zCoord;
        this.delta.xCoord /= 0.999998F;
        this.delta.yCoord -= 0.0000015;
        this.delta.zCoord /= 0.999998F;
    }
}