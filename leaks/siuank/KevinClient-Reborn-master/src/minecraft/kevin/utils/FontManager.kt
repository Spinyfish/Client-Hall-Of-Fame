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
package kevin.utils

import com.google.gson.*
import kevin.event.TextEvent
import kevin.main.KevinClient
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.*
import java.awt.*
import java.awt.image.BufferedImage
import java.io.*

class FontManager : MinecraftInstance(){
    @Retention(AnnotationRetention.RUNTIME)
    annotation class FontDetails(val fontName: String, val fontSize: Int = -1)

    @FontDetails(fontName = "Minecraft Font")
    val minecraftFont: FontRenderer = mc.fontRendererObj
    private val CUSTOM_FONT_RENDERERS: HashMap<FontInfo, GameFontRenderer> = HashMap()

    @FontDetails(fontName = "JetBrainsMono Medium", fontSize = 35)
    var font35: GameFontRenderer? = null

    @FontDetails(fontName = "JetBrainsMono Medium", fontSize = 40)
    var font40: GameFontRenderer? = null

    @FontDetails(fontName = "Novo 35", fontSize = 35)
    var novoFont35: GameFontRenderer? = null
    @FontDetails(fontName = "Novo 40", fontSize = 40)
    var novoFont40: GameFontRenderer? = null

    @FontDetails(fontName = "JetBrainsMono Bold", fontSize = 180)
    var fontBold180: GameFontRenderer? = null

