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
import kevin.event.PacketEvent
import kevin.event.TickEvent
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.Targets
import kevin.module.modules.render.ClickGui.NewClickGui.Category.*
import kevin.module.modules.render.ClickGui.NewClickGui.Category.Target
import kevin.utils.BlockUtils
import kevin.font.RainbowFontShader
import kevin.utils.render.shader.shaders.RainbowShader
import kevin.utils.RenderUtils
import kevin.utils.RenderUtils.glColor
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.util.*
import kotlin.collections.HashMap

class ClickGui : Module("ClickGui","Opens the ClickGUI.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_RSHIFT) {

    private val mode = ListValue("Mode", arrayOf("Milk","New","Old"),"Milk")

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S2EPacketCloseWindow && mc.currentScreen is kevin.hud.ClickGui) {
            event.cancelEvent()
        }
    }

    override fun onEnable() {
        when(mode.get()){
            "Milk" -> mc.displayGuiScreen(KevinClient.milkClickGui)
            "New" -> mc.displayGuiScreen(KevinClient.newClickGui)
            "Old" -> mc.displayGuiScreen(KevinClient.clickGUI)
        }
    }

    override fun onDisable() {
        if (mc.currentScreen is ClickGUI || mc.currentScreen is NewClickGui) mc.displayGuiScreen(null)
    }

    @EventTarget fun onTick(event: TickEvent) {
        if (this.state && mc.currentScreen !is kevin.hud.ClickGui) this.state = false
    }

    class ClickGUI : kevin.hud.ClickGui(){
        private val category = arrayListOf("Combat","Exploit","Misc","Movement","Player","Render","World","Targets")
        private var categoryOpen = 0
        private var categoryStart:HashMap<String,Int>? = null
        private var moduleOpen:HashMap<String,Int>? = null
        private var canModuleRoll = false
        private var canSettingRoll = false
        private var canSettingRoll2 = false
        private var settingStart:HashMap<String,Int>? = null

        override fun initGui() {
            if (categoryStart == null){
                categoryStart = HashMap()
                categoryStart!!["Combat"] = 0
                categoryStart!!["Exploit"] = 0
                categoryStart!!["Misc"] = 0
                categoryStart!!["Movement"] = 0
                categoryStart!!["Player"] = 0
                categoryStart!!["Render"] = 0
                categoryStart!!["World"] = 0
            }
            if (moduleOpen == null){
                moduleOpen = HashMap()
                moduleOpen!!["Combat"] = -1
                moduleOpen!!["Exploit"] = -1
                moduleOpen!!["Misc"] = -1
                moduleOpen!!["Movement"] = -1
                moduleOpen!!["Player"] = -1
                moduleOpen!!["Render"] = -1
                moduleOpen!!["World"] = -1
            }
            if (settingStart == null){
                settingStart = HashMap()
                settingStart!!["Combat"] = 0
                settingStart!!["Exploit"] = 0
                settingStart!!["Misc"] = 0
                settingStart!!["Movement"] = 0
                settingStart!!["Player"] = 0
                settingStart!!["Render"] = 0
                settingStart!!["World"] = 0
            }
        }

        override fun doesGuiPauseGame(): Boolean = false
        override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
            RenderUtils.drawRect(mc.currentScreen.width/4F,mc.currentScreen.height/4F,mc.currentScreen.width/4F*3,mc.currentScreen.height/4F*3,Color(80,80,80,255).rgb)
            KevinClient.fontManager.fontBold180.drawStringWithShadow("K",mc.currentScreen.width/4F + 3,mc.currentScreen.height/4F - 4,Color(0,114,255).rgb)

            canModuleRoll = isClick(mc.currentScreen.width/8F*3,mc.currentScreen.height / 4F,mc.currentScreen.width / 16F * 9,mc.currentScreen.height/4F*3,mouseX.toFloat(),mouseY.toFloat())
            canSettingRoll = isClick(mc.currentScreen.width / 16F * 9,mc.currentScreen.height / 4F,mc.currentScreen.width/4F*3,mc.currentScreen.height/4F*3,mouseX.toFloat(),mouseY.toFloat())

            glEnable(GL_BLEND)
            glDisable(GL_TEXTURE_2D)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glEnable(GL_LINE_SMOOTH)
            glColor(Color(255,255,255,150))
            glLineWidth(2F)
            glBegin(GL_LINES)

            glVertex2f(mc.currentScreen.width/4F, mc.currentScreen.height/4F)
            glVertex2f(mc.currentScreen.width/4F, mc.currentScreen.height/4F - 4 + KevinClient.fontManager.fontBold180.fontHeight)

            glVertex2f(mc.currentScreen.width/4F, mc.currentScreen.height/4F)
            glVertex2f(mc.currentScreen.width/4F*3, mc.currentScreen.height/4F)

            glVertex2f(mc.currentScreen.width/4F*3,mc.currentScreen.height/4F)
            glVertex2f(mc.currentScreen.width/4F*3, mc.currentScreen.height/4F*3)

            glVertex2f(mc.currentScreen.width/8F*3,mc.currentScreen.height/4F)
            glVertex2f(mc.currentScreen.width/8F*3, mc.currentScreen.height/4F - 4 + KevinClient.fontManager.fontBold180.fontHeight)

            if (categoryOpen != 0 && categoryOpen != 8) {
                glVertex2f(mc.currentScreen.width / 16F * 9, mc.currentScreen.height / 4F)
                glVertex2f(mc.currentScreen.width / 16F * 9, mc.currentScreen.height / 4F * 3)
            }

            glVertex2f(mc.currentScreen.width/8F*3,mc.currentScreen.height/4F*3)
            glVertex2f(mc.currentScreen.width/4F*3, mc.currentScreen.height/4F*3)

            glEnd()
            glEnable(GL_TEXTURE_2D)
            glDisable(GL_BLEND)
            glDisable(GL_LINE_SMOOTH)

            var y = mc.currentScreen.height/4F - 4 + KevinClient.fontManager.fontBold180.fontHeight
            val high = ((mc.currentScreen.height/4F*3) - (mc.currentScreen.height/4F - 4 + KevinClient.fontManager.fontBold180.fontHeight)) / 8
            for (c in category){
                val fontRainbow = category.indexOf(c) + 1 == categoryOpen
                val borderRainbow = isClick(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,mouseX.toFloat(),mouseY.toFloat())
                val noHead = isClick(mc.currentScreen.width/4F,y-high,mc.currentScreen.width/8F*3,y,mouseX.toFloat(),mouseY.toFloat())
                RenderUtils.drawRect(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,Color(60,60,60).rgb)
                RainbowShader.begin(borderRainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
//                    if (c != category.first() && noHead) drawNoHeadBorder(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,2F,Color(255,255,255,150))
//                    else RenderUtils.drawBorder(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,2F,Color(255,255,255,150).rgb)
                }
                if (c != category.first() && noHead) RainbowShader.begin(true,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                    glEnable(GL_BLEND)
                    glDisable(GL_TEXTURE_2D)
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                    glEnable(GL_LINE_SMOOTH)
                    glColor(Color.white)
                    glLineWidth(2F)
                    glBegin(GL_LINES)
                    glVertex2f(mc.currentScreen.width/4F,y)
                    glVertex2f(mc.currentScreen.width/8F*3,y)
                    glEnd()
                    glEnable(GL_TEXTURE_2D)
                    glDisable(GL_BLEND)
                    glDisable(GL_LINE_SMOOTH)
                }
                RainbowFontShader.begin(fontRainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                    KevinClient.fontManager.font35.drawString(c,mc.currentScreen.width/4F+(mc.currentScreen.width/8F*3-mc.currentScreen.width/4F)/2 - KevinClient.fontManager.font35.getStringWidth(c)/2,y+high/2- KevinClient.fontManager.font35.fontHeight/3,Color(0,111,255).rgb)
                }

                y += high
            }
            val moduleY = mc.currentScreen.height/4F
            val moduleX = mc.currentScreen.width/8F*3
            when(categoryOpen){
                1 -> {
                    val start = categoryStart!!["Combat"]!!
                    drawModule(ModuleCategory.COMBAT,start,mouseX,mouseY)
                }
                2 -> {
                    val start = categoryStart!!["Exploit"]!!
                    drawModule(ModuleCategory.EXPLOIT,start,mouseX,mouseY)
                }
                3 -> {
                    val start = categoryStart!!["Misc"]!!
                    drawModule(ModuleCategory.MISC,start,mouseX,mouseY)
                }
                4 -> {
                    val start = categoryStart!!["Movement"]!!
                    drawModule(ModuleCategory.MOVEMENT,start,mouseX,mouseY)
                }
                5 -> {
                    val start = categoryStart!!["Player"]!!
                    drawModule(ModuleCategory.PLAYER,start,mouseX,mouseY)
                }
                6 -> {
                    val start = categoryStart!!["Render"]!!
                    drawModule(ModuleCategory.RENDER,start,mouseX,mouseY)
                }
                7 -> {
                    val start = categoryStart!!["World"]!!
                    drawModule(ModuleCategory.WORLD,start,mouseX,mouseY)
                }
                8 -> {
                    var setY = moduleY
                    for (s in (KevinClient.moduleManager.getModule(Targets::class.java)).values){
                        if (s !is BooleanValue) continue
                        glPushMatrix()
                        glScalef(0.8F,0.8F,0.8F)
                        KevinClient.fontManager.font40.drawString(s.name,(moduleX + 3)/0.8F,(setY + 3)/0.8F,Color(0,0,0).rgb)
                        glPopMatrix()
                        drawButton1(mc.currentScreen.width/4F*3 - 13,setY + 3.5F,s.get(),isClick(mc.currentScreen.width/4F*3 - 13,setY + 3.5F,mc.currentScreen.width/4F*3 - 4,setY + 8.5F,mouseX.toFloat(),mouseY.toFloat()),1F)
                        setY += KevinClient.fontManager.font40.fontHeight + 2
                    }
                }
            }

            super.drawScreen(mouseX, mouseY, partialTicks)
        }

        override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
            var y = mc.currentScreen.height/4F - 4 + KevinClient.fontManager.fontBold180.fontHeight
            val high = ((mc.currentScreen.height/4F*3) - (mc.currentScreen.height/4F - 4 + KevinClient.fontManager.fontBold180.fontHeight)) / 8
            for (c in category){
                if (isClick(mc.currentScreen.width/4F,y,mc.currentScreen.width/8F*3,y+high,mouseX.toFloat(),mouseY.toFloat())) {if (mouseButton == 0) {categoryOpen = category.indexOf(c)+1;mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))} else {if (categoryOpen == category.indexOf(c)+1) {categoryOpen = 0;mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 0.6114514191981f));break} else {categoryOpen = category.indexOf(c)+1;mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))}}}
                y += high
            }
            val moduleY = mc.currentScreen.height/4F
            when(categoryOpen){
                1 -> {
                    val start = categoryStart!!["Combat"]!!
                    moduleClick(ModuleCategory.COMBAT,start,mouseX,mouseY,mouseButton)
                }
                2 -> {
                    val start = categoryStart!!["Exploit"]!!
                    moduleClick(ModuleCategory.EXPLOIT,start,mouseX,mouseY,mouseButton)
                }
                3 -> {
                    val start = categoryStart!!["Misc"]!!
                    moduleClick(ModuleCategory.MISC,start,mouseX,mouseY,mouseButton)
                }
                4 -> {
                    val start = categoryStart!!["Movement"]!!
                    moduleClick(ModuleCategory.MOVEMENT,start,mouseX,mouseY,mouseButton)
                }
                5 -> {
                    val start = categoryStart!!["Player"]!!
                    moduleClick(ModuleCategory.PLAYER,start,mouseX,mouseY,mouseButton)
                }
                6 -> {
                    val start = categoryStart!!["Render"]!!
                    moduleClick(ModuleCategory.RENDER,start,mouseX,mouseY,mouseButton)
                }
                7 -> {
                    val start = categoryStart!!["World"]!!
                    moduleClick(ModuleCategory.WORLD,start,mouseX,mouseY,mouseButton)
                }
                8 -> {
                    var setY = moduleY
                    for (s in (KevinClient.moduleManager.getModule(Targets::class.java)).values){
                        if (s !is BooleanValue) continue
                        if (isClick(mc.currentScreen.width/4F*3 - 13,setY + 3.5F,mc.currentScreen.width/4F*3 - 4,setY + 8.5F,mouseX.toFloat(),mouseY.toFloat())) {s.set(!s.get())}
                        setY += KevinClient.fontManager.font40.fontHeight + 2
                    }
                }
            }
            super.mouseClicked(mouseX, mouseY, mouseButton)
        }

        private fun isClick(x:Float,y:Float,x1:Float,y1:Float,mouseX:Float,mouseY:Float):Boolean = mouseX in x..x1 && mouseY in y..y1

        private fun drawNoHeadBorder(x: Float,y: Float,x1: Float,y1: Float,width: Float,color: Color){
            glEnable(GL_BLEND)
            glDisable(GL_TEXTURE_2D)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glEnable(GL_LINE_SMOOTH)
            glColor(color)
            glLineWidth(width)
            glBegin(GL_LINES)

            glVertex2f(x, y)
            glVertex2f(x, y1)

            glVertex2f(x, y1)
            glVertex2f(x1, y1)

            glVertex2f(x1, y)
            glVertex2f(x1, y1)
            glEnd()
            glEnable(GL_TEXTURE_2D)
            glDisable(GL_BLEND)
            glDisable(GL_LINE_SMOOTH)
        }
        private fun drawButton1(x: Float,y: Float,enable: Boolean,rainbow: Boolean,scale: Float){
            val x1 = (if (enable) x + 5 else x + 1)/scale
            val y1 = (y + 1)/scale
            glPushMatrix()
            glScalef(scale,scale,scale)
            RenderUtils.drawRect(x1,y1,x1+3/scale,y1+3/scale,if (enable) Color.green.rgb else Color.red.rgb)
            RainbowShader.begin(rainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                RenderUtils.drawBorder(x/scale,y/scale,x+9/scale,y1+4/scale,1F,Color(255,255,255).rgb)
            }
            glPopMatrix()
        }
        private fun drawModule(category: ModuleCategory,start: Int,mouseX: Int,mouseY: Int){
            var jumpOver = 0
            var y = mc.currentScreen.height / 4F
            val moduleHigh = mc.currentScreen.height/16F
            for (m in KevinClient.moduleManager.getModules()){
                if (y > mc.currentScreen.height / 4F+moduleHigh*7)break
                if (m.category != category) continue
                if (jumpOver != start){jumpOver+=1;continue}
                val rainbow = isClick(mc.currentScreen.width/8F*3,y,mc.currentScreen.width / 16F * 9,y+moduleHigh,mouseX.toFloat(),mouseY.toFloat())
                val noHead = y != mc.currentScreen.height / 4F && isClick(mc.currentScreen.width/8F*3,y-moduleHigh,mc.currentScreen.width / 16F * 9,y,mouseX.toFloat(),mouseY.toFloat())
                RainbowShader.begin(rainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                    if (noHead) drawNoHeadBorder(mc.currentScreen.width/8F*3,y,mc.currentScreen.width / 16F * 9,y+moduleHigh,2F,Color(255,255,255,150))
                    else RenderUtils.drawBorder(mc.currentScreen.width/8F*3,y,mc.currentScreen.width / 16F * 9,y+moduleHigh,2F,Color(255,255,255,150).rgb)
                }
                val fontRainbow = ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt() == when(category){
                    ModuleCategory.COMBAT -> {moduleOpen!!["Combat"]!!}
                    ModuleCategory.EXPLOIT -> {moduleOpen!!["Exploit"]!!}
                    ModuleCategory.MISC -> {moduleOpen!!["Misc"]!!}
                    ModuleCategory.MOVEMENT -> {moduleOpen!!["Movement"]!!}
                    ModuleCategory.PLAYER -> {moduleOpen!!["Player"]!!}
                    ModuleCategory.RENDER -> {moduleOpen!!["Render"]!!}
                    ModuleCategory.WORLD -> {moduleOpen!!["World"]!!}
                }
                RainbowFontShader.begin(fontRainbow,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                    KevinClient.fontManager.font35.drawString(
                        m.name,
                        mc.currentScreen.width / 8F * 3 + 5,
                        y - KevinClient.fontManager.font35.fontHeight / 3 + moduleHigh / 2,
                        Color(0, 111, 255).rgb
                    )
                }
                val moduleCategoryString = when(category){
                    ModuleCategory.COMBAT -> "Combat"
                    ModuleCategory.EXPLOIT -> "Exploit"
                    ModuleCategory.MISC -> "Misc"
                    ModuleCategory.MOVEMENT -> "Movement"
                    ModuleCategory.PLAYER -> "Player"
                    ModuleCategory.RENDER -> "Render"
                    ModuleCategory.WORLD -> "World"
                }
                if (fontRainbow) drawSettings(m,settingStart!![moduleCategoryString]!!,mouseX,mouseY)
                drawButton1(mc.currentScreen.width / 16F * 9 -13,y+moduleHigh/2-2.5F,m.state,isClick(mc.currentScreen.width / 16F * 9 -13,y+moduleHigh/2-2.5F,mc.currentScreen.width / 16F * 9 - 4,y+moduleHigh/2-2.5F + 5,mouseX.toFloat(),mouseY.toFloat()),1F)
                if (rainbow){glPushMatrix();drawHoveringText(listOf(m.description),mouseX,mouseY);RenderHelper.disableStandardItemLighting();glPopMatrix()}
                y += moduleHigh
            }
        }
        private fun moduleClick(category: ModuleCategory,start: Int,mouseX: Int,mouseY: Int,mouseButton: Int){
            var jumpOver = 0
            var y = mc.currentScreen.height / 4F
            val moduleHigh = mc.currentScreen.height/16F
            for (m in KevinClient.moduleManager.getModules()){
                if (y > mc.currentScreen.height / 4F+moduleHigh*7)break
                if (m.category != category) continue
                if (jumpOver != start){jumpOver+=1;continue}
                if (isClick(mc.currentScreen.width / 16F * 9 -13,y+moduleHigh/2-2.5F,mc.currentScreen.width / 16F * 9 - 4,y+moduleHigh/2-2.5F + 5,mouseX.toFloat(),mouseY.toFloat())){ m.toggle() }
                if (isClick(mc.currentScreen.width/8F*3,y,mc.currentScreen.width / 16F * 9,y+moduleHigh,mouseX.toFloat(),mouseY.toFloat())&&!isClick(mc.currentScreen.width / 16F * 9 -13,y+moduleHigh/2-2.5F,mc.currentScreen.width / 16F * 9 - 4,y+moduleHigh/2-2.5F + 5,mouseX.toFloat(),mouseY.toFloat())){
                    when(category){
                        ModuleCategory.COMBAT -> setModuleOpen(y,moduleHigh,"Combat",mouseButton)
                        ModuleCategory.EXPLOIT -> setModuleOpen(y,moduleHigh,"Exploit",mouseButton)
                        ModuleCategory.MISC -> setModuleOpen(y,moduleHigh,"Misc",mouseButton)
                        ModuleCategory.MOVEMENT -> setModuleOpen(y,moduleHigh,"Movement",mouseButton)
                        ModuleCategory.PLAYER -> setModuleOpen(y,moduleHigh,"Player",mouseButton)
                        ModuleCategory.RENDER -> setModuleOpen(y,moduleHigh,"Render",mouseButton)
                        ModuleCategory.WORLD -> setModuleOpen(y,moduleHigh,"World",mouseButton)
                    }
                }
                val choose = ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt() == when(category){
                    ModuleCategory.COMBAT -> {moduleOpen!!["Combat"]!!}
                    ModuleCategory.EXPLOIT -> {moduleOpen!!["Exploit"]!!}
                    ModuleCategory.MISC -> {moduleOpen!!["Misc"]!!}
                    ModuleCategory.MOVEMENT -> {moduleOpen!!["Movement"]!!}
                    ModuleCategory.PLAYER -> {moduleOpen!!["Player"]!!}
                    ModuleCategory.RENDER -> {moduleOpen!!["Render"]!!}
                    ModuleCategory.WORLD -> {moduleOpen!!["World"]!!}
                }
                val moduleCategoryString = when(category){
                    ModuleCategory.COMBAT -> "Combat"
                    ModuleCategory.EXPLOIT -> "Exploit"
                    ModuleCategory.MISC -> "Misc"
                    ModuleCategory.MOVEMENT -> "Movement"
                    ModuleCategory.PLAYER -> "Player"
                    ModuleCategory.RENDER -> "Render"
                    ModuleCategory.WORLD -> "World"
                }
                if (choose) settingsClick(m,settingStart!![moduleCategoryString]!!,mouseX,mouseY)
                y += moduleHigh
            }
        }
        private fun setModuleOpen(y: Float,moduleHigh: Float,moduleCategory: String,mouseButton: Int){
            if (mouseButton == 0) {moduleOpen!![moduleCategory] = ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt();mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f));settingStart!![moduleCategory] = 0} else {
                if (moduleOpen!![moduleCategory] == ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt()) {moduleOpen!![moduleCategory] = -114514;mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 0.6114514191981f));settingStart!![moduleCategory] = 0} else {
                    moduleOpen!![moduleCategory] = ((y - mc.currentScreen.height / 4F) / moduleHigh).toInt()
                    settingStart!![moduleCategory] = 0
                    mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))
                }
            }
        }
        override fun handleMouseInput() {
            super.handleMouseInput()
            if (categoryOpen == 0 || categoryOpen == 8 || (!canModuleRoll && !canSettingRoll)) return
            val moduleCategoryString = when(categoryOpen){
                1 -> "Combat"
                2 -> "Exploit"
                3 -> "Misc"
                4 -> "Movement"
                5 -> "Player"
                6 -> "Render"
                7 -> "World"
                else -> ""
            }
            val moduleCategory = when(categoryOpen){
                1 -> ModuleCategory.COMBAT
                2 -> ModuleCategory.EXPLOIT
                3 -> ModuleCategory.MISC
                4 -> ModuleCategory.MOVEMENT
                5 -> ModuleCategory.PLAYER
                6 -> ModuleCategory.RENDER
                7 -> ModuleCategory.WORLD
                else -> ModuleCategory.MISC
            }
            var modulesNumber = 0
            for (m in KevinClient.moduleManager.getModules()){
                if (m.category == moduleCategory) modulesNumber += 1
            }
            when{
                Mouse.getEventDWheel() > 0 ->{
                    if (canModuleRoll) {
                        if (categoryStart!![moduleCategoryString]!! != 0) {
                            categoryStart!![moduleCategoryString] = categoryStart!![moduleCategoryString]!! - 1
                            moduleOpen!![moduleCategoryString] = moduleOpen!![moduleCategoryString]!! + 1
                        }
                    }
                    if (canSettingRoll && settingStart!![moduleCategoryString]!!>=5){
                        settingStart!![moduleCategoryString] = settingStart!![moduleCategoryString]!! - 5
                    }
                }
                Mouse.getEventDWheel() < 0 ->{
                    if (canModuleRoll) {
                        if (categoryStart!![moduleCategoryString]!! + 8 < modulesNumber) {
                            categoryStart!![moduleCategoryString] = categoryStart!![moduleCategoryString]!! + 1
                            moduleOpen!![moduleCategoryString] = moduleOpen!![moduleCategoryString]!! - 1
                        }
                    }
                    if (canSettingRoll && canSettingRoll2){
                        settingStart!![moduleCategoryString] = settingStart!![moduleCategoryString]!! + 5
                    }
                }
            }
        }
        private fun drawSettings(module: Module,start: Int,mouseX: Int,mouseY: Int){
            val values = module.values
            var y = mc.currentScreen.height/4F + 5 - start
            canSettingRoll2 = false
            for (v in values){
                when(v){
                    is BooleanValue ->{
                        if (y + KevinClient.fontManager.font35.fontHeight >mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        KevinClient.fontManager.font35.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        glPopMatrix()
                        drawButton1(mc.currentScreen.width / 4F * 3 - 13,y,v.get(),isClick(mc.currentScreen.width / 4F * 3 - 13,y,mc.currentScreen.width / 4F * 3 - 4,y + 5,mouseX.toFloat(),mouseY.toFloat()),1F)
                        y += KevinClient.fontManager.font35.fontHeight
                    }
                    is ListValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight * 0.6F > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 0.6F;continue}
                        var x = 0
                        val maxWidth = (mc.currentScreen.width / 4F * 3) - (mc.currentScreen.width / 16F * 9 + 5)
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        KevinClient.fontManager.font35.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        y += KevinClient.fontManager.font35.fontHeight * 0.6F
                        for (i in v.values){
                            if (y + KevinClient.fontManager.font35.fontHeight * 0.6F > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                            if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 0.6F;continue}
                            if (x + KevinClient.fontManager.font35.getStringWidth(i) > maxWidth) {y += KevinClient.fontManager.font35.fontHeight * 0.6F;x = 0}
                            val color = if (v.get() == i) Color(0,111,255).rgb else Color(150,150,150).rgb
                            KevinClient.fontManager.font35.drawString(i,(mc.currentScreen.width / 16F * 9 + 5 + x)/0.6F,y/0.6F,color)
                            x += KevinClient.fontManager.font35.getStringWidth(i)
                        }
                        y += KevinClient.fontManager.font35.fontHeight
                        glPopMatrix()
                    }
                    is TextValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight * 1.6F > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 1.6F;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        KevinClient.fontManager.font35.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        y += KevinClient.fontManager.font35.fontHeight * 0.6F
                        KevinClient.fontManager.font35.drawString(v.get(),(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,111,255).rgb)
                        glPopMatrix()
                        y += KevinClient.fontManager.font35.fontHeight
                    }
                    is IntegerValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight * 0.8F + 8 > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 0.8F + 8;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        KevinClient.fontManager.font35.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        KevinClient.fontManager.font35.drawString(v.get().toString(),(mc.currentScreen.width / 4F * 3 - KevinClient.fontManager.font35.getStringWidth(v.get().toString())/2 - 10)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        y += KevinClient.fontManager.font35.fontHeight*0.8F
                        glPopMatrix()
                        drawButton2(mc.currentScreen.width / 16F * 9 + 5,y,mc.currentScreen.width / 4F * 3 - 5,v.minimum.toFloat(),v.maximum.toFloat(),v.get().toFloat())
                        if (isClick(mc.currentScreen.width / 16F * 9 + 5,y - 3,mc.currentScreen.width / 4F * 3 - 5,y + 3,mouseX.toFloat(),mouseY.toFloat())){
                            if (Mouse.isButtonDown(0)){
                                val i = MathHelper.clamp_double((mouseX - (mc.currentScreen.width / 16F * 9 + 5)) / ((mc.currentScreen.width / 4F * 3 - 5) - (mc.currentScreen.width / 16F * 9 + 5)).toDouble(),0.0,1.0)
                                v.set((v.minimum + (v.maximum - v.minimum) * i).toInt())
                            }
                        }
                        y += 8
                    }
                    is FloatValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight * 0.8F + 8 > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 0.8F + 8;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        KevinClient.fontManager.font35.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        KevinClient.fontManager.font35.drawString(v.get().toString(),(mc.currentScreen.width / 4F * 3 - KevinClient.fontManager.font35.getStringWidth(v.get().toString())/2 - 10)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        y += KevinClient.fontManager.font35.fontHeight*0.8F
                        glPopMatrix()
                        drawButton2(mc.currentScreen.width / 16F * 9 + 5,y,mc.currentScreen.width / 4F * 3 - 5,v.minimum,v.maximum,v.get())
                        if (isClick(mc.currentScreen.width / 16F * 9 + 5,y - 3,mc.currentScreen.width / 4F * 3 - 5,y + 3,mouseX.toFloat(),mouseY.toFloat())){
                            if (Mouse.isButtonDown(0)){
                                val i = MathHelper.clamp_double((mouseX - (mc.currentScreen.width / 16F * 9 + 5)) / ((mc.currentScreen.width / 4F * 3 - 5) - (mc.currentScreen.width / 16F * 9 + 5)).toDouble(),0.0,1.0)
                                v.set(Formatter().format("%.2f",v.minimum + (v.maximum - v.minimum) * i).toString().toFloat())
                            }
                        }
                        y += 8
                    }
                    is BlockValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight > mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight;continue}
                        glPushMatrix()
                        glScaled(0.6,0.6,0.6)
                        KevinClient.fontManager.font35.drawString(v.name,(mc.currentScreen.width / 16F * 9 + 5)/0.6F,y/0.6F,Color(0,0,0).rgb)
                        KevinClient.fontManager.font35.drawString(BlockUtils.getBlockName(v.get()),(mc.currentScreen.width / 4F * 3 - 5 - KevinClient.fontManager.font35.getStringWidth(BlockUtils.getBlockName(v.get())))/0.6F,y/0.6F,Color(60,60,60).rgb)
                        glPopMatrix()
                        y += KevinClient.fontManager.font35.fontHeight
                    }
                }
                if (y>mc.currentScreen.height/4F*3) {canSettingRoll2 = true;break}
            }
        }
        private fun drawButton2(x: Float,y: Float,x1: Float,minValue: Float,maxValue: Float,value: Float){
            glEnable(GL_BLEND)
            glDisable(GL_TEXTURE_2D)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glEnable(GL_LINE_SMOOTH)
            glColor(Color(0,111,255))
            glLineWidth(3F)
            glBegin(GL_LINES)
            glVertex2f(x, y)
            glVertex2f(x1, y)
            glEnd()
            glEnable(GL_TEXTURE_2D)
            glDisable(GL_BLEND)
            glDisable(GL_LINE_SMOOTH)
            val d = x1 - x
            val dv = (value - minValue)/(maxValue - minValue)
            val dl = d * dv
            RenderUtils.drawRect( x + dl - 1,y + 3,x + dl + 1,y - 3,Color(0,111,255).rgb)
        }
        private fun settingsClick(module: Module,start: Int,mouseX: Int,mouseY: Int){
            val values = module.values
            var y = mc.currentScreen.height/4F + 5 - start
            for (v in values){
                when(v){
                    is BooleanValue ->{
                        if (y + KevinClient.fontManager.font35.fontHeight >mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight;continue}
                        if (isClick(mc.currentScreen.width / 4F * 3 - 13,y,mc.currentScreen.width / 4F * 3 - 4,y + 5,mouseX.toFloat(),mouseY.toFloat())) {v.set(!v.get());if(v.get())mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))else mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 0.6114514191981f))}
                        y += KevinClient.fontManager.font35.fontHeight
                    }

                    is ListValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight * 0.6F > mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 0.6F;continue}
                        var x = 0
                        val maxWidth = (mc.currentScreen.width / 4F * 3) - (mc.currentScreen.width / 16F * 9 + 5)
                        y += KevinClient.fontManager.font35.fontHeight * 0.6F
                        for (i in v.values){
                            if (y + KevinClient.fontManager.font35.fontHeight * 0.6F > mc.currentScreen.height/4F*3)break
                            if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 0.6F;continue}
                            if (x + KevinClient.fontManager.font35.getStringWidth(i) > maxWidth) {y += KevinClient.fontManager.font35.fontHeight * 0.6F;x = 0}
                            val click = isClick(mc.currentScreen.width/16F*9+5+x,y,mc.currentScreen.width/16F*9+5+x+ KevinClient.fontManager.font35.getStringWidth(i),y+ KevinClient.fontManager.font35.fontHeight*0.6F,mouseX.toFloat(),mouseY.toFloat())
                            if (click) {v.set(i);mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1f))}
                            x += KevinClient.fontManager.font35.getStringWidth(i)
                        }
                        y += KevinClient.fontManager.font35.fontHeight
                    }
                    is TextValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight * 1.6F > mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 1.6F;continue}
                        y += KevinClient.fontManager.font35.fontHeight * 0.6F
                        y += KevinClient.fontManager.font35.fontHeight
                    }
                    is IntegerValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight * 0.8F + 8 > mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 0.8F + 8;continue}
                        y += KevinClient.fontManager.font35.fontHeight*0.8F
                        y += 8
                    }
                    is FloatValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight * 0.8F + 8 > mc.currentScreen.height/4F*3) break
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight * 0.8F + 8;continue}
                        y += KevinClient.fontManager.font35.fontHeight*0.8F
                        y += 8
                    }
                    is BlockValue -> {
                        if (y + KevinClient.fontManager.font35.fontHeight > mc.currentScreen.height/4F*3)break
                        if (y < mc.currentScreen.height/4F + 5) {y += KevinClient.fontManager.font35.fontHeight;continue}
                        y += KevinClient.fontManager.font35.fontHeight
                    }
                }
                if (y>mc.currentScreen.height/4F*3) break
            }
        }
    }

    //New ClickGui!!
    class NewClickGui : kevin.hud.ClickGui(){
        private val buttonsAnim = HashMap<String,Float>()
        private val lineAnim = HashMap<String,Float>()
        private enum class Category(name: String) {
            Combat("Combat"),
            Exploit("Exploit"),
            Misc("Misc"),
            Movement("Movement"),
            Player("Player"),
            Render("Render"),
            World("World"),
            Target("Target"),
            Gui("Gui")
        }
        private val categoryButtons = arrayListOf<Button>()
        private var mode = 1
        private var clickButton:Category? = null
        private var lastTickMouseDown:Int? = null
        private var firstClick:Category? = null
        private val cateY = HashMap<ModuleCategory,Int>()
        private val cateMaxY = HashMap<ModuleCategory,Int>()
        private val clickModule = HashMap<ModuleCategory,Int>()
        private val x1: Int by lazy {
            mc.currentScreen.width/4*1
        }
        private val y1: Int by lazy {
            mc.currentScreen.height/4*1
        }
        private val x2: Int by lazy {
            mc.currentScreen.width/4*3
        }
        private val y2: Int by lazy {
            mc.currentScreen.height/4*3
        }
        private val radius: Double by lazy {
            (x2-x1)/48.0
        }
        private var canR = false
        private var canSettingsR = false
        private var canSettingsR2 = false
        private var lastCategory:Category? = null
        private val moduleY = HashMap<ModuleCategory,Float>()
        private var lastTickClickValue:String? = null
        private val listOpen = HashMap<String,Boolean>()
        private val listAnim = HashMap<String,Float>()
        private var lastModule = HashMap<ModuleCategory,Int>()
        init {
            buttonsAnim["ModeButton"] = if(mode==1) 100F else 0F
            lineAnim["Line1"] = 0F
            lineAnim["Line2"] = 0F
            lineAnim["Line3"] = 0F
            lineAnim["LineTarget"] = 0F
            lineAnim["LineSettings"] = 0F
            ModuleCategory.values().forEach {
                cateY[it] = 0
                cateMaxY[it] = 0
                clickModule[it] = -1
                moduleY[it] = 0F
                lastModule[it] = -1
            }
            KevinClient.moduleManager.getModules().forEach {
                if(!(it is Targets||it is ClickGui||it is CapeManager||it is HudDesigner))
                    cateMaxY[it.category] = cateMaxY[it.category]!! + 1
            }
            ModuleCategory.values().forEach{
                cateMaxY[it] = if (cateMaxY[it]!!-8<=0) 0 else cateMaxY[it]!! - 8
            }
        }
        override fun initGui() {
            var yO = 1.5F
            val yS = (y2 - (y1+radius*2.25+2F))/9F - 1F
            values().forEach {
                categoryButtons.add(Button(it.name,x1+1.5F,y1+radius.toFloat()*2.25F+yO,x1+(x2-x1)/16*3F-2F,y1+radius.toFloat()*2.25F+yO+yS.toFloat(),Color.white,Color.black,it.name,Button.TextPos.Middle))
                yO += yS.toFloat() + 1F
            }
            super.initGui()
        }
        override fun doesGuiPauseGame() = false
        override fun onGuiClosed() {
            lineAnim["Line1"] = 0F
            lineAnim["Line2"] = 0F
            lineAnim["Line3"] = 0F
            lineAnim["LineTarget"] = 0F
            lineAnim["LineSettings"] = 0F
            categoryButtons.clear()
            super.onGuiClosed()
        }
        override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
            //DrawBackGround
            RenderUtils.drawRect(x1.toDouble(),y1.toDouble(),x2.toDouble(),y2.toDouble(),Color(49,49,49,225).rgb)
            //DrawCloseButton
            val closeButtonLight = isClick(mouseX.toFloat(),mouseY.toFloat(),x2-radius.toFloat()/4F*3F-(x2-x1)/128F,y1+radius.toFloat()/4F*3F-(x2-x1)/128F,x2-radius.toFloat()/4F*3F+(x2-x1)/128F,y1+radius.toFloat()/4F*3F+(x2-x1)/128F)
            RenderUtils.drawSector(x2-radius/4*3,y1+radius/4*3,0,360,(x2-x1)/128.0,if(closeButtonLight)Color(255,0,0,250)else Color(225,0,0))
            glPushMatrix()
            glScaled(0.5,0.5,0.5)
            KevinClient.fontManager.font35.drawString("x",(x2-radius.toFloat()/16F*13.75F)/0.5F,(y1+radius.toFloat()/4F*1.9F)/0.5F,Color.white.rgb)
            glPopMatrix()
            //DrawLine
//            RenderUtils.drawLineStart(if(mode==1)Color(255,255,255)else Color(20,20,20,225),5F)
            val lx2 = (x2 - x1) * lineAnim["Line1"]!! / 100.0
//            RenderUtils.drawLine(x1.toDouble(),y1+radius*2.25,lx2+x1,y1+radius*2.25)
//            RenderUtils.drawLineEnd()
//            RenderUtils.drawLineStart(if(mode==1)Color(255,255,255)else Color(20,20,20,225),3F)
            val l2y2 = (y2-(y1+radius*2.25)) * lineAnim["Line2"]!! / 100.0
//            RenderUtils.drawLine(x1+(x2-x1)/16*3.0,y1+radius*2.25,x1+(x2-x1)/16*3.0,y1+radius*2.25+l2y2)
//            RenderUtils.drawLineEnd()
//            //lineAnim
            animLine("Line1",1.5F,false)
            if (lineAnim["Line1"]!!>18.75F) animLine("Line2",1.5F,false)
            if (lineAnim["Line1"]!!>18.75F&&clickButton==Target) animLine("LineTarget",1.5F,false) else if (lineAnim["LineTarget"]!!>80) lineAnim["LineTarget"] = 80F else animLine("LineTarget",1.5F,true)
            //DrawTitle
            RainbowFontShader.begin(true,-0.00314514F,0.00314514F,System.currentTimeMillis() % 10000 / 10000F).use {
                KevinClient.fontManager.font35.drawString("Kevin",x1+10F,y1+6.5F,0)
            }
            //DrawModeButton
            val modeButtonLight = isClick(mouseX.toFloat(),mouseY.toFloat(),x2-32F,y1+9.5F,x2-20F,y1+7.25F+ KevinClient.fontManager.font35.fontHeight*0.6F)
            glPushMatrix()
            glScaled(.6,.6,.6)
//            KevinClient.fontManager.font35.drawString("D",(x2-35F)/.6F,(y1+9.5F)/.6F,if(mode==1)Color(255,255,255).rgb else Color(20,20,20,225).rgb)
//            KevinClient.fontManager.font35.drawString("L",(x2-19F)/.6F,(y1+9.5F)/.6F,if(mode==1)Color(255,255,255).rgb else Color(20,20,20,225).rgb)
            glPopMatrix()
            animButton("ModeButton",10F,mode==1)
            mode=0;
//            drawButton1(x2-32F,y1+9.5F,x2-20F,y1+7.25F+ KevinClient.fontManager.font35.fontHeight*0.6F,if (mode==1) if(modeButtonLight) Color(170,170,170) else Color.white else if(modeButtonLight) Color.darkGray else Color.black,Color(0,111,255),buttonsAnim["ModeButton"]!!)
            //DrawCategoryButtons
            val mouseButtonDown = Mouse.isButtonDown(0) || Mouse.isButtonDown(1)
            categoryButtons.forEach {
                val linePos = y1+radius*2.25+l2y2
                val alpha = when{
                    linePos in it.y1..it.y2 -> {
                        val l = linePos - it.y1
                        val bl = it.y2 - it.y1
                        ((l/bl)*255).toInt()
                    }
                    linePos>it.y2 -> 255
                    else -> 0
                }
                val white = Color(255,255,255,alpha)
                val black = Color(0,0,0,alpha)
                val lightGray = Color(192,192,192,alpha)
                val darkGray = Color(64,64,64,alpha)
                val blue = Color(0,0,0,50)
                val isClick = it.isClick(mouseX,mouseY)
                it.color = if (clickButton?.name==it.name) blue else
                    if(isClick) Color(170,170,170,0)
                else
                    if(isClick) lightGray else darkGray
                it.fontColor = if(clickButton?.name==it.name) white else black
                val xa1 = it.initX1+(it.initX2-it.initX1)/16
                val xsp = (it.initX2-it.initX1)/48
                val ysp = (it.initY2-it.initY1)/48
                if (firstClick?.name==it.name||(firstClick==null&&isClick&&mouseButtonDown)){
                    if (firstClick==null) firstClick= valueOf(it.name)
                    if (it.x1<xa1){
                        it.x1 += xsp
                        it.y1 += ysp
                        it.x2 -= xsp
                        it.y2 -= ysp
                    }
                } else {
                    if (it.x1>it.initX1){
                        it.x1 -= xsp
                        it.y1 -= ysp
                        it.x2 += xsp
                        it.y2 += ysp
                    }
                }
                if (alpha!=0) it.drawButton()
            }
            //DrawModulesLine
//            RenderUtils.drawLineStart(if(isLight())Color(255,255,255)else Color(20,20,20,225),3F)
//            val l3y2 = (y2-(y1+radius*2.25)) * lineAnim["Line3"]!! / 100.0
//            RenderUtils.drawLine(x1+(x2-x1)/2.0,y1+radius*2.25,x1+(x2-x1)/2.0,y1+radius*2.25+l3y2)
//            RenderUtils.drawLineEnd()
            if (lineAnim["Line1"]!!>=50F) if (clickButton!=null&&clickButton!=Target) animLine("Line3",1.6F,false)
            else if ((clickButton==null||clickButton==Target)&&lineAnim["Line3"]!!!=0F) animLine("Line3",1.6F,true)
            //CanRoll
            canR = isClick(mouseX.toFloat(),mouseY.toFloat(),x1+(x2-x1)/16*3.0F,y1+radius.toFloat()*2.25F,x1+(x2-x1)/2.0F,y2.toFloat())
            canSettingsR = isClick(mouseX.toFloat(),mouseY.toFloat(),x1+(x2-x1)/2.0F,y1+radius.toFloat()*2.25F,x2.toFloat(),y2.toFloat())
            //DrawModules
            val c = if (lastCategory!=null&&clickButton==null&&((lastCategory==Target&&lineAnim["LineTarget"]!!!=0F)||lineAnim["Line3"]!!!=0F)) lastCategory else clickButton
            drawModules(c,mouseX,mouseY,false,0)
            //DrawSettings
            drawSettings(c,mouseX,mouseY,false)
            //SettingsAnimLine
            if (lineAnim["Line1"]!!>=50F) {
                if (clickButton!=null&&clickButton!=Target&&clickButton!=Gui){
                    var category:ModuleCategory? = null
                    ModuleCategory.values().forEach {
                        if (it.name.lowercase(Locale.getDefault()) == clickButton!!.name.lowercase(Locale.getDefault())) {
                            category = it
                            return@forEach
                        }
                    }
                    if (clickModule[category]!!!=-1){
                        animLine("LineSettings",1.6F,false)
                    }else{
                        animLine("LineSettings",1.6F,true)
                    }
                }else if (clickButton==null&&lastCategory!=null&&lastCategory!=Target&&lastCategory!=Gui){
                    if (lineAnim["Line3"]!!<=lineAnim["LineSettings"]!!) lineAnim["LineSettings"] = lineAnim["Line3"]!!
                    else animLine("LineSettings",1.6F,true)
                } else if (clickButton!=null&&(clickButton==Target||clickButton==Gui)) animLine("LineSettings",1.6F,true)
                else if (clickButton==null&&lastCategory!=null&&(lastCategory==Target||lastCategory==Gui)) animLine("LineSettings",1.6F,true)
                else if (clickButton==null) animLine("LineSettings",1.6F,true)
            }

            if (clickButton != null) lastCategory = clickButton
            val cl = if (lastCategory==Target) lineAnim["LineTarget"]!! ==0F else lineAnim["Line3"]!! ==0F
            if (clickButton == null&&lastCategory != null&&cl) lastCategory = null
            if(!mouseButtonDown) lastTickMouseDown = null
            if(!mouseButtonDown) firstClick = null
            if(!mouseButtonDown) lastTickClickValue = null
            if (lineAnim["LineSettings"]!! == 0F) ModuleCategory.values().forEach {
                lastModule[it] = -1
            }
            super.drawScreen(mouseX, mouseY, partialTicks)
        }
        private fun isClick(mouseX: Float,mouseY: Float,x1: Float,y1: Float,x2: Float,y2: Float) : Boolean = mouseX in x1..x2 && mouseY in y1..y2
        override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
            //CloseButton
            if (isClick(mouseX.toFloat(),mouseY.toFloat(),x2-radius.toFloat()/4F*3F-(x2-x1)/128F,y1+radius.toFloat()/4F*3F-(x2-x1)/128F,x2-radius.toFloat()/4F*3F+(x2-x1)/128F,y1+radius.toFloat()/4F*3F+(x2-x1)/128F)) mc.displayGuiScreen(null)
            //ModeButton
