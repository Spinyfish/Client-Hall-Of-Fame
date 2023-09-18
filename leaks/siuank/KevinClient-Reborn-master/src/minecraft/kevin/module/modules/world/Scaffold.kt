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
package kevin.module.modules.world

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.*
import kevin.utils.*
import kevin.utils.BlockUtils.canBeClicked
import kevin.utils.BlockUtils.isReplaceable
import net.minecraft.block.*
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.stats.StatList
import net.minecraft.util.*
import net.minecraft.util.MathHelper.wrapAngleTo180_float
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.*

@Suppress("ControlFlowWithEmptyBody")
class Scaffold : Module("Scaffold", "Automatically places blocks beneath your feet.", category = ModuleCategory.WORLD) {
    //private val modeValue = ListValue("Mode", arrayOf("Normal", "Expand"), "Normal")
    private val towerModeValue = ListValue(
        "TowerMode",
        arrayOf("Jump", "Jump2", "Motion", "Motion2", "ConstantMotion", "MotionTP", "MotionTP2", "Packet", "Teleport", "AAC3.3.9", "AAC3.6.4", "Vulcan"),
        "Jump"
    )

    //private val matrixValue = BooleanValue("TowerMatrix", false)
    private val towerNoMoveValue = BooleanValue("TowerNoMove",false)

    // ConstantMotion
    private val constantMotionValue = FloatValue("TowerConstantMotion", 0.42f, 0.1f, 1f)
    private val constantMotionJumpGroundValue = FloatValue("TowerConstantMotionJumpGround", 0.79f, 0.76f, 1f)

    // Teleport
    private val teleportHeightValue = FloatValue("TowerTeleportHeight", 1.15f, 0.1f, 5f)
    private val teleportDelayValue = IntegerValue("TowerTeleportDelay", 0, 0, 20)
    private val teleportGroundValue = BooleanValue("TowerTeleportGround", true)
    private val teleportNoMotionValue = BooleanValue("TowerTeleportNoMotion", false)

    private val towerFakeJump = BooleanValue("TowerFakeJump",true)

    // Delay
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 0, 0, 1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelay = minDelayValue.get()
            if (minDelay > newValue) set(minDelay)
        }
    }

    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 0, 0, 1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelay = maxDelayValue.get()
            if (maxDelay < newValue) set(maxDelay)
        }
    }

    // Placeable delay
    private val placeDelay = BooleanValue("PlaceDelay", true)

    // Autoblock
    private val autoBlockValue = ListValue("AutoBlock", arrayOf("Off", "Pick", "Spoof", "Switch"), "Spoof")

    // Basic stuff
    private val sprintValue = ListValue("Sprint", arrayOf("Always", "Dynamic", "Smart", "OnGround", "OffGround", "OFF"), "Always")
    private val towerNoSprintSwitch = BooleanValue("TowerNoSprintSwitch", false)
    val canSprint: Boolean
        get() = MovementUtils.isMoving && when (sprintValue.get().lowercase()) {
            "always", "dynamic" -> true
            "smart" -> mc.thePlayer.moveForward > 0.3 && (lockRotation == null || (abs(RotationUtils.getAngleDifference(lockRotation!!.yaw, mc.thePlayer.rotationYaw)) < 90))
            "onground" -> mc.thePlayer.onGround
            "offground" -> !mc.thePlayer.onGround
            else -> false
        }
    private val autoJump = BooleanValue("AutoJump",false)
    private val swingValue = BooleanValue("Swing", true)
    private val searchValue = BooleanValue("Search", true)
    private val advancedSearchValue = BooleanValue("AdvancedSearch", true)
    private val downValue = BooleanValue("Down", true)
    private val placeModeValue = ListValue("PlaceTiming", arrayOf("Update", "Pre", "Post"), "Update")

    // Eagle
    private val eagleValue = ListValue("Eagle", arrayOf("Normal", "Smart", "Silent", "Off"), "Normal")
    private val blocksToEagleValue = IntegerValue("BlocksToEagle", 0, 0, 10)
    private val edgeDistanceValue = FloatValue("EagleEdgeDistance", 0f, 0f, 0.5f)

    // Expand
    private val expandMode = ListValue("ExpandMode", arrayOf("LiquidBounce", "Sigma"), "LiquidBounce")
    private val expandOnlyMove = BooleanValue("ExpandOnlyMove", true)
    private val expandOnlyMoveOnlyGround = BooleanValue("ExpandOnlyMoveOnlyGround", true)
    private val expandLengthValue = IntegerValue("ExpandLength", 0, 0, 6)

    private val shouldExpand
    get() = expandLengthValue.get()!=0&&!(jumpCheckValue.get()&&mc.gameSettings.keyBindJump.isKeyDown)&&!(downCheckValue.get()&&shouldGoDown)&&(!expandOnlyMove.get()||(MovementUtils.isMoving||(expandOnlyMoveOnlyGround.get()&&!mc.thePlayer.onGround)))

    // Rotation Options
    private val rotationValues = arrayOf("Off", "Normal", "AAC", "LimitedAAC", "MoveDirection", "Custom")
    private val strafeMode = ListValue("Strafe", arrayOf("Off", "AAC", "Strict"), "Off")
    private val rotationsValue = ListValue("Rotations", rotationValues, "Normal")
    private val towerRotationsValue = ListValue("TowerRotations", rotationValues, "Normal")
    private val aacYawOffsetValue = IntegerValue("AACYawOffset", 0, 0, 90)
    private val customYawValue = IntegerValue("CustomYaw", -145, -180, 180)
    private val customPitchValue = FloatValue("CustomPitch", 82.4f, -90f, 90f)
    private val customTowerYawValue = IntegerValue("CustomTowerYaw", -145, -180, 180)
    private val customTowerPitchValue = FloatValue("CustomTowerPitch", 79f, -90f, 90f)
    private val silentRotationValue = BooleanValue("SilentRotation", true)
    private val keepRotationValue = BooleanValue("KeepRotation", true)
    private val keepLengthValue = IntegerValue("KeepRotationLength", 0, 0, 20)

    private val towerState
    get() = mc.gameSettings.keyBindJump.isKeyDown
    private val rotationsOn
    get() = !if (towerState) towerRotationsValue equal "Off" else rotationsValue equal "Off"

    // XZ/Y range
    private val searchMode = ListValue("XYZSearch", arrayOf("Auto", "AutoCenter", "Manual", "Sigma"), "AutoCenter")
    private val xzRangeValue = FloatValue("xzRange", 0.8f, 0f, 1f)
    private var yRangeValue = FloatValue("yRange", 0.8f, 0f, 1f)
    private val minDistValue = FloatValue("MinDist", 0.0f, 0.0f, 0.2f)

    // Search Accuracy
    private val searchAccuracyValue: IntegerValue = object : IntegerValue("SearchAccuracy", 8, 1, 16) {
//        override fun onChanged(oldValue: Int, newValue: Int) {
//            if (maximum < newValue) {
//                set(maximum)
//            } else if (minimum > newValue) {
//                set(minimum)
//            }
//        }
    }

    //Tower XZ/Y range
    private val towerSearchMode = ListValue("Tower-XYZSearch", arrayOf("Auto", "AutoCenter", "Manual", "Sigma"), "AutoCenter")
    private val towerXZRangeValue = FloatValue("Tower-xzRange", 0.8f, 0f, 1f)
    private var towerYRangeValue = FloatValue("Tower-yRange", 0.8f, 0f, 1f)
    private val towerMinDistValue = FloatValue("Tower-MinDist", 0.0f, 0.0f, 0.2f)

    // Tower Search Accuracy
    private val towerSearchAccuracyValue: IntegerValue = object : IntegerValue("Tower-SearchAccuracy", 8, 1, 16) {
//        override fun onChanged(oldValue: Int, newValue: Int) {
//            if (maximum < newValue) {
//                set(maximum)
//            } else if (minimum > newValue) {
//                set(minimum)
//            }
//        }
    }

    // Turn Speed
    private val maxTurnSpeedValue: FloatValue = object : FloatValue("MaxTurnSpeed", 180f, 1f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeedValue.get()
            if (v > newValue) set(v)
            if (maximum < newValue) {
                set(maximum)
            } else if (minimum > newValue) {
                set(minimum)
            }
        }
    }
    private val minTurnSpeedValue: FloatValue = object : FloatValue("MinTurnSpeed", 180f, 1f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeedValue.get()
            if (v < newValue) set(v)
            if (maximum < newValue) {
                set(maximum)
            } else if (minimum > newValue) {
                set(minimum)
            }
        }
    }
    // 跳跃检测
    private val jumpCheckValue = BooleanValue("JumpCheck",false)
    // 向下检测
    private val downCheckValue = BooleanValue("DownCheck",true)

    // Zitter
    private val zitterMode = ListValue("Zitter", arrayOf("Off", "Teleport", "Smooth"), "Off")
    private val zitterSpeed = FloatValue("ZitterSpeed", 0.13f, 0.1f, 0.3f)
    private val zitterStrength = FloatValue("ZitterStrength", 0.05f, 0f, 0.2f)

    // Game
    private val timerValue = FloatValue("Timer", 1f, 0.1f, 10f)
    private val speedModifierValue = FloatValue("SpeedModifier", 1f, 0f, 2f)
    private val motionSpeedEnabled = BooleanValue("MotionSpeedSet", false)
    private val motionSpeedValue = FloatValue("MotionSpeed", 0.1f, 0.05f, 1f)
    private val slowValue = BooleanValue("Slow", false)
    private val slowSpeed = FloatValue("SlowSpeed", 0.6f, 0.2f, 1f)

    // Safety
    private val sameYValue = BooleanValue("SameY", false)
    private val sameYJumpUp = BooleanValue("SameYJumpUp", false)
    private val safeWalkValue = BooleanValue("SafeWalk", true)
    private val airSafeValue = BooleanValue("AirSafe", false)
    private val grimACRotation = BooleanValue("GrimACRotation", false)
    private val hitableCheck = ListValue("HitableCheck", arrayOf("Strict", "Simple", "Normal", "None"), "Normal")
    private val invalidPlaceFacingMode = ListValue("WhenPlaceFacingInvalid", arrayOf("CancelIt", "FixIt", "IgnoreIt"), "FixIt")

    // Visuals
    private val counterDisplayValue = BooleanValue("Counter", true)
    private val markValue = BooleanValue("Mark", false)

