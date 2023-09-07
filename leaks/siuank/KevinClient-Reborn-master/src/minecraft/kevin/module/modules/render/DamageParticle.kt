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
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent
import kevin.event.WorldEvent
import kevin.module.IntegerValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.EntityUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

class DamageParticle : Module("DamageParticle","Damage particle",category = ModuleCategory.RENDER) {
    class SingleParticle(val str:String,val posX:Double,val posY:Double,val posZ:Double,val healthIncrease: Boolean){
        var ticks=0
    }
    private val healthData=HashMap<Int,Float>()
    private val particles=ArrayList<SingleParticle>()

    private val aliveTicks= IntegerValue("AliveTicks",20,10,50)
    private val sizeValue= IntegerValue("Size",3,1,7)

    private val healthIncreaseColorR = IntegerValue("HealthIncreaseColorR",0,0,255)
    private val healthIncreaseColorG = IntegerValue("HealthIncreaseColorG",255,0,255)
    private val healthIncreaseColorB = IntegerValue("HealthIncreaseColorB",0,0,255)

    private val healthDecreaseColorR = IntegerValue("HealthDecreaseColorR",255,0,255)
    private val healthDecreaseColorG = IntegerValue("HealthDecreaseColorG",0,0,255)
    private val healthDecreaseColorB = IntegerValue("HealthDecreaseColorB",0,0,255)

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        synchronized(particles){
            for(entity in mc.theWorld.loadedEntityList){
                if(entity is EntityLivingBase && EntityUtils.isSelected(entity,true)){
                    val lastHealth=healthData.getOrDefault(entity.entityId,entity.maxHealth)
                    healthData[entity.entityId] = entity.health
                    if(lastHealth==entity.health) continue

                    val prefix=if(lastHealth>entity.health){"❤"}else{"§l❤"}
                    particles.add(SingleParticle(prefix+BigDecimal(abs(lastHealth-entity.health).toDouble()).setScale(1,BigDecimal.ROUND_HALF_UP).toDouble()
                        ,entity.posX - 0.5 + Random(System.currentTimeMillis()).nextInt(5).toDouble() * 0.1
                        ,entity.entityBoundingBox.minY + (entity.entityBoundingBox.maxY - entity.entityBoundingBox.minY) / 2.0
                        ,entity.posZ - 0.5 + Random(System.currentTimeMillis() + 1L).nextInt(5).toDouble() * 0.1
                        ,lastHealth<entity.health)
                    )
                }
            }

            val needRemove=ArrayList<SingleParticle>()
            for(particle in particles){
                particle.ticks++
                if(particle.ticks>aliveTicks.get()){
                    needRemove.add(particle)
                }
            }
            for(particle in needRemove){
                particles.remove(particle)
            }
        }
    }

    @EventTarget
    fun onRender3d(event: Render3DEvent){
        synchronized(particles){
            val renderManager=mc.renderManager
            val size = sizeValue.get()*0.01

            for(particle in particles){
                val n: Double = particle.posX - renderManager.renderPosX
                val n2: Double = particle.posY - renderManager.renderPosY
                val n3: Double = particle.posZ - renderManager.renderPosZ
                GlStateManager.pushMatrix()
                GlStateManager.enablePolygonOffset()
                GlStateManager.doPolygonOffset(1.0f, -1500000.0f)
                GlStateManager.translate(n.toFloat(), n2.toFloat(), n3.toFloat())
                GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
                val textY = if (mc.gameSettings.thirdPersonView == 2) { -1.0f } else { 1.0f }

                GlStateManager.rotate(renderManager.playerViewX, textY, 0.0f, 0.0f)
                GlStateManager.scale(-size, -size, size)
                GL11.glDepthMask(false)
                val color = if (particle.healthIncrease) Color(healthIncreaseColorR.get(),healthIncreaseColorG.get(),healthIncreaseColorB.get()) else Color(healthDecreaseColorR.get(),healthDecreaseColorG.get(),healthDecreaseColorB.get())
                mc.fontRendererObj.drawStringWithShadow(
                    particle.str,
                    (-(mc.fontRendererObj.getStringWidth(particle.str) / 2)).toFloat(),
                    (-(mc.fontRendererObj.FONT_HEIGHT - 1)).toFloat(),
                    color.rgb
                )
                GL11.glColor4f(187.0f, 255.0f, 255.0f, 1.0f)
                GL11.glDepthMask(true)
                GlStateManager.doPolygonOffset(1.0f, 1500000.0f)
                GlStateManager.disablePolygonOffset()
                GlStateManager.popMatrix()
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent){
        particles.clear()
        healthData.clear()
    }
}