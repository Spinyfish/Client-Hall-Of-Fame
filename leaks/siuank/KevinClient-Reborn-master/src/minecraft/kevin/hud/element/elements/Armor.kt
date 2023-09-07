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
import kevin.hud.element.Side
import kevin.main.KevinClient
import kevin.module.FloatValue
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.font.RainbowFontShader
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat

@ElementInfo(name = "Armor")
class Armor(x: Double = -8.0, y: Double = 57.0, scale: Float = 1F,
            side: Side = Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    private val modeValue = ListValue("Alignment", arrayOf("Horizontal", "Vertical"), "Horizontal")
    private val armorShowDamageMode = ListValue("ArmorShowDamage", arrayOf("None","Percentage","Value","All"),"All")
    private val armorDamageColor = ListValue("ArmorShowDamageColor", arrayOf("Custom","Rainbow","Damage"),"Damage")
    private val armorDamageCustomRed = IntegerValue("ArmorDamageCustomRed",255,0,255)
    private val armorDamageCustomGreen = IntegerValue("ArmorDamageCustomGreen",255,0,255)
    private val armorDamageCustomBlue = IntegerValue("ArmorDamageCustomBlue",255,0,255)
    private val armorDamageRainbowX = FloatValue("ArmorDamageRainbowX",-1000F, -2000F, 2000F)
    private val armorDamageRainbowY = FloatValue("ArmorDamageRainbowY",-1000F, -2000F, 2000F)

    override fun drawElement(): Border {
        val showDamageMode = armorShowDamageMode
        val showDamageColorMode = armorDamageColor
        val showDamageRainbow = showDamageColorMode.get().equals("rainbow",true)
        val rainbowX = armorDamageRainbowX
        val rainbowY = armorDamageRainbowY

        GL11.glPushMatrix()
        RenderHelper.enableGUIStandardItemLighting()

        val isCreative = !mc.playerController.isNotCreative
        val renderItem = mc.renderItem
        val isInsideWater = mc.thePlayer!!.isInsideOfMaterial(Material.water)

        val mode = modeValue.get()
        var x = if(mode.equals("Horizontal", true)) 27 else 87
        var y = if(mode.equals("Horizontal", true)) if (isCreative) 10 else if (isInsideWater) -10 else 0 else 38


        for (index in if (mode.equals("Horizontal", true)) 3 downTo 0 else 0..3) {
            val stack = mc.thePlayer!!.inventory.armorInventory[index] ?: continue
            renderItem.renderItemIntoGUI(stack, x, y)
            renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
            if (mode.equals("Horizontal", true))
                x += 18
            else if (mode.equals("Vertical", true))
                y -= 18
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GL11.glPopMatrix()

        x = 87
        y = 38

        val bx1 = if(mode.equals("Horizontal", true)) 27f else 87f
        var by1 = 55f
        var bx2 = 104f
        val by2 = 55f

        var damageTextMaxLong = 0
        GL11.glPushMatrix()
        for (index in 0..3){
            val stack = mc.thePlayer!!.inventory.armorInventory[index] ?: continue
            val maxDamage = stack.maxDamage
            val itemDamage = stack.itemDamage
            val df = DecimalFormat("###0.00")
            var damagePercentage = df.format((maxDamage-itemDamage).toFloat()/maxDamage.toFloat()*100F)

            try {
                damagePercentage.toFloat()
            }catch (e:NumberFormatException){
                damagePercentage = "-1"
            }
            //if (damagePercentage.contains("∞")) damagePercentage = "-1"

            val damageText = if (showDamageMode.get().equals("value",true)) "${maxDamage-itemDamage}/$maxDamage"
            else if (showDamageMode.get().equals("percentage",true)) "$damagePercentage%"
            else if (showDamageMode.get().equals("all",true)) "${maxDamage-itemDamage}/$maxDamage $damagePercentage%" else ""

            if (KevinClient.fontManager.font35!!.getStringWidth(damageText) > damageTextMaxLong) damageTextMaxLong = KevinClient.fontManager.font35!!.getStringWidth(damageText)

            val color = if (showDamageRainbow) 0
            else if (showDamageColorMode.get().equals("custom",true)) Color(armorDamageCustomRed.get(),armorDamageCustomGreen.get(),armorDamageCustomBlue.get()).rgb else {
                when{
                    damagePercentage.toFloat()>75 -> Color.green.rgb
                    damagePercentage.toFloat()>50 -> Color.orange.rgb
                    damagePercentage.toFloat()>25 -> Color.yellow.rgb
                    else -> Color.red.rgb
                }
            }

            RainbowFontShader.begin(showDamageRainbow,if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),System.currentTimeMillis() % 10000 / 10000F).use {
                KevinClient.fontManager.font35!!.drawStringWithShadow(damageText,x+20F,y+6F,color)
            }

            y -= 18
            by1 -= 18
        }
        GL11.glPopMatrix()

        if (damageTextMaxLong!=0) bx2 += damageTextMaxLong + 5
        return Border(bx1, by1, bx2, by2)
    }
}
