package kevin.module.modules.movement.flys.ncp

import kevin.event.*
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.ListValue
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MathUtils
import kevin.utils.MovementUtils
import kevin.utils.MovementUtils.direction
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.potion.Potion.moveSpeed
import net.minecraft.util.AxisAlignedBB
import kotlin.math.*


object OldHypixelBoost : FlyMode("OldHypixelBoost") {
    private val c04Flag = BooleanValue("${valuePrefix}C04Flag", false)
    private val damageMode = ListValue("${valuePrefix}DamageMode", arrayOf("NoDamage", "NCP", "HighNCP", "Hypixel", "MorePackets", "Jump3", "HighJump3"), "Hypixel")
    private val yUp = BooleanValue("${valuePrefix}YUp", true)
    private val startTimer = FloatValue("${valuePrefix}StartTimer", 1f, 0.01f, 2f)
    private val flyingTimer = FloatValue("${valuePrefix}FlyingTimer", 1f, 0.01f, 2f)
    private var failed = false
    private var state = 1
    private var speed = 0.01
    private var lastDistance = 0.01
    override fun onEnable() {
        if (!mc.thePlayer.onGround) return
        failed = false
        state = 1
        speed = 0.1
        if (c04Flag.get()) repeat(10) { mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true)) }
        if (damageMode notEqual "NoDamage") {
            if (damageMode equal "NCP") repeat(65) {
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.049, mc.thePlayer.posZ, false))
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false))
            }
            else if (damageMode equal "HighNCP") repeat(65) {
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0505625000001, mc.thePlayer.posZ, false))
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0156250000001, mc.thePlayer.posZ, false))
            }
            else if (damageMode equal "Hypixel" || damageMode equal "MorePckets") {
                var fall = if (damageMode equal "MorePacket") 3.4025
                else 3.0125
                while (fall > 0) {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0000013579, mc.thePlayer.posZ, false))
                    fall -= 0.0624986421
                }
            } else if (damageMode equal "Jump3") repeat(3) {
                MathUtils.jumpYPosArr.forEach {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + it, mc.thePlayer.posZ, false))
                }
            } else if (damageMode equal "HighJump3") repeat(3) {
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.015625, mc.thePlayer.posZ, false))
                MathUtils.jumpYPosArr.forEach {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + it + 0.015625, mc.thePlayer.posZ, false))
                }
            }
            C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true)
        }
        if (yUp.get()) {
            mc.thePlayer.jump()
            mc.thePlayer.posY += 0.42F
        }
        if (startTimer.get() != 1f) {
            mc.timer!!.timerSpeed = startTimer.get()
        }
    }

    override fun onMove(event: MoveEvent) {
        if (!MovementUtils.isMoving) {
            event.zeroXZ()
            return
        }

        if (failed) return

        val amplifier: Double = 1 + if (mc.thePlayer.isPotionActive(moveSpeed)) 0.2 * (mc.thePlayer.getActivePotionEffect(moveSpeed).amplifier + 1) else 0.0
        val baseSpeed = 0.29 * amplifier

        when (state) {
            1 -> {
                speed = (if (mc.thePlayer.isPotionActive(moveSpeed)) 1.56 else 2.034) * baseSpeed
                state = 2
            }
            2 -> {
                speed *= 2.16
                if (flyingTimer.get() != 1f) {
                    mc.timer!!.timerSpeed = flyingTimer.get()
                }
                state = 3
            }
            3 -> {
                speed =
                    lastDistance - (if (mc.thePlayer.ticksExisted % 2 == 0) 0.0103 else 0.0123) * (lastDistance - baseSpeed)
                state = 4
            }
            else -> speed = lastDistance - lastDistance / 159.8
        }

        speed = max(speed, 0.3)

        val yaw = direction
        event.x = -sin(yaw) * speed
        event.z = cos(yaw) * speed
        mc.thePlayer.motionX = event.x
        mc.thePlayer.motionZ = event.z
    }

    override fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            if (!failed) mc.thePlayer.motionY = 0.0
        } else {
            val xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX
            val zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ
            lastDistance = sqrt(xDist * xDist + zDist * zDist)
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && state != 1) event.packet.onGround = false
        else if (event.packet is S08PacketPlayerPosLook) failed = true
    }

    override fun onBB(event: BlockBBEvent) {
        if (event.block is BlockAir && event.y <= min(fly.launchY, floor(mc.thePlayer.posY))) {
            event.boundingBox = AxisAlignedBB.fromBounds(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(), event.x + 1.0, fly.launchY, event.z + 1.0)
        }
    }

    override fun onWorld(event: WorldEvent) {
        fly.state = false
    }

    override fun onDisable() {
        mc.timer!!.timerSpeed = 1f
    }
}