// Variables

    // Target block
    private var targetPlace: PlaceInfo? = null
    private var lastPlace: BlockPos? = null

    // Rotation lock
    private var lockRotation: Rotation? = null
    private var lockRotationTimer = TickTimer()
    private var aacRotationPositive = true
    private var grimRotationBoolean = false

    // Launch position
    private var launchY = 0
    private var facesBlock = false

    // AutoBlock
    private var slot = 0

    // Zitter Direction
    private var zitterDirection = false

    // Delay
    private val delayTimer = MSTimer()
    private val zitterTimer = MSTimer()
    private var delay = 0L

    // Eagle
    private var placedBlocksWithoutEagle = 0
    private var eagleSneaking: Boolean = false

    // Downwards
    private var shouldGoDown: Boolean = false

    // ENABLING MODULE
    override fun onEnable() {
        if (mc.thePlayer == null) return
        launchY = mc.thePlayer!!.posY.toInt()
        slot = mc.thePlayer!!.inventory.currentItem
        facesBlock = false
        aacRotationPositive = ThreadLocalRandom.current().nextBoolean()
//        if (rotationsOn) {
//            val calculatedRotation = calculateRotation(RotationUtils.bestServerRotation())
//            if (minTurnSpeedValue.get() < 180) {
//                val limitedRotation = RotationUtils.limitAngleChange(
//                    RotationUtils.serverRotation,
//                    calculatedRotation,
//                    (Math.random() * (maxTurnSpeedValue.get() - minTurnSpeedValue.get()) + minTurnSpeedValue.get()).toFloat()
//                )
//
//                if ((10 * wrapAngleTo180_float(limitedRotation.yaw)).roundToInt() == (10 * wrapAngleTo180_float(calculatedRotation.yaw)).roundToInt() &&
//                    (10 * wrapAngleTo180_float(limitedRotation.pitch)).roundToInt() == (10 * wrapAngleTo180_float(calculatedRotation.pitch)).roundToInt()) {
//                    setRotation(calculatedRotation)
//                    lockRotation = calculatedRotation
//                    facesBlock = true
//                } else {
//                    setRotation(limitedRotation)
//                    lockRotation = limitedRotation
//                    facesBlock = false
//                }
//            } else {
//                setRotation(calculatedRotation)
//                lockRotation = calculatedRotation
//                facesBlock = true
//            }
//            lockRotationTimer.reset()
//        }
    }

    private fun fakeJump() {
        if(!towerFakeJump.get()) return
        mc.thePlayer!!.isAirBorne = true
        mc.thePlayer!!.triggerAchievement(StatList.jumpStat)
    }

    private var jumpGround = 0.0
    private val timer = TickTimer()

    /**
     * Move player
     */
    private fun move() {
        val thePlayer = mc.thePlayer ?: return

        if (towerNoMoveValue.get()){
            mc.thePlayer.motionX = .0
            mc.thePlayer.motionZ = .0
        }

        when (towerModeValue.get().lowercase(Locale.getDefault())) {
            "jump2" -> {
                if (thePlayer.onGround) {
                    fakeJump()
                    thePlayer.motionY = thePlayer.jumpUpwardsMotionDouble()
                } else if (thePlayer.motionY < 0) {
                    fakeJump()
                    thePlayer.motionY = thePlayer.jumpUpwardsMotionDouble()
                }
            }
            "motion" -> if (thePlayer.onGround) {
                fakeJump()
                thePlayer.motionY = thePlayer.jumpUpwardsMotionDouble()
            } else if (thePlayer.motionY < 0.1) {
                thePlayer.motionY = -0.3
            }
            "motion2" -> {
                if (mc.thePlayer.onGround) {
                    fakeJump()
                    mc.thePlayer.motionY = thePlayer.jumpUpwardsMotionDouble()
                } else if (mc.thePlayer.motionY < 0.18) {
                    mc.thePlayer.motionY -= 0.02
                }
            }
            "motiontp" -> if (thePlayer.onGround) {
                fakeJump()
                thePlayer.motionY = thePlayer.jumpUpwardsMotionDouble()
            } else if (thePlayer.motionY < 0.23) {
                thePlayer.setPosition(thePlayer.posX, truncate(thePlayer.posY), thePlayer.posZ)
            }
            "motiontp2" -> {
                if (mc.thePlayer.onGround) {
                    fakeJump()
                    mc.thePlayer.motionY = thePlayer.jumpUpwardsMotionDouble()
                } else if (mc.thePlayer.motionY < 0.23) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, truncate(mc.thePlayer.posY), mc.thePlayer.posZ)
                    mc.thePlayer.onGround = true
                    mc.thePlayer.motionY = 0.42
                }
            }
            "packet" -> if (thePlayer.onGround && timer.hasTimePassed(2)) {
                fakeJump()
                mc.netHandler.addToSendQueue(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        thePlayer.posX,
                        thePlayer.posY + thePlayer.jumpUpwardsMotionDouble(), thePlayer.posZ, false
                    )
                )
                mc.netHandler.addToSendQueue(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        thePlayer.posX,
                        thePlayer.posY + 0.753, thePlayer.posZ, false
                    )
                )
                thePlayer.setPosition(thePlayer.posX, thePlayer.posY + 1.0, thePlayer.posZ)
                timer.reset()
            }
            "teleport" -> {
                if (teleportNoMotionValue.get()) {
                    thePlayer.motionY = 0.0
                }
                if ((thePlayer.onGround || !teleportGroundValue.get()) && timer.hasTimePassed(teleportDelayValue.get())) {
                    fakeJump()
                    thePlayer.setPositionAndUpdate(
                        thePlayer.posX,
                        thePlayer.posY + teleportHeightValue.get(),
                        thePlayer.posZ
                    )
                    timer.reset()
                }
            }
            "constantmotion" -> {
                if (thePlayer.onGround) {
                    fakeJump()
                    jumpGround = thePlayer.posY
                    thePlayer.motionY = constantMotionValue.get().toDouble()
                }
                if (thePlayer.posY > jumpGround + constantMotionJumpGroundValue.get()) {
                    fakeJump()
                    thePlayer.setPosition(
                        thePlayer.posX,
                        truncate(thePlayer.posY),
                        thePlayer.posZ
                    )
                    thePlayer.motionY = constantMotionValue.get().toDouble()
                    jumpGround = thePlayer.posY
                }
            }
            "aac3.3.9" -> {
                if (thePlayer.onGround) {
                    fakeJump()
                    thePlayer.motionY = 0.4001
                }
                mc.timer.timerSpeed = 1f
                if (thePlayer.motionY < 0) {
                    thePlayer.motionY -= 0.00000945
                    mc.timer.timerSpeed = 1.6f
                }
            }
            "aac3.6.4" -> if (thePlayer.ticksExisted % 4 == 1) {
                thePlayer.motionY = 0.4195464
                thePlayer.setPosition(thePlayer.posX - 0.035, thePlayer.posY, thePlayer.posZ)
            } else if (thePlayer.ticksExisted % 4 == 0) {
                thePlayer.motionY = -0.5
                thePlayer.setPosition(thePlayer.posX + 0.035, thePlayer.posY, thePlayer.posZ)
            }
        }
    }

    private fun sameY(): Boolean = sameYValue.get() && (!sameYJumpUp.get()||!mc.gameSettings.keyBindJump.isKeyDown)

