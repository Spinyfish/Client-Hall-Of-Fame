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

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.render.BlockOverlay
import kevin.utils.*
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList

class TeleportUse : Module("TeleportUse", "Allows you to use items over a long distance.", category = ModuleCategory.WORLD) {
    private var points=CopyOnWriteArrayList<Vec3>()
    private var thread: Thread? = null
    private val doSwing=BooleanValue("Swing",true)
    private val path=BooleanValue("RenderPath",true)
    private val pos=BooleanValue("RenderPos",true)
    private val rangeCheck = BooleanValue("RangeCheck",true)
    private val moveDistanceValue= FloatValue("MoveDistance",5F,2F,15F)
    private val packetMode = ListValue("PacketMode", arrayOf("C04","C06"),"C04")
    private val timer = MSTimer()
    private val aliveTicks = IntegerValue("AliveTicks",20,10,50)
    private val glLineWidthValue = FloatValue("glLineWidth",2F,1F,4F)
    private val blockLineWidthValue = FloatValue("blockLineWidth",2F,1F,4F)
    private val aliveTimer = TickTimer()

    private val colorMode = ListValue("ColorMode", arrayOf("Custom","Rainbow"),"Custom")
    private val colorR = IntegerValue("R",255,0,255)
    private val colorG = IntegerValue("G",255,0,255)
    private val colorB = IntegerValue("B",255,0,255)

    private val onlyBlockV = BooleanValue("OnlyBlock",true)

    override fun onEnable() {
        timer.reset()
        points.clear()
        aliveTimer.reset()
    }
    override fun onDisable() {
        timer.reset()
        points.clear()
        aliveTimer.reset()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if (timer.hasTimePassed(250L) && mc.gameSettings.keyBindUseItem.isKeyDown){
            if(thread == null || !thread!!.isAlive) {
                if ((mc.thePlayer?.inventory?.getCurrentItem()?.item) !is ItemBlock && onlyBlockV.get()) return
                if (rangeCheck.get()&&(KevinClient.moduleManager.getModule(BlockOverlay::class.java)).currentBlock!=null) return
                thread = Thread {
                    val entityLookVec = mc.thePlayer!!.lookVec ?: return@Thread
                    val lookVec = Vec3(entityLookVec.xCoord * 300, entityLookVec.yCoord * 300, entityLookVec.zCoord * 300)
                    val posVec = Vec3(mc.thePlayer!!.posX, mc.thePlayer!!.posY + 1.62, mc.thePlayer!!.posZ)
                    val endBlock = mc.theWorld!!.rayTraceBlocks(posVec, posVec.add(lookVec), false, false, false) ?: return@Thread
                    doTPUse(endBlock)
                }
                aliveTimer.reset()
                points.clear()
                timer.reset()
                thread!!.start()
            }else timer.reset()
        }
        if (aliveTimer.hasTimePassed(aliveTicks.get())) points.clear()
        aliveTimer.update()
    }

