/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.module.modules.combat

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.movement.Speed
import kevin.utils.*
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.util.MathHelper
import java.util.*

class AntiKnockback : Module("AntiKnockback","Allows you to modify the amount of knockback you take.", category = ModuleCategory.COMBAT) {
    private val horizontalValue = FloatValue("Horizontal", 0F, -1F, 1F)
    private val verticalValue = FloatValue("Vertical", 0F, -1F, 1F)
    private val modeValue = ListValue("Mode", arrayOf("Simple", "AAC", "AACPush", "AACZero", "AACv4",
        "Reverse", "SmoothReverse", "HypixelReverse", "Jump", "Glitch", "AAC5Packet", "MatrixReduce", "MatrixSimple", "MatrixReverse",
        "AllowFirst", "Click", "LegitSmart", "IntaveJump", "TestBuzzReverse", "PikaStable", "TestGrimAC"), "Simple")

    // Simple
    private val simpleCancelTransaction = BooleanValue("SimpleCancelTransactions", false)
    private val simpleCancelTransactionCount = IntegerValue("SimpleCancelTransactionsCount", 6, 0, 20)

    // Reverse
    private val reverseStrengthValue = FloatValue("ReverseStrength", 1F, 0.1F, 1F)
    private val reverse2StrengthValue = FloatValue("SmoothReverseStrength", 0.05F, 0.02F, 0.1F)

    // AAC Push
    private val aacPushXZReducerValue = FloatValue("AACPushXZReducer", 2F, 1F, 3F)
    private val aacPushYReducerValue = BooleanValue("AACPushYReducer", true)

    // AAc v4
    private val aacv4MotionReducerValue = FloatValue("AACv4MotionReducer", 0.62F,0F,1F)

    // Click
    private val clickCount = IntegerValue("ClickCount", 2, 1, 10)
    private val clickTime = IntegerValue("ClickMinHurtTime", 8, 1, 10) // 10: only click when receive velocity packet
    private val clickRange = FloatValue("ClickRange", 3.0F, 2.5F, 7F)
    private val clickOnPacket = BooleanValue("ClickOnPacket", true)
    private val clickSwing = BooleanValue("ClickSwing", false)
    private val clickFakeSwing = BooleanValue("ClickFakeSwing", true)

    // explosion value
    private val cancelExplosionPacket = BooleanValue("CancelExplosionPacket",false)
    private val explosionCheck = BooleanValue("ExplosionCheck",true)

    private val fireCheck = BooleanValue("FireCheck",true)

    private var velocityTimer = MSTimer()
    private var velocityInput = false

    private var explosion = false

    // SmoothReverse
    private var reverseHurt = false

    // legit smart
    private var jumped = 0

    // AACPush
    private var jump = false

    private var transactionCancelCount = 0

    override val tag: String
        get() = if (modeValue.get() == "Simple") "H:${horizontalValue.get()*100}% V:${verticalValue.get()*100}%" else modeValue.get()

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (modeValue.get().lowercase()) {
            "jump" -> if (thePlayer.hurtTime > 7 && thePlayer.onGround) {
                thePlayer.jump()
            }

            "glitch" -> {
                thePlayer.noClip = velocityInput

                if (thePlayer.hurtTime == 7)
                    thePlayer.motionY = 0.4

                velocityInput = false
            }

            "reverse" -> {
                if (!velocityInput)
                    return

                if (!thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.speed * reverseStrengthValue.get())
                } else if (velocityTimer.hasTimePassed(80L))
                    velocityInput = false
            }

