package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.module.*
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

class ChinaHat : Module("ChinaHat", "Gives you a chinese hat", category = ModuleCategory.RENDER) {
    private val quality = ListValue("Quality", arrayOf("VeryVeryLow", "Umbrella", "VeryLow", "Low", "Normal", "High", "VeryHigh", "Smooth"), "Normal")
    private val firstPerson = BooleanValue("ShowInFirstPerson", false)

    private val topColorR = IntegerValue("TopColorR", 196, 0, 255)
    private val topColorG = IntegerValue("TopColorG", 10, 0, 255)
    private val topColorB = IntegerValue("TopColorB", 196, 0, 255)
    private val topColorA = IntegerValue("TopColorA", 200, 0, 255)

    private val bottomColorR = IntegerValue("BottomColorR", 252, 0, 255)
    private val bottomColorG = IntegerValue("BottomColorG", 65, 0, 255)
    private val bottomColorB = IntegerValue("BottomColorB", 65, 0, 255)
    private val bottomColorA = IntegerValue("BottomColorA", 100, 0, 255)

    @EventTarget fun onRender(event: Render3DEvent) {
        if (mc.gameSettings.thirdPersonView == 0 && !firstPerson.get()) return
        val x =
            mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.viewerPosX
        val y: Double =
            mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.viewerPosY + mc.thePlayer.eyeHeight + 0.5 + if (mc.thePlayer.isSneaking) -0.2 else 0.0
        val z =
            mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.viewerPosZ

        val rad = 0.65

        var q = 64

        var increaseCount = false

        when (quality.get()) {
            "VeryVeryLow" -> {
                q = 8
                increaseCount = true
            }
            "Umbrella" -> q = 16
            "VeryLow" -> {
                q = 32
                increaseCount = true
            }
            "Low" -> {
                q = 64
                increaseCount = true
            }
            "Normal" -> q = 128
            "High" -> {
                q = 256
                increaseCount = true
            }
            "VeryHigh" -> {
                q = 512
                increaseCount = true
            }
            "Smooth" -> {
                q = 1024
                increaseCount = true
            }
        }

        glPushMatrix()
        glDisable(3553)
        glEnable(2848)
        glEnable(2832)
        glEnable(3042)
        glShadeModel(GL_SMOOTH)
        GlStateManager.disableCull()
        glBegin(GL_TRIANGLE_FAN)

        glColor4f(
            topColorR.get() / 255f,
            topColorG.get() / 255f,
            topColorB.get() / 255f,
            topColorA.get() / 255f
        )
        glVertex3d(x, y, z)
        var i = 0f
        while (i < Math.PI * 2 + (if (increaseCount) 0.01 else 0.0)) {
            val vecX = x + rad * cos(i)
            val vecZ = z + rad * sin(i)
            glColor4f(
                bottomColorR.get() / 255f,
                bottomColorG.get() / 255f,
                bottomColorB.get() / 255f,
                bottomColorA.get() / 255f
            )
            glVertex3d(vecX, y - 0.25, vecZ)
            i += (Math.PI * 4 / q).toFloat()
        }


        glEnd()
        glShadeModel(GL_FLAT)
        glDepthMask(true)
        glEnable(2929)
        GlStateManager.enableCull()
        glDisable(2848)
        glEnable(2832)
        glEnable(3553)
        glPopMatrix()

        glColor3f(255f, 255f, 255f)
    }

    override val tag: String = quality.get()
}