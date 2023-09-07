package kevin.module.modules.movement.flys.other

import kevin.event.MoveEvent
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MovementUtils
import kevin.utils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import kotlin.math.max

object Pika : FlyMode("PikaSW") {
    private val boostY = BooleanValue("${valuePrefix}BoostY", true)
    private var flag = false
    private var boost = false
    private var boostTick = 0
    override fun onEnable() {
        flag = false
        boost = false
        if (mc.thePlayer.onGround.not()) {
            fly.state = false
            return
        }
        PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.05 - Math.random() / 20, mc.thePlayer.posZ, false))
    }

    override fun onUpdate(event: UpdateEvent) {
        if (!flag) return
        // 哪里来的浮点? 我不知道, 可能是脸滚键盘乱打的
        if (!boost) {
            boost = true
            boostTick = 0
            MovementUtils.setMotion(0.12731721995808304)
            return
        }
        ++boostTick
        when (boostTick) {
            0, 1 -> {
                mc.thePlayer.motionY = if (boostY.get()) 3.7f.toDouble() else 0.42f.toDouble()
                MovementUtils.setMotion(3.424987625453621)
            }
            2 -> {
                if (boostY.get()) mc.thePlayer.motionY = 3.54f.toDouble()
                MovementUtils.setMotion(3.141592653589793)
            }
            else -> {
                if (boostTick > 5) mc.thePlayer.motionY = 0.0
                MovementUtils.setMotion(max(1.0f, MovementUtils.speed).toDouble())
            }
        }
    }

    override fun onDisable() {
        MovementUtils.resetMotion(true)
    }

    override fun onMove(event: MoveEvent) {
        if (!flag) event.zero()
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            if (!flag || !packet.isMoving) event.cancelEvent()
        } else if (packet is S08PacketPlayerPosLook) {
            flag = true
        }
    }
}