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
package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent
import kevin.event.WorldEvent
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.utils.ChatUtils
import kevin.utils.ColorUtils
import kevin.utils.RenderUtils
import kevin.utils.WorldToScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.item.Item
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector3f
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

object HideAndSeekHack : Module("HideAndSeekHack","Mark every hider.") {
    private val hiderBlocks = HashSet<BlockPos>()
    private val airs = HashSet<BlockPos>()
    private val searchingBlocks = HashSet<BlockPos>()
    private val maxY = IntegerValue("MaxY",200,10,255)

    private val blocksShowMode = ListValue("BlocksShowMode", arrayOf("Box","2D"),"Box")
    private val hiderShowMode = ListValue("HidersShowMode", arrayOf("Box","2D"),"Box")
    private val showSearch = BooleanValue("ShowSearch",true)
    private val searchMode = object : ListValue("SearchMode", arrayOf("Region","Layer"),"Region") {
        override fun onChange(oldValue: String, newValue: String) {
            if (!KevinClient.isStarting){
                hiderBlocks.clear()
                airs.clear()
                sY = 0
                sX = -200
                sZ = -200
            }
        }
    }

    private val colorRedValue = IntegerValue("BlocksR", 255, 0, 255)
    private val colorGreenValue = IntegerValue("BlocksG", 0, 0, 255)
    private val colorBlueValue = IntegerValue("BlocksB", 0, 0, 255)
    private val colorRainbow = BooleanValue("BlocksRainbow", false)

    private val searchRedValue = IntegerValue("SearchR", 255, 0, 255)
    private val searchGreenValue = IntegerValue("SearchG", 0, 0, 255)
    private val searchBlueValue = IntegerValue("SearchB", 0, 0, 255)
    private val searchRainbow = BooleanValue("SearchRainbow", false)

    private val hidersRedValue = IntegerValue("HidersR", 255, 0, 255)
    private val hidersGreenValue = IntegerValue("HidersG", 0, 0, 255)
    private val hidersBlueValue = IntegerValue("HidersB", 0, 0, 255)
    private val hidersRainbow = BooleanValue("HidersRainbow", false)

    private val debug = BooleanValue("Debug",false)

    private var sY = 0
    private var sX = -200
    private var sZ = -200
    private var posX = .0
    private var posZ = .0

    private fun getHiders(fallingBlock: Boolean,armorStand: Boolean): List<Entity>{
        return try{
            mc.theWorld.loadedEntityList.filter {
                (armorStand&&it is EntityArmorStand) || (fallingBlock &&it is EntityFallingBlock)
            }
        } catch (e:Exception) { listOf() }
    }

    fun isHider(entity: Entity?):Boolean{
        if (!this.state || entity !is EntityFallingBlock)
            return false

        for (it in 36..44) {
            val itemStack = mc.thePlayer!!.inventoryContainer.getSlot(it).stack
            val item = itemStack?.item
            if (itemStack != null && item is ItemSword && item.material == Item.ToolMaterial.WOOD){
                return true
            }
        }
        return false
    }

    fun isSeeker(): Boolean{
        if (!this.state) return false
        for (it in 36..44) {
            val itemStack = mc.thePlayer!!.inventoryContainer.getSlot(it).stack
            val item = itemStack?.item
            if (itemStack != null && item is ItemSword && item.material == Item.ToolMaterial.EMERALD){
                return true
            }
            // Gamster
            if (itemStack != null && item is ItemAxe && item.toolMaterial == Item.ToolMaterial.EMERALD) {
                return true
            }
        }
        return false
    }

    override fun onDisable() {
        hiderBlocks.clear()
        airs.clear()
        sY = 0
        sX = -200
        sZ = -200
    }

