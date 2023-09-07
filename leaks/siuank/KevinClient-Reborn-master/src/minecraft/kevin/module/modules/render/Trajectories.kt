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

import kevin.event.*
import kevin.module.*
import kevin.utils.ColorUtils
import kevin.utils.RenderUtils
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderPearl
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.entity.projectile.EntityEgg
import net.minecraft.entity.projectile.EntityFireball
import net.minecraft.entity.projectile.EntityFishHook
import net.minecraft.entity.projectile.EntityPotion
import net.minecraft.entity.projectile.EntitySnowball
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Cylinder
import org.lwjgl.util.glu.GLU
import java.awt.Color
import kotlin.math.floor
import kotlin.math.min

class Trajectories : Module("Trajectories", description = "Shows the trajectory of the flying arrows.", category = ModuleCategory.RENDER) {

    private val colorMode = ListValue("ColorMode", arrayOf("Custom","Rainbow", "Distance", "Speed"),"Rainbow")
    private val cColorR = IntegerValue("R",255,0,255)
    private val cColorG = IntegerValue("G",0,0,255)
    private val cColorB = IntegerValue("B",0,0,255)
    private val cColorA = IntegerValue("A",0,0,255)
    private val lineWidth = FloatValue("LineWidth",2F,0.5F,3F)

    @EventTarget
    fun onRender3D(event: Render3DEvent){
        for (e in mc.theWorld?.loadedEntityList ?: return) {
            if (e !is EntityArrow && e !is EntityFishHook && e !is EntitySnowball && e !is EntityEnderPearl && e !is EntityEgg && e !is EntityFireball) continue
            val thePlayer = mc.thePlayer ?: return
            val theWorld = mc.theWorld ?: return
            val renderManager = mc.renderManager
            var motionSlowdown = 0.99F
            var gravity = 0.05F
            var size = 0.3F
            when (e) {
                is EntityArrow -> {
                    motionSlowdown = 0.99F
                }
                is EntityFishHook -> {
                    gravity = 0.04F
                    size = 0.25F
                    motionSlowdown = 0.92F
                }
                is EntityFireball -> {
                    gravity = 0F
                    motionSlowdown = 1.0F
                }
                else -> {
                    gravity = 0.03F
                    size = 0.25F
                }
            }

            var motionX = e.motionX
            var motionY = e.motionY
            var motionZ = e.motionZ

            var posX = e.posX
            var posY = e.posY
            var posZ = e.posZ

            var aliveTicks = 0

            val tessellator = Tessellator.getInstance()
            val worldRenderer = tessellator.worldRenderer
            GL11.glDepthMask(false)
            RenderUtils.enableGlCap(GL11.GL_BLEND, GL11.GL_LINE_SMOOTH)
            RenderUtils.disableGlCap(GL11.GL_DEPTH_TEST, GL11.GL_ALPHA_TEST, GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
            if (colorMode.get() == "Rainbow"){
                RenderUtils.glColor(ColorUtils.rainbow())
            }else if (colorMode equal "Distance") {
                val distance = min(Vec3(posX, posY, posZ).distanceTo(Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).toInt(), 255)
                RenderUtils.glColor(Color(255 - distance, min(255, cColorG.get() + distance), cColorB.get(), cColorA.get()))
            } else if (colorMode equal "Speed") {
                RenderUtils.glColor(Color(cColorR.get(), 255 - min((motionX * motionX * 100 + motionZ * motionZ * 100).toInt(), 255), cColorB.get(), cColorA.get()))
            } else {
                RenderUtils.glColor(Color(cColorR.get(), cColorG.get(), cColorB.get(), cColorA.get()))
            }
            GL11.glLineWidth(lineWidth.get())
            worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
            var hasLanded = false
            var landingPosition: MovingObjectPosition? = null
            var hitEntity = false
            while (!hasLanded && posY > 0.0) {
                val posBefore = Vec3(posX, posY, posZ)
                val posAfter = Vec3(posX + motionX, posY + motionY, posZ + motionZ)

                landingPosition = theWorld.rayTraceBlocks(posBefore, posAfter,false, true,false)

                if (landingPosition != null || posBefore.squareDistanceTo(posAfter) <= 0) {
                    hasLanded = true
                }

                val arrowBox = AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size,
                    posY + size, posZ + size).addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0)

                val chunkMinX = floor((arrowBox.minX - 2.0) / 16.0).toInt()
                val chunkMaxX = floor((arrowBox.maxX + 2.0) / 16.0).toInt()
                val chunkMinZ = floor((arrowBox.minZ - 2.0) / 16.0).toInt()
                val chunkMaxZ = floor((arrowBox.maxZ + 2.0) / 16.0).toInt()

                val collidedEntities = mutableListOf<Entity>()

                for (x in chunkMinX..chunkMaxX)
                    for (z in chunkMinZ..chunkMaxZ)
                        theWorld.getChunkFromChunkCoords(x, z)
                            .getEntitiesWithinAABBForEntity(thePlayer, arrowBox, collidedEntities, null)
                collidedEntities.add(thePlayer)

                for (possibleEntity in collidedEntities) {
                    if (possibleEntity.canBeCollidedWith() && e != possibleEntity) {
                        val possibleEntityBoundingBox = possibleEntity.entityBoundingBox
                            .expand(size.toDouble(), size.toDouble(), size.toDouble())

                        val possibleEntityLanding = possibleEntityBoundingBox
                            .calculateIntercept(posBefore, posAfter) ?: continue
                        hasLanded = true
                        hitEntity = true
                        landingPosition = possibleEntityLanding
                    }
                }

                posX += motionX
                posY += motionY
                posZ += motionZ

                val blockState = theWorld.getBlockState(BlockPos(posX, posY, posZ))

                if (blockState.block.material == Material.water) {
                    motionX *= 0.6
                    motionY *= 0.6
                    motionZ *= 0.6
                } else {
                    motionX *= motionSlowdown.toDouble()
                    motionY *= motionSlowdown.toDouble()
                    motionZ *= motionSlowdown.toDouble()
                }

                motionY -= gravity.toDouble()

                worldRenderer.pos(posX - renderManager.renderPosX, posY - renderManager.renderPosY,
                    posZ - renderManager.renderPosZ).endVertex()

                if (e is EntityFireball) { // IT MAKE MY COMPUTER LAG...
                    if (++aliveTicks >= 200) break
                }
            }
            tessellator.draw()
            GL11.glPushMatrix()
            GL11.glTranslated(
                posX - renderManager.renderPosX, posY - renderManager.renderPosY,
                posZ - renderManager.renderPosZ
            )
            if (landingPosition != null) {
                // Switch rotation of hit cylinder of the hit axis
                when (landingPosition.sideHit!!.axis.ordinal) {
                    0 -> GL11.glRotatef(90F, 0F, 0F, 1F)
                    2 -> GL11.glRotatef(90F, 1F, 0F, 0F)
                }

                // Check if hitting an entity
                if (hitEntity)
                    RenderUtils.glColor(Color(255, 0, 0, 150))
            }

            // Rendering hit cylinder
            GL11.glRotatef(-90F, 1F, 0F, 0F)

            val cylinder = Cylinder()
            cylinder.drawStyle = GLU.GLU_LINE
            cylinder.draw(0.2F, 0F, 0F, 60, 1)

            GL11.glPopMatrix()
            GL11.glDepthMask(true)
            RenderUtils.resetCaps()
            GL11.glColor4f(1F, 1F, 1F, 1F)
        }
    }
}