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
package kevin.hud.element.elements

import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.module.modules.render.ESP
import kevin.utils.*
import kevin.utils.render.shader.shaders.RainbowShader
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector2f
import java.awt.Color
import kotlin.math.*

@ElementInfo(name = "Radar", disableScale = true, priority = 1)
class Radar(x: Double = 5.0, y: Double = 130.0) : Element(x, y) {

    companion object {
        private val SQRT_OF_TWO = sqrt(2f)
    }

    private val sizeValue = FloatValue("Size", 90f, 30f, 500f)
    private val viewDistanceValue = FloatValue("View Distance", 4F, 0.5F, 32F)

    private val playerShapeValue = ListValue("Player Shape", arrayOf("Triangle", "Rectangle", "Circle"), "Triangle")
    private val playerSizeValue = FloatValue("Player Size", 2.0F, 0.5f, 20F)
    private val useESPColorsValue = BooleanValue("Use ESP Colors", true)
    private val fovSizeValue = FloatValue("FOV Size", 10F, 0F, 50F)
    private val fovAngleValue = FloatValue("FOV Angle", 70F, 30F, 160F)

    private val minimapValue = BooleanValue("Minimap", true)

    private val rainbowXValue = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowYValue = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)

    private val backgroundRedValue = IntegerValue("Background Red", 0, 0, 255)
    private val backgroundGreenValue = IntegerValue("Background Green", 0, 0, 255)
    private val backgroundBlueValue = IntegerValue("Background Blue", 0, 0, 255)
    private val backgroundAlphaValue = IntegerValue("Background Alpha", 50, 0, 255)

    private val borderStrengthValue = FloatValue("Border Strength", 2F, 1F, 5F)
    private val borderRedValue = IntegerValue("Border Red", 0, 0, 255)
    private val borderGreenValue = IntegerValue("Border Green", 0, 0, 255)
    private val borderBlueValue = IntegerValue("Border Blue", 0, 0, 255)
    private val borderAlphaValue = IntegerValue("Border Alpha", 150, 0, 255)
    private val borderRainbowValue = BooleanValue("Border Rainbow", false)

    private var fovMarkerVertexBuffer: VertexBuffer? = null
    private var lastFov = 0f

    override fun drawElement(): Border {
        MiniMapRegister.radarEnabled = true
        MiniMapRegister.updateChunks()

        val fovAngle = fovAngleValue.get()

        if (lastFov != fovAngle || fovMarkerVertexBuffer == null) {
            // Free Memory
            fovMarkerVertexBuffer?.deleteGlBuffers()

            fovMarkerVertexBuffer = createFovIndicator(fovAngle)
            lastFov = fovAngle
        }

        val renderViewEntity = mc.renderViewEntity!!

        val size = sizeValue.get()

        if (!minimapValue.get()) {
            RenderUtils.drawRect(0F, 0F, size, size, Color(backgroundRedValue.get(), backgroundGreenValue.get(),
                backgroundBlueValue.get(), backgroundAlphaValue.get()).rgb)
        }

        val viewDistance = viewDistanceValue.get() * 16.0F

        val maxDisplayableDistanceSquare = ((viewDistance + fovSizeValue.get().toDouble()) *
                (viewDistance + fovSizeValue.get().toDouble()))
        val halfSize = size / 2f

        RenderUtils.makeScissorBox(x.toFloat(), y.toFloat(), x.toFloat() + ceil(size), y.toFloat() + ceil(size))

        GL11.glEnable(GL11.GL_SCISSOR_TEST)

        GL11.glPushMatrix()

        GL11.glTranslatef(halfSize, halfSize, 0f)
        GL11.glRotatef(renderViewEntity.rotationYaw, 0f, 0f, -1f)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        if (minimapValue.get()) {
            GL11.glEnable(GL11.GL_TEXTURE_2D)

            val chunkSizeOnScreen = size / viewDistanceValue.get()
            val chunksToRender = max(1, ceil((SQRT_OF_TWO * (viewDistanceValue.get() * 0.5f))).toInt())

            val currX = renderViewEntity.posX / 16.0
            val currZ = renderViewEntity.posZ / 16.0

            for (x in -chunksToRender..chunksToRender) {
                for (z in -chunksToRender..chunksToRender) {
                    val currChunk = MiniMapRegister.getChunkTextureAt(floor(currX).toInt() + x, floor(currZ).toInt() + z)

                    if (currChunk != null) {
                        val sc = chunkSizeOnScreen.toDouble()

                        val onScreenX = (currX - floor(currX).toLong() - 1 - x) * sc
                        val onScreenZ = (currZ - floor(currZ).toLong() - 1 - z) * sc

                        GlStateManager.bindTexture(currChunk.texture.glTextureId)

                        GL11.glBegin(GL11.GL_QUADS)

                        GL11.glTexCoord2f(0f, 0f)
                        GL11.glVertex2d(onScreenX, onScreenZ)
                        GL11.glTexCoord2f(0f, 1f)
                        GL11.glVertex2d(onScreenX, onScreenZ + chunkSizeOnScreen)
                        GL11.glTexCoord2f(1f, 1f)
                        GL11.glVertex2d(onScreenX + chunkSizeOnScreen, onScreenZ + chunkSizeOnScreen)
                        GL11.glTexCoord2f(1f, 0f)
                        GL11.glVertex2d(onScreenX + chunkSizeOnScreen, onScreenZ)

                        GL11.glEnd()
                    }

                }
            }

            GlStateManager.bindTexture(0)

            GL11.glDisable(GL11.GL_TEXTURE_2D)
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)

        val triangleMode = playerShapeValue.get().equals("triangle", true)
        val circleMode = playerShapeValue.get().equals("circle", true)

        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        if (circleMode) {
            GL11.glEnable(GL11.GL_POINT_SMOOTH)
        }

        var playerSize = playerSizeValue.get()

        GL11.glEnable(GL11.GL_POLYGON_SMOOTH)

        if (triangleMode) {
            playerSize *= 2
        } else {
            worldRenderer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION)
            GL11.glPointSize(playerSize)
        }

        for (entity in mc.theWorld!!.loadedEntityList) {
            if (entity != mc.thePlayer && EntityUtils.isSelected(entity, false) && entity is EntityLivingBase) {
                val positionRelativeToPlayer = Vector2f((renderViewEntity.posX - entity.posX).toFloat(),
                    (renderViewEntity.posZ - entity.posZ).toFloat())

                if (maxDisplayableDistanceSquare < positionRelativeToPlayer.lengthSquared())
                    continue

                val transform = triangleMode || fovSizeValue.get() > 0F

                if (transform) {
                    GL11.glPushMatrix()

                    GL11.glTranslatef(
                        (positionRelativeToPlayer.x / viewDistance) * size,
                        (positionRelativeToPlayer.y / viewDistance) * size, 0f
                    )
                    GL11.glRotatef(entity.rotationYaw, 0f, 0f, 1f)
                }

                if (fovSizeValue.get() > 0F) {
                    GL11.glPushMatrix()
                    GL11.glRotatef(180.0f, 0f, 0f, 1f)
                    val sc = (fovSizeValue.get() / viewDistance) * size
                    GL11.glScalef(sc, sc, sc)

                    GL11.glColor4f(1.0f, 1.0f, 1.0f, if (minimapValue.get()) 0.75f else 0.25f)

                    val vbo = fovMarkerVertexBuffer!!

                    vbo.bindBuffer()

                    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
                    GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L)

                    vbo.drawArrays(GL11.GL_TRIANGLE_FAN)
                    vbo.unbindBuffer()

                    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY)

                    GL11.glPopMatrix()
                }

                if (triangleMode) {
                    if (useESPColorsValue.get()) {
                        val color = (KevinClient.moduleManager.getModule(ESP::class.java)).getColor(entity)

                        GL11.glColor4f(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, 1.0f)
                    } else {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    }

                    GL11.glBegin(GL11.GL_TRIANGLES)

                    GL11.glVertex2f(-playerSize * 0.25f, playerSize * 0.5f)
                    GL11.glVertex2f(playerSize * 0.25f, playerSize * 0.5f)
                    GL11.glVertex2f(0f, -playerSize * 0.5f)

                    GL11.glEnd()
                } else {
                    val color = (KevinClient.moduleManager.getModule(ESP::class.java)).getColor(entity)

                    worldRenderer.pos(((positionRelativeToPlayer.x / viewDistance) * size).toDouble(), ((positionRelativeToPlayer.y / viewDistance) * size).toDouble(), 0.0)
                        .color(color.red / 255.0f, color.green / 255.0f,
                            color.blue / 255.0f, 1.0f).endVertex()
                }

                if (transform) {
                    GL11.glPopMatrix()
                }

            }
        }

        if (!triangleMode)
            tessellator.draw()

        if (circleMode) {
            GL11.glDisable(GL11.GL_POINT_SMOOTH)
        }

        GL11.glDisable(GL11.GL_POLYGON_SMOOTH)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)

        GL11.glDisable(GL11.GL_SCISSOR_TEST)

        GL11.glPopMatrix()

        RainbowShader.begin(borderRainbowValue.get(), if (rainbowXValue.get() == 0.0F) 0.0F else 1.0F / rainbowXValue.get(),
            if (rainbowYValue.get() == 0.0F) 0.0F else 1.0F / rainbowYValue.get(), System.currentTimeMillis() % 10000 / 10000F).use {
            RenderUtils.drawBorder(0F, 0F, size, size, borderStrengthValue.get(), Color(borderRedValue.get(),
                borderGreenValue.get(), borderBlueValue.get(), borderAlphaValue.get()).rgb)

            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)

            RenderUtils.glColor(borderRedValue.get(), borderGreenValue.get(), borderBlueValue.get(), borderAlphaValue.get())
            GL11.glLineWidth(borderStrengthValue.get())

            GL11.glBegin(GL11.GL_LINES)

            GL11.glVertex2f(halfSize, 0f)
            GL11.glVertex2f(halfSize, size)

            GL11.glVertex2f(0f, halfSize)
            GL11.glVertex2f(size, halfSize)

            GL11.glEnd()

            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
        }

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        return Border(0F, 0F, size, size)
    }

    private fun createFovIndicator(angle: Float): VertexBuffer {
        // Rendering
        val worldRenderer = Tessellator.getInstance().worldRenderer

        worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION)

        val start = (90.0f - (angle * 0.5f)) / 180.0f * Math.PI.toFloat()
        val end = (90.0f + (angle * 0.5f)) / 180.0f * Math.PI.toFloat()

        var curr = end
        val radius = 1.0

        worldRenderer.pos(0.0, 0.0, 0.0).endVertex()

        while (curr >= start) {
            worldRenderer.pos(cos(curr) * radius, sin(curr) * radius, 0.0).endVertex()

            curr -= 0.15f
        }

        // Uploading to VBO

        val safeVertexBuffer = SafeVertexBuffer(worldRenderer.vertexFormat)

        worldRenderer.finishDrawing()
        worldRenderer.reset()
        safeVertexBuffer.bufferData(worldRenderer.byteBuffer)

        return safeVertexBuffer
    }

}