    fun loadFonts(){
        font35 = GameFontRenderer(getFont("JetBrainsMono-Medium.ttf",35))
        font40 = GameFontRenderer(getFont("JetBrainsMono-Medium.ttf",40))
        novoFont35 = GameFontRenderer(getFont("Novo.ttf",35))
        novoFont40 = GameFontRenderer(getFont("Novo.ttf",40))
        fontBold180 = GameFontRenderer(getFont("JetBrainsMono-Bold.ttf",180))
        try {
            CUSTOM_FONT_RENDERERS.clear()
            val fontsFile = File(KevinClient.fileManager.fontsDir, "fonts.json")
            if (fontsFile.exists()) {
                val jsonElement = JsonParser().parse(BufferedReader(FileReader(fontsFile)))
                if (jsonElement is JsonNull) return
                val jsonArray = jsonElement as JsonArray
                for (element in jsonArray) {
                    if (element is JsonNull) return
                    val fontObject = element as JsonObject
                    val font = this.getFont(
                        fontObject["fontFile"].asString,
                        fontObject["fontSize"].asInt
                    )
                    CUSTOM_FONT_RENDERERS[FontInfo(font)] = GameFontRenderer(font)
                }
            } else {
                fontsFile.createNewFile()
                val printWriter = PrintWriter(FileWriter(fontsFile))
                printWriter.println(GsonBuilder().setPrettyPrinting().create().toJson(JsonArray()))
                printWriter.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    class FontInfo(val name: String?, val fontSize: Int) {

        constructor(font: Font) : this(font.name, font.size) {}

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val fontInfo = other as FontInfo
            return if (fontSize != fontInfo.fontSize) false else name == fontInfo.name
        }

        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + fontSize
            return result
        }
    }

    fun getFontRenderer(name: String?, size: Int): GameFontRenderer {
        for (field in this::class.java.declaredFields) {
            try {
                field.isAccessible = true
                val o = field[null]
                if (o is GameFontRenderer) {
                    val fontDetails = field.getAnnotation(FontDetails::class.java)
                    if (fontDetails.fontName == name && fontDetails.fontSize === size) return o
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return CUSTOM_FONT_RENDERERS.getOrDefault(
            FontInfo(
                name,
                size
            ), font40!!
        )
    }

    fun getFontDetails(fontRenderer: GameFontRenderer): FontInfo? {
        for (field in this::class.java.declaredFields) {
            try {
                field.isAccessible = true
                val o = field[null]
                if (o == fontRenderer) {
                    val fontDetails = field.getAnnotation(FontDetails::class.java)
                    return FontInfo(
                        fontDetails.fontName,
                        fontDetails.fontSize
                    )
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        for ((key, value) in CUSTOM_FONT_RENDERERS.entries) {
            if (value === fontRenderer) return key
        }
        return null
    }

    fun getFonts(): List<GameFontRenderer> {
        val fonts: MutableList<GameFontRenderer> = ArrayList()
        for (fontField in this::class.java.declaredFields) {
            try {
                fontField.isAccessible = true
                val fontObj = fontField[null]
                if (fontObj is GameFontRenderer) fonts.add(fontObj)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        fonts.addAll(CUSTOM_FONT_RENDERERS.values)
        return fonts
    }

    private fun getFont(fontName: String, size: Int): Font {
        return try {
            val inputStream = FontManager::class.java.getResourceAsStream("fonts/$fontName")
            var awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream)
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size.toFloat())
            inputStream.close()
            awtClientFont
        } catch (e: Exception) {
            e.printStackTrace()
            Font("default", Font.PLAIN, size)
        }
    }

    interface IFontRenderer {
        fun drawString(s: String?, x: Float, y: Float, color: Int): Int
        fun drawStringWithShadow(text: String?, x: Float, y: Float, color: Int): Int
        fun drawCenteredString(s: String, x: Float, y: Float, color: Int, shadow: Boolean): Int
        fun drawCenteredString(s: String, x: Float, y: Float, color: Int): Int
        fun drawString(text: String?, x: Float, y: Float, color: Int, shadow: Boolean): Int
        fun getColorCode(charCode: Char): Int
        fun getStringWidth(text: String?): Int
        fun getCharWidth(character: Char): Int
    }

    class GameFontRenderer(font: Font) : IFontRenderer {

        val fontHeight: Int
        var defaultFont = AWTFontRenderer(font)
        private var boldFont = AWTFontRenderer(font.deriveFont(Font.BOLD))
        private var italicFont = AWTFontRenderer(font.deriveFont(Font.ITALIC))
        private var boldItalicFont = AWTFontRenderer(font.deriveFont(Font.BOLD or Font.ITALIC))

        val height: Int
            get() = defaultFont.height / 2

        val size: Int
            get() = defaultFont.font.size

        init {
            fontHeight = height
        }

        override fun drawString(s: String?, x: Float, y: Float, color: Int) = drawString(s, x, y, color, false)

        override fun drawStringWithShadow(text: String?, x: Float, y: Float, color: Int) = drawString(text, x, y, color, true)

        override fun drawCenteredString(s: String, x: Float, y: Float, color: Int, shadow: Boolean) = drawString(s, x - getStringWidth(s) / 2F, y, color, shadow)

        override fun drawCenteredString(s: String, x: Float, y: Float, color: Int) =
            drawStringWithShadow(s, x - getStringWidth(s) / 2F, y, color)

        override fun drawString(text: String?, x: Float, y: Float, color: Int, shadow: Boolean): Int {
            var currentText = text

            val event = TextEvent(currentText)
            KevinClient.eventManager.callEvent(event)

            currentText = event.text ?: return 0

            val currY = y - 3F

            val rainbow = RainbowFontShader.isInUse

            if (shadow) {
                GL20.glUseProgram(0)

                drawText(currentText, x + 1f, currY + 1f, Color(0, 0, 0, 150).rgb, true)
            }

            return drawText(currentText, x, currY, color, false, rainbow)
        }

        private fun drawText(text: String?, x: Float, y: Float, color: Int, ignoreColor: Boolean, rainbow: Boolean = false): Int {
            if (text == null)
                return 0
            if (text.isNullOrEmpty())
                return x.toInt()

            val rainbowShaderId = RainbowFontShader.programId

            if (rainbow)
                GL20.glUseProgram(rainbowShaderId)

            GL11.glTranslated(x - 1.5, y + 0.5, 0.0)
            GlStateManager.enableAlpha()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
            GlStateManager.enableTexture2D()

            var currentColor = color

            if (currentColor and -0x4000000 == 0)
                currentColor = currentColor or -16777216

            val defaultColor = currentColor

            val alpha: Int = (currentColor shr 24 and 0xff)

            if (text.contains("§")) {
                val parts = text.split("§")

                var currentFont = defaultFont

                var width = 0.0

                // Color code states
                var randomCase = false
                var bold = false
                var italic = false
                var strikeThrough = false
                var underline = false

                parts.forEachIndexed { index, part ->
                    if (part.isEmpty())
                        return@forEachIndexed

                    if (index == 0) {
                        currentFont.drawString(part, width, 0.0, currentColor)
                        width += currentFont.getStringWidth(part)
                    } else {
                        val words = part.substring(1)
                        val type = part[0]

                        when (val colorIndex = getColorIndex(type)) {
                            in 0..15 -> {
                                if (!ignoreColor) {
                                    currentColor = ColorUtils.hexColors[colorIndex] or (alpha shl 24)

                                    if (rainbow)
                                        GL20.glUseProgram(0)
                                }

                                bold = false
                                italic = false
                                randomCase = false
                                underline = false
                                strikeThrough = false
                            }
                            16 -> randomCase = true
                            17 -> bold = true
                            18 -> strikeThrough = true
                            19 -> underline = true
                            20 -> italic = true
                            21 -> {
                                currentColor = color

                                if (currentColor and -67108864 == 0)
                                    currentColor = currentColor or -16777216

                                if (rainbow)
                                    GL20.glUseProgram(rainbowShaderId)

                                bold = false
                                italic = false
                                randomCase = false
                                underline = false
                                strikeThrough = false
                            }
                        }

                        currentFont = if (bold && italic)
                            boldItalicFont
                        else if (bold)
                            boldFont
                        else if (italic)
                            italicFont
                        else
                            defaultFont

                        currentFont.drawString(if (randomCase) ColorUtils.randomMagicText(words) else words, width, 0.0, currentColor)

                        if (strikeThrough)
                            RenderUtils.drawLine(width / 2.0 + 1, currentFont.height / 3.0,
                                (width + currentFont.getStringWidth(words)) / 2.0 + 1, currentFont.height / 3.0,
                                fontHeight / 16F)

                        if (underline)
                            RenderUtils.drawLine(width / 2.0 + 1, currentFont.height / 2.0,
                                (width + currentFont.getStringWidth(words)) / 2.0 + 1, currentFont.height / 2.0,
                                fontHeight / 16F)

                        width += currentFont.getStringWidth(words)
                    }
                }
            } else {
                // Color code states
                defaultFont.drawString(text, 0.0, 0.0, currentColor)
            }

            GlStateManager.disableBlend()
            GL11.glTranslated(-(x - 1.5), -(y + 0.5), 0.0)
            GL11.glColor4f(1f, 1f, 1f, 1f)

            return (x + getStringWidth(text)).toInt()
        }

        override fun getColorCode(charCode: Char) =
            ColorUtils.hexColors[getColorIndex(charCode)]

        override fun getStringWidth(text: String?): Int {
            var currentText = text

            val event = TextEvent(currentText)
            KevinClient.eventManager.callEvent(event)
            currentText = event.text ?: return 0

            return if (currentText.contains("§")) {
                val parts = currentText.split("§")

                var currentFont = defaultFont
                var width = 0
                var bold = false
                var italic = false

                parts.forEachIndexed { index, part ->
                    if (part.isEmpty())
                        return@forEachIndexed

                    if (index == 0) {
                        width += currentFont.getStringWidth(part)
                    } else {
                        val words = part.substring(1)
                        val type = part[0]
                        val colorIndex = getColorIndex(type)
                        when {
                            colorIndex < 16 -> {
                                bold = false
                                italic = false
                            }
                            colorIndex == 17 -> bold = true
                            colorIndex == 20 -> italic = true
                            colorIndex == 21 -> {
                                bold = false
                                italic = false
                            }
                        }

                        currentFont = if (bold && italic)
                            boldItalicFont
                        else if (bold)
                            boldFont
                        else if (italic)
                            italicFont
                        else
                            defaultFont

                        width += currentFont.getStringWidth(words)
                    }
                }

                width / 2
            } else
                defaultFont.getStringWidth(currentText) / 2
        }

        override fun getCharWidth(character: Char) = getStringWidth(character.toString())

        companion object {
            @JvmStatic
            fun getColorIndex(type: Char): Int {
                return when (type) {
                    in '0'..'9' -> type - '0'
                    in 'a'..'f' -> type - 'a' + 10
                    in 'k'..'o' -> type - 'k' + 16
                    'r' -> 21
                    else -> -1
                }
            }
        }
    }
    class AWTFontRenderer(val font: Font, startChar: Int = 0, stopChar: Int = 255, var loadingScreen: Boolean = false) : MinecraftInstance() {
        companion object {
            var assumeNonVolatile: Boolean = false
            val activeFontRenderers: ArrayList<AWTFontRenderer> = ArrayList()

            private var gcTicks: Int = 0
            private const val GC_TICKS = 600 // Start garbage collection every 600 frames
            private const val CACHED_FONT_REMOVAL_TIME = 30000 // Remove cached texts after 30s of not being used

            fun garbageCollectionTick() {
                if (gcTicks++ > GC_TICKS) {
                    activeFontRenderers.forEach { it.collectGarbage() }

                    gcTicks = 0
                }
            }
        }

        private fun collectGarbage() {
            val currentTime = System.currentTimeMillis()

            cachedStrings.filter { currentTime - it.value.lastUsage > CACHED_FONT_REMOVAL_TIME }.forEach {
                GL11.glDeleteLists(it.value.displayList, 1)

                it.value.deleted = true

                cachedStrings.remove(it.key)
            }
        }

        private var fontHeight = -1
        private val charLocations = arrayOfNulls<CharLocation>(stopChar)

        private val cachedStrings: HashMap<String, CachedFont> = HashMap()

        private var textureID = -1
        private var textureWidth = 0
        private var textureHeight = 0

        val height: Int
            get() = (fontHeight - 8) / 2

        init {
            renderBitmap(startChar, stopChar)

            activeFontRenderers.add(this)
        }

        fun drawString(text: String, x: Double, y: Double, color: Int) {
            val scale = 0.25
            val reverse = 1 / scale

            GL11.glPushMatrix()
            GL11.glScaled(scale, scale, scale)
            GL11.glTranslated(x * 2F, y * 2.0 - 2.0, 0.0)

            if (this.loadingScreen)
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
            else
                GlStateManager.bindTexture(textureID)

            val red: Float = (color shr 16 and 0xff) / 255F
            val green: Float = (color shr 8 and 0xff) / 255F
            val blue: Float = (color and 0xff) / 255F
            val alpha: Float = (color shr 24 and 0xff) / 255F

            GL11.glColor4f(red, green, blue, alpha)

            var currX = 0.0

            val cached: CachedFont? = cachedStrings[text]

            if (cached != null) {
                GL11.glCallList(cached.displayList)

                cached.lastUsage = System.currentTimeMillis()

                GL11.glPopMatrix()

                return
            }

            var list = -1

            if (assumeNonVolatile) {
                list = GL11.glGenLists(1)

                GL11.glNewList(list, GL11.GL_COMPILE_AND_EXECUTE)
            }

            GL11.glBegin(GL11.GL_QUADS)

            for (char in text.toCharArray()) {
                if (char.toInt() >= charLocations.size) {
                    GL11.glEnd()

                    // Ugly solution, because floating point numbers, but I think that shouldn't be that much of a problem
                    GL11.glScaled(reverse, reverse, reverse)
                    mc.fontRendererObj.drawString("$char", currX.toFloat() * scale.toFloat() + 1, 2f, color, false)
                    currX += mc.fontRendererObj.getStringWidth("$char") * reverse

                    GL11.glScaled(scale, scale, scale)

                    if (this.loadingScreen)
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
                    else
                        GlStateManager.bindTexture(textureID)

                    GL11.glColor4f(red, green, blue, alpha)

                    GL11.glBegin(GL11.GL_QUADS)
                } else {
                    val fontChar = charLocations[char.toInt()] ?: continue

                    drawChar(fontChar, currX.toFloat(), 0f)
                    currX += fontChar.width - 8.0
                }
            }

            GL11.glEnd()

            if (assumeNonVolatile) {
                cachedStrings[text] = CachedFont(list, System.currentTimeMillis())
                GL11.glEndList()
            }

            GL11.glPopMatrix()
        }

        private fun drawChar(char: CharLocation, x: Float, y: Float) {
            val width = char.width.toFloat()
            val height = char.height.toFloat()
            val srcX = char.x.toFloat()
            val srcY = char.y.toFloat()
            val renderX = srcX / textureWidth
            val renderY = srcY / textureHeight
            val renderWidth = width / textureWidth
            val renderHeight = height / textureHeight

            GL11.glTexCoord2f(renderX, renderY)
            GL11.glVertex2f(x, y)
            GL11.glTexCoord2f(renderX, renderY + renderHeight)
            GL11.glVertex2f(x, y + height)
            GL11.glTexCoord2f(renderX + renderWidth, renderY + renderHeight)
            GL11.glVertex2f(x + width, y + height)
            GL11.glTexCoord2f(renderX + renderWidth, renderY)
            GL11.glVertex2f(x + width, y)
        }

        private fun renderBitmap(startChar: Int, stopChar: Int) {
            val fontImages = arrayOfNulls<BufferedImage>(stopChar)
            var rowHeight = 0
            var charX = 0
            var charY = 0

            for (targetChar in startChar until stopChar) {
                val fontImage = drawCharToImage(targetChar.toChar())
                val fontChar = CharLocation(charX, charY, fontImage.width, fontImage.height)

                if (fontChar.height > fontHeight)
                    fontHeight = fontChar.height
                if (fontChar.height > rowHeight)
                    rowHeight = fontChar.height

                charLocations[targetChar] = fontChar
                fontImages[targetChar] = fontImage

                charX += fontChar.width

                if (charX > 2048) {
                    if (charX > textureWidth)
                        textureWidth = charX

                    charX = 0
                    charY += rowHeight
                    rowHeight = 0
                }
            }
            textureHeight = charY + rowHeight

            val bufferedImage = BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB)
            val graphics2D = bufferedImage.graphics as Graphics2D
            graphics2D.font = font
            graphics2D.color = Color(255, 255, 255, 0)
            graphics2D.fillRect(0, 0, textureWidth, textureHeight)
            graphics2D.color = Color.white

            for (targetChar in startChar until stopChar)
                if (fontImages[targetChar] != null && charLocations[targetChar] != null)
                    graphics2D.drawImage(fontImages[targetChar], charLocations[targetChar]!!.x, charLocations[targetChar]!!.y,
                        null)

            textureID = TextureUtil.uploadTextureImageAllocate(
                TextureUtil.glGenTextures(), bufferedImage, true,
                true)
        }

        private fun drawCharToImage(ch: Char): BufferedImage {
            val graphics2D = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).graphics as Graphics2D

            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            graphics2D.font = font

            val fontMetrics = graphics2D.fontMetrics

            var charWidth = fontMetrics.charWidth(ch) + 8
            if (charWidth <= 0)
                charWidth = 7

            var charHeight = fontMetrics.height + 3
            if (charHeight <= 0)
                charHeight = font.size

            val fontImage = BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB)
            val graphics = fontImage.graphics as Graphics2D
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            graphics.font = font
            graphics.color = Color.WHITE
            graphics.drawString(ch.toString(), 3, 1 + fontMetrics.ascent)

            return fontImage
        }

        fun getStringWidth(text: String): Int {
            var width = 0

            for (c in text.toCharArray()) {
                val fontChar = charLocations[
                        if (c.toInt() < charLocations.size)
                            c.toInt()
                        else
                            '\u0003'.toInt()
                ] ?: continue

                width += fontChar.width - 8
            }

            return width / 2
        }

        fun delete() {
            if (textureID != -1) {
                GL11.glDeleteTextures(textureID)
                textureID = -1
            }

            activeFontRenderers.remove(this)
        }

        fun finalize() {
            delete()
        }

        private data class CharLocation(var x: Int, var y: Int, var width: Int, var height: Int)
    }

    data class CachedFont(val displayList: Int, var lastUsage: Long, var deleted: Boolean = false) {
        protected fun finalize() {
            if (!deleted) {
                GL11.glDeleteLists(displayList, 1)
            }
        }
    }

    object RainbowFontShader : Shader("rainbow_font_shader.frag"), Closeable {
        var isInUse = false
            private set

        var strengthX = 0f
        var strengthY = 0f
        var offset = 0f

        override fun setupUniforms() {
            setupUniform("offset")
            setupUniform("strength")
        }

        override fun updateUniforms() {
            GL20.glUniform2f(getUniform("strength"), strengthX, strengthY)
            GL20.glUniform1f(getUniform("offset"), offset)
        }

        override fun startShader() {
            super.startShader()

            isInUse = true
        }

        override fun stopShader() {
            super.stopShader()

            isInUse = false
        }

        override fun close() {
            if (isInUse)
                stopShader()
        }

        @Suppress("NOTHING_TO_INLINE")
        @JvmStatic
        inline fun begin(enable: Boolean, x: Float, y: Float, offset: Float): RainbowFontShader {
            if (enable) {
                strengthX = x
                strengthY = y
                this.offset = offset

                startShader()
            }

            return this
        }
    }

    abstract class Shader(fragmentShader: String) : MinecraftInstance() {
        var programId: Int = 0
        private var uniformsMap: MutableMap<String, Int>? = null
        open fun startShader() {
            GL11.glPushMatrix()
            GL20.glUseProgram(programId)
            if (uniformsMap == null) {
                uniformsMap = HashMap()
                setupUniforms()
            }
            updateUniforms()
        }

        open fun stopShader() {
            GL20.glUseProgram(0)
            GL11.glPopMatrix()
        }

        abstract fun setupUniforms()
        abstract fun updateUniforms()
        private fun createShader(shaderSource: String, shaderType: Int): Int {
            var shader = 0
            return try {
                shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType)
                if (shader == 0) return 0
                ARBShaderObjects.glShaderSourceARB(shader, shaderSource)
                ARBShaderObjects.glCompileShaderARB(shader)
                if (ARBShaderObjects.glGetObjectParameteriARB(
                        shader,
                        ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB
                    ) == GL11.GL_FALSE
                ) throw RuntimeException("Error creating shader: " + getLogInfo(shader))
                shader
            } catch (e: java.lang.Exception) {
                ARBShaderObjects.glDeleteObjectARB(shader)
                throw e
            }
        }

        private fun getLogInfo(i: Int): String {
            return ARBShaderObjects.glGetInfoLogARB(
                i,
                ARBShaderObjects.glGetObjectParameteriARB(i, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB)
            )
        }

        fun setUniform(uniformName: String, location: Int) {
            uniformsMap!![uniformName] = location
        }

        fun setupUniform(uniformName: String) {
            setUniform(uniformName, GL20.glGetUniformLocation(programId, uniformName))
        }

        fun getUniform(uniformName: String): Int {
            return uniformsMap!![uniformName]!!
        }

        init {
            var vertexShaderID = 0
            var fragmentShaderID = 0
            var con = true
            try {
                val vertexStream = javaClass.getResourceAsStream("/kevin/utils/fonts/vertex.vert")
                vertexShaderID = createShader(IOUtils.toString(vertexStream), ARBVertexShader.GL_VERTEX_SHADER_ARB)
                IOUtils.closeQuietly(vertexStream)
                val fragmentStream =
                    javaClass.getResourceAsStream("/kevin/utils/fonts/$fragmentShader")
                fragmentShaderID =
                    createShader(IOUtils.toString(fragmentStream), ARBFragmentShader.GL_FRAGMENT_SHADER_ARB)
                IOUtils.closeQuietly(fragmentStream)
            } catch (e: java.lang.Exception) {
                Minecraft.logger.error("An exception occluding when shader init:\n${e.stackTraceToString()}")
                con = false
            }
            if (con) if (vertexShaderID == 0 || fragmentShaderID == 0) con = false
            if (con) {
                programId = ARBShaderObjects.glCreateProgramObjectARB()
                if (programId == 0) con = false
                if (con){
                    ARBShaderObjects.glAttachObjectARB(programId, vertexShaderID)
                    ARBShaderObjects.glAttachObjectARB(programId, fragmentShaderID)
                    ARBShaderObjects.glLinkProgramARB(programId)
                    ARBShaderObjects.glValidateProgramARB(programId)
                }
            }
        }
    }
}