//            if (isClick(mouseX.toFloat(),mouseY.toFloat(),x2-32F,y1+9.5F,x2-20F,y1+7.25F+ KevinClient.fontManager.font35.fontHeight*0.6F)) mode = if (mode==1) 2 else 1
            //CategoryButtons
            categoryButtons.forEach {
                if (it.isClick(mouseX,mouseY)) {
                    clickButton = if (mouseButton==0||clickButton?.name!=it.name) valueOf(it.name) else null
                    return@forEach
                }
            }
            //Modules
            drawModules(clickButton,mouseX,mouseY,true,mouseButton)
            //Settings
            drawSettings(clickButton,mouseX,mouseY,true)
            super.mouseClicked(mouseX, mouseY, mouseButton)
        }
        private fun drawButton1(x1: Float,y1: Float,x2: Float,y2: Float,color: Color,color2: Color,anim: Float){
            val r = (y2-y1)/2.0
            val x = x1 + r
            val y = y1 + r
            RenderUtils.drawSector(x,y,90,270,r,color)
            val x22 = x2 - r
            RenderUtils.drawSector(x22,y,90,-90,r,color)
            RenderUtils.drawRect(x.toFloat(),y1,x22.toFloat(),y2,color)
            RenderUtils.drawSector(x+(x22-x)*(anim/100F),y,0,360,r-.8,color2)
        }
        private fun animLine(lineName: String,animSpeed: Float,oppositeDirection: Boolean){
            if (lineAnim[lineName]==null) lineAnim[lineName] = 0F
            if (oppositeDirection) {
                if (lineAnim[lineName]!!!=0F){
                    if (lineAnim[lineName]!!>0F) lineAnim[lineName] = lineAnim[lineName]!! - animSpeed
                    if (lineAnim[lineName]!!<0) lineAnim[lineName] = 0F
                }
            } else if (lineAnim[lineName]!!!=100F){
                if (lineAnim[lineName]!!<100) lineAnim[lineName] = lineAnim[lineName]!! + animSpeed
                if (lineAnim[lineName]!! > 100) lineAnim[lineName] = 100F
            }
        }
        private fun animButton(buttonName: String,animSpeed: Float,buttonState: Boolean){
            if (buttonsAnim[buttonName]==null) buttonsAnim[buttonName] = if(buttonState) 100F else 0F
            if(buttonState){
                if (buttonsAnim[buttonName]!!<100) buttonsAnim[buttonName] = buttonsAnim[buttonName]!! + animSpeed
                if (buttonsAnim[buttonName]!!>100) buttonsAnim[buttonName] = 100F
            } else {
                if (buttonsAnim[buttonName]!!>0) buttonsAnim[buttonName] = buttonsAnim[buttonName]!! - animSpeed
                if (buttonsAnim[buttonName]!!<0) buttonsAnim[buttonName] = 0F
            }
        }
        private fun isLight():Boolean = mode == 1
        private class Button(val name: String,var x1: Float,var y1: Float,var x2: Float,var y2: Float,var color: Color,var fontColor: Color,private val text: String,private val textPos: TextPos) {
            val initX1 = x1
            val initY1 = y1
            val initX2 = x2
            val initY2 = y2
            fun isClick(mouseX: Int,mouseY: Int):Boolean = mouseX.toFloat() in initX1..initX2 && mouseY.toFloat() in initY1..initY2
            fun drawButtonNoText(){
                RenderUtils.drawRect(x1.toDouble(),y1.toDouble(),x2.toDouble(),y2.toDouble(),color.rgb)
            }
            fun drawButton(){
                drawButtonNoText()
                val fontHigh = KevinClient.fontManager.font35.fontHeight
                val x = ((x2-x1)- KevinClient.fontManager.font35.getStringWidth(text)*0.8f)/2F
                glPushMatrix()
                glScaled(0.8,0.8,0.8)
                if (textPos==TextPos.Middle) KevinClient.fontManager.font35.drawString(text,(x1+x)/0.8f,(y1+(y2-y1)/2F-fontHigh/4F)/0.8f,fontColor.rgb)
                if (textPos==TextPos.Left) KevinClient.fontManager.font35.drawString(text,(x1+5)/0.8f,(y1+(y2-y1)/2F-fontHigh/4F)/0.8f,fontColor.rgb)
                glPopMatrix()
            }
            enum class TextPos{
                Left,Middle
            }
        }
        private fun drawModules(category: Category?,mouseX: Int,mouseY: Int,click: Boolean,mouseButton: Int){
            if(category==null) return
            val startX = x1+(x2-x1)/16*3.0+2.0
            val endX = x1+(x2-x1)/2.0-2.0
            val yS = (y2 - (y1+radius*2.25+2F))/8F - 1F
            when(category){
                Combat -> {
                    drawModules(ModuleCategory.COMBAT,mouseX,mouseY,mouseButton,click,yS,startX,endX)
                }
                Exploit -> {
                    drawModules(ModuleCategory.EXPLOIT,mouseX,mouseY,mouseButton,click,yS,startX,endX)
                }
                Misc -> {
                    drawModules(ModuleCategory.MISC,mouseX,mouseY,mouseButton,click,yS,startX,endX)
                }
                Movement -> {
                    drawModules(ModuleCategory.MOVEMENT,mouseX,mouseY,mouseButton,click,yS,startX,endX)
                }
                Player -> {
                    drawModules(ModuleCategory.PLAYER,mouseX,mouseY,mouseButton,click,yS,startX,endX)
                }
                Render -> {
                    drawModules(ModuleCategory.RENDER,mouseX,mouseY,mouseButton,click,yS,startX,endX)
                }
                World -> {
                    drawModules(ModuleCategory.WORLD,mouseX,mouseY,mouseButton,click,yS,startX,endX)
                }
                Target -> {
                    val targets = KevinClient.moduleManager.getModule(Targets::class.java)
                    val buttonList = arrayListOf<Button>()
                    var yO = 1.5F
                    targets.values.forEach{
                        if (it is BooleanValue){
                            buttonList.add(Button(it.name,startX.toFloat(),y1+radius.toFloat()*2.25F+yO,x2-1F,y1+radius.toFloat()*2.25F+yO+yS.toFloat(),Color.white,Color.black,it.name,Button.TextPos.Left))
                            yO += yS.toFloat() + 1F
                        }
                    }
                    if (click){
                        buttonList.forEach {
                            val h = (it.y2 - it.y1)/3
                            val w = h*2.25F
                            val x1 = it.x2 - w - 6F
                            val y1 = it.y1+(it.y2 - it.y1)/2-h/2
                            val x2 = it.x2 - 6F
                            val y2 = it.y1+(it.y2 - it.y1)/2+h/2
                            val isC = isClick(mouseX.toFloat(),mouseY.toFloat(),x1,y1,x2,y2)
                            if (isC) (targets.getValue(it.name)as BooleanValue).set(!(targets.getValue(it.name)as BooleanValue).get())
                        }
                    }else{
                        buttonList.forEach{
                            val line3 = lineAnim["Line3"]!!!=0F
                            val line3Pos = y1+radius*2.25+((y2-(y1+radius*2.25)) * lineAnim["Line3"]!! / 100.0)
                            val line2 = lineAnim["Line2"]!!!=100F
                            val linePos = y1+radius*2.25+((y2-(y1+radius*2.25)) * lineAnim["Line2"]!! / 100.0)
                            val lineTar = lineAnim["LineTarget"]!!
                            val lineTarPos = y1+radius*2.25+((y2-(y1+radius*2.25)) * lineTar / 100.0)
                            val alpha = when{
                                line3 -> when{
                                    line3Pos<it.y2 -> 255
                                    else -> 0
                                }
                                line2 -> when{
                                    linePos in it.y1..it.y2 -> {
                                        val l = linePos - it.y1
                                        val bl = it.y2 - it.y1
                                        ((l/bl)*255).toInt()
                                    }
                                    linePos>it.y2 -> 255
                                    else -> 0
                                }
                                lineTar!=100F -> when{
                                    lineTarPos in it.y1..it.y2 -> {
                                        val l = lineTarPos - it.y1
                                        val bl = it.y2 - it.y1
                                        ((l/bl)*255).toInt()
                                    }
                                    lineTarPos>it.y2 -> 255
                                    else -> 0
                                }
                                else -> 255
                            }
                            val white = Color(255,255,255,alpha)
                            val black = Color(0,0,0,alpha)
                            val lightGray = Color(192,192,192,alpha)
                            val darkGray = Color(64,64,64,alpha)
                            val h = (it.y2 - it.y1)/3
                            val w = h*2.25F
                            val x1 = it.x2 - w - 6F
                            val y1 = it.y1+(it.y2 - it.y1)/2-h/2
                            val x2 = it.x2 - 6F
                            val y2 = it.y1+(it.y2 - it.y1)/2+h/2
                            val isC = isClick(mouseX.toFloat(),mouseY.toFloat(),x1,y1,x2,y2)
                            it.color = if(isLight()) white else darkGray
                            it.fontColor = if(isLight()) black else white
                            if (alpha!=0) {
                                it.drawButton()
                                val state = (targets.getValue(it.name)as BooleanValue).get()
                                if (buttonsAnim["${it.name}Button"]==null) buttonsAnim["${it.name}Button"] = if(state) 100F else 0F
                                drawButton1(x1,y1,x2,y2,if(isLight())if(isC)Color(170,170,170,alpha) else lightGray else if(isC) lightGray else black,if(state) Color.green else Color.red,buttonsAnim["${it.name}Button"]!!)
                                animButton("${it.name}Button",8F,state)
                            }
                        }
                    }
                }
                Gui -> {
                    val buttonList = arrayListOf<Button>()
                    var yO = 1.5F
                    KevinClient.moduleManager.getModules().forEach {
                        if (it is ClickGui||it is CapeManager||it is HudDesigner){
                            buttonList.add(Button(it.name,startX.toFloat(),y1+radius.toFloat()*2.25F+yO,endX.toFloat(),y1+radius.toFloat()*2.25F+yO+yS.toFloat(),Color.white,Color.black,it.name,Button.TextPos.Middle))
                            yO += yS.toFloat() + 1F
                        }
                    }
                    if (click) {
                        buttonList.forEach{
                            if (it.isClick(mouseX,mouseY)) guiOpen = if (mouseButton==0||guiOpen!=buttonList.indexOf(it)) buttonList.indexOf(it) else -1
                        }
                    }else{
                        buttonList.forEach {
                            val line3Pos = y1+radius*2.25+((y2-(y1+radius*2.25)) * lineAnim["Line3"]!! / 100.0)
                            val alpha = when{
                                line3Pos in it.y1..it.y2 -> {
                                    val l = line3Pos - it.y1
                                    val bl = it.y2 - it.y1
                                    ((l/bl)*255).toInt()
                                }
                                line3Pos>it.y2 -> 255
                                else -> 0
                            }
                            val white = Color(255,255,255,alpha)
                            val black = Color(0,0,0,alpha)
                            val lightGray = Color(192,192,192,alpha)
                            val darkGray = Color(64,64,64,alpha)
                            val blue = Color(0,111,255,150)
                            val isClick = it.isClick(mouseX,mouseY)
                            it.color = if (guiOpen == buttonList.indexOf(it)) blue else if(isLight())
                                if(isClick) Color(170,170,170,alpha) else white
                            else
                                if(isClick) lightGray else darkGray
                            it.fontColor = if(isLight()) black else white
                            val xa1 = it.initX1+(it.initX2-it.initX1)/12
                            val xsp = (it.initX2-it.initX1)/48
                            val ysp = (it.initY2-it.initY1)/48
                            val mouseButtonDown = Mouse.isButtonDown(0) || Mouse.isButtonDown(1)
                            if (lastTickMouseDown == buttonList.indexOf(it)||(lastTickMouseDown==null&&isClick&&mouseButtonDown)){
                                if (lastTickMouseDown==null) lastTickMouseDown = buttonList.indexOf(it)
                                if (it.x1<xa1){
                                    it.x1 += xsp
                                    it.y1 += ysp
                                    it.x2 -= xsp
                                    it.y2 -= ysp
                                }
                            } else {
                                if (it.x1>it.initX1){
                                    it.x1 -= xsp
                                    it.y1 -= ysp
                                    it.x2 += xsp
                                    it.y2 += ysp
                                }
                            }
                            if (alpha!=0) it.drawButton()
                        }
                    }
                }
            }
        }
        private var guiOpen = -1
        private fun drawModules(moduleCategory: ModuleCategory,mouseX: Int,mouseY: Int,mouseButton: Int,click: Boolean,yS: Double,startX: Double,endX: Double){
            val buttonList = arrayListOf<Button>()
            var yO = 1.5F
            val start = cateY[moduleCategory]!!
            var r = 0
            for (it in KevinClient.moduleManager.getModules()) {
                if (it is Targets||it is ClickGui||it is CapeManager||it is HudDesigner) continue
                if (it.category==moduleCategory){
                    if (r!=start){r+=1;continue}
                    if (buttonList.size==8)break
                    buttonList.add(Button(it.name,startX.toFloat(),y1+radius.toFloat()*2.25F+yO,endX.toFloat(),y1+radius.toFloat()*2.25F+yO+yS.toFloat(),Color.white,Color.black,it.name,Button.TextPos.Middle))
                    yO += yS.toFloat() + 1F
                }
            }
            if (click) {
                buttonList.forEach{
                    if (it.isClick(mouseX,mouseY)) {
                        if (clickModule[moduleCategory]!! != buttonList.indexOf(it)+r) moduleY[moduleCategory] = 0F

                        clickModule[moduleCategory] =
                            if (mouseButton == 0 || clickModule[moduleCategory]!! != buttonList.indexOf(it) + r) buttonList.indexOf(it) + r else {
                                lastModule[moduleCategory] = clickModule[moduleCategory]!!
                                -1
                            }
                    }
                }
            } else {
                buttonList.forEach {
                    val line3Pos = y1+radius*2.25+((y2-(y1+radius*2.25)) * lineAnim["Line3"]!! / 100.0)
                    val alpha = when{
                        line3Pos in it.y1..it.y2 -> {
                            val l = line3Pos - it.y1
                            val bl = it.y2 - it.y1
                            ((l/bl)*255).toInt()
                        }
                        line3Pos>it.y2 -> 255
                        else -> 0
                    }
                    val white = Color(255,255,255,alpha)
                    val black = Color(0,0,0,alpha)
                    val lightGray = Color(192,192,192,alpha)
                    val darkGray = Color(64,64,64,alpha)
                    val blue = Color(0,111,255,150)
                    val isClick = it.isClick(mouseX,mouseY)
                    it.color = if (clickModule[moduleCategory]!! == buttonList.indexOf(it)+r) blue else if(isLight())
                        if(isClick) Color(170,170,170,alpha) else white
                    else
                        if(isClick) lightGray else darkGray
                    it.fontColor = if(isLight()) black else white
                    val xa1 = it.initX1+(it.initX2-it.initX1)/12
                    val xsp = (it.initX2-it.initX1)/48
                    val ysp = (it.initY2-it.initY1)/48
                    val mouseButtonDown = Mouse.isButtonDown(0) || Mouse.isButtonDown(1)
                    if (lastTickMouseDown == buttonList.indexOf(it)||(lastTickMouseDown==null&&isClick&&mouseButtonDown)){
                        if (lastTickMouseDown==null) lastTickMouseDown = buttonList.indexOf(it)
                        if (it.x1<xa1){
                            it.x1 += xsp
                            it.y1 += ysp
                            it.x2 -= xsp
                            it.y2 -= ysp
                        }
                    } else {
                        if (it.x1>it.initX1){
                            it.x1 -= xsp
                            it.y1 -= ysp
                            it.x2 += xsp
                            it.y2 += ysp
                        }
                    }
                    if (alpha!=0) it.drawButton()
                }
            }
        }
        override fun handleMouseInput() {
            super.handleMouseInput()
            if (clickButton==null||clickButton==Target||clickButton==Gui) return
            val cate = ModuleCategory.valueOf(clickButton!!.name.uppercase(Locale.getDefault()))
            if (canR) {
                when{
                    Mouse.getEventDWheel() > 0 ->{
                        if (cateY[cate]!!>0) cateY[cate] = cateY[cate]!! - 1
                    }
                    Mouse.getEventDWheel() < 0 ->{
                        if (cateY[cate]!!<cateMaxY[cate]!!)cateY[cate] = cateY[cate]!! + 1
                    }
                }
            }
            if (canSettingsR){
                if (clickModule[cate]!! == -1) return
                when{
                    Mouse.getEventDWheel() > 0 ->{
                        if (moduleY[cate]!!>0) moduleY[cate] = moduleY[cate]!! - 5F
                    }
                    Mouse.getEventDWheel() < 0 && canSettingsR2 ->{
                        moduleY[cate] = moduleY[cate]!! + 5F
                    }
                }
            }
        }
        private fun drawSettings(category: Category?,mouseX: Int,mouseY: Int,click: Boolean){
            if (category==null) return
            var moduleCategory:ModuleCategory?=null
            ModuleCategory.values().forEach {
                if (it.name.equals(category.name,true)){
                    moduleCategory=it
                    return@forEach
                }
            }
            if (moduleCategory!=null&&(clickModule[moduleCategory]!! == -1 && lineAnim["LineSettings"]!! == 0F)) return
            when(category){
                Combat ->{
                    drawSettings(ModuleCategory.COMBAT,click,mouseX,mouseY)
                }
                Exploit -> {
                    drawSettings(ModuleCategory.EXPLOIT,click,mouseX,mouseY)
                }
                Misc -> {
                    drawSettings(ModuleCategory.MISC,click,mouseX,mouseY)
                }
                Movement -> {
                    drawSettings(ModuleCategory.MOVEMENT,click,mouseX,mouseY)
                }
                Player -> {
                    drawSettings(ModuleCategory.PLAYER,click,mouseX,mouseY)
                }
                Render -> {
                    drawSettings(ModuleCategory.RENDER,click,mouseX,mouseY)
                }
                World -> {
                    drawSettings(ModuleCategory.WORLD,click,mouseX,mouseY)
                }
                Target -> {}
                Gui -> {
                    when(guiOpen){
                        0 -> {
                            val capeManager = KevinClient.moduleManager.getModule(CapeManager::class.java)
                            drawGuiModuleSetting(capeManager,0F,click,mouseX,mouseY)
                        }
                        1 -> {
                            val clickGui = KevinClient.moduleManager.getModule(ClickGui::class.java)
                            drawGuiModuleSetting(clickGui,0F,click,mouseX,mouseY)
                        }
                        2 -> {
                            val hudDesigner = KevinClient.moduleManager.getModule(HudDesigner::class.java)
                            drawGuiModuleSetting(hudDesigner,0F,click,mouseX,mouseY)
                        }
                    }
                }
            }
        }
        private fun animList(name: String,animSpeed: Float,state: Boolean){
            if(state){
                if (listAnim[name]!!<100) listAnim[name] = listAnim[name]!! + animSpeed
                if (listAnim[name]!!>100) listAnim[name] = 100F
            } else {
                if (listAnim[name]!!>0) listAnim[name] = listAnim[name]!! - animSpeed
                if (listAnim[name]!!<0) listAnim[name] = 0F
            }
        }

        private fun drawGuiModuleSetting(module: Module,yad: Float,isClick: Boolean,mouseX: Int,mouseY: Int){
            val startX = x1+(x2-x1)/2.0+2.0
            val endX = x2-2.0
            val startY = y1+radius*2.25 + 2.0
            val endY = y2-2.0
            if (isClick) {
                if (mouseX.toFloat() !in startX..endX || mouseY.toFloat() !in startY..endY) return
                var yO = 1.5F - yad
                //Open
                yO -= 2F
                val tr = ((yO + startY.toFloat() + KevinClient.fontManager.font35.fontHeight * 0.8F) - (yO + startY.toFloat()))/2.0
                val tx = endX.toFloat() - 25F + tr
                val ty = yO + startY.toFloat() + tr
                val l = isClick(
                    mouseX.toFloat(),
                    mouseY.toFloat(),
                    tx.toFloat()-tr.toFloat(),
                    ty.toFloat()-tr.toFloat(),
                    tx.toFloat()+tr.toFloat(),
                    ty.toFloat()+tr.toFloat()
                )
                if (l) module.toggle()
                yO += KevinClient.fontManager.font35.fontHeight * 0.8F + 4F
                //Description
                val textList = arrayListOf<String>()
                var text = ""
                val t = module.description.split(" ").toMutableList()
                var cou = 0
                t.forEach {
                    if (KevinClient.fontManager.font35.getStringWidth("$text $it") * 0.7 < endX - startX) {
                        text += if (text.isEmpty()) it else " $it"
                    } else {
                        textList += text
                        text = it
                    }
                    cou += 1
                    if (cou == t.size && text.isNotEmpty()) textList += text
                }
                textList.forEach { _ ->
                    yO += KevinClient.fontManager.font35.fontHeight * 0.7F
                }
                yO += 3F
                //Settings
                val moduleName = module.name
                val settings = module.values
                settings.forEach {
                    when (it) {
                        is BooleanValue -> {
                            yO -= 1.5F
                            val cl = isClick(
                                mouseX.toFloat(),
                                mouseY.toFloat(),
                                endX.toFloat() - 18F,
                                startY.toFloat() + yO,
                                endX.toFloat() - 5F,
                                startY.toFloat() + yO + KevinClient.fontManager.font35.fontHeight * 0.65F
                            ) && (mouseX.toFloat() in startX - 2F..endX + 2F && mouseY.toFloat() in startY - 2F..endY + 2F)
                            if (cl) it.set(!it.get())
                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F + 3.5F
                        }
                        is TextValue -> {
                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F
                            val tl = arrayListOf<String>()
                            var tt = ""
                            val tl2 = it.get().toCharArray()
                            var co = 0
                            tl2.forEach { ch ->
                                if (KevinClient.fontManager.font35.getStringWidth("$tt$ch") * 0.5 < endX - startX) {
                                    tt += ch
                                } else {
                                    tl += tt
                                    tt = "$ch"
                                }
                                co += 1
                                if (co == tl2.size && tt.isNotEmpty()) tl += tt
                            }
                            tl.forEach { _ ->
                                yO += KevinClient.fontManager.font35.fontHeight * 0.5F
                            }
                            yO += 3F
                        }
                        is IntegerValue -> {
                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F
                            yO += 1.5F

                            val dv = (it.get() - it.minimum).toFloat() / (it.maximum - it.minimum).toFloat()
                            val dl = ((endX - 5.0) - (startX + 5.0)) * dv
                            val x = startX + 5.0 + dl
                            val y = startY + yO
                            if (isClick(
                                    mouseX.toFloat(),
                                    mouseY.toFloat(),
                                    x.toFloat() - 2.0F,
                                    y.toFloat() - 2.0F,
                                    x.toFloat() + 2.0F,
                                    y.toFloat() + 2.0F
                                )
                            ) {
                                lastTickClickValue = "$moduleName${it.name}"
                            }

                            yO += 1.5F
                            yO += 4F
                        }
                        is FloatValue -> {
                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F
                            yO += 1.5F

                            val dv = (it.get() - it.minimum) / (it.maximum - it.minimum)
                            val dl = ((endX - 5.0) - (startX + 5.0)) * dv
                            val x = startX + 5.0 + dl
                            val y = startY + yO
                            if (isClick(
                                    mouseX.toFloat(),
                                    mouseY.toFloat(),
                                    x.toFloat() - 2.0F,
                                    y.toFloat() - 2.0F,
                                    x.toFloat() + 2.0F,
                                    y.toFloat() + 2.0F
                                )
                            ) {
                                lastTickClickValue = "$moduleName${it.name}"
                            }

                            yO += 1.5F
                            yO += 4F
                        }
                        is ListValue -> {
                            val p1x = (endX.toFloat() - 5F - 5F)
                            val p1y =
                                (startY.toFloat() + yO + KevinClient.fontManager.font35.fontHeight * 0.65F / 2)
                            val p23x = (endX.toFloat() - 5F)
                            if (isClick(
                                    mouseX.toFloat(),
                                    mouseY.toFloat(),
                                    (p1x + (p23x - p1x) / 2.0F) - ((p23x - p1x) / 2.0F),
                                    p1y + (-KevinClient.fontManager.font35.fontHeight * 0.65F / 2 + 1F),
                                    (p1x + (p23x - p1x) / 2.0F) + (p23x - p1x) / 2.0F,
                                    p1y + (+KevinClient.fontManager.font35.fontHeight * 0.65F / 2 - 1F)
                                )
                            ) listOpen["$moduleName${it.name}"] = !listOpen["$moduleName${it.name}"]!!
                            val open = listOpen["$moduleName${it.name}"]!!
                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F
                            if (open || listAnim["$moduleName${it.name}"] != 0F) {
                                var ly = 0F
                                it.values.forEach { _ ->
                                    ly += KevinClient.fontManager.font35.fontHeight * 0.6F
                                }
                                val animY = ly * listAnim["$moduleName${it.name}"]!! / 100F
                                val yOf = yO + animY
                                it.values.forEach vfe@{ v ->
                                    val alpha = when {
                                        yOf > yO + KevinClient.fontManager.font35.fontHeight * 0.6F -> 255
                                        yOf in yO..yO + KevinClient.fontManager.font35.fontHeight * 0.6F -> {
                                            val ya = yOf - yO
                                            ((ya / (KevinClient.fontManager.font35.fontHeight * 0.6F)) * 255).toInt()
                                        }
                                        else -> 0
                                    }
                                    if (alpha == 0) return@vfe
                                    if (isClick(
                                            mouseX.toFloat(),
                                            mouseY.toFloat(),
                                            endX.toFloat() - 10F - KevinClient.fontManager.font35.getStringWidth(
                                                v
                                            ) * 0.6F,
                                            startY.toFloat() + yO,
                                            endX.toFloat() - 10F,
                                            startY.toFloat() + yO + KevinClient.fontManager.font35.fontHeight * 0.6F
                                        )
                                    ) it.set(v)
                                    yO += KevinClient.fontManager.font35.fontHeight * 0.6F
                                }
                            }
                            yO += 3F
                        }
                    }
                }

            } else {

                val dx1 = mc.displayWidth / 4
                val dy1 = mc.displayHeight / 4
                val dx2 = mc.displayWidth / 4 * 3
                val dy2 = mc.displayHeight / 4 * 3
                val dStartX = dx1 + (dx2 - dx1) / 2
                val dStartY = (dy1 + ((dx2 - dx1) / 48.0) * 2.25).toInt()
                glEnable(GL_SCISSOR_TEST)
                glScissor(dStartX, dy1, dx2 - dStartX, dy2 - dStartY)
                //RenderUtils.drawRect(0,0,mc.currentScreen.width,mc.currentScreen.height,Color(255,255,255,50).rgb)

                val settings = module.values
                var yO = 1.5F - yad
                //Open
                glPushMatrix()
                glScaled(0.8, 0.8, 0.8)
                KevinClient.fontManager.font35.drawString(
                    "Open",
                    (startX.toFloat() + 1F) / 0.8F,
                    (yO + startY.toFloat()) / 0.8F,
                    Color(0, 111, 255).rgb
                )
                glPopMatrix()
                yO -= 2F
                val tr = ((yO + startY.toFloat() + KevinClient.fontManager.font35.fontHeight * 0.8F) - (yO + startY.toFloat()))/2.0
                val tx = endX.toFloat() - 25F + tr
                val ty = yO + startY.toFloat() + tr
                val l = isClick(
                    mouseX.toFloat(),
                    mouseY.toFloat(),
                    tx.toFloat()-tr.toFloat(),
                    ty.toFloat()-tr.toFloat(),
                    tx.toFloat()+tr.toFloat(),
                    ty.toFloat()+tr.toFloat()
                ) && (mouseX.toFloat() in startX - 2F..endX + 2F && mouseY.toFloat() in startY - 2F..endY + 2F)
                RenderUtils.drawSector(tx,ty,0,360,tr,if (l) Color(0,255,150) else Color.green)
                yO += KevinClient.fontManager.font35.fontHeight * 0.8F + 4F
                //DrawDescription
                glPushMatrix()
                glScaled(0.7, 0.7, 0.7)
                val textList = arrayListOf<String>()
                var text = ""
                val t = module.description.split(" ").toMutableList()
                var cou = 0
                t.forEach {
                    if (KevinClient.fontManager.font35.getStringWidth("$text $it") * 0.7 < endX - startX) {
                        text += if (text.isEmpty()) it else " $it"
                    } else {
                        textList += text
                        text = it
                    }
                    cou += 1
                    if (cou == t.size && text.isNotEmpty()) textList += text
                }
                textList.forEach {
                    KevinClient.fontManager.font35.drawString(
                        it,
                        (startX.toFloat() + 1F) / 0.7F,
                        (yO + startY.toFloat()) / 0.7F,
                        if (isLight()) Color.black.rgb else Color.white.rgb
                    )
                    yO += KevinClient.fontManager.font35.fontHeight * 0.7F
                }
                yO += 3F
                glPopMatrix()
                //DrawSettings
                val moduleName = module.name
                settings.forEach {
                    when (it) {
                        is BooleanValue -> {
                            glPushMatrix()
                            glScaled(0.65, 0.65, 0.65)
                            KevinClient.fontManager.font35.drawString(
                                it.name,
                                (startX.toFloat() + 1F) / 0.65F,
                                (startY.toFloat() + yO) / 0.65F,
                                if (isLight()) Color.black.rgb else Color.white.rgb
                            )
                            glPopMatrix()
                            if (buttonsAnim["$moduleName${it.name}Button"] != null) {
                                animButton("$moduleName${it.name}Button", 10F, it.get())
                            }
                            if (buttonsAnim["$moduleName${it.name}Button"] == null) buttonsAnim["$moduleName${it.name}Button"] =
                                if (it.get()) 100F else 0F
                            yO -= 1.5F
                            val cl = isClick(
                                mouseX.toFloat(),
                                mouseY.toFloat(),
                                endX.toFloat() - 18F,
                                startY.toFloat() + yO,
                                endX.toFloat() - 5F,
                                startY.toFloat() + yO + KevinClient.fontManager.font35.fontHeight * 0.65F
                            ) && (mouseX.toFloat() in startX - 2F..endX + 2F && mouseY.toFloat() in startY - 2F..endY + 2F)
                            drawButton1(
                                endX.toFloat() - 18F,
                                startY.toFloat() + yO,
                                endX.toFloat() - 5F,
                                startY.toFloat() + yO + KevinClient.fontManager.font35.fontHeight * 0.65F,
                                if (isLight()) if (cl) Color(
                                    170,
                                    170,
                                    170
                                ) else Color.white else if (cl) Color.darkGray else Color.black,
                                if (it.get()) Color.green else Color.red,
                                buttonsAnim["$moduleName${it.name}Button"]!!
                            )
                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F + 3.5F
                        }
                        is TextValue -> {
                            glPushMatrix()
                            glScaled(0.65, 0.65, 0.65)
                            KevinClient.fontManager.font35.drawString(
                                it.name,
                                (startX.toFloat() + 1F) / 0.65F,
                                (startY.toFloat() + yO) / 0.65F,
                                if (isLight()) Color.black.rgb else Color.white.rgb
                            )
                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F
                            glPopMatrix()
                            glPushMatrix()
                            glScaled(0.5, 0.5, 0.5)
                            val tl = arrayListOf<String>()
                            var tt = ""
                            val tl2 = it.get().toCharArray()
                            var co = 0
                            tl2.forEach { ch ->
                                if (KevinClient.fontManager.font35.getStringWidth("$tt$ch") * 0.5 < endX - startX) {
                                    tt += ch
                                } else {
                                    tl += tt
                                    tt = "$ch"
                                }
                                co += 1
                                if (co == tl2.size && tt.isNotEmpty()) tl += tt
                            }
                            tl.forEach { s ->
                                KevinClient.fontManager.font35.drawString(
                                    s,
                                    (startX.toFloat() + 1F) / 0.5F,
                                    (yO + startY.toFloat()) / 0.5F,
                                    Color(0, 111, 255).rgb
                                )
                                yO += KevinClient.fontManager.font35.fontHeight * 0.5F
                            }
                            glPopMatrix()
                            yO += 3F
                        }
                        is IntegerValue -> {
                            glPushMatrix()
                            glScaled(0.65, 0.65, 0.65)
                            KevinClient.fontManager.font35.drawString(
                                it.name,
                                (startX.toFloat() + 1F) / 0.65F,
                                (startY.toFloat() + yO) / 0.65F,
                                if (isLight()) Color.black.rgb else Color.white.rgb
                            )
                            glPopMatrix()
                            glPushMatrix()
                            glScaled(0.55, 0.55, 0.55)
                            val ra =
                                (KevinClient.fontManager.font35.fontHeight * 0.65F - KevinClient.fontManager.font35.fontHeight * 0.55F) / 2F
                            KevinClient.fontManager.font35.drawString(
                                "${it.get()}",
                                (endX.toFloat() - 5F - KevinClient.fontManager.font35.getStringWidth("${it.get()}") * 0.55F) / 0.55F,
                                (startY.toFloat() + yO + ra) / 0.55F,
                                if (isLight()) Color.black.rgb else Color.white.rgb
                            )
                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F
                            glPopMatrix()
                            yO += 1.5F
                            RenderUtils.drawLineStart(Color(0, 111, 255), 4F)
                            RenderUtils.drawLine(
                                startX + 5.0,
                                startY + yO.toDouble(),
                                endX - 5.0,
                                startY + yO.toDouble()
                            )
                            RenderUtils.drawLineEnd()
                            val isClicked = lastTickClickValue == "$moduleName${it.name}"
                            val x = if (isClicked) when {
                                mouseX.toFloat() in startX + 5.0..endX - 5.0 -> mouseX.toDouble()
                                mouseX.toFloat() < startX + 5.0 -> startX + 5.0
                                else -> endX - 5.0
                            } else {
                                val dv = (it.get() - it.minimum).toFloat() / (it.maximum - it.minimum).toFloat()
                                val dl = ((endX - 5.0) - (startX + 5.0)) * dv
                                startX + 5.0 + dl
                            }
                            val y = startY + yO
                            val isMouseOn = isClick(
                                mouseX.toFloat(),
                                mouseY.toFloat(),
                                x.toFloat() - 2F,
                                y.toFloat() - 2F,
                                x.toFloat() + 2F,
                                y.toFloat() + 2F
                            ) || isClicked
                            RenderUtils.drawSector(
                                x,
                                y,
                                0,
                                360,
                                if (isClicked) 2.5 else 2.0,
                                if (isMouseOn) Color(0, 150, 255) else Color(0, 111, 255)
                            )
                            if (isClicked) {
                                val i = MathHelper.clamp_double(
                                    (x - (startX + 5.0)) / ((endX - 5.0) - (startX + 5.0)),
                                    0.0,
                                    1.0
                                )
                                it.set((it.minimum + (it.maximum - it.minimum) * i).toInt())
                            }
                            yO += 1.5F
                            yO += 4F
                        }
                        is FloatValue -> {
                            glPushMatrix()
                            glScaled(0.65, 0.65, 0.65)
                            KevinClient.fontManager.font35.drawString(
                                it.name,
                                (startX.toFloat() + 1F) / 0.65F,
                                (startY.toFloat() + yO) / 0.65F,
                                if (isLight()) Color.black.rgb else Color.white.rgb
                            )
                            glPopMatrix()
                            glPushMatrix()
                            glScaled(0.55, 0.55, 0.55)
                            val ra =
                                (KevinClient.fontManager.font35.fontHeight * 0.65F - KevinClient.fontManager.font35.fontHeight * 0.55F) / 2F
                            KevinClient.fontManager.font35.drawString(
                                "${it.get()}",
                                (endX.toFloat() - 5F - KevinClient.fontManager.font35.getStringWidth("${it.get()}") * 0.55F) / 0.55F,
                                (startY.toFloat() + yO + ra) / 0.55F,
                                if (isLight()) Color.black.rgb else Color.white.rgb
                            )
                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F
                            glPopMatrix()
                            yO += 1.5F
                            RenderUtils.drawLineStart(Color(0, 111, 255), 4F)
                            RenderUtils.drawLine(
                                startX + 5.0,
                                startY + yO.toDouble(),
                                endX - 5.0,
                                startY + yO.toDouble()
                            )
                            RenderUtils.drawLineEnd()
                            val isClicked = lastTickClickValue == "$moduleName${it.name}"
                            val x = if (isClicked) when {
                                mouseX.toFloat() in startX + 5.0..endX - 5.0 -> mouseX.toDouble()
                                mouseX.toFloat() < startX + 5.0 -> startX + 5.0
                                else -> endX - 5.0
                            } else {
                                val dv = (it.get() - it.minimum) / (it.maximum - it.minimum)
                                val dl = ((endX - 5.0) - (startX + 5.0)) * dv
                                startX + 5.0 + dl
                            }
                            val y = startY + yO
                            val isMouseOn = isClick(
                                mouseX.toFloat(),
                                mouseY.toFloat(),
                                x.toFloat() - 2F,
                                y.toFloat() - 2F,
                                x.toFloat() + 2F,
                                y.toFloat() + 2F
                            ) || isClicked
                            RenderUtils.drawSector(
                                x,
                                y,
                                0,
                                360,
                                if (isClicked) 2.5 else 2.0,
                                if (isMouseOn) Color(0, 150, 255) else Color(0, 111, 255)
                            )
                            if (isClicked) {
                                val i = MathHelper.clamp_double(
                                    (x - (startX + 5.0)) / ((endX - 5.0) - (startX + 5.0)),
                                    0.0,
                                    1.0
                                )
                                it.set(String.format("%.2f", (it.minimum + (it.maximum - it.minimum) * i)).toFloat())
                            }
                            yO += 1.5F
                            yO += 4F
                        }
                        is ListValue -> {
                            if (listOpen["$moduleName${it.name}"] == null) listOpen["$moduleName${it.name}"] = false
                            val open = listOpen["$moduleName${it.name}"]!!
                            if (listAnim["$moduleName${it.name}"] != null) animList("$moduleName${it.name}", 5F, open)
                            if (listAnim["$moduleName${it.name}"] == null) listAnim["$moduleName${it.name}"] = 0F
                            glPushMatrix()
                            glScaled(0.65, 0.65, 0.65)
                            KevinClient.fontManager.font35.drawString(
                                it.name,
                                (startX.toFloat() + 1F) / 0.65F,
                                (startY.toFloat() + yO) / 0.65F,
                                if (isLight()) Color.black.rgb else Color.white.rgb
                            )

                            //KevinClient.fontManager.font35!!.drawString("<",(endX.toFloat()-5F-KevinClient.fontManager.font35!!.getStringWidth("<")*0.65F)/0.65F,(startY.toFloat()+yO)/0.65F,if(open) Color(0,111,255).rgb else if(isLight())Color.black.rgb else Color.white.rgb)
                            val p1x = (endX.toFloat() - 5F - 5F) / .65
                            val p1y =
                                (startY.toFloat() + yO + KevinClient.fontManager.font35.fontHeight * 0.65F / 2) / .65
                            val p23x = (endX.toFloat() - 5F) / .65
                            GlStateManager.disableCull()
                            glEnable(GL_BLEND)
                            glDisable(GL_TEXTURE_2D)
                            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                            val isMouseOn = isClick(
                                mouseX.toFloat(),
                                mouseY.toFloat(),
                                (p1x * .65F + (p23x * .65F - p1x * .65F) / 2.0F).toFloat() - ((p23x * .65F - p1x * .65F) / 2.0F).toFloat(),
                                p1y.toFloat() * .65F + (-KevinClient.fontManager.font35.fontHeight * 0.65F / 2 + 1F),
                                (p1x * .65F + (p23x * .65F - p1x * .65F) / 2.0F).toFloat() + (p23x * .65F - p1x * .65F).toFloat() / 2.0F,
                                p1y.toFloat() * .65F + (+KevinClient.fontManager.font35.fontHeight * 0.65F / 2 - 1F)
                            )
                            glColor(
                                if (open) if (isMouseOn) Color(0, 150, 255) else Color(
                                    0,
                                    111,
                                    255
                                ) else if (isLight()) if (isMouseOn) Color(
                                    170,
                                    170,
                                    170
                                ) else Color.black else if (isMouseOn) Color.lightGray else Color.white
                            )
                            glTranslated(p1x + (p23x - p1x) / 2.0, p1y, 0.0)
                            glRotated((listAnim["$moduleName${it.name}"]!! / 100.0) * -90.0, .0, .0, 1.0)
                            glBegin(GL_TRIANGLES)
                            glVertex3d(-((p23x - p1x) / 2.0), .0, .0)
                            glVertex3d(
                                (p23x - p1x) / 2.0,
                                (-KevinClient.fontManager.font35.fontHeight * 0.65F / 2 + 1F) / .65,
                                .0
                            )
                            glVertex3d(
                                (p23x - p1x) / 2.0,
                                (+KevinClient.fontManager.font35.fontHeight * 0.65F / 2 - 1F) / .65,
                                .0
                            )
                            glEnd()
                            glEnable(GL_TEXTURE_2D)
                            glDisable(GL_BLEND)

                            yO += KevinClient.fontManager.font35.fontHeight * 0.65F
                            glPopMatrix()
                            if (open || listAnim["$moduleName${it.name}"] != 0F) {
                                var ly = 0F
                                it.values.forEach { _ ->
                                    ly += KevinClient.fontManager.font35.fontHeight * 0.6F
                                }
                                glPushMatrix()
                                glScaled(.6, .6, .6)
                                val animY = ly * listAnim["$moduleName${it.name}"]!! / 100F
                                val yOf = yO + animY
                                it.values.forEach vfe@{ v ->
                                    val alpha = when {
                                        yOf > yO + KevinClient.fontManager.font35.fontHeight * 0.6F -> 255
                                        yOf in yO..yO + KevinClient.fontManager.font35.fontHeight * 0.6F -> {
                                            val ya = yOf - yO
                                            ((ya / (KevinClient.fontManager.font35.fontHeight * 0.6F)) * 255).toInt()
                                        }
                                        else -> 0
                                    }
                                    if (alpha == 0) return@vfe
                                    val w = Color(255, 255, 255, alpha)
                                    val b = Color(0, 0, 0, alpha)
                                    KevinClient.fontManager.font35.drawString(
                                        v,
                                        (endX.toFloat() - 10F - KevinClient.fontManager.font35.getStringWidth(v) * 0.6F) / 0.6F,
                                        (startY.toFloat() + yO) / 0.6F,
                                        if (it.get() == v) Color(0, 111, 255).rgb else if (isLight()) b.rgb else w.rgb
                                    )
                                    yO += KevinClient.fontManager.font35.fontHeight * 0.6F
                                }
                                glPopMatrix()
                            }
                            yO += 3F
                        }
                    }
                }
                canSettingsR2 = yO+startY>endY
                glDisable(GL_SCISSOR_TEST)
            }
        }

        private fun getAlpha(animY: Float,y1: Float,y2: Float) =
            when{
                animY > y2 -> 255
                animY in y1..y2 -> {
                    val ya = animY-y1
                    ((ya/(y2-y1))*255).toInt()
                }
                else -> 0
            }

        private fun drawSettings(category: ModuleCategory,isClick: Boolean,mouseX: Int,mouseY: Int){
            val startX = x1+(x2-x1)/2.0+2.0
            val endX = x2-2.0
            val startY = y1+radius*2.25 + 2.0
            val endY = y2-2.0
            var c = 0
            val cn = if (clickModule[category]!=-1) clickModule[category] else lastModule[category]
            for (it in KevinClient.moduleManager.getModules()){
                if (it is Targets||it is ClickGui||it is CapeManager||it is HudDesigner) continue
                if (it.category!=category) continue
                if (c != cn) {c+=1;continue}

                val animLineY = if (lineAnim["LineSettings"]!!!=100F) startY.toFloat() + ((((endY+2F) - (startY-2F))) * lineAnim["LineSettings"]!! / 100F).toFloat() else Float.MAX_VALUE

                if (isClick) {
                    if (mouseX.toFloat() !in startX..endX||mouseY.toFloat() !in startY..endY) return
                    var yO = 1.5F - moduleY[category]!!
                    //State
                    //A1
                    val alpha1 = getAlpha(animLineY,yO+startY.toFloat(),yO+startY.toFloat()+ KevinClient.fontManager.font35.fontHeight*0.8F)
                    if (alpha1==0) return
                    yO -= 2F
                    //A2
                    val alpha2 = getAlpha(animLineY,yO+startY.toFloat(),yO+startY.toFloat()+ KevinClient.fontManager.font35.fontHeight*0.8F)
                    if (alpha2==0) return
                    if (isClick(mouseX.toFloat(),mouseY.toFloat(),endX.toFloat()-25F,yO+startY.toFloat(),endX.toFloat()-5F,yO+startY.toFloat()+ KevinClient.fontManager.font35.fontHeight*0.8F)) it.toggle()
                    yO += KevinClient.fontManager.font35.fontHeight*0.8F + 4F
                    //Description
                    val textList = arrayListOf<String>()
                    var text = ""
                    val t = it.description.split(" ").toMutableList()
                    var cou = 0
                    t.forEach{
                        if (KevinClient.fontManager.font35.getStringWidth("$text $it")*0.7<endX-startX){
                            text += if (text.isEmpty()) it else " $it"
                        } else {
                            textList += text
                            text = it
                        }
                        cou += 1
                        if (cou == t.size&&text.isNotEmpty()) textList += text
                    }
                    textList.forEach { _ ->
                        //A3
                        val alpha3 = getAlpha(animLineY,yO+startY.toFloat(),yO+startY.toFloat()+ KevinClient.fontManager.font35.fontHeight*0.7F)
                        if (alpha3 == 0) return

                        yO += KevinClient.fontManager.font35.fontHeight*0.7F
                    }
                    yO += 3F
                    //Settings
                    val moduleName = it.name
                    val settings = it.values
                    settings.forEach{
                        when(it){
                            is BooleanValue -> {
                                //A4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4 == 0) return
                                yO -= 1.5F
                                //A5
                                val alpha5 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha5 == 0) return
                                val cl = isClick(mouseX.toFloat(),mouseY.toFloat(),endX.toFloat()-18F,startY.toFloat()+yO,endX.toFloat()-5F,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F) && (mouseX.toFloat() in startX-2F..endX+2F && mouseY.toFloat() in startY-2F..endY+2F)
                                if (cl) it.set(!it.get())
                                yO += KevinClient.fontManager.font35.fontHeight*0.65F+3.5F
                            }
                            is TextValue -> {
                                //A4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4 == 0) return
                                yO += KevinClient.fontManager.font35.fontHeight*0.65F
                                val tl = arrayListOf<String>()
                                var tt = ""
                                val tl2 = it.get().toCharArray()
                                var co = 0
                                tl2.forEach { ch ->
                                    if (KevinClient.fontManager.font35.getStringWidth("$tt$ch")*0.5<endX-startX){
                                        tt += ch
                                    } else {
                                        tl += tt
                                        tt = "$ch"
                                    }
                                    co += 1
                                    if (co == tl2.size&&tt.isNotEmpty()) tl += tt
                                }
                                tl.forEach { _ ->
                                    //A5
                                    val alpha5 = getAlpha(animLineY,yO+startY.toFloat(), KevinClient.fontManager.font35.fontHeight*0.5F)
                                    if (alpha5 == 0) return
                                    yO += KevinClient.fontManager.font35.fontHeight*0.5F
                                }
                                yO += 3F
                            }
                            is IntegerValue -> {
                                //A4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4==0) return
                                //A5
                                val ra = (KevinClient.fontManager.font35.fontHeight*0.65F- KevinClient.fontManager.font35.fontHeight*0.55F)/2F
                                val alpha5 = getAlpha(animLineY,startY.toFloat()+yO+ra,startY.toFloat()+yO+ra+ KevinClient.fontManager.font35.fontHeight*0.55F)
                                if (alpha5==0) return
                                yO += KevinClient.fontManager.font35.fontHeight*0.65F
                                yO += 1.5F
                                //A6
                                val alpha6 = getAlpha(animLineY,startY.toFloat()+yO-1.5F,startY.toFloat()+yO+1.5F)
                                if (alpha6 == 0) return
                                val dv = (it.get() - it.minimum).toFloat() / (it.maximum - it.minimum).toFloat()
                                val dl = ((endX-5.0)-(startX+5.0)) * dv
                                val x = startX+5.0 + dl
                                val y = startY+yO
                                if (isClick(mouseX.toFloat(),mouseY.toFloat(),x.toFloat()-2.0F,y.toFloat()-2.0F,x.toFloat()+2.0F,y.toFloat()+2.0F)){
                                    lastTickClickValue = "$moduleName${it.name}"
                                }

                                yO += 1.5F
                                yO += 4F
                            }
                            is FloatValue -> {
                                //A4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4 == 0) return
                                //A5
                                val ra = (KevinClient.fontManager.font35.fontHeight*0.65F- KevinClient.fontManager.font35.fontHeight*0.55F)/2F
                                val alpha5 = getAlpha(animLineY,startY.toFloat()+yO+ra,startY.toFloat()+yO+ra+ KevinClient.fontManager.font35.fontHeight*0.55F)
                                if (alpha5 == 0) return
                                yO += KevinClient.fontManager.font35.fontHeight*0.65F
                                yO += 1.5F
                                //A6
                                val alpha6 = getAlpha(animLineY,startY.toFloat()+yO-1.5F,startY.toFloat()+yO+1.5F)
                                if (alpha6==0) return
                                val dv = (it.get() - it.minimum)/(it.maximum - it.minimum)
                                val dl = ((endX-5.0)-(startX+5.0)) * dv
                                val x = startX+5.0 + dl
                                val y = startY+yO
                                if (isClick(mouseX.toFloat(),mouseY.toFloat(),x.toFloat()-2.0F,y.toFloat()-2.0F,x.toFloat()+2.0F,y.toFloat()+2.0F)){
                                    lastTickClickValue = "$moduleName${it.name}"
                                }

                                yO += 1.5F
                                yO += 4F
                            }
                            is ListValue -> {
                                //A4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4 == 0) return
                                val p1x = (endX.toFloat()-5F-5F)
                                val p1y = (startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F/2)
                                val p23x = (endX.toFloat()-5F)
                                //A5
                                val alpha5 = getAlpha(animLineY,p1y.toFloat()*.65F+ (-KevinClient.fontManager.font35.fontHeight*0.65F/2+1F),p1y.toFloat()*.65F+(+KevinClient.fontManager.font35.fontHeight*0.65F/2-1F))
                                if (alpha5 == 0) return
                                if (isClick(mouseX.toFloat(),mouseY.toFloat(),(p1x+(p23x-p1x)/2.0F)-((p23x-p1x)/2.0F),p1y+(-KevinClient.fontManager.font35.fontHeight*0.65F/2+1F),(p1x+(p23x-p1x)/2.0F)+(p23x-p1x)/2.0F,p1y+(+KevinClient.fontManager.font35.fontHeight*0.65F/2-1F))) listOpen["$moduleName${it.name}"] = !listOpen["$moduleName${it.name}"]!!
                                val open = listOpen["$moduleName${it.name}"]!!
                                yO += KevinClient.fontManager.font35.fontHeight*0.65F
                                if (open||listAnim["$moduleName${it.name}"] != 0F) {
                                    var ly = 0F
                                    it.values.forEach { _ ->
                                        ly += KevinClient.fontManager.font35.fontHeight*0.6F
                                    }
                                    val animY=ly * listAnim["$moduleName${it.name}"]!! / 100F
                                    val yOf = yO + animY
                                    it.values.forEach vfe@ { v ->
                                        //A6
                                        val alpha6 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.6F)
                                        if (alpha6 == 0) return
                                        val alpha = when {
                                            yOf > yO+ KevinClient.fontManager.font35.fontHeight*0.6F -> 255
                                            yOf in yO..yO+ KevinClient.fontManager.font35.fontHeight*0.6F -> {
                                                val ya = yOf-yO
                                                ((ya/(KevinClient.fontManager.font35.fontHeight*0.6F))*255).toInt()
                                            }
                                            else -> 0
                                        }
                                        if (alpha == 0) return@vfe
                                        if (isClick(mouseX.toFloat(),mouseY.toFloat(),endX.toFloat()-10F- KevinClient.fontManager.font35.getStringWidth(v)*0.6F,startY.toFloat()+yO,endX.toFloat()-10F,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.6F)) it.set(v)
                                        yO += KevinClient.fontManager.font35.fontHeight*0.6F
                                    }
                                }
                                yO += 3F
                            }
                        }
                    }

                } else {

                    val dx1 = mc.displayWidth/4
                    val dy1 = mc.displayHeight/4
                    val dx2 = mc.displayWidth/4*3
                    val dy2 = mc.displayHeight/4*3
                    val dStartX = dx1+(dx2-dx1)/2
                    val dStartY = (dy1+((dx2-dx1)/48.0)*2.25).toInt()
                    glEnable(GL_SCISSOR_TEST)
                    glScissor(dStartX,dy1,dx2-dStartX,dy2-dStartY)
                    //RenderUtils.drawRect(0,0,mc.currentScreen.width,mc.currentScreen.height,Color(255,255,255,50).rgb)

                    val settings = it.values
                    var yO = 1.5F - moduleY[category]!!
                    //DrawModuleState
                    glPushMatrix()
                    glScaled(0.8,0.8,0.8)

                    //Alpha1
                    val alpha1 = getAlpha(animLineY,yO+startY.toFloat(),yO+startY.toFloat()+ KevinClient.fontManager.font35.fontHeight*0.8F)
                    if (alpha1==0) {
                        glPopMatrix()
                        glDisable(GL_SCISSOR_TEST)
                        return
                    }
                    KevinClient.fontManager.font35.drawString("State",(startX.toFloat()+1F)/0.8F,(yO+startY.toFloat())/0.8F,Color(0,111,255,alpha1).rgb)

                    glPopMatrix()
                    if (buttonsAnim["${it.name}State"]!=null){
                        animButton("${it.name}State",9F,it.state)
                    }
                    if (buttonsAnim["${it.name}State"]==null) buttonsAnim["${it.name}State"] = if (it.state) 100F else 0F
                    yO -= 2F
                    val l = isClick(mouseX.toFloat(),mouseY.toFloat(),endX.toFloat()-25F,yO+startY.toFloat(),endX.toFloat()-5F,yO+startY.toFloat()+ KevinClient.fontManager.font35.fontHeight*0.8F) && (mouseX.toFloat() in startX-2F..endX+2F && mouseY.toFloat() in startY-2F..endY+2F)

                    //Alpha2
                    val alpha2 = getAlpha(animLineY,yO+startY.toFloat(),yO+startY.toFloat()+ KevinClient.fontManager.font35.fontHeight*0.8F)
                    if (alpha2==0) {
                        glDisable(GL_SCISSOR_TEST)
                        return
                    }
                    drawButton1(endX.toFloat()-25F,yO+startY.toFloat(),endX.toFloat()-5F,yO+startY.toFloat()+ KevinClient.fontManager.font35.fontHeight*0.8F,if(isLight()) if (l) Color(170,170,170,alpha2) else Color(255,255,255,alpha2) else if (l) Color(64,64,64,alpha2) else Color(0,0,0,alpha2),if (it.state) Color(0,255,0,alpha2) else Color(255,0,0,alpha2),buttonsAnim["${it.name}State"]!!)

                    yO += KevinClient.fontManager.font35.fontHeight*0.8F + 4F
                    //DrawDescription
                    glPushMatrix()
                    glScaled(0.7,0.7,0.7)
                    val textList = arrayListOf<String>()
                    var text = ""
                    val t = it.description.split(" ").toMutableList()
                    var cou = 0
                    t.forEach{
                        if (KevinClient.fontManager.font35.getStringWidth("$text $it")*0.7<endX-startX){
                            text += if (text.isEmpty()) it else " $it"
                        } else {
                            textList += text
                            text = it
                        }
                        cou += 1
                        if (cou == t.size&&text.isNotEmpty()) textList += text
                    }
                    textList.forEach {

                        //Alpha3
                        val alpha3 = getAlpha(animLineY,yO+startY.toFloat(),yO+startY.toFloat()+ KevinClient.fontManager.font35.fontHeight*0.7F)
                        if (alpha3 == 0) {
                            glPopMatrix()
                            glDisable(GL_SCISSOR_TEST)
                            return
                        }
                        KevinClient.fontManager.font35.drawString(it,(startX.toFloat()+1F)/0.7F,(yO+startY.toFloat())/0.7F,if(isLight())Color(0,0,0,alpha3).rgb else Color(255,255,255,alpha3).rgb)

                        yO += KevinClient.fontManager.font35.fontHeight*0.7F
                    }
                    yO += 3F
                    glPopMatrix()
                    //DrawSettings
                    val moduleName = it.name
                    settings.forEach{
                        when(it){
                            is BooleanValue -> {
                                glPushMatrix()
                                glScaled(0.65,0.65,0.65)

                                //Alpha4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4 == 0) {
                                    glPopMatrix()
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                KevinClient.fontManager.font35.drawString(it.name,(startX.toFloat()+1F)/0.65F,(startY.toFloat()+yO)/0.65F,if(isLight())Color(0,0,0,alpha4).rgb else Color(255,255,255,alpha4).rgb)

                                glPopMatrix()
                                if(buttonsAnim["$moduleName${it.name}Button"]!=null){
                                    animButton("$moduleName${it.name}Button",10F,it.get())
                                }
                                if(buttonsAnim["$moduleName${it.name}Button"]==null) buttonsAnim["$moduleName${it.name}Button"] = if(it.get()) 100F else 0F
                                yO -= 1.5F
                                val cl = isClick(mouseX.toFloat(),mouseY.toFloat(),endX.toFloat()-18F,startY.toFloat()+yO,endX.toFloat()-5F,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F) && (mouseX.toFloat() in startX-2F..endX+2F && mouseY.toFloat() in startY-2F..endY+2F)

                                //Alpha5
                                val alpha5 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha5 == 0) {
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                drawButton1(endX.toFloat()-18F,startY.toFloat()+yO,endX.toFloat()-5F,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F,if(isLight()) if (cl) Color(170,170,170,alpha5) else Color(255,255,255,alpha5) else if (cl) Color(64,64,64,alpha5) else Color(0,0,0,alpha5),if(it.get())Color(0,255,0,alpha5) else Color(255,0,0,alpha5),buttonsAnim["$moduleName${it.name}Button"]!!)

                                yO += KevinClient.fontManager.font35.fontHeight*0.65F+3.5F
                            }
                            is TextValue -> {
                                glPushMatrix()
                                glScaled(0.65,0.65,0.65)

                                //Alpha4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4 == 0) {
                                    glPopMatrix()
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                KevinClient.fontManager.font35.drawString(it.name,(startX.toFloat()+1F)/0.65F,(startY.toFloat()+yO)/0.65F,if(isLight())Color(0,0,0,alpha4).rgb else Color(255,255,255,alpha4).rgb)

                                yO += KevinClient.fontManager.font35.fontHeight*0.65F
                                glPopMatrix()
                                glPushMatrix()
                                glScaled(0.5,0.5,0.5)
                                val tl = arrayListOf<String>()
                                var tt = ""
                                val tl2 = it.get().toCharArray()
                                var co = 0
                                tl2.forEach { ch ->
                                    if (KevinClient.fontManager.font35.getStringWidth("$tt$ch")*0.5<endX-startX){
                                        tt += ch
                                    } else {
                                        tl += tt
                                        tt = "$ch"
                                    }
                                    co += 1
                                    if (co == tl2.size&&tt.isNotEmpty()) tl += tt
                                }
                                tl.forEach { s ->
                                    //Alpha5
                                    val alpha5 = getAlpha(animLineY,yO+startY.toFloat(), KevinClient.fontManager.font35.fontHeight*0.5F)
                                    if (alpha5 == 0) {
                                        glPopMatrix()
                                        glDisable(GL_SCISSOR_TEST)
                                        return
                                    }
                                    KevinClient.fontManager.font35.drawString(s,(startX.toFloat()+1F)/0.5F,(yO+startY.toFloat())/0.5F,Color(0,111,255,alpha5).rgb)

                                    yO += KevinClient.fontManager.font35.fontHeight*0.5F
                                }
                                glPopMatrix()
                                yO += 3F
                            }
                            is IntegerValue -> {
                                glPushMatrix()
                                glScaled(0.65,0.65,0.65)

                                //Alpha4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4==0) {
                                    glPopMatrix()
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                KevinClient.fontManager.font35.drawString(it.name,(startX.toFloat()+1F)/0.65F,(startY.toFloat()+yO)/0.65F,if(isLight())Color(0,0,0,alpha4).rgb else Color(255,255,255,alpha4).rgb)

                                glPopMatrix()
                                glPushMatrix()
                                glScaled(0.55,0.55,0.55)
                                val ra = (KevinClient.fontManager.font35.fontHeight*0.65F- KevinClient.fontManager.font35.fontHeight*0.55F)/2F

                                //Alpha5
                                val alpha5 = getAlpha(animLineY,startY.toFloat()+yO+ra,startY.toFloat()+yO+ra+ KevinClient.fontManager.font35.fontHeight*0.55F)
                                if (alpha5==0) {
                                    glPopMatrix()
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                KevinClient.fontManager.font35.drawString("${it.get()}",(endX.toFloat()-5F- KevinClient.fontManager.font35.getStringWidth("${it.get()}")*0.55F)/0.55F,(startY.toFloat()+yO+ra)/0.55F,if(isLight())Color(0,0,0,alpha5).rgb else Color(255,255,255,alpha5).rgb)

                                yO += KevinClient.fontManager.font35.fontHeight*0.65F
                                glPopMatrix()
                                yO += 1.5F

                                //Alpha6
                                val alpha6 = getAlpha(animLineY,startY.toFloat()+yO-1.5F,startY.toFloat()+yO+1.5F)
                                if (alpha6 == 0) {
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                RenderUtils.drawLineStart(Color(0,111,255,alpha6),4F)
                                RenderUtils.drawLine(startX+5.0,startY+yO.toDouble(),endX-5.0,startY+yO.toDouble())
                                RenderUtils.drawLineEnd()

                                val isClicked = lastTickClickValue=="$moduleName${it.name}"
                                val x = if (isClicked) when{
                                    mouseX.toFloat() in startX+5.0..endX-5.0 -> mouseX.toDouble()
                                    mouseX.toFloat() < startX+5.0 -> startX+5.0
                                    else -> endX-5.0
                                } else {
                                    val dv = (it.get() - it.minimum).toFloat() / (it.maximum - it.minimum).toFloat()
                                    val dl = ((endX-5.0)-(startX+5.0)) * dv
                                    startX+5.0 + dl
                                }
                                val y = startY+yO
                                val isMouseOn = isClick(mouseX.toFloat(),mouseY.toFloat(),x.toFloat()-2F,y.toFloat()-2F,x.toFloat()+2F,y.toFloat()+2F) || isClicked
                                RenderUtils.drawSector(x,y,0,360,if(isClicked) 2.5 else 2.0,if (isMouseOn) Color(0,150,255,alpha6) else Color(0,111,255,alpha6))
                                if (isClicked){
                                    val i = MathHelper.clamp_double((x - (startX+5.0)) / ((endX-5.0)-(startX+5.0)),0.0,1.0)
                                    it.set((it.minimum + (it.maximum - it.minimum) * i).toInt())
                                }
                                yO += 1.5F
                                yO += 4F
                            }
                            is FloatValue -> {
                                glPushMatrix()
                                glScaled(0.65,0.65,0.65)

                                //Alpha4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4 == 0) {
                                    glPopMatrix()
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                KevinClient.fontManager.font35.drawString(it.name,(startX.toFloat()+1F)/0.65F,(startY.toFloat()+yO)/0.65F,if(isLight())Color(0,0,0,alpha4).rgb else Color(255,255,255,alpha4).rgb)

                                glPopMatrix()
                                glPushMatrix()
                                glScaled(0.55,0.55,0.55)
                                val ra = (KevinClient.fontManager.font35.fontHeight*0.65F- KevinClient.fontManager.font35.fontHeight*0.55F)/2F

                                //Alpha5
                                val alpha5 = getAlpha(animLineY,startY.toFloat()+yO+ra,startY.toFloat()+yO+ra+ KevinClient.fontManager.font35.fontHeight*0.55F)
                                if (alpha5 == 0) {
                                    glPopMatrix()
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                KevinClient.fontManager.font35.drawString("${it.get()}",(endX.toFloat()-5F- KevinClient.fontManager.font35.getStringWidth("${it.get()}")*0.55F)/0.55F,(startY.toFloat()+yO+ra)/0.55F,if(isLight())Color(0,0,0,alpha5).rgb else Color(255,255,255,alpha5).rgb)

                                yO += KevinClient.fontManager.font35.fontHeight*0.65F
                                glPopMatrix()
                                yO += 1.5F

                                //Alpha6
                                val alpha6 = getAlpha(animLineY,startY.toFloat()+yO-1.5F,startY.toFloat()+yO+1.5F)
                                if (alpha6==0) {
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                RenderUtils.drawLineStart(Color(0,111,255,alpha6),4F)
                                RenderUtils.drawLine(startX+5.0,startY+yO.toDouble(),endX-5.0,startY+yO.toDouble())
                                RenderUtils.drawLineEnd()

                                val isClicked = lastTickClickValue=="$moduleName${it.name}"
                                val x = if (isClicked) when{
                                    mouseX.toFloat() in startX+5.0..endX-5.0 -> mouseX.toDouble()
                                    mouseX.toFloat() < startX+5.0 -> startX+5.0
                                    else -> endX-5.0
                                } else {
                                    val dv = (it.get() - it.minimum)/(it.maximum - it.minimum)
                                    val dl = ((endX-5.0)-(startX+5.0)) * dv
                                    startX+5.0 + dl
                                }
                                val y = startY+yO
                                val isMouseOn = isClick(mouseX.toFloat(),mouseY.toFloat(),x.toFloat()-2F,y.toFloat()-2F,x.toFloat()+2F,y.toFloat()+2F) || isClicked
                                RenderUtils.drawSector(x,y,0,360,if(isClicked) 2.5 else 2.0,if (isMouseOn) Color(0,150,255,alpha6) else Color(0,111,255,alpha6))
                                if (isClicked){
                                    val i = MathHelper.clamp_double((x - (startX+5.0)) / ((endX-5.0)-(startX+5.0)),0.0,1.0)
                                    it.set(String.format("%.2f",(it.minimum + (it.maximum - it.minimum) * i)).toFloat())
                                }
                                yO += 1.5F
                                yO += 4F
                            }
                            is ListValue -> {
                                if(listOpen["$moduleName${it.name}"]==null) listOpen["$moduleName${it.name}"] = false
                                val open = listOpen["$moduleName${it.name}"]!!
                                if (listAnim["$moduleName${it.name}"]!=null) animList("$moduleName${it.name}",5F,open)
                                if(listAnim["$moduleName${it.name}"]==null) listAnim["$moduleName${it.name}"] = 0F
                                glPushMatrix()
                                glScaled(0.65,0.65,0.65)

                                //Alpha4
                                val alpha4 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F)
                                if (alpha4 == 0) {
                                    glPopMatrix()
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                KevinClient.fontManager.font35.drawString(it.name,(startX.toFloat()+1F)/0.65F,(startY.toFloat()+yO)/0.65F,if(isLight())Color(0,0,0,alpha4).rgb else Color(255,255,255,alpha4).rgb)

                                //KevinClient.fontManager.font35!!.drawString("<",(endX.toFloat()-5F-KevinClient.fontManager.font35!!.getStringWidth("<")*0.65F)/0.65F,(startY.toFloat()+yO)/0.65F,if(open) Color(0,111,255).rgb else if(isLight())Color.black.rgb else Color.white.rgb)
                                val p1x = (endX.toFloat()-5F-5F)/.65
                                val p1y = (startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.65F/2)/.65
                                val p23x = (endX.toFloat()-5F)/.65
                                //Alpha5
                                val alpha5 = getAlpha(animLineY,p1y.toFloat()*.65F+ (-KevinClient.fontManager.font35.fontHeight*0.65F/2+1F),p1y.toFloat()*.65F+(+KevinClient.fontManager.font35.fontHeight*0.65F/2-1F))
                                if (alpha5 == 0) {
                                    glPopMatrix()
                                    glDisable(GL_SCISSOR_TEST)
                                    return
                                }
                                GlStateManager.disableCull()
                                glEnable(GL_BLEND)
                                glDisable(GL_TEXTURE_2D)
                                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                                val isMouseOn = isClick(mouseX.toFloat(),mouseY.toFloat(),(p1x*.65F+(p23x*.65F-p1x*.65F)/2.0F).toFloat()-((p23x*.65F-p1x*.65F)/2.0F).toFloat(),p1y.toFloat()*.65F+ (-KevinClient.fontManager.font35.fontHeight*0.65F/2+1F),(p1x*.65F+(p23x*.65F-p1x*.65F)/2.0F).toFloat()+(p23x*.65F-p1x*.65F).toFloat()/2.0F,p1y.toFloat()*.65F+(+KevinClient.fontManager.font35.fontHeight*0.65F/2-1F))
                                glColor(if(open) if (isMouseOn) Color(0,150,255,alpha5) else Color(0,111,255,alpha5) else if(isLight()) if (isMouseOn) Color(170,170,170,alpha5) else Color(0,0,0,alpha5) else if (isMouseOn) Color(192,192,192,alpha5) else Color(255,255,255,alpha5))
                                glTranslated(p1x+(p23x-p1x)/2.0,p1y,0.0)
                                glRotated((listAnim["$moduleName${it.name}"]!!/100.0)*-90.0,.0,.0,1.0)
                                glBegin(GL_TRIANGLES)
                                glVertex3d(-((p23x-p1x)/2.0),.0,.0)
                                glVertex3d((p23x-p1x)/2.0,(-KevinClient.fontManager.font35.fontHeight*0.65F/2+1F)/.65,.0)
                                glVertex3d((p23x-p1x)/2.0,(+KevinClient.fontManager.font35.fontHeight*0.65F/2-1F)/.65,.0)
                                glEnd()
                                glEnable(GL_TEXTURE_2D)
                                glDisable(GL_BLEND)

                                yO += KevinClient.fontManager.font35.fontHeight*0.65F
                                glPopMatrix()
                                if (open||listAnim["$moduleName${it.name}"] != 0F) {
                                    var ly = 0F
                                    it.values.forEach { _ ->
                                        ly += KevinClient.fontManager.font35.fontHeight*0.6F
                                    }
                                    glPushMatrix()
                                    glScaled(.6, .6, .6)
                                    val animY= ly * listAnim["$moduleName${it.name}"]!! / 100F
                                    val yOf = yO + animY
                                    it.values.forEach vfe@ { v ->
                                        //Alpha6
                                        val alpha6 = getAlpha(animLineY,startY.toFloat()+yO,startY.toFloat()+yO+ KevinClient.fontManager.font35.fontHeight*0.6F)
                                        if (alpha6 == 0) {
                                            glPopMatrix()
                                            glDisable(GL_SCISSOR_TEST)
                                            return
                                        }
                                        val alpha = if (alpha6!=255) alpha6 else when {
                                            yOf > yO+ KevinClient.fontManager.font35.fontHeight*0.6F -> 255
                                            yOf in yO..yO+ KevinClient.fontManager.font35.fontHeight*0.6F -> {
                                                val ya = yOf-yO
                                                ((ya/(KevinClient.fontManager.font35.fontHeight*0.6F))*255).toInt()
                                            }
                                            else -> 0
                                        }
                                        if (alpha == 0) return@vfe
                                        val w = Color(255,255,255,alpha)
                                        val b = Color(0,0,0,alpha)
                                        KevinClient.fontManager.font35.drawString(v,(endX.toFloat()-10F- KevinClient.fontManager.font35.getStringWidth(v)*0.6F)/0.6F,(startY.toFloat()+yO)/0.6F,if(it.get()==v) Color(0,111,255,alpha).rgb else if(isLight()) b.rgb else w.rgb)
                                        yO += KevinClient.fontManager.font35.fontHeight*0.6F
                                    }
                                    glPopMatrix()
                                }
                                yO += 3F
                            }
                        }
                    }
                    canSettingsR2 = yO+startY>endY

                    glDisable(GL_SCISSOR_TEST)
                }
                break
            }
        }
    }
}