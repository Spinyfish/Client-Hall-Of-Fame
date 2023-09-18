package kevin.module.modules.movement.flys.other

import kevin.event.BlockBBEvent
import kevin.event.PacketEvent
import kevin.module.BooleanValue
import kevin.module.modules.movement.flys.FlyMode
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import kotlin.math.floor

object FakeGround : FlyMode("FakeGround") {
    private val noGround = BooleanValue("${valuePrefix}SpoofNoGround", false)
    private val jumpUp = BooleanValue("${valuePrefix}JumpUp", true)
    override fun onBB(event: BlockBBEvent) {
        if (event.block is BlockAir && event.y <= (if (jumpUp.get()) floor(mc.thePlayer.posY) else fly.launchY)) {
            event.boundingBox = AxisAlignedBB.fromBounds(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(), event.x + 1.0, fly.launchY, event.z + 1.0)
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (noGround.get() && event.packet is C03PacketPlayer) {
            event.packet.onGround = false
        }
    }
}