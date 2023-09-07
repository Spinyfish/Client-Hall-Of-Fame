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
package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.Render2DEvent
import kevin.event.Render3DEvent
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.*
import kevin.utils.render.shader.shaders.GlowShader
import kevin.utils.render.shader.shaders.OutlineShader
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.entity.item.EntityMinecartChest
import net.minecraft.tileentity.*
import org.lwjgl.opengl.EXTFramebufferObject
import org.lwjgl.opengl.EXTPackedDepthStencil
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StorageESP : Module("StorageESP", "Allows you to see chests, dispensers, etc. through walls.", category = ModuleCategory.RENDER) {
    val modeValue = ListValue("Mode", arrayOf("Box", "OtherBox", "Outline", "ShaderOutline", "ShaderGlow", "2D", "WireFrame"), "Outline")
    private val chestValue = BooleanValue("Chest", true)
    private val enderChestValue = BooleanValue("EnderChest", true)
    private val furnaceValue = BooleanValue("Furnace", true)
    private val dispenserValue = BooleanValue("Dispenser", true)
    private val hopperValue = BooleanValue("Hopper", true)

    private fun getColor(tileEntity: TileEntity): Color?{
        return when {
            chestValue.get() && (tileEntity) is TileEntityChest /**&& !clickedBlocks.contains(tileEntity.pos)**/ -> Color(0, 66, 255)
            enderChestValue.get() && (tileEntity) is TileEntityEnderChest /**&& !clickedBlocks.contains(tileEntity.pos)**/ -> Color.MAGENTA
            furnaceValue.get() && (tileEntity) is TileEntityFurnace -> Color.BLACK
            dispenserValue.get() && (tileEntity) is TileEntityDispenser -> Color.BLACK
            hopperValue.get() && (tileEntity) is TileEntityHopper -> Color.GRAY
            else -> null
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        try {
            val mode = modeValue.get()

            if (mode.equals("outline", ignoreCase = true)) {

                if (mc.gameSettings.ofFastRender){
                    ChatUtils.messageWithStart("§cTurn off fastrender!!")
                    state = false
                    return
                }

                val fbo = Minecraft.getMinecraft().framebuffer

                if (fbo != null) {
                    if (fbo.depthBuffer > -1) {
                        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer)
                        val stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT()
                        EXTFramebufferObject.glBindRenderbufferEXT(
                            EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                            stencil_depth_buffer_ID
                        )
                        EXTFramebufferObject.glRenderbufferStorageEXT(
                            EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                            EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT,
                            Minecraft.getMinecraft().displayWidth,
                            Minecraft.getMinecraft().displayHeight
                        )
                        EXTFramebufferObject.glFramebufferRenderbufferEXT(
                            EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                            EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT,
                            EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                            stencil_depth_buffer_ID
                        )
                        EXTFramebufferObject.glFramebufferRenderbufferEXT(
                            EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                            EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                            EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                            stencil_depth_buffer_ID
                        )
                        fbo.depthBuffer = -1
                    }
                }
            }

            val gamma = mc.gameSettings.gammaSetting

            mc.gameSettings.gammaSetting = 100000.0f

            for (tileEntity in mc.theWorld!!.loadedTileEntityList) {
                val color: Color = getColor(tileEntity) ?: continue

                if (!((tileEntity) is TileEntityChest || (tileEntity) is TileEntityEnderChest)) {
                    RenderUtils.drawBlockBox(tileEntity.pos, color, !mode.equals("otherbox", ignoreCase = true))
                    continue
                }
                when (mode.lowercase(Locale.getDefault())) {
                    "otherbox", "box" -> RenderUtils.drawBlockBox(
                        tileEntity.pos,
                        color,
                        !mode.equals("otherbox", ignoreCase = true)
                    )
                    "2d" -> RenderUtils.draw2D(tileEntity.pos, color.rgb, Color.BLACK.rgb)
                    "outline" -> {
                        RenderUtils.glColor(color)
                        OutlineUtils.renderOne(3f)
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                        OutlineUtils.renderTwo()
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                        OutlineUtils.renderThree()
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                        OutlineUtils.renderFour(color)
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                        OutlineUtils.renderFive()
                        OutlineUtils.setColor(Color.WHITE)
                    }
                    "wireframe" -> {
                        GL11.glPushMatrix()
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS)
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
                        GL11.glDisable(GL11.GL_TEXTURE_2D)
                        GL11.glDisable(GL11.GL_LIGHTING)
                        GL11.glDisable(GL11.GL_DEPTH_TEST)
                        GL11.glEnable(GL11.GL_LINE_SMOOTH)
                        GL11.glEnable(GL11.GL_BLEND)
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                        RenderUtils.glColor(color)
                        GL11.glLineWidth(1.5f)
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                        GL11.glPopAttrib()
                        GL11.glPopMatrix()
                    }
                }
            }
            for (entity in mc.theWorld!!.loadedEntityList) {
                if ((entity)is EntityMinecartChest) {
                    when (mode.lowercase(Locale.getDefault())) {
                        "otherbox", "box" -> RenderUtils.drawEntityBox(
                            entity,
                            Color(0, 66, 255),
                            !mode.equals("otherbox", ignoreCase = true)
                        )
                        "2d" -> RenderUtils.draw2D(entity.position, Color(0, 66, 255).rgb, Color.BLACK.rgb)
                        "outline" -> {
                            val entityShadow: Boolean = mc.gameSettings.entityShadows
                            mc.gameSettings.entityShadows = false
                            RenderUtils.glColor(Color(0, 66, 255))
                            OutlineUtils.renderOne(3f)
                            mc.renderManager.renderEntityStatic(entity, mc.timer.renderPartialTicks, true)
                            OutlineUtils.renderTwo()
                            mc.renderManager.renderEntityStatic(entity, mc.timer.renderPartialTicks, true)
                            OutlineUtils.renderThree()
                            mc.renderManager.renderEntityStatic(entity, mc.timer.renderPartialTicks, true)
                            OutlineUtils.renderFour(Color(0, 66, 255))
                            mc.renderManager.renderEntityStatic(entity, mc.timer.renderPartialTicks, true)
                            OutlineUtils.renderFive()
                            OutlineUtils.setColor(Color.WHITE)
                            mc.gameSettings.entityShadows = entityShadow
                        }
                        "wireframe" -> {
                            val entityShadow: Boolean = mc.gameSettings.entityShadows
                            mc.gameSettings.entityShadows = false
                            GL11.glPushMatrix()
                            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS)
                            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
                            GL11.glDisable(GL11.GL_TEXTURE_2D)
                            GL11.glDisable(GL11.GL_LIGHTING)
                            GL11.glDisable(GL11.GL_DEPTH_TEST)
                            GL11.glEnable(GL11.GL_LINE_SMOOTH)
                            GL11.glEnable(GL11.GL_BLEND)
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                            RenderUtils.glColor(Color(0, 66, 255))
                            mc.renderManager.renderEntityStatic(entity, mc.timer.renderPartialTicks, true)
                            RenderUtils.glColor(Color(0, 66, 255))
                            GL11.glLineWidth(1.5f)
                            mc.renderManager.renderEntityStatic(entity, mc.timer.renderPartialTicks, true)
                            GL11.glPopAttrib()
                            GL11.glPopMatrix()
                            mc.gameSettings.entityShadows = entityShadow
                        }
                    }
                }
            }
            RenderUtils.glColor(Color(255, 255, 255, 255))
            mc.gameSettings.gammaSetting = gamma
        } catch (ignored: Exception) {
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val mode = modeValue.get()
        val shader = (if (mode.equals("shaderoutline", ignoreCase = true)) OutlineShader.OUTLINE_SHADER else if (mode.equals("shaderglow", ignoreCase = true)) GlowShader.GLOW_SHADER else null)
            ?: return
        val partialTicks = event.partialTicks
        val renderManager = mc.renderManager
        shader.startDraw(event.partialTicks)

        try {
            val entityMap = HashMap<Color, ArrayList<TileEntity>>()
            for (tileEntity in mc.theWorld!!.loadedTileEntityList) {
                val color: Color = getColor(tileEntity) ?: continue

                if (!entityMap.containsKey(color)) {
                    entityMap[color] = ArrayList()
                }

                entityMap[color]!!.add(tileEntity)
            }

            for ((color, arr) in entityMap) {
                shader.startDraw(partialTicks)
                for (entity in arr) {
                    TileEntityRendererDispatcher.instance.renderTileEntityAt(
                        entity,
                        entity.pos.x - renderManager.renderPosX,
                        entity.pos.y - renderManager.renderPosY,
                        entity.pos.z - renderManager.renderPosZ,
                        partialTicks
                    )
                }
                shader.stopDraw(color, if (mode.equals("shaderglow", ignoreCase = true)) 2.5f else 1.5f, 1f)
            }
        } catch (ex: Exception) {
            println("An error occurred while rendering all storages for shader esp $ex")
        }

        shader.stopDraw(Color(0, 66, 255), if (mode.equals("shaderglow", ignoreCase = true)) 2.5f else 1.5f, 1f)
    }
}