    override fun onEnable() {
        if (!KevinClient.isStarting){
            posX = mc.thePlayer.posX
            posZ = mc.thePlayer.posZ
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if (showSearch.get()){
            searchingBlocks.clear()
            for (x in -100..100){
                searchingBlocks.add(BlockPos(posX+x+sX,sY.toDouble(),posZ+(-100)+sZ))
                searchingBlocks.add(BlockPos(posX+x+sX,sY.toDouble(),posZ+100+sZ))
            }
            for (z in -100..100){
                searchingBlocks.add(BlockPos(posX+(-100)+sX,sY.toDouble(),posZ+z+sZ))
                searchingBlocks.add(BlockPos(posX+100+sX,sY.toDouble(),posZ+z+sZ))
            }
        }
        for (x in -100..100){
            for (z in -100..100){
                val blockPos = BlockPos(posX+x+sX,sY.toDouble(),posZ+z+sZ)
                if (mc.theWorld.isAirBlock(blockPos)&&
                    !mc.theWorld.isAirBlock(blockPos.add(0,-1,0))&&
                    mc.theWorld.isBlockLoaded(blockPos)) {
                    airs.add(blockPos)
                }
            }
        }
        when(searchMode.get()){
            "Region" -> {
                sY++
                if (sY>maxY.get()) {
                    sY = 0
                    if (sX!=200){
                        sX+=200
                    }else{
                        sX=-200
                        if (sZ!=200){
                            sZ+=200
                        }else{
                            sZ=-200
                            posX = mc.thePlayer.posX
                            posZ = mc.thePlayer.posZ
                        }
                    }
                    if (debug.get())
                        ChatUtils.messageWithStart("§l§7[§l§9HideAndSeekDebug§l§7] §l§9 XDeviation:$sX ZDeviation:$sZ HidePoints:${airs.size}")
                }
            }
            "Layer" -> {
                if (sX!=200){
                    sX+=200
                }else{
                    sX=-200
                    if (sZ!=200){
                        sZ+=200
                    }else{
                        sZ=-200
                        if (sY<=maxY.get()) sY++ else {
                            sY = 0
                            posX = mc.thePlayer.posX
                            posZ = mc.thePlayer.posZ
                        }
                        if (debug.get())
                            ChatUtils.messageWithStart("§l§7[§l§9HideAndSeekDebug§l§7] §l§9 Y:$sY HidePoints:${airs.size}")
                    }
                }
            }
        }
        val oldSize = hiderBlocks.size
        hiderBlocks.clear()
        hiderBlocks.addAll(airs.filter {
            mc.theWorld.isBlockLoaded(it)&&
            !mc.theWorld.isAirBlock(it)&&
            mc.thePlayer.getDistance(it.x.toDouble(),it.y.toDouble(),it.z.toDouble())<40 })
        if (oldSize != hiderBlocks.size && debug.get()){ ChatUtils.messageWithStart("§l§7[§l§9HideAndSeekDebug§l§7] §l§9$oldSize -> ${hiderBlocks.size}") }
    }
    @EventTarget
    fun onWorld(event: WorldEvent){
        if (event.worldClient == null)
            return
        hiderBlocks.clear()
        airs.clear()
        sY = 0
        sX = -200
        sZ = -200
        posX = mc.thePlayer.posX
        posZ = mc.thePlayer.posZ
    }
    @EventTarget
    fun onRender3D(event: Render3DEvent){
        val blockColor = if (colorRainbow.get()) ColorUtils.rainbow() else Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
        val hidersColor = if (hidersRainbow.get()) ColorUtils.rainbow() else Color(hidersRedValue.get(), hidersGreenValue.get(), hidersBlueValue.get())
        for (blockPos in hiderBlocks){
            when (blocksShowMode.get()) {
                "Box" -> RenderUtils.drawBlockBox(blockPos, blockColor, true)
                "2D" -> RenderUtils.draw2D(blockPos, blockColor.rgb, Color.BLACK.rgb)
            }
        }
        if (showSearch.get()) {
            val searchColor = if (searchRainbow.get()) ColorUtils.rainbow() else Color(searchRedValue.get(), searchGreenValue.get(), searchBlueValue.get())
            for (blockPos in searchingBlocks) RenderUtils.drawBlockBox(blockPos, searchColor, true)
        }
        val mvMatrix = WorldToScreen.getMatrix(GL11.GL_MODELVIEW_MATRIX)
        val projectionMatrix = WorldToScreen.getMatrix(GL11.GL_PROJECTION_MATRIX)
        if (hiderShowMode.get() == "2D"){
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glMatrixMode(GL11.GL_PROJECTION)
            GL11.glPushMatrix()
            GL11.glLoadIdentity()
            GL11.glOrtho(0.0, mc.displayWidth.toDouble(), mc.displayHeight.toDouble(), 0.0, -1.0, 1.0)
            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPushMatrix()
            GL11.glLoadIdentity()
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.enableTexture2D()
            GL11.glDepthMask(true)
            GL11.glLineWidth(3.0f)
        }
        for (entity in getHiders(fallingBlock = true, armorStand = true)){
            when (hiderShowMode.get()) {
                "Box" -> RenderUtils.drawEntityBox(entity, hidersColor, true)
                "2D" -> {
                    val renderManager = mc.renderManager
                    val timer = mc.timer
                    val bb = entity.entityBoundingBox
                        .offset(-entity.posX, -entity.posY, -entity.posZ)
                        .offset(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks,
                            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks,
                            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks)
                        .offset(-renderManager.renderPosX, -renderManager.renderPosY, -renderManager.renderPosZ)
                    val boxVertices = arrayOf(doubleArrayOf(bb.minX, bb.minY, bb.minZ), doubleArrayOf(bb.minX, bb.maxY, bb.minZ), doubleArrayOf(bb.maxX, bb.maxY, bb.minZ), doubleArrayOf(bb.maxX, bb.minY, bb.minZ), doubleArrayOf(bb.minX, bb.minY, bb.maxZ), doubleArrayOf(bb.minX, bb.maxY, bb.maxZ), doubleArrayOf(bb.maxX, bb.maxY, bb.maxZ), doubleArrayOf(bb.maxX, bb.minY, bb.maxZ))
                    var minX = Float.MAX_VALUE
                    var minY = Float.MAX_VALUE
                    var maxX = -1f
                    var maxY = -1f
                    for (boxVertex in boxVertices) {
                        val screenPos = WorldToScreen.worldToScreen(Vector3f(boxVertex[0].toFloat(), boxVertex[1].toFloat(), boxVertex[2].toFloat()), mvMatrix, projectionMatrix, mc.displayWidth, mc.displayHeight)
                            ?: continue
                        minX = min(screenPos.x, minX)
                        minY = min(screenPos.y, minY)
                        maxX = max(screenPos.x, maxX)
                        maxY = max(screenPos.y, maxY)
                    }
                    if (minX > 0 || minY > 0 || maxX <= mc.displayWidth || maxY <= mc.displayWidth) {
                        GL11.glColor4f(hidersColor.red / 255.0f, hidersColor.green / 255.0f, hidersColor.blue / 255.0f, 1.0f)
                        GL11.glBegin(GL11.GL_LINE_LOOP)
                        GL11.glVertex2f(minX, minY)
                        GL11.glVertex2f(minX, maxY)
                        GL11.glVertex2f(maxX, maxY)
                        GL11.glVertex2f(maxX, minY)
                        GL11.glEnd()
                    }
                }
            }
        }
        if (hiderShowMode.get() == "2D"){
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glMatrixMode(GL11.GL_PROJECTION)
            GL11.glPopMatrix()
            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPopMatrix()
            GL11.glPopAttrib()
        }
    }
    override val tag: String
        get() = "H: ${getHiders(
            fallingBlock = true,
            armorStand = true
        ).size}(FB:${getHiders(
            fallingBlock = true,
            armorStand = false
        ).size} AS:${getHiders(
            fallingBlock = false,
            armorStand = true
        ).size}) B: ${hiderBlocks.size} Y:$sY SearchMode:${searchMode.get()} HidePoints:${airs.size}"
}