    private fun doTPUse(targetBlock: MovingObjectPosition){

        points.add(Vec3(mc.thePlayer!!.posX,mc.thePlayer!!.posY,mc.thePlayer!!.posZ))
        val targetBlockPos = targetBlock.blockPos?:return
        val path = PathUtils.findBlinkPath2(mc.thePlayer!!.posX,mc.thePlayer!!.posY,mc.thePlayer!!.posZ,targetBlockPos.x.toDouble(),targetBlockPos.y.toDouble(),targetBlockPos.z.toDouble(),moveDistanceValue.get().toDouble())
        path.forEach {
            val packet = if (packetMode.get() == "C04")
                C03PacketPlayer.C04PacketPlayerPosition(it.xCoord,it.yCoord,it.zCoord,true)
            else
                C03PacketPlayer.C06PacketPlayerPosLook(it.xCoord,it.yCoord,it.zCoord,mc.thePlayer!!.rotationYaw,mc.thePlayer!!.rotationPitch,true)
            mc.netHandler.addToSendQueue(packet)
            points.add(it)
        }

        val itemStack = mc.thePlayer!!.heldItem ?: return

        if (targetBlock.blockPos == null && targetBlock.sideHit == null) return

        if (mc.playerController.onPlayerRightClick(
                mc.thePlayer!!,
                mc.theWorld!!,
                itemStack,
                targetBlock.blockPos!!,
                targetBlock.sideHit!!,
                targetBlock.hitVec
            )
        ) {
            if (doSwing.get()) {
                mc.thePlayer!!.swingItem()
            } else {
                mc.netHandler.addToSendQueue(C0APacketAnimation())
            }
        }


        for(i in path.size-1 downTo 0){
            val vec=path[i]
            val packet = if (packetMode.get() == "C04")
                C03PacketPlayer.C04PacketPlayerPosition(vec.xCoord,vec.yCoord,vec.zCoord,true)
            else
                C03PacketPlayer.C06PacketPlayerPosLook(vec.xCoord,vec.yCoord,vec.zCoord,mc.thePlayer!!.rotationYaw,mc.thePlayer!!.rotationPitch,true)
            mc.netHandler.addToSendQueue(packet)
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val color = if (colorMode.get() == "Custom") Color(colorR.get(),colorG.get(),colorB.get()) else ColorUtils.rainbow()

        synchronized(points) {
            if(points.isEmpty()||!path.get()) return@synchronized
            val renderPosX = mc.renderManager.viewerPosX
            val renderPosY = mc.renderManager.viewerPosY
            val renderPosZ = mc.renderManager.viewerPosZ

            GL11.glPushMatrix()
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glShadeModel(GL11.GL_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LIGHTING)
            GL11.glDepthMask(false)
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)

            for (vec in points){
                val x = vec.xCoord - renderPosX
                val y = vec.yCoord - renderPosY
                val z = vec.zCoord - renderPosZ
                val width = 0.3
                val height = mc.thePlayer!!.eyeHeight.toDouble()
                GL11.glLoadIdentity()
                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2)
                RenderUtils.glColor(color)
                GL11.glLineWidth(glLineWidthValue.get())
                GL11.glBegin(GL11.GL_LINE_STRIP)
                GL11.glVertex3d(x - width, y, z - width)
                GL11.glVertex3d(x - width, y, z - width)
                GL11.glVertex3d(x - width, y + height, z - width)
                GL11.glVertex3d(x + width, y + height, z - width)
                GL11.glVertex3d(x + width, y, z - width)
                GL11.glVertex3d(x - width, y, z - width)
                GL11.glVertex3d(x - width, y, z + width)
                GL11.glEnd()
                GL11.glBegin(GL11.GL_LINE_STRIP)
                GL11.glVertex3d(x + width, y, z + width)
                GL11.glVertex3d(x + width, y + height, z + width)
                GL11.glVertex3d(x - width, y + height, z + width)
                GL11.glVertex3d(x - width, y, z + width)
                GL11.glVertex3d(x + width, y, z + width)
                GL11.glVertex3d(x + width, y, z - width)
                GL11.glEnd()
                GL11.glBegin(GL11.GL_LINE_STRIP)
                GL11.glVertex3d(x + width, y + height, z + width)
                GL11.glVertex3d(x + width, y + height, z - width)
                GL11.glEnd()
                GL11.glBegin(GL11.GL_LINE_STRIP)
                GL11.glVertex3d(x - width, y + height, z + width)
                GL11.glVertex3d(x - width, y + height, z - width)
                GL11.glEnd()
            }

            GL11.glDepthMask(true)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glPopMatrix()
            GL11.glColor4f(1F, 1F, 1F, 1F)
        }

        if (!pos.get()) return
        if (rangeCheck.get()&&(KevinClient.moduleManager.getModule(BlockOverlay::class.java)).currentBlock!=null) return
        val entityLookVec = mc.thePlayer!!.lookVec ?: return
        val lookVec = Vec3(entityLookVec.xCoord * 300, entityLookVec.yCoord * 300, entityLookVec.zCoord * 300)
        val posVec = Vec3(mc.thePlayer!!.posX, mc.thePlayer!!.posY + 1.62, mc.thePlayer!!.posZ)
        val endBlock = mc.theWorld!!.rayTraceBlocks(posVec, posVec.add(lookVec), false, false, false) ?: return
        val blockPosition = endBlock.blockPos ?: return
        if (!mc.theWorld!!.worldBorder.contains(blockPosition)) return
        val block = mc.theWorld!!.getBlockState(blockPosition).block
        val partialTicks = event.partialTicks
        val st = BlockUtils.getState(blockPosition) ?:return
        if (st.block.material == Material.air || ((mc.thePlayer?.inventory?.getCurrentItem()?.item) !is ItemBlock && onlyBlockV.get())) return

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        RenderUtils.glColor(color)
        GL11.glLineWidth(blockLineWidthValue.get())
        GlStateManager.disableTexture2D()
        GL11.glDepthMask(false)
        block.setBlockBoundsBasedOnState(mc.theWorld!!, blockPosition)
        val thePlayer = mc.thePlayer ?: return

        val x = thePlayer.lastTickPosX + (thePlayer.posX - thePlayer.lastTickPosX) * partialTicks
        val y = thePlayer.lastTickPosY + (thePlayer.posY - thePlayer.lastTickPosY) * partialTicks
        val z = thePlayer.lastTickPosZ + (thePlayer.posZ - thePlayer.lastTickPosZ) * partialTicks

        val axisAlignedBB = block.getSelectedBoundingBox(mc.theWorld!!, blockPosition)
            .expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026)
            .offset(-x, -y, -z)

        RenderUtils.drawSelectionBoundingBox(axisAlignedBB)
        GL11.glDepthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.resetColor()
    }
}