// UPDATE EVENTS

    /** @param */

    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        //if (event.eventState == UpdateState.OnUpdate) return

        if (!sameY()) launchY = mc.thePlayer!!.posY.toInt()

        mc.timer.timerSpeed = timerValue.get()
        shouldGoDown =
            downValue.get() && !sameY() && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && blocksAmount > 1
        if (shouldGoDown) {
            mc.gameSettings.keyBindSneak.pressed = false
        }
        // AutoJump
        if (mc.thePlayer.onGround
            && mc.thePlayer.jumpTicks == 0
            && MovementUtils.isMoving
            && !mc.thePlayer.isInLava
            && !mc.thePlayer.isInWater
            && !mc.thePlayer.inWeb
            && !mc.thePlayer.isOnLadder
            && !mc.gameSettings.keyBindJump.isKeyDown
            && autoJump.get()){
            mc.thePlayer.jump()
        }
        if (slowValue.get()) {
            mc.thePlayer!!.motionX = mc.thePlayer!!.motionX * slowSpeed.get()
            mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ * slowSpeed.get()
        }
        if (mc.thePlayer!!.onGround) {
            when (zitterMode.get().lowercase(Locale.getDefault())) {
                //"off" -> return //LiquidBounce B73, WTF is this?? if you turn off zitter u can't use eagle??????
                "smooth" -> {
                    if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) {
                        mc.gameSettings.keyBindRight.pressed = false
                    }
                    if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) {
                        mc.gameSettings.keyBindLeft.pressed = false
                    }
                    if (zitterTimer.hasTimePassed(100)) {
                        zitterDirection = !zitterDirection
                        zitterTimer.reset()
                    }
                    if (zitterDirection) {
                        mc.gameSettings.keyBindRight.pressed = true
                        mc.gameSettings.keyBindLeft.pressed = false
                    } else {
                        mc.gameSettings.keyBindRight.pressed = false
                        mc.gameSettings.keyBindLeft.pressed = true
                    }
                }
                "teleport" -> {
                    MovementUtils.strafe(zitterSpeed.get())
                    val yaw: Double =
                        Math.toRadians(mc.thePlayer!!.rotationYaw + if (zitterDirection) 90.0 else -90.0)
                    mc.thePlayer!!.motionX = mc.thePlayer!!.motionX - sin(yaw) * zitterStrength.get()
                    mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ + cos(yaw) * zitterStrength.get()
                    zitterDirection = !zitterDirection
                }
            }
        }
        // Eagle
        if (!eagleValue.get().equals("Off", true) && !shouldGoDown) {
            var dif = 0.5
            if (edgeDistanceValue.get() > 0) {
                for (facingType in EnumFacing.values()) {
                    if (facingType != EnumFacing.NORTH && facingType != EnumFacing.EAST && facingType != EnumFacing.SOUTH && facingType != EnumFacing.WEST)
                        continue
                    val blockPosition = BlockPos(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY - 1.0,
                        mc.thePlayer!!.posZ
                    )
                    val neighbor = blockPosition.offset(facingType, 1)
                    if (mc.theWorld!!.getBlockState(neighbor).block == (Blocks.air)) {
                        val calcDif = (if (facingType == EnumFacing.NORTH || facingType == EnumFacing.SOUTH)
                            abs((neighbor.z + 0.5) - mc.thePlayer!!.posZ) else
                            abs((neighbor.x + 0.5) - mc.thePlayer!!.posX)) - 0.5
                        if (calcDif < dif)
                            dif = calcDif
                    }
                }
            }
            if (placedBlocksWithoutEagle >= blocksToEagleValue.get() || !facesBlock) {
                val shouldEagle: Boolean = mc.theWorld!!.getBlockState(
                    BlockPos(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY - 1.0,
                        mc.thePlayer!!.posZ
                    )
                ).block == (Blocks.air) || dif < edgeDistanceValue.get()
                if (eagleValue.get().equals("Silent", true)) {
                    if (eagleSneaking != shouldEagle) {
                        mc.netHandler.addToSendQueue(
                            C0BPacketEntityAction(
                                mc.thePlayer!!, if (shouldEagle)
                                    C0BPacketEntityAction.Action.START_SNEAKING
                                else
                                    C0BPacketEntityAction.Action.STOP_SNEAKING
                            )
                        )
                    }
                    eagleSneaking = shouldEagle
                } else {
                    mc.gameSettings.keyBindSneak.pressed = shouldEagle
                    placedBlocksWithoutEagle = 0
                }
            } else {
                placedBlocksWithoutEagle++
            }
        }
        grimRotationBoolean = lockRotation != null
        update()
        if ((facesBlock || !rotationsOn || hitableCheck equal "None") && placeModeValue equal "Update") {
            place()
            timer.update()
            val update = if (!autoBlockValue.get().equals("Off", ignoreCase = true)) {
                InventoryUtils.findAutoBlockBlock() != -1 || mc.thePlayer.heldItem != null && mc.thePlayer.heldItem!!.item is ItemBlock
            } else {
                mc.thePlayer.heldItem != null && mc.thePlayer.heldItem!!.item is ItemBlock
            }
            if (update&&mc.gameSettings.keyBindJump.isKeyDown) move()
        }

        mc.thePlayer.isSprinting = canSprint
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return
        val packet = event.packet
        if ((packet) is C09PacketHeldItemChange) {
            slot = packet.slotId
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (strafeMode.get().equals("Off", true) || event.isCancelled)
            return

//        update()
        if (rotationsOn
            && (keepRotationValue.get() || !lockRotationTimer.hasTimePassed(keepLengthValue.get()))
            && lockRotation != null
        ) {
            if (targetPlace == null) {
                var yaw = 0F
                for (i in 0..7) {
                    if (abs(
                            RotationUtils.getAngleDifference(
                                lockRotation!!.yaw,
                                (i * 45).toFloat()
                            )
                        ) < abs(RotationUtils.getAngleDifference(lockRotation!!.yaw, yaw))
                    ) {
                        yaw = wrapAngleTo180_float((i * 45).toFloat())
                    }
                }
                lockRotation!!.yaw = yaw
            }
            setRotation(lockRotation!!)
            lockRotationTimer.update()
        }
        // not null
        lockRotation?.let {
            if (strafeMode.get().equals("AAC", true)) {
                it.applyStrafeToPlayer(event)
            } else { // strafeMode.get().equals("Strict", true)
                var strafe: Float = event.strafe
                var forward: Float = event.forward
                val deltaYaw = abs(RotationUtils.getAngleDifference(it.yaw, mc.thePlayer.rotationYaw))
                if (strafe == 0f && deltaYaw > 45f && deltaYaw < 170 && forward != 0f) {
                    val floorX = floor(mc.thePlayer.posX)
                    val floorZ = floor(mc.thePlayer.posZ)
                    val xChange = mc.thePlayer.posX - floorX
                    val zChange = mc.thePlayer.posZ - floorZ
                    val absForward = abs(forward)
                    when (mc.thePlayer.getHorizontalFacing(mc.thePlayer.rotationYaw)) {
                        EnumFacing.EAST -> strafe = if (zChange > 0.5) absForward else -absForward
                            EnumFacing.WEST -> strafe = if (zChange < 0.5) absForward else -absForward
                        EnumFacing.SOUTH -> strafe = if (xChange < 0.5) absForward else -absForward
                        EnumFacing.NORTH -> strafe = if (xChange > 0.5) absForward else -absForward
                        else -> {}
                    }
                    if (forward < 0f) {
                        strafe = -strafe
                    }
                    if (!mc.thePlayer.isSprinting && abs(deltaYaw - 90f) < 18f) {
                        forward = 0f
                    }
                }
                if (deltaYaw >= 90) {
                    strafe = -strafe
                    forward = -forward
                }

                val friction: Float = event.friction
                var f = strafe * strafe + forward * forward

                if (f >= 1.0E-4f) {
                    f = MathHelper.sqrt_float(f)
                    if (f < 1.0f) {
                        f = 1.0f
                    }
                    f = friction / f
                    strafe *= f
                    forward *= f
                    val f1 = MathHelper.sin(RotationUtils.targetRotation.yaw * Math.PI.toFloat() / 180.0f)
                    val f2 = MathHelper.cos(RotationUtils.targetRotation.yaw * Math.PI.toFloat() / 180.0f)
                    mc.thePlayer.motionX += (strafe * f2 - forward * f1).toDouble()
                    mc.thePlayer.motionZ += (forward * f2 + strafe * f1).toDouble()
                }
            }
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val eventState: EventState = event.eventState

        if (motionSpeedEnabled.get())
            MovementUtils.setMotion(motionSpeedValue.get().toDouble())

        // Lock Rotation
        if (rotationsOn
            && (keepRotationValue.get() || !lockRotationTimer.hasTimePassed(keepLengthValue.get()))
            && lockRotation != null
            && strafeMode.get().equals("Off", true)
        ) {
            setRotation(lockRotation!!)
            if (eventState == EventState.POST)
                lockRotationTimer.update()
        }

        // Face block
        if ((facesBlock || !rotationsOn || hitableCheck equal "None") && placeModeValue.get()
                .equals(eventState.stateName, true)
        )
            place()

        if (eventState == EventState.PRE) {
            timer.update()
            val update = if (!autoBlockValue.get().equals("Off", ignoreCase = true)) {
                InventoryUtils.findAutoBlockBlock() != -1 || mc.thePlayer.heldItem != null && mc.thePlayer.heldItem!!.item is ItemBlock
            } else {
                mc.thePlayer.heldItem != null && mc.thePlayer.heldItem!!.item is ItemBlock
            }
            if (update&&mc.gameSettings.keyBindJump.isKeyDown) move()
        }

        // Update and search for a new block
        if (eventState == EventState.PRE && strafeMode.get().equals("Off", true)) {}
//            update()

        // Reset placeable delay
        if (targetPlace == null && placeDelay.get())
            delayTimer.reset()
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (towerModeValue equal "Jump") {

        } else if (mc.gameSettings.keyBindJump.isKeyDown) event.cancelEvent()
    }

    fun update() {
        val isHeldItemBlock: Boolean =
            mc.thePlayer!!.heldItem != null && (mc.thePlayer!!.heldItem!!.item) is ItemBlock
        if (if (!autoBlockValue.get()
                    .equals("Off", true)
            ) InventoryUtils.findAutoBlockBlock() == -1 && !isHeldItemBlock else !isHeldItemBlock
        )
            return
        findBlock(shouldExpand)
    }

    private fun setRotation(rotation: Rotation) {
        if (silentRotationValue.get()) {
            RotationUtils.setTargetRotation(rotation, 0)
        } else {
            mc.thePlayer!!.rotationYaw = rotation.yaw
            mc.thePlayer!!.rotationPitch = rotation.pitch
        }
    }
/*
    private enum class X8Direction(val range: Pair<Double,Double>, val cross: Boolean, val leftRight: Double? = null, val x4: Boolean = false, val x: Int = 1, val z: Int = 1) {
        South(-22.5 to 22.5, false, x4 = true),        //南
        WestSouth(22.5 to 67.5, false, 45.0, x = -1, z = 1),        //西南
        West(67.5 to 112.5, false, x4 = true),         //西
        WestNorth(112.5 to 157.5, false, 135.0, x = -1, z = -1),    //西北
        North(157.5 to -157.5, true, x4 = true),       //北
        EastNorth(-157.5 to -112.5, false, -135.0, x = 1, z = -1),  //东北
        East(-112.5 to -67.5, false, x4 = true),       //东
        EastSouth(-67.5 to -22.5, false, -45.0, x = 1, z = 1),      //东南
    }
    private enum class EnumLeftRight {
        Left,Right
    }
    private val getDirection: Pair<X8Direction,EnumLeftRight>
    get() {
        val rotationYaw = wrapAngleTo180_float(mc.thePlayer.rotationYaw)
        val x8Direction = X8Direction.values().find {
            if (it.cross)
                rotationYaw >= it.range.first || rotationYaw < it.range.second
            else
                rotationYaw >= it.range.first && rotationYaw < it.range.second
        } ?: X8Direction.North
        val leftRight = if (x8Direction.leftRight != null)
                if (rotationYaw >= x8Direction.range.first && rotationYaw < x8Direction.leftRight)
                    EnumLeftRight.Left
                else
                    EnumLeftRight.Right
            else
                EnumLeftRight.Left
        return x8Direction to leftRight
    }
*/

    private fun isAirBlock(block: Block): Boolean {
        return if (block.material.isReplaceable) {
            !(block is BlockSnow && block.getBlockBoundsMaxY() > 0.125)
        } else false
    }
    private fun getExpandCords(x: Double, z: Double, forward: Double, strafe: Double, YAW: Float, expandLength: Double): Pair<Double,Double> {
        var underPos = BlockPos(x, mc.thePlayer.posY - 1, z)
        var underBlock = mc.theWorld.getBlockState(underPos).block
        var xCalc = x//-999.0
        var zCalc = z//-999.0
        var dist = 0.0
        val expandDist = expandLength * 2
        while (!isAirBlock(underBlock)) {
            xCalc = x
            zCalc = z
            dist++
            if (dist > expandDist) {
                dist = expandDist
            }
            xCalc += (forward * 0.45 * cos(Math.toRadians(YAW + 90.0)) + strafe * 0.45 * sin(Math.toRadians(YAW + 90.0))) * dist
            zCalc += (forward * 0.45 * sin(Math.toRadians(YAW + 90.0)) - strafe * 0.45 * cos(Math.toRadians(YAW + 90.0))) * dist
            if (dist == expandDist) {
                break
            }
            underPos = BlockPos(xCalc, mc.thePlayer.posY - 1, zCalc)
            underBlock = mc.theWorld.getBlockState(underPos).block
        }
        return xCalc to zCalc
    }
    private fun isPosSolid(pos: BlockPos): Boolean {
        val block = mc.theWorld.getBlockState(pos).block
        return ((block.material.isSolid || !block.isTranslucent || block.isBlockNormalCube || block is BlockLadder || block is BlockCarpet
                || block is BlockSnow || block is BlockSkull)
                && !block.material.isLiquid && block !is BlockContainer)
    }
    private fun getBlockData(pos: BlockPos): Pair<BlockPos,EnumFacing>? {
        when {
            isPosSolid(pos.add(0, -1, 0)) -> return pos.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos.add(-1, 0, 0)) -> return pos.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos.add(1, 0, 0)) -> return pos.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos.add(0, 0, 1)) -> return pos.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos.add(0, 0, -1)) -> return pos.add(0, 0, -1) to EnumFacing.SOUTH
        }
        val pos1 = pos.add(-1, 0, 0)
        when {
            isPosSolid(pos1.add(0, -1, 0)) -> return pos1.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos1.add(-1, 0, 0)) -> return pos1.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos1.add(1, 0, 0)) -> return pos1.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos1.add(0, 0, 1)) -> return pos1.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos1.add(0, 0, -1)) -> return pos1.add(0, 0, -1) to EnumFacing.SOUTH
        }
        val pos2 = pos.add(1, 0, 0)
        when {
            isPosSolid(pos2.add(0, -1, 0)) -> return pos2.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos2.add(-1, 0, 0)) -> return pos2.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos2.add(1, 0, 0)) -> return pos2.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos2.add(0, 0, 1)) -> return pos2.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos2.add(0, 0, -1)) -> return pos2.add(0, 0, -1) to EnumFacing.SOUTH
        }
        val pos3 = pos.add(0, 0, 1)
        when {
            isPosSolid(pos3.add(0, -1, 0)) -> return pos3.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos3.add(-1, 0, 0)) -> return pos3.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos3.add(1, 0, 0)) -> return pos3.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos3.add(0, 0, 1)) -> return pos3.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos3.add(0, 0, -1)) -> return pos3.add(0, 0, -1) to EnumFacing.SOUTH
        }
        val pos4 = pos.add(0, 0, -1)
        when {
            isPosSolid(pos4.add(0, -1, 0)) -> return pos4.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos4.add(-1, 0, 0)) -> return pos4.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos4.add(1, 0, 0)) -> return pos4.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos4.add(0, 0, 1)) -> return pos4.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos4.add(0, 0, -1)) -> return pos4.add(0, 0, -1) to EnumFacing.SOUTH
            isPosSolid(pos1.add(0, -1, 0)) -> return pos1.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos1.add(-1, 0, 0)) -> return pos1.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos1.add(1, 0, 0)) -> return pos1.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos1.add(0, 0, 1)) -> return pos1.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos1.add(0, 0, -1)) -> return pos1.add(0, 0, -1) to EnumFacing.SOUTH
            isPosSolid(pos2.add(0, -1, 0)) -> return pos2.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos2.add(-1, 0, 0)) -> return pos2.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos2.add(1, 0, 0)) -> return pos2.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos2.add(0, 0, 1)) -> return pos2.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos2.add(0, 0, -1)) -> return pos2.add(0, 0, -1) to EnumFacing.SOUTH
            isPosSolid(pos3.add(0, -1, 0)) -> return pos3.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos3.add(-1, 0, 0)) -> return pos3.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos3.add(1, 0, 0)) -> return pos3.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos3.add(0, 0, 1)) -> return pos3.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos3.add(0, 0, -1)) -> return pos3.add(0, 0, -1) to EnumFacing.SOUTH
            isPosSolid(pos4.add(0, -1, 0)) -> return pos4.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos4.add(-1, 0, 0)) -> return pos4.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos4.add(1, 0, 0)) -> return pos4.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos4.add(0, 0, 1)) -> return pos4.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos4.add(0, 0, -1)) -> return pos4.add(0, 0, -1) to EnumFacing.SOUTH
        }
        val pos5 = pos.add(0, -1, 0)
        when {
            isPosSolid(pos5.add(0, -1, 0)) -> return pos5.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos5.add(-1, 0, 0)) -> return pos5.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos5.add(1, 0, 0)) -> return pos5.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos5.add(0, 0, 1)) -> return pos5.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos5.add(0, 0, -1)) -> return pos5.add(0, 0, -1) to EnumFacing.SOUTH
        }
        val pos6 = pos5.add(1, 0, 0)
        when {
            isPosSolid(pos6.add(0, -1, 0)) -> return pos6.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos6.add(-1, 0, 0)) -> return pos6.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos6.add(1, 0, 0)) -> return pos6.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos6.add(0, 0, 1)) -> return pos6.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos6.add(0, 0, -1)) -> return pos6.add(0, 0, -1) to EnumFacing.SOUTH
        }
        val pos7 = pos5.add(-1, 0, 0)
        when {
            isPosSolid(pos7.add(0, -1, 0)) -> return pos7.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos7.add(-1, 0, 0)) -> return pos7.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos7.add(1, 0, 0)) -> return pos7.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos7.add(0, 0, 1)) -> return pos7.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos7.add(0, 0, -1)) -> return pos7.add(0, 0, -1) to EnumFacing.SOUTH
        }
        val pos8 = pos5.add(0, 0, 1)
        when {
            isPosSolid(pos8.add(0, -1, 0)) -> return pos8.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos8.add(-1, 0, 0)) -> return pos8.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos8.add(1, 0, 0)) -> return pos8.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos8.add(0, 0, 1)) -> return pos8.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos8.add(0, 0, -1)) -> return pos8.add(0, 0, -1) to EnumFacing.SOUTH
        }
        val pos9 = pos5.add(0, 0, -1)
        when {
            isPosSolid(pos9.add(0, -1, 0)) -> return pos9.add(0, -1, 0) to EnumFacing.UP
            isPosSolid(pos9.add(-1, 0, 0)) -> return pos9.add(-1, 0, 0) to EnumFacing.EAST
            isPosSolid(pos9.add(1, 0, 0)) -> return pos9.add(1, 0, 0) to EnumFacing.WEST
            isPosSolid(pos9.add(0, 0, 1)) -> return pos9.add(0, 0, 1) to EnumFacing.NORTH
            isPosSolid(pos9.add(0, 0, -1)) -> return pos9.add(0, 0, -1) to EnumFacing.SOUTH
        }
        return null
    }
    private fun randomNumber(max: Double, min: Double) =
        Math.random() * (max - min) + min
    private fun getVec3(pos: BlockPos, face: EnumFacing): Vec3 {
        var x = pos.x + 0.5
        var y = pos.y + 0.5
        var z = pos.z + 0.5
        x += face.frontOffsetX.toDouble() / 2
        z += face.frontOffsetZ.toDouble() / 2
        y += face.frontOffsetY.toDouble() / 2
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += randomNumber(0.3, -0.3)
            z += randomNumber(0.3, -0.3)
        } else {
            y += randomNumber(0.3, -0.3)
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += randomNumber(0.3, -0.3)
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += randomNumber(0.3, -0.3)
        }
        return Vec3(x, y, z)
    }
    private fun getRotations(block: BlockPos, face: EnumFacing): Rotation {
        val x = block.x + 0.5 - mc.thePlayer.posX + face.frontOffsetX.toDouble() / 2
        val z = block.z + 0.5 - mc.thePlayer.posZ + face.frontOffsetZ.toDouble() / 2
        val y = block.y + 0.5
        val d1 = mc.thePlayer.posY + mc.thePlayer.eyeHeight - y
        val d3 = MathHelper.sqrt_double(x * x + z * z).toDouble()
        var yaw = (atan2(z, x) * 180.0 / Math.PI).toFloat() - 90.0f
        val pitch = (atan2(d1, d3) * 180.0 / Math.PI).toFloat()
        if (yaw < 0.0f) {
            yaw += 360f
        }
        return Rotation(yaw, pitch)
    }

    // Search for new target block
    private fun findBlock(expand: Boolean) {
        val blockPosition: BlockPos =
            if (shouldGoDown) (if (mc.thePlayer!!.posY == mc.thePlayer!!.posY.toInt() + 0.5) BlockPos(
                mc.thePlayer!!.posX,
                mc.thePlayer!!.posY - 0.6,
                mc.thePlayer!!.posZ
            )
            else BlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY - 0.6, mc.thePlayer!!.posZ).down())
            else
                (if (sameY() && launchY <= mc.thePlayer!!.posY) BlockPos(
                    mc.thePlayer!!.posX,
                    launchY - 1.0,
                    mc.thePlayer!!.posZ
                ) else (if (mc.thePlayer!!.posY == mc.thePlayer!!.posY.toInt() + 0.5) BlockPos(mc.thePlayer!!) else BlockPos(
                    mc.thePlayer!!.posX,
                    mc.thePlayer!!.posY,
                    mc.thePlayer!!.posZ
                ).down()))
        if (!expand && (!isReplaceable(blockPosition) || search(blockPosition, !shouldGoDown)))
            return

        if (expand) {
            when(expandMode.get()) {
                "LiquidBounce" -> {
                    for (i in 0 until expandLengthValue.get()) {
                        if (search(
                                blockPosition.add(
                                    when (mc.thePlayer!!.horizontalFacing) {
                                        EnumFacing.WEST -> -i
                                        EnumFacing.EAST -> i
                                        else -> 0
                                    }, 0,
                                    when (mc.thePlayer!!.horizontalFacing) {
                                        EnumFacing.NORTH -> -i
                                        EnumFacing.SOUTH -> i
                                        else -> 0
                                    }
                                ), false
                            )
                        )
                            return
                    }
                }
                "Sigma" -> {
                    /*if (isReplaceable(blockPosition)&&search(blockPosition, !shouldGoDown))
                        return*/
                    var x = mc.thePlayer.posX
                    var z = mc.thePlayer.posZ
                    //if (!mc.thePlayer.isCollidedHorizontally) {
                        val expandCords = getExpandCords(
                            x,
                            z,
                            mc.thePlayer.movementInput.moveForward.toDouble(),
                            mc.thePlayer.movementInput.moveStrafe.toDouble(),
                            mc.thePlayer.rotationYaw,
                            expandLengthValue.get().toDouble()
                        )
                        /*if (expandCords.first == -999.0 || expandCords.second == -999.0)
                            return*/
                        x = expandCords.first
                        z = expandCords.second
                    //}
                    if (search(BlockPos(x, blockPosition.y.toDouble(), z),false))
                        return
                }
            }
        } else if (searchValue.get()) {
            if (advancedSearchValue.get() && lastPlace != null) {
                if (search(lastPlace!!, !shouldGoDown)) {
                    return
                }
                for (x in -1..1) {
                    for (z in -1..1) {
                        if (search(blockPosition.add(x, 0, z), !shouldGoDown)) {
                            return
                        }
                    }
                }
            } else {
                for (x in -1..1) {
                    for (z in -1..1) {
                        if (search(blockPosition.add(x, 0, z), !shouldGoDown)) {
                            return
                        }
                    }
                }
            }
        }
    }

    fun place() {
        if (targetPlace == null) {
            if (placeDelay.get())
                delayTimer.reset()
            return
        }

        // HitableCheck
        if (grimACRotation.get()) {
            if (lockRotation != null && !grimRotationBoolean) return
        }
        if (hitableCheck.get() !in arrayOf("None", "Normal")) {
            val eyesVec: Vec3 = if (placeModeValue equal "Pre") { // we aren't tell server our position in pre MotionEvent, so...
                Vec3(mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosY, mc.thePlayer.lastReportedPosZ)
            } else {
                mc.thePlayer.getPositionEyes(1f)
            }
            val lookVec = RotationUtils.bestServerRotation().toDirection().multiply(5.0).add(eyesVec)
            val movingObjectPosition = mc.theWorld.rayTraceBlocks(eyesVec, lookVec, false, true, false)
            if (movingObjectPosition.blockPos != targetPlace!!.blockPos || (hitableCheck equal "Strict" && movingObjectPosition.sideHit != targetPlace!!.enumFacing)) {
                targetPlace!!.vec3 = movingObjectPosition.hitVec
                if (eagleValue equal "Smart") {
                    mc.gameSettings.keyBindSneak.pressed = true
                }
                return
            }
        }

        //2022-12-19 Hit facing check
        val f: Float = (targetPlace!!.vec3.xCoord - targetPlace!!.blockPos.x.toDouble()).toFloat()
        val f1: Float = (targetPlace!!.vec3.yCoord - targetPlace!!.blockPos.y.toDouble()).toFloat()
        val f2: Float = (targetPlace!!.vec3.zCoord - targetPlace!!.blockPos.z.toDouble()).toFloat()
        if (f > 1 || f1 > 1 || f2 > 1 || f < 0 || f1 < 0 || f2 < 0) {
            if (invalidPlaceFacingMode equal "CancelIt") {
                targetPlace = null
                facesBlock = false
                return
            } else if (invalidPlaceFacingMode equal "FixIt") {
                val vec = targetPlace!!.vec3
                val pos = targetPlace!!.blockPos
                targetPlace!!.vec3 = Vec3(
                    MathHelper.clamp_double(vec.xCoord, pos.x + 0.0, pos.x + 1.0),
                    MathHelper.clamp_double(vec.yCoord, pos.y + 0.0, pos.y + 1.0),
                    MathHelper.clamp_double(vec.zCoord, pos.z + 0.0, pos.z + 1.0)
                )
            }
        }

        if (!delayTimer.hasTimePassed(delay) || sameY() && launchY - 1 != targetPlace!!.vec3.yCoord.toInt())
            return

        val isDynamicSprint = sprintValue equal "dynamic" && (!towerNoSprintSwitch.get() || !towerState)

        var itemStack: ItemStack? = mc.thePlayer!!.heldItem
        if (itemStack == null || (itemStack.item) !is ItemBlock ||
            ((itemStack.item!! as ItemBlock).block) is BlockBush || mc.thePlayer!!.heldItem!!.stackSize <= 0
        ) {

            val blockSlot = InventoryUtils.findAutoBlockBlock()

            if (blockSlot == -1)
                return

            when (autoBlockValue.get().lowercase(Locale.getDefault())) {
                "off" -> return
                "pick" -> {
                    mc.thePlayer!!.inventory.currentItem = blockSlot - 36
                    mc.playerController.updateController()
                }
                "spoof" -> {
                    if (blockSlot - 36 != slot) {
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(blockSlot - 36))
                    }
                }
                "switch" -> {
                    if (blockSlot - 36 != slot) {
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(blockSlot - 36))
                    }
                }
            }
            itemStack = mc.thePlayer!!.inventoryContainer.getSlot(blockSlot).stack
        }

        if (isDynamicSprint) {
            mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
        }

        if (mc.playerController.onPlayerRightClick(
                mc.thePlayer!!,
                mc.theWorld!!,
                itemStack,
                targetPlace!!.blockPos,
                targetPlace!!.enumFacing,
                targetPlace!!.vec3
            )
        ) {
            delayTimer.reset()
            delay = if (!placeDelay.get()) 0 else TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

            if (mc.thePlayer!!.onGround) {
                val modifier: Float = speedModifierValue.get()
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionX * modifier
                mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ * modifier
            }

            if (swingValue.get()) {
                mc.thePlayer!!.swingItem()
            } else {
                mc.netHandler.addToSendQueue(C0APacketAnimation())
            }

            lastPlace = targetPlace!!.blockPos.add(targetPlace!!.enumFacing.frontOffsetX, targetPlace!!.enumFacing.frontOffsetY, targetPlace!!.enumFacing.frontOffsetZ)
        }

        if (isDynamicSprint) {
            mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
        }

        if (autoBlockValue.get().equals("Switch", true)) {
            if (slot != mc.thePlayer!!.inventory.currentItem) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
            }
        }
