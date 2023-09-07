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

import kevin.event.*
import kevin.module.*
import kevin.module.modules.movement.flys.FlyMode
import kevin.module.modules.movement.flys.aac.*
import kevin.module.modules.movement.flys.matrix.*
import kevin.module.modules.movement.flys.ncp.*
import kevin.module.modules.movement.flys.other.*
import kevin.module.modules.movement.flys.vanilla.*
import kevin.module.modules.movement.flys.verus.VerusAuto
import kevin.module.modules.movement.flys.vulcan.*
import kevin.utils.*
import org.lwjgl.input.Keyboard
import java.awt.Color

class Fly : Module("Fly","Allow you fly", Keyboard.KEY_F,ModuleCategory.MOVEMENT) {
    private val flys = arrayListOf(
        Vanilla,
        Creative,
        AAC5,
//        ACD,
        FunCraft,
        Teleport,
        VerusAuto,
        NCPPacket,
        NCPFly,
        NCPNew,
        HypixelFly,
        HypixelVanilla,
        Intave13, // from Rise
        OldHypixelBoost,
        OldNCP,
        Matrix, // from FDP
        NewMatrix, // from FDP
        NewMatrixClip, // from FDP
        GrimExplosion,
        Pika,
        Sparky,
        TestSparky,
        Vulcan, // from FDP
        VulcanClip, // from FDP
        VulcanDamage, // from FDP
        Spartan, // from Rise
        DCJBoost() // from Milk
    )

    private val names: Array<String>
    init {
        val arrayList = arrayListOf<String>()
        flys.forEach { arrayList.add(it.modeName) }
        names = arrayList.toTypedArray()
    }

    private val nowMode: FlyMode
    get() = flys.find { mode equal it.modeName }!!

    val mode = ListValue("Mode",names,names.first())
    val speed = FloatValue("Speed",2F,0.5F,5F)
    val keepAlive = BooleanValue("KeepAlive",false)
    private val resetMotion = BooleanValue("ResetMotion",false)
    private val fakeDamageValue = BooleanValue("FakeDamage", true)
    private val markValue = ListValue("Mark", arrayOf("Up", "Down", "Off"), "Up")

    private var isFlying = false
    val flyTimer = MSTimer()

    var launchY = 0.0

    override fun onEnable() {
        isFlying = mc.thePlayer.capabilities.isFlying
        if(mc.thePlayer.onGround&&fakeDamageValue.get()) mc.thePlayer.handleStatusUpdate(2)
        launchY = mc.thePlayer.posY
        nowMode.onEnable()
    }
    override fun onDisable() {
        mc.thePlayer.capabilities.isFlying = isFlying&&(mc.playerController.isSpectator||mc.playerController.isInCreativeMode)
        if (resetMotion.get()) {
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
        nowMode.onDisable()
    }

    @EventTarget
    fun onRender3d(event: Render3DEvent) {
        if (markValue equal "Off") {
            return
        }

        RenderUtils.drawPlatform(
            if (markValue equal "Up") launchY + 2.0 else launchY,
            if (mc.thePlayer.entityBoundingBox.maxY < launchY + 2.0) Color(0, 255, 0, 90) else Color(255, 0, 0, 90),
            1.0)
    }

    @EventTarget fun onMotion(event: MotionEvent) = nowMode.onMotion(event)
    @EventTarget fun onRender3D(event: Render3DEvent) = nowMode.onRender3D(event)
    @EventTarget fun onWorld(event: WorldEvent) = nowMode.onWorld(event)
    @EventTarget fun onBB(event: BlockBBEvent) = nowMode.onBB(event)
    @EventTarget fun onStep(event: StepEvent) = nowMode.onStep(event)
    @EventTarget fun onJump(event: JumpEvent) = nowMode.onJump(event)
    @EventTarget fun onUpdate(event: UpdateEvent) = nowMode.onUpdate(event)
    @EventTarget fun onPacket(event: PacketEvent) = nowMode.onPacket(event)
    @EventTarget fun onMove(event: MoveEvent) = nowMode.onMove(event)

    override val values: List<Value<*>> = super.values.toMutableList().also { list -> flys.forEach { flyMode -> list.addAll(flyMode.values) } }

    override val tag: String
        get() = "${mode.get()}${if (nowMode.tagV!=null) "(${nowMode.tagV})" else ""}"
}