            "hypixelreverse" -> {
                if (!velocityInput)
                    return

                if (!thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.speed * reverseStrengthValue.get())
                } else if (velocityTimer.hasTimePassed(120L))
                    velocityInput = false
            }

            "smoothreverse" -> {
                if (!velocityInput) {
                    thePlayer.speedInAir = 0.02F
                    return
                }

                if (thePlayer.hurtTime > 0)
                    reverseHurt = true

                if (!thePlayer.onGround) {
                    if (reverseHurt)
                        thePlayer.speedInAir = reverse2StrengthValue.get()
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                    reverseHurt = false
                }
            }

            "aac" -> if (velocityInput && velocityTimer.hasTimePassed(80L)) {
                thePlayer.motionX *= horizontalValue.get()
                thePlayer.motionZ *= horizontalValue.get()
                //mc.thePlayer.motionY *= verticalValue.get() ?
                velocityInput = false
            }

            "aacv4" -> {
                if (thePlayer.hurtTime>0 && !thePlayer.onGround){
                    val reduce=aacv4MotionReducerValue.get();
                    thePlayer.motionX *= reduce
                    thePlayer.motionZ *= reduce
                }
            }

            "aacpush" -> {
                if (jump) {
                    if (thePlayer.onGround)
                        jump = false
                } else {
                    // Strafe
                    if (thePlayer.hurtTime > 0 && thePlayer.motionX != 0.0 && thePlayer.motionZ != 0.0)
                        thePlayer.onGround = true

                    // Reduce Y
                    if (thePlayer.hurtResistantTime > 0 && aacPushYReducerValue.get()
                        && !KevinClient.moduleManager.getModule(Speed::class.java).state)
                        thePlayer.motionY -= 0.014999993
                }

                // Reduce XZ
                if (thePlayer.hurtResistantTime >= 19) {
                    val reduce = aacPushXZReducerValue.get()

                    thePlayer.motionX /= reduce
                    thePlayer.motionZ /= reduce
                }
            }

            "aaczero" -> if (thePlayer.hurtTime > 0) {
                if (!velocityInput || thePlayer.onGround || thePlayer.fallDistance > 2F)
                    return

                thePlayer.motionY -= 1.0
                thePlayer.isAirBorne = true
                thePlayer.onGround = true
            } else
                velocityInput = false
            "matrixreduce" -> {
                if (mc.thePlayer.hurtTime > 0) {
                    if (mc.thePlayer.onGround) {
                        if (mc.thePlayer.hurtTime <= 6) {
                            mc.thePlayer.motionX *= 0.70
                            mc.thePlayer.motionZ *= 0.70
                        }
                        if (mc.thePlayer.hurtTime <= 5) {
                            mc.thePlayer.motionX *= 0.80
                            mc.thePlayer.motionZ *= 0.80
                        }
                    } else if (mc.thePlayer.hurtTime <= 10) {
                        mc.thePlayer.motionX *= 0.60
                        mc.thePlayer.motionZ *= 0.60
                    }
                }
            }
            "allowfirst" -> if (velocityInput && mc.thePlayer.hurtTime == 8) {
                velocityInput = false
                mc.thePlayer.setVelocity(0.0, 0.0, 0.0)
            }
            "click" -> if (velocityInput && thePlayer.hurtTime >= clickTime.get()) {
                if (!attackRayTrace(clickCount.get(), clickRange.get().toDouble(), thePlayer.isSprinting)) {
                    if (clickFakeSwing.get()) mc.netHandler.addToSendQueue(C0APacketAnimation())
                    velocityInput = false
                }
            } else velocityInput = false
            "legitsmart" -> if (velocityInput) {
                if (mc.thePlayer.onGround && mc.thePlayer.hurtTime == 9 && mc.thePlayer.isSprinting && mc.currentScreen == null) {
                    if (jumped > 2) {
                        jumped = 0
                    } else {
                        ++jumped
                        if (mc.thePlayer.ticksExisted % 5 != 0) mc.gameSettings.keyBindJump.pressed = true
                    }
                } else if (mc.thePlayer.hurtTime == 8) {
                    mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                    velocityInput = false
                }
            }
            "intavejump" -> if (velocityInput) {
                if (mc.thePlayer.hurtTime == 9) {
                    if (++jumped % 2 == 0 && mc.thePlayer.onGround && mc.thePlayer.isSprinting && mc.currentScreen == null) {
                        mc.gameSettings.keyBindJump.pressed = true
                        jumped = 0 // reset
                    }
                } else {
                    mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                    velocityInput = false
                }
            }
            "pikastable" -> if (velocityInput && velocityTimer.hasTimePassed(80)) {
                if (!thePlayer.onGround) {
                    val reducer = (Math.random() - 0.5) / 50.0 + 0.2f
                    thePlayer.motionY *= reducer
                }
                velocityInput = false
            }
            // by IamFrozenMilk
            "testgrimac" -> if (velocityInput && thePlayer.hurtTime == 8) {
                MovementUtils.strafe(0.025f)
                velocityInput = false
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet

        if (packet is S12PacketEntityVelocity) {

            if ((mc.theWorld?.getEntityByID(packet.entityID) ?: return) != thePlayer)
                return

            velocityTimer.reset()

            when (modeValue.get().lowercase(Locale.getDefault())) {
                "simple" -> {
                    if (explosion && explosionCheck.get()) {
                        explosion = false;return
                    }

                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F)
                        event.cancelEvent()

                    packet.motionX = (packet.motionX * horizontal).toInt()
                    packet.motionY = (packet.motionY * vertical).toInt()
                    packet.motionZ = (packet.motionZ * horizontal).toInt()

                    if (simpleCancelTransaction.get()) transactionCancelCount = simpleCancelTransactionCount.get()
                }

                "aac", "reverse", "smoothreverse", "aaczero", "allowfirst", "pikastable", "intavejump" -> velocityInput = true

                "testgrimac" -> if (thePlayer.onGround) { velocityInput = true }

                "hypixelreverse" -> {
                    if (MovementUtils.isMoving) {
                        velocityInput = true
                    } else {
                        packet.motionX = 0
                        packet.motionZ = 0
                    }
                }

                "legitsmart" -> {
                    if (packet.motionX * packet.motionX + packet.motionZ * packet.motionZ + packet.motionY * packet.motionY > 640000) velocityInput = true
                }

                "aac5packet" -> {
                    if (mc.isIntegratedServerRunning) return
                    if (thePlayer.isBurning && fireCheck.get()) return
                    PacketUtils.sendPacketNoEvent(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            Double.MAX_VALUE,
                            mc.thePlayer.posZ,
                            mc.thePlayer.onGround
                        )
                    )
                    event.cancelEvent()
                }

                "glitch" -> {
                    if (!thePlayer.onGround)
                        return

                    velocityInput = true
                    event.cancelEvent()
                }

                "matrixsimple" -> {
                    packet.motionX = (packet.motionX * 0.36).toInt()
                    packet.motionZ = (packet.motionZ * 0.36).toInt()
                    if (mc.thePlayer.onGround) {
                        packet.motionX = (packet.motionX * 0.9).toInt()
                        packet.motionZ = (packet.motionZ * 0.9).toInt()
                    }
                }

                "matrixreverse" -> {
                    packet.motionX = (packet.motionX * -0.3).toInt()
                    packet.motionZ = (packet.motionZ * -0.3).toInt()
                }
                "testbuzzreverse" -> {
                    packet.motionX = -packet.motionX
                    packet.motionZ = -packet.motionZ
                }
                "click" -> {
                    if (packet.motionX == 0 && packet.motionZ == 0) return
                    if (attackRayTrace(
                            clickCount.get(),
                            clickRange.get().toDouble(),
                            clickOnPacket.get() && mc.thePlayer.isSprinting
                        )
                    )
                        velocityInput = true
                }
            }
        } else if (packet is S27PacketExplosion) {
            if (packet.func_149149_c() != 0F ||
                packet.func_149144_d() != 0F ||
                packet.func_149147_e() != 0F) explosion = true
            if (cancelExplosionPacket.get()) event.cancelEvent()
        } else if (packet is C0FPacketConfirmTransaction) {
            if (transactionCancelCount > 0) {
                --transactionCancelCount
                event.cancelEvent()
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "aacpush" -> {
                jump = true

                if (!thePlayer.isCollidedVertically)
                    event.cancelEvent()
            }
            "aaczero" -> if (thePlayer.hurtTime > 0)
                event.cancelEvent()
        }
    }

    fun attackRayTrace(attack: Int, range: Double, doAttack: Boolean=true): Boolean {
        if (mc.thePlayer == null) return false
        val raycastedEntity = RaycastUtils.raycastEntity(range + 1, object : RaycastUtils.EntityFilter {
            override fun canRaycast(entity: Entity?): Boolean {
                return entity != null && entity is EntityLivingBase
            }
        })

        raycastedEntity?.let {
            if (it !is EntityPlayer) return true
            if (it.entityBoundingBox.expands(it.collisionBorderSize.toDouble()).getLookingTargetRange(mc.thePlayer) > range) return false
            if (doAttack) {
                KevinClient.eventManager.callEvent(AttackEvent(it))
                repeat(attack) { _ ->
                    if (clickSwing.get()) mc.thePlayer.swingItem()
                    else mc.netHandler.addToSendQueue(C0APacketAnimation())
                    mc.netHandler.addToSendQueue(C02PacketUseEntity(it, C02PacketUseEntity.Action.ATTACK))
                }
                mc.thePlayer.attackTargetEntityWithCurrentItem(it)
            }
            return true
        }
        return false
    }
}