//        if (eagleValue equal "Smart") {
//            mc.gameSettings.keyBindSneak.pressed = false
//        }
        targetPlace = null
        grimRotationBoolean = false
    }

    // DISABLING MODULE
    override fun onDisable() {
        if (mc.thePlayer == null) return
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.pressed = false
            if (eagleSneaking)
                mc.netHandler.addToSendQueue(
                    C0BPacketEntityAction(
                        mc.thePlayer!!,
                        C0BPacketEntityAction.Action.STOP_SNEAKING
                    )
                )
        }
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight))
            mc.gameSettings.keyBindRight.pressed = false
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft))
            mc.gameSettings.keyBindLeft.pressed = false

        lockRotation = null
        facesBlock = false
        mc.timer.timerSpeed = 1f
        shouldGoDown = false

        if (slot != mc.thePlayer!!.inventory.currentItem) {
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
        }
    }

    // Entity movement event
    /** @param event */
    @EventTarget
    fun onMove(event: MoveEvent) {
        if (!safeWalkValue.get() || shouldGoDown)
            return
        if (airSafeValue.get() || mc.thePlayer!!.onGround)
            event.isSafeWalk = true
    }

    // Scaffold visuals
    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (counterDisplayValue.get()) {
            GL11.glPushMatrix()
            val info = "Blocks: §7$blocksAmount"
            val scaledResolution = ScaledResolution(mc)

            RenderUtils.drawBorderedRect(
                scaledResolution.scaledWidth / 2 - 2.toFloat(),
                scaledResolution.scaledHeight / 2 + 5.toFloat(),
                scaledResolution.scaledWidth / 2 + KevinClient.fontManager.font40.getStringWidth(info) + 2.toFloat(),
                scaledResolution.scaledHeight / 2 + 16.toFloat(), 3f, Color.BLACK.rgb, Color.BLACK.rgb
            )

            GlStateManager.resetColor()

            KevinClient.fontManager.font40.drawString(
                info, scaledResolution.scaledWidth / 2.toFloat(),
                scaledResolution.scaledHeight / 2 + 7.toFloat(), Color.WHITE.rgb
            )

            GlStateManager.enableBlend()

            GL11.glPopMatrix()
        }
    }
