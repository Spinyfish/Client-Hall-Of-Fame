package kevin.module.modules.movement.flys.other

import kevin.event.*
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import kevin.utils.RandomUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook

object TestSparky : FlyMode("TestSparky") {
    private var flagState = 0
    private val flagTimer = MSTimer()
    override fun onEnable() {
        flagState = 1
        flagTimer.reset()
        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + if (mc.thePlayer.onGround) -0.0784 else -0.9, mc.thePlayer.posZ, false))
    }

    override fun onUpdate(event: UpdateEvent) {
        mc.timer.timerSpeed = 0.1f
        if (flagState == 2) {
            MovementUtils.forward(9.0)
            mc.thePlayer.motionY = 0.2132011119 + RandomUtils.nextDouble(0.0, 0.1)
        } else if (flagTimer.hasTimePassed(1500) && flagState == 1) {
            // time out
            tryFlag()
        }
    }

    override fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST) {
            if (flagState == 2) tryFlag()
        }
    }

    override fun onMove(event: MoveEvent) {
        event.zeroXZ()
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S08PacketPlayerPosLook) {
            flagState = 2
            flagTimer.reset()
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    private fun tryFlag() {
        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 11 - RandomUtils.nextDouble(0.0, 3.0), mc.thePlayer.posZ, false))
        flagState = 1
    }
}