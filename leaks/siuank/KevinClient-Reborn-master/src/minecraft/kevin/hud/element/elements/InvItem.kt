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
import kevin.module.IntegerValue
import kevin.module.modules.world.ChestStealer
import kevin.font.RainbowFontShader
import kevin.utils.render.shader.shaders.RainbowShader
import kevin.utils.RenderUtils
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "InvItem")
class InvItem(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale){
    private val bgRValue = IntegerValue("BGR", 0, 0, 255)
    private val bgGValue = IntegerValue("BGG", 0, 0, 255)
    private val bgBValue = IntegerValue("BGB", 0, 0, 255)
    private val bgAlphaValue = IntegerValue("BGAlpha", 150, 0, 255)
    private val tiRValue = IntegerValue("TIR", 255, 0, 255)
    private val tiGValue = IntegerValue("TIG", 255, 0, 255)
    private val tiBValue = IntegerValue("TIB", 255, 0, 255)
    private val titleRainbow = BooleanValue("TitleRainbow",true)
    private val boRValue = IntegerValue("BOR", 0, 0, 255)
    private val boGValue = IntegerValue("BOG", 0, 0, 255)
    private val boBValue = IntegerValue("BOB", 0, 0, 255)
    private val borderRainbow = BooleanValue("BorderRainbow",true)
    private val iESRValue = IntegerValue("IER", 0, 0, 255)
    private val iESGValue = IntegerValue("IEG", 0, 0, 255)
    private val iESBValue = IntegerValue("IEB", 0, 0, 255)
    private val invEmptyStringRainbow = BooleanValue("InvEmptyStringRainbow",true)
    private val startY = -(6+KevinClient.fontManager.font35!!.fontHeight).toFloat()
    override fun drawElement(): Border {
        drawRects()
        drawTitle()
        if(!invEmpty() || isChest){ drawInv() }else{ drawInvEmptyString() }
        return Border(0F,startY,174F,getY2())
    }
    private fun getY2(): Float{
        val chestStealer = KevinClient.moduleManager.getModule(ChestStealer::class.java)
        return if (
            chestStealer.state &&
            chestStealer.chestItems.size > 27 &&
            chestStealer.overrideShowInvValue.get() &&
            (mc.currentScreen) is GuiChest
        ) 110 - startY else 66F
    }
    private fun drawRects() {
        RenderUtils.drawRect(0F,startY,174F,getY2(),Color(bgRValue.get(),bgGValue.get(),bgBValue.get(),bgAlphaValue.get()))
        if (borderRainbow.get()) {
            RainbowShader.begin(true, 1.0F / 1000, 1.0F / 1000, System.currentTimeMillis() % 10000 / 10000F).use {
                RenderUtils.drawRect(0F, startY, 1F, getY2(), Color.WHITE)//getY2()
                RenderUtils.drawRect(0F, startY, 174F, startY + 1, Color.WHITE)
                RenderUtils.drawRect(0F, 0F, 174F, 1F, Color.WHITE)
                RenderUtils.drawRect(0F, getY2()-1, 174F, getY2(), Color.WHITE)//getY2()
                RenderUtils.drawRect(173F, startY, 174F, getY2(), Color.WHITE)//getY2()
            }
        }else{
            RenderUtils.drawRect(0F, startY, 1F, getY2(), Color(boRValue.get(),boGValue.get(),boBValue.get()))
            RenderUtils.drawRect(0F, startY, 174F, startY + 1, Color(boRValue.get(),boGValue.get(),boBValue.get()))
            RenderUtils.drawRect(0F, 0F, 174F, 1F, Color(boRValue.get(),boGValue.get(),boBValue.get()))
            RenderUtils.drawRect(0F, getY2()-1, 174F, getY2(), Color(boRValue.get(),boGValue.get(),boBValue.get()))
            RenderUtils.drawRect(173F, startY, 174F, getY2(), Color(boRValue.get(),boGValue.get(),boBValue.get()))
        }
    }
    private fun drawInv() {
        GL11.glPushMatrix()
        RenderHelper.enableGUIStandardItemLighting()
        val chestStealer = KevinClient.moduleManager.getModule(ChestStealer::class.java)
        val chest = (mc.currentScreen) is GuiChest && chestStealer.state && chestStealer.overrideShowInvValue.get()

        if (chestStealer.chestItems.isEmpty()&&chest) {
            RenderHelper.disableStandardItemLighting()
            GlStateManager.enableAlpha()
            GlStateManager.disableBlend()
            GlStateManager.disableLighting()
            GlStateManager.disableCull()
            GL11.glPopMatrix()
            return
        }

        val mode = if (chest) {
            if (chestStealer.chestItems.size > 27) "BigChest" else "Chest"
        }else "Inv"

        if ((mode=="Chest"&&chestStealer.chestItems.size!=27)||(mode=="BigChest"&&chestStealer.chestItems.size!=54)) {
            RenderHelper.disableStandardItemLighting()
            GlStateManager.enableAlpha()
            GlStateManager.disableBlend()
            GlStateManager.disableLighting()
            GlStateManager.disableCull()
            GL11.glPopMatrix()
            return
        }

        when(mode){
            "Inv" -> {
                drawItems(9, 17, 6)
                drawItems(18, 26, 24)
                drawItems(27, 35, 42)
            }
            "Chest" -> {
                drawChestItems(0,8,6,chestStealer.chestItems)
                drawChestItems(9,17,24,chestStealer.chestItems)
                drawChestItems(18,26,42,chestStealer.chestItems)
            }
            else -> {
                drawChestItems(0,8,6,chestStealer.chestItems)
                drawChestItems(9,17,24,chestStealer.chestItems)
                drawChestItems(18,26,42,chestStealer.chestItems)
                drawChestItems(27,35,60,chestStealer.chestItems)
                drawChestItems(36,44,78,chestStealer.chestItems)
                drawChestItems(45,53,96,chestStealer.chestItems)
            }
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GL11.glPopMatrix()
    }
    private fun drawItems(slot: Int,endSlot: Int,y: Int){
        var xOffset=6
        for(i in slot..endSlot){
            xOffset+=18
            val stack=mc.thePlayer!!.inventoryContainer.getSlot(i).stack ?: continue
            mc.renderItem.renderItemAndEffectIntoGUI(stack, xOffset-18, y)
            mc.renderItem.renderItemOverlays(mc.fontRendererObj, stack, xOffset-18, y)
        }
    }
    private val isChest: Boolean get() {
        val chestStealer = KevinClient.moduleManager.getModule(ChestStealer::class.java)
        return (mc.currentScreen) is GuiChest && chestStealer.state && chestStealer.overrideShowInvValue.get()
    }
    private fun drawChestItems(slot: Int,endSlot: Int,y: Int,chestItems: MutableList<ItemStack?>){
        var xOffset=6
        for (i in slot..endSlot){
            xOffset+=18
            if (chestItems[i] == null || chestItems[i]!!.item == null) continue
            val stack = chestItems[i]!!
            mc.renderItem.renderItemAndEffectIntoGUI(stack, xOffset-18, y)
            mc.renderItem.renderItemOverlays(mc.fontRendererObj, stack, xOffset-18, y)
        }
    }
    private fun drawTitle(){
        val chestStealer = KevinClient.moduleManager.getModule(ChestStealer::class.java)
        val name = if (
            chestStealer.overrideShowInvValue.get()
            && (mc.currentScreen) is GuiChest
            && chestStealer.state
        ) "Chest"
        else "Inventory"
        if (titleRainbow.get()) {
            RainbowFontShader.begin(true, 1.0F / 1000, 1.0F / 1000, System.currentTimeMillis() % 10000 / 10000F).use {
                KevinClient.fontManager.font35!!.drawString(
                    name,
                    (174F / 2F) - (KevinClient.fontManager.font35!!.getStringWidth(name) * 0.5F),
                    -(KevinClient.fontManager.font35!!.fontHeight).toFloat(),
                    0,
                    false
                )
            }
        }else{
            KevinClient.fontManager.font35!!.drawString(
                name,
                (174F / 2F) - (KevinClient.fontManager.font35!!.getStringWidth(name) * 0.5F),
                -(KevinClient.fontManager.font35!!.fontHeight).toFloat(),
                Color(tiRValue.get(),tiGValue.get(),tiBValue.get()).rgb,
                false
            )}
    }
    private fun invEmpty():Boolean{
        for(slot in 9..35){
            if(mc.thePlayer!!.inventoryContainer.getSlot(slot).stack != null) return false
        }
        return true
    }
    private fun drawInvEmptyString(){
        val chestStealer = KevinClient.moduleManager.getModule(ChestStealer::class.java)
        if (chestStealer.overrideShowInvValue.get() && (mc.currentScreen) is GuiChest && chestStealer.state) return
        if (invEmptyStringRainbow.get()) {
            RainbowFontShader.begin(true, 1.0F / 1000, 1.0F / 1000, System.currentTimeMillis() % 10000 / 10000F).use {
                KevinClient.fontManager.font35!!.drawString(
                    "Your inventory is empty...",
                    (174F / 2F) - (KevinClient.fontManager.font35!!.getStringWidth("Your inventory is empty...") * 0.5F),
                    -(KevinClient.fontManager.font35!!.fontHeight) + 30.toFloat(),
                    0,
                    false
                )
            }
        }else{
            KevinClient.fontManager.font35!!.drawString(
                "Your inventory is empty...",
                (174F / 2F) - (KevinClient.fontManager.font35!!.getStringWidth("Your inventory is empty...") * 0.5F),
                -(KevinClient.fontManager.font35!!.fontHeight) + 30.toFloat(),
                Color(iESRValue.get(),iESGValue.get(),iESBValue.get()).rgb,
                false
            )
        }
    }
}