// SCAFFOLD VISUALS
    /** @param  */
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (!markValue.get()) return
        if (!shouldExpand || expandMode equal "LiquidBounce") {
            for (i in 0 until if (shouldExpand) expandLengthValue.get() + 1 else 2) {
                val blockPos = BlockPos(
                    mc.thePlayer!!.posX + when (mc.thePlayer!!.horizontalFacing) {
                        EnumFacing.WEST -> -i.toDouble()
                        EnumFacing.EAST -> i.toDouble()
                        else -> 0.0
                    },
                    if (sameY() && launchY <= mc.thePlayer!!.posY) launchY - 1.0 else mc.thePlayer!!.posY - (if (mc.thePlayer!!.posY == mc.thePlayer!!.posY + 0.5) 0.0 else 1.0) - if (shouldGoDown) 1.0 else 0.0,
                    mc.thePlayer!!.posZ + when (mc.thePlayer!!.horizontalFacing) {
                        EnumFacing.NORTH -> -i.toDouble()
                        EnumFacing.SOUTH -> i.toDouble()
                        else -> 0.0
                    }
                )
                val placeInfo: PlaceInfo? = PlaceInfo.get(blockPos)
                if (isReplaceable(blockPos) && placeInfo != null) {
                    RenderUtils.drawBlockBox(blockPos, Color(68, 117, 255, 100), false)
                    break
                }
            }
        } else {
            //if (!mc.thePlayer.isCollidedHorizontally) {
                val expandCords = getExpandCords(
                    mc.thePlayer.posX,
                    mc.thePlayer.posZ,
                    mc.thePlayer.movementInput.moveForward.toDouble(),
                    mc.thePlayer.movementInput.moveStrafe.toDouble(),
                    mc.thePlayer.rotationYaw,
                    expandLengthValue.get()+1.0
                )
                val blockPos = BlockPos(expandCords.first, mc.thePlayer!!.posY - (if (mc.thePlayer!!.posY == mc.thePlayer!!.posY + 0.5) 0.0 else 1.0), expandCords.second)
                val placeInfo: PlaceInfo? = PlaceInfo.get(blockPos)
                if (isReplaceable(blockPos) && placeInfo != null) {
                    RenderUtils.drawBlockBox(blockPos, Color(68, 117, 255, 100), false)
                }
            //}
        }
    }

    private fun calculateRotation(rotation: Rotation): Rotation {
        return if (towerState) when(towerRotationsValue.get()) {
            "Custom" -> Rotation(mc.thePlayer.rotationYaw + customTowerYawValue.get(), customTowerPitchValue.get())
            "MoveDirection" -> Rotation(MovementUtils.movingYaw - 180, rotation.pitch)
            else -> rotation
        } else when(rotationsValue.get()) {
            "AAC" -> Rotation(mc.thePlayer.rotationYaw + (((if (mc.thePlayer.movementInput.moveForward < 0) 0 else 180) - aacYawOffsetValue.get()) * if (aacRotationPositive) 1 else -1), rotation.pitch)
            "LimitedAAC" -> Rotation(mc.thePlayer.rotationYaw + (((if (mc.thePlayer.movementInput.moveForward < 0) 0 else 180) - aacYawOffsetValue.get()) * if (aacRotationPositive) 1 else -1), customPitchValue.get())
            "MoveDirection" -> Rotation(MovementUtils.movingYaw - 180, customPitchValue.get())
            "Custom" -> Rotation(mc.thePlayer.rotationYaw + customYawValue.get(), customPitchValue.get())
            else -> rotation
        }
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */

    private fun search(blockPosition: BlockPos, checks: Boolean): Boolean {
        facesBlock = false
        if (!isReplaceable(blockPosition)) return false

        // Search Ranges
        val xzRV = if (towerState) towerXZRangeValue.get().toDouble() else xzRangeValue.get().toDouble()
        val xzSSV = if (towerState) calcTowerStepSize(xzRV.toFloat()) else calcStepSize(xzRV.toFloat())
        val yRV = if (towerState) towerYRangeValue.get().toDouble() else yRangeValue.get().toDouble()
        val ySSV = if (towerState) calcTowerStepSize(yRV.toFloat()) else calcStepSize(yRV.toFloat())
        val eyesPos = Vec3(
            mc.thePlayer!!.posX,
            mc.thePlayer!!.entityBoundingBox.minY + mc.thePlayer!!.eyeHeight,
            mc.thePlayer!!.posZ
        )
        var placeRotation: PlaceRotation? = null
        if ((if (towerState) towerSearchMode else searchMode) equal "Sigma" && !shouldGoDown) { // Sigma的搜索无法处理下降
            val data = getBlockData(blockPosition)
            if (data != null) {
                placeRotation = PlaceRotation(PlaceInfo(data.first, data.second, getVec3(data.first, data.second)), getRotations(data.first, data.second))
            } else return false
        } else {
            for (facingType in EnumFacing.values()) {
                val neighbor = blockPosition.offset(facingType)
                if (!canBeClicked(neighbor)) continue
                val dirVec = Vec3(facingType.directionVec)
                val auto = (if (towerState) towerSearchMode else searchMode).get().equals("Auto", true)
                val center = (if (towerState) towerSearchMode else searchMode).get().equals("AutoCenter", true)
                var xSearch = if (auto) 0.1 else 0.5 - xzRV / 2
                while (xSearch <= if (auto) 0.9 else 0.5 + xzRV / 2) {
                    var ySearch = if (auto) 0.1 else 0.5 - yRV / 2
                    while (ySearch <= if (auto) 0.9 else 0.5 + yRV / 2) {
                        var zSearch = if (auto) 0.1 else 0.5 - xzRV / 2
                        while (zSearch <= if (auto) 0.9 else 0.5 + xzRV / 2) {
                            val posVec = Vec3(blockPosition).addVector(
                                if (center) 0.5 else xSearch,
                                if (center) 0.5 else ySearch,
                                if (center) 0.5 else zSearch
                            )
                            val distanceSqPosVec = eyesPos.squareDistanceTo(posVec)
                            val hitVec = posVec.add(Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5))
                            if (checks && (eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(
                                    posVec.add(dirVec)
                                ) || mc.theWorld!!.rayTraceBlocks(
                                    eyesPos, hitVec,
                                    false,
                                    true,
                                    false
                                ) != null)
                            ) {
                                zSearch += if (auto) 0.1 else xzSSV
                                continue
                            }

                            // Face block
                            val diffX = hitVec.xCoord - eyesPos.xCoord
                            val diffY = hitVec.yCoord - eyesPos.yCoord
                            val diffZ = hitVec.zCoord - eyesPos.zCoord
                            val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)
                            if ((facingType == EnumFacing.NORTH || facingType == EnumFacing.EAST || facingType == EnumFacing.SOUTH || facingType == EnumFacing.WEST) && (if (towerState) towerMinDistValue else minDistValue).get() > 0) {
                                val diff: Double =
                                    abs(if (facingType == EnumFacing.NORTH || facingType == EnumFacing.SOUTH) diffZ else diffX)
                                if (diff < (if (towerState) towerMinDistValue else minDistValue).get() || diff > 0.3f) {
                                    zSearch += if (auto) 0.1 else xzSSV
                                    continue
                                }
                            }
                            val rotation = Rotation(
                                wrapAngleTo180_float(Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90f),
                                wrapAngleTo180_float(-Math.toDegrees(atan2(diffY, diffXZ)).toFloat())
                            )
                            val rotationVector = RotationUtils.getVectorForRotation(rotation)
                            val vector = eyesPos.addVector(
                                rotationVector.xCoord * distanceSqPosVec,
                                rotationVector.yCoord * distanceSqPosVec,
                                rotationVector.zCoord * distanceSqPosVec
                            )
                            val obj = mc.theWorld!!.rayTraceBlocks(
                                eyesPos, vector,
                                false,
                                false,
                                true
                            )
                            if (obj!!.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || obj.blockPos!! != neighbor) {
                                zSearch += if (auto) 0.1 else xzSSV
                                continue
                            }
                            if (advancedSearchValue.get()) {
                                val rot = Rotation(wrapAngleTo180_float(MovementUtils.direction.toFloat() - 180F), RotationUtils.bestServerRotation().pitch)
                                if (placeRotation == null || RotationUtils.getRotationDifference(rotation, rot) < RotationUtils.getRotationDifference(placeRotation.rotation, rot)) {
                                    placeRotation = PlaceRotation(PlaceInfo(neighbor, facingType.opposite, hitVec), rotation)
                                }
                            } else {
                                if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(placeRotation.rotation)) {
                                    placeRotation = PlaceRotation(PlaceInfo(neighbor, facingType.opposite, hitVec), rotation)
                                }
                            }

                            zSearch += if (auto) 0.1 else xzSSV
                        }
                        ySearch += if (auto) 0.1 else ySSV
                    }
                    xSearch += if (auto) 0.1 else xzSSV
                }
            }
        }
        if (placeRotation == null) return false
        if (rotationsOn) {
            val calculatedRotation = calculateRotation(placeRotation.rotation)
            if (minTurnSpeedValue.get() < 180) {
                val limitedRotation = RotationUtils.limitAngleChange(
                    RotationUtils.serverRotation,
                    calculatedRotation,
                    (Math.random() * (maxTurnSpeedValue.get() - minTurnSpeedValue.get()) + minTurnSpeedValue.get()).toFloat()
                )

                if ((10 * wrapAngleTo180_float(limitedRotation.yaw)).roundToInt() == (10 * wrapAngleTo180_float(calculatedRotation.yaw)).roundToInt() &&
                    (10 * wrapAngleTo180_float(limitedRotation.pitch)).roundToInt() == (10 * wrapAngleTo180_float(calculatedRotation.pitch)).roundToInt()) {
                    setRotation(calculatedRotation)
                    lockRotation = calculatedRotation
                    facesBlock = true
                } else {
                    setRotation(limitedRotation)
                    lockRotation = limitedRotation
                    facesBlock = false
                }
            } else {
                setRotation(calculatedRotation)
                lockRotation = calculatedRotation
                facesBlock = true
            }
            lockRotationTimer.reset()
        }
        targetPlace = placeRotation.placeInfo
        return true
    }

    private fun calcStepSize(range: Float): Double {
        var accuracy: Double = searchAccuracyValue.get().toDouble()
        accuracy += accuracy % 2 // If it is set to uneven it changes it to even. Fixes a bug
        return if (range / accuracy < 0.01) 0.01 else (range / accuracy)
    }

    private fun calcTowerStepSize(range: Float): Double {
        var accuracy: Double = towerSearchAccuracyValue.get().toDouble()
        accuracy += accuracy % 2
        return if (range / accuracy < 0.01) 0.01 else (range / accuracy)
    }

    // RETURN HOTBAR AMOUNT
    private val blocksAmount: Int
        get() {
            var amount = 0
            for (i in 36..44) {
                val itemStack: ItemStack? = mc.thePlayer!!.inventoryContainer.getSlot(i).stack
                if (itemStack != null && (itemStack.item) is ItemBlock) {
                    val block: Block = (itemStack.item!! as ItemBlock).block
                    val heldItem: ItemStack? = mc.thePlayer!!.heldItem
                    if (heldItem != null && heldItem == itemStack || !InventoryUtils.BLOCK_BLACKLIST.contains(block) && (block) !is BlockBush) {
                        amount += itemStack.stackSize
                    }
                }
            }
            return amount
        }
    override val tag: String
        get() = if (!(towerModeValue equal "Jump")&&mc.gameSettings.keyBindJump.isKeyDown) "Tower" else if (mc.gameSettings.keyBindJump.isKeyDown) "JumpUp" else if (shouldGoDown) "Down" else if (shouldExpand) "Expand" else "Normal"
}