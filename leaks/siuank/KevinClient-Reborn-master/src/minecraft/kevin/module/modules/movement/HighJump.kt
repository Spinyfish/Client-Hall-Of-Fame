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
package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.JumpEvent
import kevin.event.MoveEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.BlockUtils.getBlock
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockPane
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class HighJump : Module("HighJump", "Allows you to jump higher.", category = ModuleCategory.MOVEMENT) {
    private val heightValue = FloatValue("Height", 2f, 0.5f, 5f)
    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Damage", "AACv3", "DAC", "Mineplex", "Timer", "VulcanLong", "Matrix","MatrixWater"), "Vanilla")
    private val glassValue = BooleanValue("OnlyGlassPane", false)
    private val timerValue = FloatValue("Timer-Timer",0.1f,0.01f,1f)
    private val waitTimeValue = IntegerValue("Timer-WaitTime",1,0,5)
    private val flyValue = BooleanValue("Timer-Glide",false)

    private var jumpState = 1
    private var fly = false
    private var flyState = 0
    private var timer = -1
    private var timerlock = false

    private var matrixStatus=0
    private var matrixWasTimer=false
    private val matrixTimer = MSTimer()

    override fun onDisable() {
        when(modeValue.get()){
            "Timer" -> {
                mc.timer.timerSpeed = 1F
                jumpState = 1
                fly = false
                flyState = 0
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer!!

        if (modeValue equal "Timer") {
            if (mc.thePlayer!!.onGround) {
                if (fly) {
                    state = false
                    return
                }
                mc.timer.timerSpeed = timerValue.get()
                mc.thePlayer!!.jump()
                jumpState = 2
            } else {
                if (jumpState == 2) {
                    mc.timer.timerSpeed = 1F
                    if (!flyValue.get()) state = false
                    if (!timerlock) {
                        timerlock = true
                        timer = 0
                    }
                    if (timer >= waitTimeValue.get())
                        timerlock = false
                    fly = true
                    timer = -1
                }

                if (jumpState != 2) {
                    jumpState = 2
                }
            }
            if (timer != -1)
                timer += 1

            if (fly) {
                flyState += 1
                if (flyState >= 6) {
                    mc.thePlayer!!.motionY = .015
                    flyState = 0
                }
            }
            return
        }

        if (modeValue equal "VulcanLong") {
            mc.thePlayer.motionY = 0.8995931202
            MovementUtils.strafe(0.48f)
            state = false
        }

        if (glassValue.get() && (getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ))) !is BlockPane)
            return

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "damage" -> if (thePlayer.hurtTime > 0 && thePlayer.onGround) thePlayer.motionY += 0.42f * heightValue.get()
            "aacv3" -> if (!thePlayer.onGround) thePlayer.motionY += 0.059
            "dac" -> if (!thePlayer.onGround) thePlayer.motionY += 0.049999
            "mineplex" -> if (!thePlayer.onGround) MovementUtils.strafe(0.35f)
            "matrixwater" -> {
                if (mc.thePlayer.isInWater) {
                    if (mc.theWorld.getBlockState(
                            BlockPos(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 1,
                                mc.thePlayer.posZ
                            )
                        ).block == Block.getBlockById(9)
                    ) {
                        mc.thePlayer.motionY = 0.18
                    } else if (mc.theWorld.getBlockState(
                            BlockPos(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ
                            )
                        ).block == Block.getBlockById(9)
                    ) {
                        mc.thePlayer.motionY = heightValue.get().toDouble()
                        mc.thePlayer.onGround = true
                    }
                }
            }
            "matrix" -> {
                if (matrixWasTimer) {
                    mc.timer.timerSpeed = 1.00f
                    matrixWasTimer = false
                }
                if ((mc.theWorld.getCollidingBoundingBoxes(
                        mc.thePlayer,
                        mc.thePlayer.entityBoundingBox.offset(0.0, mc.thePlayer.motionY, 0.0).expand(0.0, 0.0, 0.0)
                    ).isNotEmpty()
                            || mc.theWorld.getCollidingBoundingBoxes(
                        mc.thePlayer,
                        mc.thePlayer.entityBoundingBox.offset(0.0, -4.0, 0.0).expand(0.0, 0.0, 0.0)
                    ).isNotEmpty())
                    && mc.thePlayer.fallDistance > 10
                ) {
                    if (!mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = 0.1f
                        matrixWasTimer = true
                    }
                }
                if (matrixTimer.hasTimePassed(1000) && matrixStatus == 1) {
                    mc.timer.timerSpeed = 1.0f
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    matrixStatus = 0
                    return
                }
                if (matrixStatus == 1 && mc.thePlayer.hurtTime > 0) {
                    mc.timer.timerSpeed = 1.0f
                    mc.thePlayer.motionY = 3.0
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.jumpMovementFactor = 0.00f
                    matrixStatus = 0
                    return
                }
                if (matrixStatus == 2) {
                    mc.thePlayer.sendQueue.addToSendQueue(C0APacketAnimation())
                    mc.thePlayer.sendQueue.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    repeat(8) {
                        mc.thePlayer.sendQueue.addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 0.3990,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        mc.thePlayer.sendQueue.addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                    }
                    mc.thePlayer.sendQueue.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                    mc.thePlayer.sendQueue.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                    mc.timer.timerSpeed = 0.6f
                    matrixStatus = 1
                    matrixTimer.reset()
                    mc.thePlayer.sendQueue.addToSendQueue(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ),
                            EnumFacing.UP
                        )
                    )
                    mc.thePlayer.sendQueue.addToSendQueue(C0APacketAnimation())
                    return
                }
                if (mc.thePlayer.isCollidedHorizontally && matrixStatus == 0 && mc.thePlayer.onGround) {
                    mc.thePlayer.sendQueue.addToSendQueue(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ),
                            EnumFacing.UP
                        )
                    )
                    mc.thePlayer.sendQueue.addToSendQueue(C0APacketAnimation())
                    matrixStatus = 2
                    mc.timer.timerSpeed = 0.05f
                }
                if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround) {
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.onGround = false
                }
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        val thePlayer = mc.thePlayer ?: return

        if (glassValue.get() && (getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ)))!is BlockPane)
            return
        if (!thePlayer.onGround) {
            if ("mineplex" == modeValue.get().lowercase(Locale.getDefault())) {
                thePlayer.motionY += if (thePlayer.fallDistance == 0.0f) 0.0499 else 0.05
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        val thePlayer = mc.thePlayer ?: return
        if (modeValue equal "Timer") {
            event.motion = heightValue.get()
            return
        }

        if (glassValue.get() && (getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ))) !is BlockPane)
            return
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "vanilla" -> event.motion = event.motion * heightValue.get()
            "mineplex" -> event.motion = 0.47f
        }
    }

    override val tag: String
        get() = modeValue.get()
}