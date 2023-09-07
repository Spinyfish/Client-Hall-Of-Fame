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
package kevin.cape

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kevin.file.FileManager
import kevin.main.KevinClient
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.GL11
import java.io.*
import java.lang.String.join
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO

class CapeManager : GuiScreen() {
    private val saveFile = File(KevinClient.fileManager.capesDir,"cape.json")
    private val capes = mutableListOf<Cape>()
    var nowCape:Cape?= null

    fun load(){
        capes.clear()
        for(file in KevinClient.fileManager.capesDir.listFiles()!!){
            if(file.isFile&&!file.name.equals(saveFile.name)){
                try {
                    val args=file.name.split(".").toTypedArray()
                    capes.add(Cape(join(".", *args.copyOfRange(0, args.size - 1)), ImageIO.read(file)))
                }catch (e: Exception){
                    println("Occurred an error while loading cape from file: ${file.name}")
                    e.printStackTrace()
                }
            }
        }
        if(!saveFile.exists()) return
        val json= JsonParser().parse(IOUtils.toString(FileInputStream(saveFile),"utf-8")).asJsonObject
        if(json.has("name")){
            val name=json.get("name").asString
            if(!name.equals("NONE")){
                val result=capes.filter { it.name == name }
                if(result.isNotEmpty())
                    nowCape=result[0]
            }
        }
    }
    fun save(){
        val json= JsonObject()
        json.addProperty("name", if(nowCape!=null){ nowCape!!.name }else{ "NONE" })
        val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(saveFile), StandardCharsets.UTF_8))
        writer.write(FileManager.PRETTY_GSON.toJson(json))
        writer.close()
    }
    override fun onGuiClosed() = save()
    override fun doesGuiPauseGame() = false
    override fun initGui() {
        this.buttonList.add(GuiButton(0, 0, 0, KevinClient.fontManager.font40.getStringWidth("< QUIT")+10, 20, "< QUIT"))
        this.buttonList.add(GuiButton(1, (width*0.3).toInt(), (height*0.5).toInt(), KevinClient.fontManager.font40.getStringWidth("<-")+10, 20, "<-"))
        this.buttonList.add(GuiButton(2, (width*0.7).toInt(), (height*0.5).toInt(), KevinClient.fontManager.font40.getStringWidth("->")+10, 20, "->"))
    }

    override fun actionPerformed(p_actionPerformed_1_: GuiButton) {
        fun next(index: Int){
            var chooseIndex=index
            if(chooseIndex>=capes.size)
                chooseIndex=-1

            if(chooseIndex<-1)
                chooseIndex=capes.size-1

            nowCape = if(chooseIndex!=-1)
                capes[chooseIndex]
            else
                null
        }

        when(p_actionPerformed_1_.id){
            0 -> mc.displayGuiScreen(null)
            1 -> next(capes.indexOf(nowCape)-1)
            2 -> next(capes.indexOf(nowCape)+1)
        }
    }

    override fun drawScreen(p_drawScreen_1_: Int, p_drawScreen_2_: Int, p_drawScreen_3_: Float) {

        GL11.glPushMatrix()
        KevinClient.fontManager.font40.drawCenteredString(if(nowCape==null){ "§cNONE" }else{ "§a${nowCape!!.name}" },width*0.50f,height*0.23f, -1, false)
        GL11.glScalef(2f,2f,2f)
        KevinClient.fontManager.font40.drawCenteredString("Cape Manager",width*0.25f,height*0.03f, -1, false)
        GL11.glPopMatrix()

        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_)

        mc.thePlayer ?: return
        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)
        GlStateManager.enableColorMaterial()
        GlStateManager.pushMatrix()
        GL11.glTranslatef(width*0.5f-60, height*0.3f, 0f)
        GL11.glScalef(2f,2f,2f)
        GL11.glTranslatef(30f, 100f, 0f)
        GlStateManager.translate(0F, 0F, 50F)
        GlStateManager.scale(-50F, 50F, 50F)
        GlStateManager.rotate(180F, 0F, 0F, 1F)

        val renderYawOffset = mc.thePlayer.renderYawOffset
        val rotationYaw = mc.thePlayer.rotationYaw
        val rotationPitch = mc.thePlayer.rotationPitch
        val prevRotationYawHead = mc.thePlayer.prevRotationYawHead
        val rotationYawHead = mc.thePlayer.rotationYawHead
        val armor0=mc.thePlayer.inventory.armorInventory[0]
        val armor1=mc.thePlayer.inventory.armorInventory[1]
        val armor2=mc.thePlayer.inventory.armorInventory[2]
        val armor3=mc.thePlayer.inventory.armorInventory[3]
        val current=mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem]

        GlStateManager.rotate(135F, 0F, 1F, 0F)
        RenderHelper.enableStandardItemLighting()
        GlStateManager.rotate(-135F, 0F, 1F, 0F)
        GlStateManager.rotate(0f, 1F, 0F, 0F)

        mc.thePlayer.renderYawOffset = 180f
        mc.thePlayer.rotationYaw = 180f
        mc.thePlayer.rotationPitch = 0f
        mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw
        mc.thePlayer.prevRotationYawHead = mc.thePlayer.rotationYaw
        mc.thePlayer.inventory.armorInventory[0]=null
        mc.thePlayer.inventory.armorInventory[1]=null
        mc.thePlayer.inventory.armorInventory[2]=null
        mc.thePlayer.inventory.armorInventory[3]=null
        mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem]=null

        GlStateManager.translate(0F, 0F, 0F)

        val renderManager = mc.renderManager
        renderManager.setPlayerViewY(180F)
        renderManager.isRenderShadow = false
        renderManager.renderEntityWithPosYaw(mc.thePlayer, 0.0, 0.0, 0.0, 0F, 1F)
        renderManager.isRenderShadow = true

        mc.thePlayer.renderYawOffset = renderYawOffset
        mc.thePlayer.rotationYaw = rotationYaw
        mc.thePlayer.rotationPitch = rotationPitch
        mc.thePlayer.prevRotationYawHead = prevRotationYawHead
        mc.thePlayer.rotationYawHead = rotationYawHead
        mc.thePlayer.inventory.armorInventory[0]=armor0
        mc.thePlayer.inventory.armorInventory[1]=armor1
        mc.thePlayer.inventory.armorInventory[2]=armor2
        mc.thePlayer.inventory.armorInventory[3]=armor3
        mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem]=current

        GlStateManager.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.disableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
        GlStateManager.resetColor()
    }
}