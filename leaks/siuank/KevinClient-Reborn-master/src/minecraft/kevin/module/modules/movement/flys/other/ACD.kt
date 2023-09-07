package kevin.module.modules.movement.flys.other

import kevin.event.BlockBBEvent
import kevin.event.UpdateEvent
import kevin.module.modules.movement.flys.FlyMode
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.AxisAlignedBB

object ACD : FlyMode("ACD") {
    override fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.onGround) {
            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
            mc.thePlayer.jump()
        }
    }

    override fun onBB(event: BlockBBEvent) {
        if (event.block is BlockAir && event.y <= fly.launchY) event.boundingBox = AxisAlignedBB.fromBounds(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(), event.x + 1.0, fly.launchY, event.z + 1.0)
    }
}