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

import blur.BloomUtil
import blur.GaussianBlur
import blur.KawaseBlur.createFrameBuffer
import kevin.persional.milk.guis.font.FontLoaders
import kevin.persional.milk.utils.StencilUtil
import kevin.font.RainbowFontShader
import kevin.hud.designer.GuiHudDesigner
import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side
import kevin.main.KevinClient
import kevin.module.*
import kevin.utils.AnimationUtils
import kevin.utils.MilkUtils
import kevin.utils.RenderUtils
import kevin.utils.render.shader.shaders.RainbowShader
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.ceil
import kotlin.math.sin


@ElementInfo(name = "Arraylist", single = true)
class Arraylist(x: Double = 1.0, y: Double = 2.0, scale: Float = 1F,
                side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.UP)) : Element(x, y, scale, side) {

    fun SkyRainbow(var2: Int, st: Float, bright: Float): Int {
        var v1 = ceil((System.currentTimeMillis() + (var2 * 109).toLong()).toDouble()) / 5
        return Color.getHSBColor(if ((360.0.also { v1 %= it } / 360.0).toFloat()
                .toDouble() < 0.5) -(v1 / 360.0).toFloat() else (v1 / 360.0).toFloat(), st, bright).rgb
    }
    private var fontValue = FontLoaders.novo20
    private var modules = emptyList<Module>()

    private val tagMode = ListValue("ArrayList-TagMode", arrayOf("<>","[]","None"),"<>")
    private val fontMode = ListValue("Font-Mode", arrayOf("16","18","20","22"),"20")
    private val arrayListRainbowX = FloatValue("ArrayList-Rainbow-X",-1000F, -2000F, 2000F)
    private val arrayListRainbowY = FloatValue("ArrayList-Rainbow-Y",-1000F, -2000F, 2000F)
    private val arrayListTextColorMode = ListValue("ArrayList-TextColor-Mode", arrayOf("Custom","Random","SkyRainbow",
        "Rainbow","LDColor","Test"),"Rainbow")
    // Custom -> 0
    // Random -> 1
    // SkyRainbow -> 2
    // Rainbow -> 3
    // LDColor -> 4
    // Test -> 5
    private var textColorCode = 0
    private val testMode = ListValue("TestColor-Mode", arrayOf("Night","Stichh","2","3"),"Stichh")
    private val blurMode = ListValue("Blur-Mode", arrayOf("None","Kawase","Gaussian"),"None")
    private val bloomMode = ListValue("Bloom-Mode", arrayOf("None","TextColor","BackGroundColor"),"None")
    private val blurStrength = FloatValue("BlurStrength",10F, 6F, 40F)
    private val arraylistTextCustomRed = IntegerValue("ArrayList-TextColor-CustomRed",0,0,255)
    private val arraylistTextCustomGreen = IntegerValue("ArrayList-TextColor-CustomGreen",0,0,255)
    private val arraylistTextCustomBlue = IntegerValue("ArrayList-TextColor-CustomBlue",0,0,255)
    private val arraylistTextSaturation = IntegerValue("ArrayList-TextColor-SkyRainbow-Saturation",0,0,100)
    private val arraylistTextBrightness = IntegerValue("ArrayList-TextColor-SkyRainbow-Brightness",0,0,100)
    private val arrayListTags = BooleanValue("ArrayList-Tags", true)
    private val arrayListTagsShadow = BooleanValue("ArrayList-ShadowText", true)
    private val arrayListBackgroundColorModeValue = ListValue("ArrayList-Background-ColorMode", arrayOf("Custom", "Random", "Rainbow"), "Custom")
    private val arrayListBackgroundColorRedValue = IntegerValue("ArrayList-Background-R", 0, 0, 255)
    private val arrayListBackgroundColorGreenValue = IntegerValue("ArrayList-Background-G", 0, 0, 255)
    private val arrayListBackgroundColorBlueValue = IntegerValue("ArrayList-Background-B", 0, 0, 255)
    private val arrayListBackgroundColorAlphaValue = IntegerValue("ArrayList-Background-Alpha", 0, 0, 255)
    private val arrayListUpperCaseValue = BooleanValue("ArrayList-UpperCase", false)
    private val arrayListSpaceValue = FloatValue("ArrayList-Space", 0F, 0F, 5F)
    private val arrayListTextHeightValue = FloatValue("ArrayList-TextHeight", 11F, 1F, 20F)
    private val arrayListTextYValue = FloatValue("ArrayList-TextY", 1F, 0F, 20F)
    private val arrayListTagsArrayColor = BooleanValue("ArrayList-TagsArrayColor", false)
    private val arrayListSaturationValue = FloatValue("ArrayList-Random-Saturation", 0.9f, 0f, 1f)
    private val arrayListBrightnessValue = FloatValue("ArrayList-Random-Brightness", 1f, 0f, 1f)
    private val arrayListRectColorModeValue = ListValue("ArrayList-Rect-Color", arrayOf("Custom", "Random", "Rainbow","Test","SkyRainbow"), "Rainbow")
    // Custom -> 0
    // Random -> 1
    // Rainbow -> 2
    // Test -> 3
    // SkyRainbow -> 4
    private var arrayListRectColorModeCode = 0
    private val arrayListRectColorRedValue = IntegerValue("ArrayList-Rect-R", 255, 0, 255)
    private val arrayListRectColorGreenValue = IntegerValue("ArrayList-Rect-G", 255, 0, 255)
    private val arrayListRectColorBlueValue = IntegerValue("ArrayList-Rect-B", 255, 0, 255)
    private val arrayListRectColorBlueAlpha = IntegerValue("ArrayList-Rect-Alpha", 255, 0, 255)
    private val hideRenderModuleValue = BooleanValue("HideRenderModule", false)
    private val arrayListRectValue = ListValue("ArrayList-Rect", arrayOf("None", "Left", "Right", "All"), "All")
    // None -> 0
    // Left -> 1
    // Right -> 2
    // All -> 3
    private var rectCode = 0
    private val arrayListRectWidth = FloatValue("ArrayList-RectWidth",2F,1F,3F)

    private var hasChanged = true
    private var rainbowTick = 0
    var bloomFramebuffer: Framebuffer? = null

    override fun drawElement(): Border? {
        val fontRenderer = fontValue
        val tagMode = tagMode.get()
        val tagLeft = if (tagMode == "<>") "<" else if (tagMode == "[]") "[" else ""
        val tagRight = if (tagMode == "<>") ">" else if (tagMode == "[]") "]" else ""
        val rainbowX = arrayListRainbowX
        val rainbowY = arrayListRainbowY
        val tags = arrayListTags
        val upperCaseValue = arrayListUpperCaseValue
        val tagsArrayColor = arrayListTagsArrayColor
        val rectWidth = arrayListRectWidth

        //AWTFontRenderer.assumeNonVolatile = true

        // Slide animation - update every render
        val delta = RenderUtils.deltaTime

        for (module in KevinClient.moduleManager.getModules()) {
            if (!module.array || (!module.state && module.slide == 0F)) continue

            var displayString = if (!tags.get())
                module.name
            else if (tagsArrayColor.get())
                module.getColorlessTagName(tagLeft,tagRight)
            else module.getTagName(tagLeft,tagRight)

            if (upperCaseValue.get())
                displayString = displayString.uppercase()

            val width = fontRenderer.getStringWidth(displayString)

            if (module.state) {
                if (module.slide < width) {
                    module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                    module.slideStep += delta / 4F
                }
            } else if (module.slide > 0) {
                module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                module.slideStep -= delta / 4F
            }

            module.slide = module.slide.coerceIn(0F, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
        }

        // Draw arraylist
        val backgroundColorMode = arrayListBackgroundColorModeValue.get()
        val customColor = Color(arraylistTextCustomRed.get(), arraylistTextCustomGreen.get(), arraylistTextCustomBlue.get(), 1).rgb
        val rectCustomColor = Color(arrayListRectColorRedValue.get(), arrayListRectColorGreenValue.get(), arrayListRectColorBlueValue.get(),
            arrayListRectColorBlueAlpha.get()).rgb
        val space = arrayListSpaceValue.get()
        val textHeight = arrayListTextHeightValue.get()
        val textY = arrayListTextYValue.get()
        val backgroundCustomColor = Color(arrayListBackgroundColorRedValue.get(), arrayListBackgroundColorGreenValue.get(),
            arrayListBackgroundColorBlueValue.get(), arrayListBackgroundColorAlphaValue.get()).rgb
        val textShadow = arrayListTagsShadow.get()
        val textSpacer = textHeight + space
        val saturation = arrayListSaturationValue.get()
        val brightness = arrayListBrightnessValue.get()

        GL11.glTranslated(-renderX, -renderY, 0.0)
        //绘制Bloom & Blur
        if(blurMode.get() != "None") {
            StencilUtil.initStencilToWrite()
            GL11.glColor3f(0f, 0f, 0f)
            modules.forEachIndexed { index, module ->
                val xPos = -module.slide - 2 + renderX.toFloat()
                val yPos = renderY.toFloat() + (if (side.vertical == Side.Vertical.DOWN) -textSpacer else textSpacer) *
                        (if (side.vertical == Side.Vertical.DOWN) index + 1 else index)
                RenderUtils.drawRectVertex(
                    xPos - (if (rectCode > 1/*rectMode.equals("right", true) || rectMode.equals("all", true)*/)
                        rectWidth.get() + 2F else 2F),
                    yPos,
                    renderX.toFloat() + (if (rectCode > 1/*rectMode.equals("right", true) || rectMode.equals("all", true)*/) -rectWidth.get() else 0F),
                    yPos + textHeight
                )
            }
            StencilUtil.readStencilBuffer(1)
            GaussianBlur.renderBlur(blurStrength.get())
            StencilUtil.uninitStencilBuffer()
        }

        //Bloom
        if(bloomMode.get() != "None") {
            bloomFramebuffer = createFrameBuffer(bloomFramebuffer)
            bloomFramebuffer!!.framebufferClear()
            bloomFramebuffer!!.bindFramebuffer(true)
            modules.forEachIndexed { index, module ->
                val xPos = -module.slide - 2 + renderX.toFloat()
                val yPos = renderY.toFloat() + (if (side.vertical == Side.Vertical.DOWN) -textSpacer else textSpacer) *
                        (if (side.vertical == Side.Vertical.DOWN) index + 1 else index)
                    RenderUtils.drawRect(
                        xPos - if (rectCode > 1/*rectMode.equals("right", true)||rectMode.equals("all",true)*/)
                            rectWidth.get() + 2F else 2F,
                        yPos,
                        renderX.toFloat() + if (rectCode > 1/*rectMode.equals("right", true)||rectMode.equals("all",true)*/)
                            -rectWidth.get() else 0F,
                        yPos + textHeight, -1
                    )
            }
            bloomFramebuffer!!.unbindFramebuffer()
            BloomUtil.renderBlur(bloomFramebuffer!!.framebufferTexture, 6, 2 , Color(0,0,0))
        }
        GL11.glTranslated(renderX, renderY, 0.0)
        modules.forEachIndexed { index, module ->
            var displayString = if (!tags.get())
                module.name
            else if (tagsArrayColor.get())
                module.getColorlessTagName(tagLeft,tagRight)
            else module.getTagName(tagLeft,tagRight)

            if (upperCaseValue.get())
                displayString = displayString.uppercase()

            val xPos = -module.slide - 2
            val yPos = (if (side.vertical == Side.Vertical.DOWN) -textSpacer else textSpacer) *
                    if (side.vertical == Side.Vertical.DOWN) index + 1 else index
            val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb

            val backgroundRectRainbow = backgroundColorMode.equals("Rainbow", ignoreCase = true)

            RainbowShader.begin(backgroundRectRainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                RenderUtils.drawRect(
                    xPos - if (rectCode > 1/*rectMode.equals("right", true)||rectMode.equals("all",true)*/) rectWidth.get() + 2F else 2F,
                    yPos,
                    if (rectCode > 1/*rectMode.equals("right", true)||rectMode.equals("all",true)*/) -rectWidth.get() else 0F,
                    yPos + textHeight,
                    when {
                        backgroundRectRainbow -> 0xFF shl 24
                        backgroundColorMode.equals("Random", ignoreCase = true) -> moduleColor
                        else -> backgroundCustomColor
                    }
                )
            }

            val tmp = Color(Color.HSBtoRGB(
                (mc.thePlayer.ticksExisted.toDouble() / 50.0 + sin((rainbowTick + (yPos - 4) / 12).toDouble() / 50.0 * 1.6)).toFloat() % 1.0f,
                0.5f,
                1f
            ))
            val ldColor = Color(
                arraylistTextCustomRed.get(),
                arraylistTextCustomGreen.get(),
                arraylistTextCustomBlue.get(),
                tmp.red
            ).rgb
//            GlStateManager.resetColor()
            RainbowFontShader.begin(textColorCode == 1, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                fontRenderer.drawString(displayString,
                    (xPos - if (rectCode > 1/*rectMode.equals("right", true)||rectMode.equals("all",true)*/) rectWidth.get() else 0F).toDouble(),
                    (yPos + textY).toDouble(),
                    when (textColorCode) {
                        3/*rainbow*/ -> 0
                        1/*colorMode.equals("Random", ignoreCase = true)*/ -> moduleColor
                        2/*colorMode.equals("SkyRainbow", ignoreCase = true)*/ -> SkyRainbow((yPos * 0.1F).toInt(),
                        arraylistTextSaturation.get() / 100f,
                        arraylistTextBrightness.get() / 100f)
                        4/*colorMode.equals("LDColor", ignoreCase = true)*/ -> ldColor
                        5/*colorMode.equals("Test", ignoreCase = true)*/ ->
                            MilkUtils.getMixedColor(yPos.toInt() * 30 ,10, testMode.get()).rgb
                        else -> customColor
                                         }
                    , textShadow)
            }

            if (rectCode > 0/*!rectMode.equals("none", true)*/) {
                /*val rectRainbow = rectColorMode.equals("Rainbow", ignoreCase = true)*/

                RainbowShader.begin(arrayListRectColorModeCode == 2, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                    val rectColor = when (arrayListRectColorModeCode) {
                        2/*rectRainbow*/ -> 0
                        1/*rectColorMode.equals("Random", ignoreCase = true)*/ -> moduleColor
                        3/*rectColorMode.equals("Test", ignoreCase = true)*/ -> MilkUtils.getMixedColor(yPos.toInt() * 30 ,10, testMode.get()).rgb
                        4/*rectColorMode.equals("SkyRainbow", ignoreCase = true)*/ -> SkyRainbow((yPos * 0.1F).toInt(),
                            arraylistTextSaturation.get() / 100f,
                            arraylistTextBrightness.get() / 100f)
                        else -> rectCustomColor
                    }

                    when (rectCode) {
                        1/*rectMode.equals("left", true)*/ -> RenderUtils.drawRect(xPos - rectWidth.get() - 2, yPos, xPos - 2, yPos + textHeight,
                            rectColor)
                        2/*rectMode.equals("right", true)*/ -> RenderUtils.drawRect(-rectWidth.get()+1.1F, yPos, 1.1F,
                            yPos + textHeight, rectColor)
                        3/*rectMode.equals("all",true)*/ -> {
                            RenderUtils.drawRect(xPos - rectWidth.get()*2 - 3, yPos, xPos - 3 - rectWidth.get(), yPos + textHeight, rectColor)
                            RenderUtils.drawRect(-rectWidth.get()+1.1F, yPos, 0.0F, yPos + textHeight, rectColor)
                            if (index + 1 <= modules.size - 1){
                                val m = modules[index+1]
                                val mPosX = -m.slide - 2
                                val x1 = xPos - rectWidth.get()*2 - 3
                                val x2 = mPosX - rectWidth.get()*2 - 3
                                if (x1<x2)
                                    RenderUtils.drawRect(x1, yPos + textHeight, x2, yPos + textHeight + 1, rectColor)
                                else
                                    RenderUtils.drawRect(x2, yPos + textHeight - 1, x1, yPos + textHeight, rectColor)
                            }else{
                                RenderUtils.drawRect(xPos - rectWidth.get()*2 - 3, yPos + textHeight, 0.0F, yPos + textHeight + 1, rectColor)
                            }
                        }
                    }
                }
            }
        }

        if ((mc.currentScreen) is GuiHudDesigner) {
            hasChanged = true
            var x2 = Int.MIN_VALUE

            if (modules.isEmpty()) {
                return if (side.horizontal == Side.Horizontal.LEFT)
                    Border(0F, -1F, 20F, 20F)
                else
                    Border(0F, -1F, -20F, 20F)
            }

            for (module in modules) {
                when (side.horizontal) {
                    Side.Horizontal.RIGHT, Side.Horizontal.MIDDLE -> {
                        val xPos = -module.slide.toInt() - 2
                        if (x2 == Int.MIN_VALUE || xPos < x2) x2 = xPos
                    }
                    Side.Horizontal.LEFT -> {
                        val xPos = module.slide.toInt() + 14
                        if (x2 == Int.MIN_VALUE || xPos > x2) x2 = xPos
                    }
                }
            }
            val y2 = (if (side.vertical == Side.Vertical.DOWN) -textSpacer else textSpacer) * modules.size

            return Border(0F, 0F, x2 - 7F, y2 - if (side.vertical == Side.Vertical.DOWN) 1F else 0F)
        }

        //AWTFontRenderer.assumeNonVolatile = false
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        return null
    }

    override fun updateElement() {
        if (rainbowTick++ > 50) {
            rainbowTick = 0
        }
        when(fontMode.get()){
            "16"->{
                fontValue = FontLoaders.novo16
            }
            "18"->{
                fontValue = FontLoaders.novo18
            }
            "20"->{
                fontValue = FontLoaders.novo20
            }
            "22"->{
                fontValue = FontLoaders.novo22
            }
        }
        if (hasChanged) {
            hasChanged = false
            rectCode = when {
                arrayListRectValue equal "Left" -> 1
                arrayListRectValue equal "Right" -> 2
                arrayListRectValue equal "All" -> 3
                else -> 0
            }
            textColorCode = when {
                arrayListTextColorMode equal "Random" -> 1
                arrayListTextColorMode equal "SkyRainbow" -> 2
                arrayListTextColorMode equal "Rainbow" -> 3
                arrayListTextColorMode equal "LDColor" -> 4
                arrayListTextColorMode equal "Test" -> 5
                else -> 0
            }
            if (rectCode > 0) arrayListRectColorModeCode = when {
                arrayListRectColorModeValue equal "Random" -> 1
                arrayListRectColorModeValue equal "Rainbow" -> 2
                arrayListRectColorModeValue equal "Test" -> 3
                arrayListRectColorModeValue equal "SkyRainbow" -> 4
                else -> 0
            }
        }
        val tagMode = this.tagMode.get()
        val tagLeft = if (tagMode == "<>") "<" else if (tagMode == "[]") "[" else ""
        val tagRight = if (tagMode == "<>") ">" else if (tagMode == "[]") "]" else ""
        val tags = arrayListTags
        val upperCaseValue = arrayListUpperCaseValue
        val tagsArrayColor = arrayListTagsArrayColor
        modules = KevinClient.moduleManager.getModules()
            .filter { it.array && it.slide > 0 && (!hideRenderModuleValue.get() || it.category != ModuleCategory.RENDER) }
            .sortedBy { -fontValue.getStringWidth(if (upperCaseValue.get()) (if (!tags.get()) it.name else if (tagsArrayColor.get()) it.getColorlessTagName(tagLeft,tagRight) else it.getTagName(tagLeft,tagRight)).uppercase() else if (!tags.get()) it.name else if (tagsArrayColor.get()) it.getColorlessTagName(tagLeft,tagRight) else it.getTagName(tagLeft,tagRight)) }
    }
}