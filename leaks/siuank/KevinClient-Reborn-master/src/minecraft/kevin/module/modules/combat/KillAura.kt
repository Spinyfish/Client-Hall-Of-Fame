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
import kevin.module.modules.exploit.TP
import kevin.module.modules.misc.AntiBot
import kevin.module.modules.misc.AntiShop
import kevin.module.modules.misc.HideAndSeekHack
import kevin.module.modules.misc.Teams
import kevin.module.modules.movement.Fly
import kevin.module.modules.player.Blink
import kevin.module.modules.render.FreeCam
import kevin.module.modules.world.Scaffold
import kevin.utils.*
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemSpade
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.util.*
import net.minecraft.world.WorldSettings
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import kotlin.math.*

@Suppress("unused_parameter")
class KillAura : Module("KillAura","Automatically attacks targets around you.", Keyboard.KEY_R, ModuleCategory.COMBAT) {
    /**
     * OPTIONS
     */

    // CPS - Attack speed
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }

    private val noSpamClick = BooleanValue("NoSpamClick", true)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val smartAttackValue = BooleanValue("SmartAttack", false)

    // Range (public because velocity...)
    val rangeValue: FloatValue = object : FloatValue("Range", 3.7f, 1f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)
        }
    }
    private val throughWallsRangeValue: FloatValue = object : FloatValue("ThroughWallsRange", 3f, 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = rangeValue.get()
            if (i < newValue) set(i)
        }
    }
    private val rangeSprintReducementValue = FloatValue("RangeSprintReducement", 0f, 0f, 0.4f)
    private val discoverRangeValue: FloatValue = object : FloatValue("DiscoverRange", 4.0f, 2.0f, 10f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = rangeValue.get()
            if (i > newValue) set(i)
        }
    }

    // Modes
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Direction", "LivingTime"), "Distance")
    private val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Switch")

    // Bypass
    private val swingValue = ListValue("SwingMode", arrayOf("Normal", "OnlyWhenNoHurt", "Packet", "OFF"), "Normal")
    private val keepSprintValue = ListValue("KeepSprint", arrayOf("Off", "On", "OnlyNoHurt"), "Off")
    private val scaffoldCheck = BooleanValue("ScaffoldCheck", true)

    //Timing
    private val hightVersionAttackDelay = BooleanValue("HighVersionAttackDelay", false)
    private val hightVersionAttackSwing = BooleanValue("HighVersionAttackSwing", false)
    private val attackTimingValue = ListValue("AttackTiming", arrayOf("Legit", "Pre", "Post"), "Legit")
    private val extraBlockTimingValue // vanilla will send block packet at pre
    = ListValue("ExtraBlockTiming", arrayOf("NoExtra", "Pre", "Post", "Update", "Pre&Post", "Update&Pre", "Update&Post", "Update&Pre&Post"), "NoExtra")
    private val afterTickTimingValue = ListValue("AfterTickBlockTiming", arrayOf("Pre", "Post", "Both"), "Post")

    // AutoBlock
    private val autoBlockValue = ListValue("AutoBlock", arrayOf("Off", "Packet", "AfterTick", "Keep", "Vulcan"), "Off")
    private val interactAutoBlockValue = BooleanValue("InteractAutoBlock", true)
    private val blockStatusCheck = BooleanValue("BlockStatusCheck", true)
    private val blockRate = IntegerValue("BlockRate", 100, 1, 100)
    private val blockRange: FloatValue = object : FloatValue("BlockRange", rangeValue.get(), 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)
        }
    }

    // Raycast
    private val raycastValue = BooleanValue("RayCast", true)
    private val raycastIgnoredValue = BooleanValue("RayCastIgnored", false)
    private val livingRaycastValue = BooleanValue("LivingRayCast", true)
    private val anythingsRayCast = BooleanValue("AnythingsRayCast", false)

    // Bypass
    private val aacValue = BooleanValue("AAC", false)
    private val keepRotationTickValue = IntegerValue("KeepRotationTick", 0, 0, 30)

    // Turn Speed
    private val yawMaxTurnSpeed: FloatValue = object : FloatValue("YawMaxTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = yawMinTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }

    private val yawMinTurnSpeed: FloatValue = object : FloatValue("YawMinTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = yawMaxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }

    private val pitchMaxTurnSpeed: FloatValue = object : FloatValue("PitchMaxTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = pitchMinTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }

    private val pitchMinTurnSpeed: FloatValue = object : FloatValue("PitchMinTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = pitchMaxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }

    private val rotationModeValue = ListValue("RotationMode", arrayOf("LiquidBounce", "Advanced", "LiquidSense", "NearestPoint"), "LiquidBounce")
    private val silentRotationValue = ListValue("SilentRotation", arrayOf("Always", "OnlyNoMove", "Off"), "Always")
    private val rotationStrafeValue = ListValue("Strafe", arrayOf("Off", "Vanilla", "Strict", "Silent"), "Off")
    private val randomCenterValue = BooleanValue("RandomCenter", true)
    private val outborderValue = BooleanValue("Outborder", false)
    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)
    private val onlyRotationInAttack = BooleanValue("OnlyRotationInAttack", false)

    // Predict
    private val predictValue = BooleanValue("Predict", true)

    private val maxPredictSize: FloatValue = object : FloatValue("MaxPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSize.get()
            if (v > newValue) set(v)
        }
    }

    private val minPredictSize: FloatValue = object : FloatValue("MinPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSize.get()
            if (v < newValue) set(v)
        }
    }

    // Bypass
    private val alwaysHitable = BooleanValue("AlwaysHitable", false)
    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val fakeSwingValue = BooleanValue("FakeSwing", true)
    private val noInventoryAttackValue = BooleanValue("NoInvAttack", false)
    private val noInventoryDelayValue = IntegerValue("NoInvDelay", 200, 0, 500)
    private val limitedMultiTargetsValue = IntegerValue("LimitedMultiTargets", 0, 0, 50)

    // Advanced
    private val delayRotationTick = IntegerValue("DelayRotationTick", 0, 0, 20)
    private val hitBoxMode = ListValue("HitBoxMode", arrayOf("1.8", "HigherVersion"), "1.8")
    private val reachCalculateMode = ListValue("ReachCalculateMode", arrayOf("Look", "DirectionDistance"), "Look")

    // Visuals
    private val markValue = ListValue("Mark", arrayOf("Off", "LiquidBounce", "Box", "Jello"), "LiquidBounce")
    private val renderViewLine = BooleanValue("RenderViewLine", false)
    private val fakeSharpValue = BooleanValue("FakeSharp", true)

    /**
     * MODULE
     */

    // Target
    var sTarget: Entity? = null
    var target: Entity? = null
    private var currentTarget: Entity? = null
    private var lastHitVec = Vec3(0.0, 0.0, 0.0)
    private var hitable = false
    private val prevTargetEntities = mutableListOf<Int>()
    private val discoveredTargets = mutableListOf<Entity>()

    // Attack delay
    private val attackTimer = MSTimer()
    private val attackTickTimer = TickTimer()
    private var attackDelay = 0L
    var clicks = 0

    // Container Delay
    private var containerOpen = -1L

    // Fake block status
    var blockingStatus = false
    var lastBlocking = false

    // Rotation delay
    private val delayedRotations = TimeList<Rotation>(1200)
    private var rotationTime = 3

    private val expandHitBox: Boolean
    get() = hitBoxMode equal "1.8"

    private val vectorReach: Boolean
    get() = reachCalculateMode equal "Look"

    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
    }

    //Keep AutoBlock
    private val keepAutoBlock: Boolean
    get() = autoBlockValue equal "Keep" || autoBlockValue equal "Vulcan"

    /**
     * Disable kill aura module
     */
    override fun onDisable() {
        target = null
        sTarget = null
        currentTarget = null
        hitable = false
        prevTargetEntities.clear()
        discoveredTargets.clear()
        attackTimer.reset()
        clicks = 0

        stopBlocking()
        // need check scaffold?
        RotationUtils.reset()
    }

    private val afterTick
    get() = autoBlockValue equal "AfterTick"

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (afterTick&&(
            afterTickTimingValue equal "Both" ||
            (afterTickTimingValue equal "Pre" && event.eventState == EventState.PRE) ||
            (afterTickTimingValue equal "Post" && event.eventState == EventState.POST)
        )) {
            if (target != null && currentTarget != null) {
                updateHitable()
                if(canBlock)
                    startBlocking(currentTarget!!, hitable)
            }
        }

        if ((attackTimingValue equal "Pre" && event.eventState == EventState.PRE) ||
            (attackTimingValue equal "Post" && event.eventState == EventState.POST)
        ) {
            runAttackLoop()
        }

        if (!afterTick&&(extraBlockTimingValue equal "Update&Pre&Post" || extraBlockTimingValue equal "Pre&Post" ||
            ((extraBlockTimingValue equal "Pre" || extraBlockTimingValue equal "Update&Pre") && event.eventState == EventState.PRE) ||
            ((extraBlockTimingValue equal "Post" || extraBlockTimingValue equal "Update&Post") && event.eventState == EventState.POST)
        )) {
            runBlock()
        }

        if (event.eventState == EventState.POST) {
            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()
/*
            // AutoBlock
            if (autoBlockValue.get().equals("AfterTick", true) && canBlock)
                startBlocking(currentTarget!!, hitable)
*/
            return
        }

        if (rotationStrafeValue.get().equals("Off", true))
            update()
    }

    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (rotationStrafeValue.get().equals("Off", true))
            return

        update()

        if (currentTarget != null && RotationUtils.targetRotation != null && !(scaffoldCheck.get() && KevinClient.moduleManager.getModule(Scaffold::class.java).state)) {
            when (rotationStrafeValue.get().lowercase()) {
                "vanilla", "strict" -> {
                    val (yaw) = RotationUtils.targetRotation ?: return
                    event.yaw = yaw;
                }
                "silent" -> {
                    update()

                    RotationUtils.targetRotation.applyStrafeToPlayer(event)
                    event.cancelEvent()
                }
            }
        }
    }
    @EventTarget fun onJump(event: JumpEvent) {
        if (rotationStrafeValue equal "Vanilla") {
            if (event.isCancelled) return
            val thePlayer = mc.thePlayer ?: return
            val (yaw) = RotationUtils.targetRotation ?: return
            thePlayer.motionY = event.motion.toDouble()

            if (thePlayer.isPotionActive(Potion.jump)) thePlayer.motionY += ((thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1).toFloat() * 0.1f).toDouble()

            if (thePlayer.isSprinting) {
                val f: Float = yaw * (Math.PI.toFloat() / 180f)
                thePlayer.motionX -= (MathHelper.sin(f) * 0.2f).toDouble()
                thePlayer.motionZ += (MathHelper.cos(f) * 0.2f).toDouble()
            }

            thePlayer.isAirBorne = true
            event.cancelEvent()
        }
    }

    fun update() {
        if (cancelRun || (noInventoryAttackValue.get() && ((mc.currentScreen)is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())))
            return

        // Update target
//        updateTarget()

        if (target == null) {
            stopBlocking()
            return
        }

        // Target
        currentTarget = target

        if (!targetModeValue.get().equals("Switch", ignoreCase = true) && isEnemy(currentTarget)) {
            target = currentTarget
            sTarget = currentTarget
        }
    }

    // MOVED: PacketEvent & TickEvent -> utils.RotationUtils

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        // Debug ChatUtils().message("Yaw:${RotationUtils.serverRotation.yaw} Pitch:${RotationUtils.serverRotation.pitch}")

        if (cancelRun) {
            target = null
            sTarget = null
            currentTarget = null
            hitable = false
            stopBlocking()
            discoveredTargets.clear()
            return
        }
        updateTarget()

        if (noInventoryAttackValue.get() && ((mc.currentScreen)is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())) {
            target = null
            sTarget = null
            currentTarget = null
            hitable = false
            if ((mc.currentScreen)is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        if (attackTimingValue equal "Update&Pre&Post" || attackTimingValue equal "Update&Pre" || attackTimingValue equal "Update&Post" || attackTimingValue equal "Update")
            runAttackLoop()
        if (!afterTick&&(extraBlockTimingValue equal "Update&Pre&Post" || extraBlockTimingValue equal "Update&Pre" || extraBlockTimingValue equal "Update&Post" || extraBlockTimingValue equal "Update"))
            runBlock()

        lastBlocking = if (mc.thePlayer.isBlocking || blockingStatus) {
            true
        } else {
            if (lastBlocking && autoBlockValue equal "Vulcan") {
                PacketUtils.sendPacketNoEvent(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            }
            false
        }
        attackTickTimer.update()
        if (hightVersionAttackDelay.get()) {
            var delayValue = 4

            if (mc.thePlayer.heldItem != null) {
                val item = mc.thePlayer.heldItem.item

                if (item is ItemSpade || item == Items.golden_axe || item == Items.diamond_axe || item == Items.wooden_hoe || item == Items.golden_hoe)
                    delayValue = 20

                if (item == Items.wooden_axe || item == Items.stone_axe)
                    delayValue = 25

                if (item is ItemSword)
                    delayValue = 12

                if (item is ItemPickaxe)
                    delayValue = 17

                if (item == Items.iron_axe)
                    delayValue = 22

                if (item == Items.stone_hoe)
                    delayValue = 10

                if (item == Items.iron_hoe)
                    delayValue = 7
            }

            if (attackTickTimer.hasTimePassed(ceil(delayValue * max(1f, mc.timer.timerSpeed)).toInt())) {
                ++clicks
                rotationTime = 3
            }
        }
    }

    @EventTarget
    fun onClick(event: ClickUpdateEvent) {
        if (attackTimingValue equal "Legit") {
            if (mc.thePlayer == null || mc.theWorld == null) return
            updateTarget()
            updateHitable()
            runAttackLoop()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (autoBlockValue equal "Vulcan" && ((packet is C07PacketPlayerDigging && packet.status == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) || packet is C08PacketPlayerBlockPlacement) && lastBlocking)
            event.cancelEvent()
    }

    private fun runAttackLoop() {
        if (target != null && currentTarget != null) {
            if (noSpamClick.get()) {
                if (clicks > 0) {
                    runAttack()
                    clicks = 0
                }
            } else {
                while (clicks > 0) {
                    runAttack()
                    clicks--
                }
            }
        }
    }

    private fun runBlock() {
        if (discoveredTargets.isNotEmpty() && canBlock) {
            val target = this.sTarget ?: discoveredTargets.first()
            if (mc.thePlayer.getDistanceToEntityBox(target) <= blockRange.get()) {
                startBlocking(
                    target,
                    interactAutoBlockValue.get() && (mc.thePlayer.getDistanceToEntityBox(target) < maxRange)
                )
            } else {
                if (!mc.thePlayer.isBlocking) {
                    stopBlocking()
                }
            }
        }
    }

    /**
     * Render event
     */
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (cancelRun) {
            target = null
            sTarget = null
            currentTarget = null
            hitable = false
            stopBlocking()
            discoveredTargets.clear()
            return
        }

        if (noInventoryAttackValue.get() && ((mc.currentScreen) is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())) {
            target = null
            sTarget = null
            currentTarget = null
            hitable = false
            if ((mc.currentScreen) is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        if (sTarget != null) {
            when (markValue.get()) {
                "LiquidBounce" -> {
                    if (targetModeValue.get().equals("Multi", ignoreCase = true))
                        discoveredTargets.forEach { RenderUtils.drawPlatform(it, Color(37, 126, 255, 70)) }
                    else
                        RenderUtils.drawPlatform(sTarget, if (hitable) Color(37, 126, 255, 70) else Color(255, 0, 0, 70))
                }
                "Box" -> {
                    if (targetModeValue.get().equals("Multi", ignoreCase = true))
                        discoveredTargets.forEach { RenderUtils.drawEntityBox(it, Color(37, 126, 255, 120), false) }
                    else
                        RenderUtils.drawEntityBox(
                            sTarget, if (hitable) {
                                if (sTarget !is EntityLivingBase || (sTarget as EntityLivingBase).hurtTime > 0) Color(255, 0, 0, 120) else Color(37, 126, 255, 120)
                            } else Color(37, 255, 126, 120), false
                        )
                }
                "Jello" -> {
                    if (targetModeValue.get().equals("Multi", ignoreCase = true))
                        discoveredTargets.forEach { RenderUtils.drawCircle(it, -1.0, Color(255, 255, 255), true) }
                    else
                        RenderUtils.drawCircle(sTarget, -1.0, Color(255, 255, 255), true)
                }
            }
            if (renderViewLine.get() && sTarget != null) {
                val targetEntity = sTarget!!
                val partialTicks = mc.timer.renderPartialTicks
                val box = targetEntity.entityBoundingBox.expands(targetEntity.collisionBorderSize.toDouble())
                val rot = RotationUtils.targetRotation ?: RotationUtils.serverRotation
//                val dYaw = rot.yaw - lastRot.yaw
//                val dPitch = rot.pitch - lastRot.pitch
                val vecEyes = mc.thePlayer.getPositionEyes(partialTicks)
//                var vecRot = Rotation(lastRot.yaw + dYaw * partialTicks, lastRot.pitch * dPitch * partialTicks).toDirection().multiply(discoverRangeValue.get().toDouble()).add(vecEyes)
                var vecRot = rot.toDirection().multiply(discoverRangeValue.get().toDouble()).add(vecEyes)
                val obj = box.calculateIntercept(vecEyes, vecRot)
                vecRot = obj.hitVec ?: vecRot
                val renderPosX = mc.renderManager.renderPosX
                val renderPosY = mc.renderManager.renderPosY
                val renderPosZ = mc.renderManager.renderPosZ
                GL11.glPushMatrix()
//                GL11.glEnable(GL11.GL_BLEND)
//                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GL11.glShadeModel(GL11.GL_SMOOTH)
                GL11.glDisable(GL11.GL_TEXTURE_2D)
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
//                GL11.glDisable(GL11.GL_DEPTH_TEST)
                GL11.glDisable(GL11.GL_LIGHTING)
//                GL11.glDepthMask(false)
                GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
                GL11.glLoadIdentity()
                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2)
                RenderUtils.glColor(255, 255, 255, 255)
                GL11.glLineWidth(1.2f)

                GL11.glBegin(GL11.GL_LINE)
                GL11.glVertex3d(vecEyes.xCoord - renderPosX, vecEyes.yCoord - renderPosY, vecEyes.zCoord - renderPosZ)
                GL11.glVertex3d(vecRot.xCoord - renderPosX, vecRot.yCoord - renderPosY, vecRot.zCoord - renderPosZ)
                GL11.glEnd()
//                GL11.glColor4f(1F, 1F, 1F, 1F)
//                GL11.glDepthMask(true)
//                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glDisable(GL11.GL_LINE_SMOOTH)
                GL11.glEnable(GL11.GL_TEXTURE_2D)
//                GL11.glDisable(GL11.GL_BLEND)
                GL11.glEnable(GL11.GL_LIGHTING)
                GL11.glPopMatrix()
            }
        }

        target ?: return

        // prevent duplicated swing / attack
        if (hightVersionAttackDelay.get()) return

        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay + if (!smartAttackValue.get() || mc.thePlayer.hurtTime != 0 || (target !is EntityLivingBase || (target as EntityLivingBase).hurtTime <= 3)) 0 else 500) &&
            (currentTarget !is EntityLivingBase || (currentTarget as EntityLivingBase).hurtTime <= hurtTimeValue.get())) {
            ++clicks
            rotationTime = 3
            attackTimer.reset()
            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
    }

    /**
     * Handle entity move
     */
    @EventTarget
    fun onEntityMove(event: EntityMovementEvent) {
        val movedEntity = event.movedEntity

        if (target == null || movedEntity != currentTarget)
            return

        updateHitable()
    }

    private fun doSwing() {
        when(swingValue.get()) {
            "Normal" -> mc.thePlayer.swingItem()
            "OnlyWhenNoHurt" -> {
                if (currentTarget == null || currentTarget !is EntityLivingBase || (currentTarget as EntityLivingBase).hurtTime < 1)
                    mc.thePlayer.swingItem()
                else
                    mc.netHandler.addToSendQueue(C0APacketAnimation())
            }
            "Packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
        }
        attackTickTimer.reset()
    }

    /**
     * Attack enemy
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return
        val thePlayer = mc.thePlayer ?: return
        val theWorld = mc.theWorld ?: return

        // Settings
        val failRate = failRateValue.get()
        val swing = !(swingValue equal "OFF")
        val multi = targetModeValue.get().equals("Multi", ignoreCase = true)
        val openInventory = aacValue.get() && (mc.currentScreen) is GuiContainer
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate
        val range = currentTarget!!.entityBoundingBox.expands(if (expandHitBox) currentTarget!!.collisionBorderSize.toDouble() else 0.0).getLookingTargetRange(mc.thePlayer)

        // Close inventory when open
        if (openInventory)
            mc.netHandler.addToSendQueue(C0DPacketCloseWindow())

        // Check is not hitable or check failrate

        if (!hitable || failHit || range > maxRange) {
            if (swing && (fakeSwingValue.get() || failHit))
                doSwing()
        } else {
            // Attack
            if (!multi) {
                attackEntity(currentTarget!!)
            } else {
                var targets = 0

                for (entity in theWorld.loadedEntityList) {
                    val distance = thePlayer.getDistanceToEntityBox(entity)

                    if ((entity is EntityLivingBase || HideAndSeekHack.isHider(entity)) && isEnemy(entity) && distance <= getRange(entity)) {
                        attackEntity(entity)

                        targets += 1

                        if (limitedMultiTargetsValue.get() != 0 && limitedMultiTargetsValue.get() <= targets)
                            break
                    }
                }
            }

            prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)

            if (target == currentTarget)
                target = null
        }

        // Open inventory
        if (openInventory)
            mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Reset fixed target to null
        target = null
        sTarget = null

        // Settings
        val hurtTime = hurtTimeValue.get()
        val fov = fovValue.get()
        val switchMode = targetModeValue.get().equals("Switch", ignoreCase = true)

        // Find possible targets
        discoveredTargets.clear()

        val theWorld = mc.theWorld!!
        val thePlayer = mc.thePlayer!!

        for (entity in theWorld.loadedEntityList) {
            if ((entity !is EntityLivingBase && !HideAndSeekHack.isHider(entity)) || !isEnemy(entity) || (switchMode && prevTargetEntities.contains(entity.entityId)))
                continue

            val distance = thePlayer.getDistanceToEntityBox(entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= discoverRangeValue.get() && (fov == 180F || entityFov <= fov) && (entity !is EntityLivingBase || entity.hurtTime <= hurtTime))
                discoveredTargets.add(entity)
        }

        // Sort targets by priority
        when (priorityValue.get().lowercase()) {
            "distance" -> discoveredTargets.sortBy { thePlayer.getDistanceToEntityBox(it) } // Sort by distance
            "health" -> discoveredTargets.sortBy { if (it is EntityLivingBase) it.health else thePlayer.getDistanceToEntityBox(it).toFloat() } // Sort by health
            "direction" -> discoveredTargets.sortBy { RotationUtils.getRotationDifference(it) } // Sort by FOV
            "livingtime" -> discoveredTargets.sortBy { -it.ticksExisted } // Sort by existence
        }

        // Find best target
        for (entity in discoveredTargets) {
            // Update rotations to current target
            if (!updateRotations(entity)) // when failed then try another target
                continue

            // Set target to current entity
            target = entity
            sTarget = entity
            return
        }

        // Cleanup last targets when no target found and try again
        if (prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
        }
    }

    /**
     * Check if [entity] is selected as enemy with current target options and other modules
     */
    private fun isEnemy(entity: Entity?): Boolean {
        if (HideAndSeekHack.isHider(entity)){
            return true
        } else if (HideAndSeekHack.isSeeker()) return false
        if ((entity) is EntityLivingBase && (EntityUtils.targetDeath || isAlive(entity)) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.isInvisible)
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {

                if (entity.isSpectator || AntiBot.isBot(entity)) return false
/**
                if (player.isClientFriend() && !LiquidBounce.moduleManager[NoFriends::class.java].state)
                    return false
 **/
                val antiShop = KevinClient.moduleManager.getModule(AntiShop::class.java)
                if (antiShop.isShop(entity))
                    return false

                val teams = KevinClient.moduleManager.getModule(Teams::class.java)
                return !teams.state || !teams.isInYourTeam(entity)
            }

            return EntityUtils.targetMobs && entity.isMob() || EntityUtils.targetAnimals && entity.isAnimal()
        }

        return false
    }

    /**
     * Attack [entity]
     */
    private fun attackEntity(entity: Entity) {
        // Stop blocking
        val thePlayer = mc.thePlayer!!

        if ((thePlayer.isBlocking || blockingStatus)&&!keepAutoBlock)
            stopBlocking()

        // Call attack event
        KevinClient.eventManager.callEvent(AttackEvent(entity))

        // Attack target
        val highVersionSwing = hightVersionAttackSwing.get()
        if (!highVersionSwing) doSwing()

        mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        if (keepSprintValue equal "On" || (keepSprintValue equal "OnlyNoHurt" && thePlayer.hurtTime == 0)) {
            // Critical Effect
            if (thePlayer.fallDistance > 0F && !thePlayer.onGround && !thePlayer.isOnLadder &&
                !thePlayer.isInWater && !thePlayer.isPotionActive(Potion.blindness) && !thePlayer.isRiding)
                thePlayer.onCriticalHit(entity)

            // Enchant Effect
            if (entity is EntityLivingBase && EnchantmentHelper.getModifierForCreature(thePlayer.heldItem, entity.creatureAttribute) > 0F)
                thePlayer.onEnchantmentCritical(entity)
        } else {
            if (mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR)
                thePlayer.attackTargetEntityWithCurrentItem(entity)
        }

        // Extra critical effects
        val criticals = KevinClient.moduleManager.getModule(Criticals::class.java)

        for (i in 0..2) {
            // Critical Effect
            if (thePlayer.fallDistance > 0F && !thePlayer.onGround && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isPotionActive(Potion.blindness) && thePlayer.ridingEntity == null || criticals.state && criticals.msTimer.hasTimePassed(criticals.delayValue.get().toLong()) && !thePlayer.isInWater && !thePlayer.isInLava && !thePlayer.isInWeb)
                thePlayer.onCriticalHit(target!!)

            // Enchant Effect
            if (target is EntityLivingBase && EnchantmentHelper.getModifierForCreature(thePlayer.heldItem, (target as EntityLivingBase).creatureAttribute) > 0.0f || fakeSharpValue.get())
                thePlayer.onEnchantmentCritical(target!!)
        }

        if (highVersionSwing) doSwing()

        // Start blocking after attack
        if ((autoBlockValue equal "Packet"||keepAutoBlock) && (thePlayer.isBlocking || canBlock))
            startBlocking(entity, interactAutoBlockValue.get())
    }

    /**
     * Update killaura rotations to enemy
     */
    private fun updateRotations(entity: Entity): Boolean {
        if (yawMaxTurnSpeed.get() <= 0F)
            return true

        var boundingBox = entity.entityBoundingBox

        if (predictValue.get())
            boundingBox = boundingBox.offset(
                (entity.posX - entity.prevPosX - (mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX)) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                (entity.posY - entity.prevPosY - (mc.thePlayer!!.posY - mc.thePlayer!!.prevPosY)) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                (entity.posZ - entity.prevPosZ - (mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ)) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
            )

        var limitedRotation: Rotation = if (rotationModeValue.get().contains("LiquidBounce", true)) {
            val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                discoverRangeValue.get()
            ) ?: return false
                RotationUtils.limitAngleChange(aimingRotation, rotation,
                    (Math.random() * (yawMaxTurnSpeed.get() - yawMinTurnSpeed.get()) + yawMinTurnSpeed.get()).toFloat(),
                    (Math.random() * (pitchMaxTurnSpeed.get() - pitchMinTurnSpeed.get()) + pitchMinTurnSpeed.get()).toFloat()
                )
        } else if (rotationModeValue.get().contains("NearestPoint", true)) {
            val bb : AxisAlignedBB = entity.entityBoundingBox
            val thePlayer = mc.thePlayer
            RotationUtils.limitAngleChange(
                aimingRotation,
                RotationUtils.getOtherRotation(
                    boundingBox,
                    if (randomCenterValue.get()) {
                        if (RotationUtils.targetRotation == null || (RandomUtils.random.nextBoolean() && !attackTimer.hasTimePassed(attackDelay / 2))) {
                            lastHitVec = Vec3(
                                MathHelper.clamp_double(thePlayer.posX, bb.minX, bb.maxX) + RandomUtils.nextDouble(-0.2, 0.2),
                                MathHelper.clamp_double(thePlayer.posY + 1.62F, bb.minY, bb.maxY) + RandomUtils.nextDouble(-0.2, 0.2),
                                MathHelper.clamp_double(thePlayer.posZ, bb.minZ, bb.maxZ) + RandomUtils.nextDouble(-0.2, 0.2)
                            )
                        }
                        lastHitVec
                    } else getNearestPointBB(mc.thePlayer.getPositionEyes(1f), entity.entityBoundingBox),
                    predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                    discoverRangeValue.get()
                ) ?: return false,
                (Math.random() * (yawMaxTurnSpeed.get() - yawMinTurnSpeed.get()) + yawMinTurnSpeed.get()).toFloat(),
                (Math.random() * (pitchMaxTurnSpeed.get() - pitchMinTurnSpeed.get()) + pitchMinTurnSpeed.get()).toFloat()
            )
        } else if (rotationModeValue equal "Advanced") {
            val (_, rotation) = RotationUtils.newSearchCenter(
                boundingBox,
                outborderValue.get() && entity.hurtResistantTime > 15,
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                discoverRangeValue.get(),
                rangeValue.get()
            ) ?: return false
            RotationUtils.limitAngleChange(aimingRotation, rotation,
                (Math.random() * (yawMaxTurnSpeed.get() - yawMinTurnSpeed.get()) + yawMinTurnSpeed.get()).toFloat(),
                (Math.random() * (pitchMaxTurnSpeed.get() - pitchMinTurnSpeed.get()) + pitchMinTurnSpeed.get()).toFloat()
            )
        } else {
            RotationUtils.limitAngleChange(
                aimingRotation,
                RotationUtils.getOtherRotation(
                    boundingBox,
                    RotationUtils.getCenter(entity.entityBoundingBox),
                    predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                    discoverRangeValue.get()
                ) ?: return false,
                (Math.random() * (yawMaxTurnSpeed.get() - yawMinTurnSpeed.get()) + yawMinTurnSpeed.get()).toFloat(),
                (Math.random() * (pitchMaxTurnSpeed.get() - pitchMinTurnSpeed.get()) + pitchMinTurnSpeed.get()).toFloat()
            )
        }
        if (delayRotationTick.get() > 0) {
            delayedRotations.add(limitedRotation)
            val rot = delayedRotations.getNearestAlive(delayRotationTick.get() * 50L) ?: limitedRotation
            limitedRotation = rot
        }
        if (!onlyRotationInAttack.get() || rotationTime-- > 0)
        when (silentRotationValue.get()) {
            "Always" -> RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get() || keepRotationTickValue.get() > 0) keepRotationTickValue.get() + (if (aacValue.get()) 15 else 0) else 0)
            "Off" -> limitedRotation.toPlayer(mc.thePlayer!!)
            else -> {
                if (MovementUtils.isMoving) limitedRotation.toPlayer(mc.thePlayer!!)
                else RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get() || keepRotationTickValue.get() > 0) keepRotationTickValue.get() + (if (aacValue.get()) 15 else 0) else 0)
            }
        }

        return true
    }

    /**
     * Check if enemy is hitable with current rotations
     */
    private fun updateHitable() {
        // Disable hitable check if turn speed is zero
        if (yawMaxTurnSpeed.get() <= 0F || alwaysHitable.get()) {
            hitable = true
            return
        }
        if (target == null || mc.thePlayer == null) {
            hitable = false
            return
        }

        val reach = min(maxRange.toDouble(), mc.thePlayer!!.getDistanceToEntityBox(target!!)) + 1


        if (raycastValue.get()) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach, object : RaycastUtils.EntityFilter {
                override fun canRaycast(entity: Entity?): Boolean {
                    return (!livingRaycastValue.get() || (((entity) is EntityLivingBase || HideAndSeekHack.isHider(entity)) && (entity) !is EntityArmorStand)) &&
                            (isEnemy(entity) || raycastIgnoredValue.get() || aacValue.get() && (anythingsRayCast.get() || mc.theWorld!!.getEntitiesWithinAABBExcludingEntity(entity, entity!!.entityBoundingBox).isNotEmpty()))
                }

            })

            if (raycastValue.get() && raycastedEntity != null && (raycastedEntity is EntityLivingBase || HideAndSeekHack.isHider(raycastedEntity))
                /**&& (LiquidBounce.moduleManager[NoFriends::class.java].state || !(classProvider.isEntityPlayer(raycastedEntity) && raycastedEntity.asEntityPlayer().isClientFriend()))**/)
                currentTarget = raycastedEntity

            hitable = if (yawMaxTurnSpeed.get() > 0F) currentTarget == raycastedEntity else true
        } else
            hitable = RotationUtils.isFaced(currentTarget, reach)
    }

    /**
     * Start blocking
     */
    private fun startBlocking(interactEntity: Entity, interact: Boolean) {
        if (!(blockRate.get() > 0 && RandomUtils.random.nextInt(100) <= blockRate.get()))
            return

        if (blockingStatus&&blockStatusCheck.get()) {
            return
        }

        if (autoBlockValue equal "Fake") {
            blockingStatus = true
            return
        }

        if (interact) {
            val positionEye = mc.renderViewEntity?.getPositionEyes(1F)

            val expandSize = interactEntity.collisionBorderSize.toDouble()
            val boundingBox = interactEntity.entityBoundingBox.expand(expandSize, expandSize, expandSize)

            val (yaw, pitch) = RotationUtils.targetRotation ?: Rotation(mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch)
            val yawCos = cos(-yaw * 0.017453292F - Math.PI.toFloat())
            val yawSin = sin(-yaw * 0.017453292F - Math.PI.toFloat())
            val pitchCos = -cos(-pitch * 0.017453292F)
            val pitchSin = sin(-pitch * 0.017453292F)
            val range = min(maxRange.toDouble(), mc.thePlayer!!.getDistanceToEntityBox(interactEntity)) + 1
            val lookAt = positionEye!!.addVector(yawSin * pitchCos * range, pitchSin * range, yawCos * pitchCos * range)

            val movingObject = boundingBox.calculateIntercept(positionEye, lookAt) ?: return
            val hitVec = movingObject.hitVec

            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, Vec3(
                hitVec.xCoord - interactEntity.posX,
                hitVec.yCoord - interactEntity.posY,
                hitVec.zCoord - interactEntity.posZ)
            ))
            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, C02PacketUseEntity.Action.INTERACT))
        }

        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1),
            255, mc.thePlayer!!.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F))
        blockingStatus = true
    }


    /**
     * Stop blocking
     */
    private fun stopBlocking() {
        if (blockingStatus) {
            if (autoBlockValue notEqual "Fake") mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            blockingStatus = false
        }
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        inline get() = mc.thePlayer!!.isSpectator || !isAlive(mc.thePlayer!!)
                || KevinClient.moduleManager.getModule(Blink::class.java).state
                || KevinClient.moduleManager.getModule(FreeCam::class.java).state
                || (KevinClient.moduleManager.getModule(TP::class.java).state&&(KevinClient.moduleManager.getModule(TP::class.java)).mode.get().equals("AAC",true))
                || (KevinClient.moduleManager.getModule(Fly::class.java).state&&(KevinClient.moduleManager.getModule(Fly::class.java)).mode.get().equals("Vulcan",true))
                || (scaffoldCheck.get()&&KevinClient.moduleManager.getModule(Scaffold::class.java).state)

    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: EntityLivingBase) = entity.isEntityAlive && entity.health > 0 ||
            aacValue.get() && entity.hurtTime > 5

    /**
     * Check if player is able to block
     */
    private val canBlock: Boolean
        inline get() = mc.thePlayer!!.heldItem != null && (mc.thePlayer!!.heldItem!!.item) is ItemSword

    /**
     * Range
     */
    private val maxRange: Float
        get() = rangeValue.get()

    private fun getRange(entity: Entity) =
        (if (mc.thePlayer!!.getDistanceToEntityBox(entity) >= throughWallsRangeValue.get()) rangeValue.get() else throughWallsRangeValue.get()) - if (mc.thePlayer!!.isSprinting) rangeSprintReducementValue.get() else 0F

    private val aimingRotation: Rotation
        get() = if (delayRotationTick.get() > 0 && delayedRotations.size() > 0) delayedRotations.getNearestAlive(0) else RotationUtils.serverRotation

    /**
     * HUD Tag
     */
    override val tag: String
        get() = "${targetModeValue.get()} ${autoBlockValue.get()} [${discoveredTargets.size}]"

    val isBlockingChestAura: Boolean
        get() = this.state && target != null
}