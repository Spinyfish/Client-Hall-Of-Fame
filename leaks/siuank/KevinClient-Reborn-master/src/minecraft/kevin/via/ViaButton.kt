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
package kevin.via

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.MathHelper

class ViaButton(width: Int) : GuiButton(4514,width - 95,5,90, 20, "") {
    private var dragging = false
    private var sliderValue = 0F
    private val step = 1F/28.5F
    private var setValue = 0
    init {
        upDateValue()
    }
    private fun upDateValue(){
        val value = ProtocolCollection.getProtocolById(ViaVersion.nowVersion)!!
        displayString = value.protocolVersion.name
        setValue = ViaVersion.versions.indexOf(value)
        this.sliderValue = if (value!=ViaVersion.versions.last()) (setValue*step) else 1F
    }
    override fun mouseReleased(mouseX: Int, mouseY: Int) {
        dragging = false
        ViaVersion.nowVersion = ViaVersion.versions[setValue].protocolVersion.version
    }
    override fun getHoverState(mouseOver: Boolean): Int { return 0 }
    override fun mouseDragged(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (visible) {
            if (dragging) {
                this.sliderValue = (mouseX - (xPosition + 4)).toFloat() / (width - 8).toFloat()
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0f, 1.0f)
                setValue = (sliderValue/step).toInt()
                val value = ViaVersion.versions[setValue]
                this.sliderValue = if (value!=ViaVersion.versions.last()) ((sliderValue/step).toInt()*step) else 1F
                displayString = value.protocolVersion.name
            }
            mc.textureManager.bindTexture(buttonTextures)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            this.drawTexturedModalRect(
                xPosition + (this.sliderValue * (width - 8).toFloat()).toInt(),
                yPosition, 0, 66, 4, 20
            )
            this.drawTexturedModalRect(
                xPosition + (this.sliderValue * (width - 8).toFloat()).toInt() + 4,
                yPosition, 196, 66, 4, 20
            )
        }
    }
    override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        return if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (mouseX - (xPosition + 4)).toFloat() / (width - 8).toFloat()
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0f, 1.0f)
            setValue = (sliderValue/step).toInt()
            val value = ViaVersion.versions[setValue]
            this.sliderValue = if (value!=ViaVersion.versions.last()) ((sliderValue/step).toInt()*step) else 1F
            displayString = value.protocolVersion.name
            dragging = true
            true
        } else {
            false
        }
    }
}