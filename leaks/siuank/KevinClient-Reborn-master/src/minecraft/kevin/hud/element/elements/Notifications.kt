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

import blur.GaussianBlur
import kevin.persional.milk.utils.StencilUtil
import kevin.hud.designer.GuiHudDesigner
import kevin.hud.element.*
import kevin.hud.element.elements.ConnectNotificationType.*
import kevin.hud.element.elements.Notification.FadeState.*
import kevin.main.KevinClient
import kevin.module.ListValue
import kevin.utils.AnimationUtils
import kevin.utils.MSTimer
import kevin.utils.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F, side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    private val notificationMode = ListValue("NotificationMode", arrayOf("Connect","MilkNew","LiquidBounce-Kevin","Kevin", "Normal", "Simple"),"Connect")
    private val exampleNotification = Notification("Example Notification", "Example title")
    override fun drawElement(): Border? {
        var animationY = 30F
        val notifications = mutableListOf<Notification>()
        val hud = KevinClient.hud
        for(i in hud.notifications)
            notifications.add(i)
        for(i in notifications){
            if (mc.currentScreen !is GuiHudDesigner) {
                when (notificationMode.get()) {
                    "LiquidBounce-Kevin" -> i.drawNotification(animationY).also { animationY += 20 }
                    "Kevin" -> i.drawNotificationKevinNew(animationY).also { animationY += 40 }
                    "Connect" -> i.drawConnectNotification(animationY).also { animationY += 24 }
                    "MilkNew" -> i.drawMilkNewNotification(animationY, renderX, renderY).also { animationY += 35 }
                    "Normal" -> i.drawNormalNotification(animationY).also { animationY += 24 }
                    "Simple" -> i.drawSimpleNotification(animationY).also { animationY += 24 }
                }
            } else {
                when (notificationMode.get()) {
                    "LiquidBounce-Kevin" -> exampleNotification.drawNotification(animationY)
                    "Kevin" -> exampleNotification.drawNotificationKevinNew(animationY)
                    "Connect" -> exampleNotification.drawConnectNotification(animationY)
                    "MilkNew" -> i.drawMilkNewNotification(animationY, renderX, renderY)
                    "Normal" -> i.drawNormalNotification(animationY)
                    "Simple" -> i.drawSimpleNotification(animationY)
                }
            }
        }
        if (mc.currentScreen is GuiHudDesigner) {
            if (!KevinClient.hud.notifications.contains(exampleNotification)) KevinClient.hud.addNotification(exampleNotification)
            exampleNotification.fadeState = STAY

            exampleNotification.x = if (notificationMode equal "Connect") {
                val c = 16.0 / 48.0
                val iconWidth = (48 * c).toFloat() + 4F
                exampleNotification.textLength + KevinClient.fontManager.font35.getStringWidth("[${exampleNotification.title}]: ") + iconWidth
            } else {
                exampleNotification.textLength.toFloat()
            } + 8F

            when (notificationMode.get()) {
                "LiquidBounce-Kevin" -> return Border(-118.114514191981F, -50F, 0F, -30F)
                "Kevin" -> return Border(-114.5F, -70F, 0F, -30F)
                "Connect" -> return Border(-220F, -50F, 0F, -30F)
                "MilkNew" -> return Border(-125F, -55F, 0F, -0F)
                "Normal" -> return Border(-150F, -50F, 0F, -30F)
                "Simple" -> return Border(-110F, -50F, 0F, -30F)
            }
        }
        GL11.glDisable(GL11.GL_BLEND)
        return null
    }
}

enum class ConnectNotificationType {
    Connect,Disconnect,OK,Warn,Info,Error
}

class Notification(private val message: String, val title: String = "", val type: ConnectNotificationType = Info) {
    var x = 0F
    var textLength = 0

    private var stay = 0F
    private var fadeStep = 0F
    var fadeState = IN

    private val stayTimer = MSTimer()
    private var timer = 0L
    private var firstY = 0f
    private var animeTime: Long = 0

    enum class FadeState { IN, STAY, OUT, END }

    init {
        stayTimer.reset()
        firstY = 1919F
        textLength = KevinClient.fontManager.font35.getStringWidth(message)
    }

    fun drawNotification(animationY: Float) {
        var y = animationY
        if (firstY == 1919.0F) {
            firstY = y
        }
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y) {
                firstY = cacheY
            }
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        // Draw notification
        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -20F-y, Color(0,0,0,155).rgb)
        if (message.contains("Enabled")) {
            RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(0, 255, 160,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(0, 255, 160,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(0, 255, 160,225).rgb)
            KevinClient.fontManager.font35.drawString(message, -x + 4, -14F-y, Color(0, 255, 160).rgb)
        }else if (message.contains("Disabled")) {
            RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(255, 0, 80,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(255, 0, 80,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(255, 0, 80,225).rgb)
            KevinClient.fontManager.font35.drawString(message, -x + 4, -14F-y, Color(255, 0, 80).rgb)
        }else {
            RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(0, 160, 255,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(0, 160, 255,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(0, 160, 255,225).rgb)
            KevinClient.fontManager.font35.drawString(message, -x + 4, -14F-y, Color(0, 160, 255).rgb)
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = textLength + 8F
        when (fadeState) {
            IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = STAY
                    x = width
                    fadeStep = width
                }

                stay = 60F
            }

            STAY -> {
                if (stay > 0) {
                    stay = 0F
                    stayTimer.reset()
                }
                if (stayTimer.hasTimePassed(500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(1000L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(1500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2000L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2850L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(3000L))
                    fadeState = OUT
            }

            OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else
                fadeState = END

            END -> {
                val hud = KevinClient.hud
                hud.removeNotification(this)
            }

        }
    }

    fun drawNotificationKevinNew(animationY: Float) {
        var y = animationY
        if (firstY == 1919.0F) {
            firstY = y
        }
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y) {
                firstY = cacheY
            }
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        // Draw notification
        val color = if (message.contains("Enabled")) Color(0, 255, 160).rgb else if (message.contains("Disabled")) Color(255, 0, 80).rgb else Color(0, 160, 255).rgb
        textLength = if (textLength > 100) textLength else 100

        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -40F-y, Color(0,0,0,100).rgb)

        RenderUtils.drawRect(-x, -y, -x - 1, -40F-y, color)
        RenderUtils.drawRect(-x + 8 + textLength, -39F-y, -x, -40F-y, color)
        RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -39F-y, color)
        KevinClient.fontManager.font35.drawString(message, -x + (8F + textLength)/2 - KevinClient.fontManager.font35.getStringWidth(message)/2, -14F-y, color)
        KevinClient.fontManager.font35.drawString(
            this.title,
            -x + (8F + textLength) / 2 - KevinClient.fontManager.font35.getStringWidth(this.title) / 2,
            -30F - y,
            color
        )
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = textLength + 8F
        when (fadeState) {
            IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = STAY
                    x = width
                    fadeStep = width
                }

                stay = 60F
            }

            STAY -> {
                if (stay > 0) {
                    stay = 0F
                    stayTimer.reset()
                }
                if (stayTimer.hasTimePassed(500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(1000L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(1500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2000L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2850L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(3000L))
                    fadeState = OUT
            }

            OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else
                fadeState = END

            END -> {
                val hud = KevinClient.hud
                hud.removeNotification(this)
            }
        }
    }

    fun drawConnectNotification(animationY: Float) {
        var y = animationY
        if (firstY == 1919.0F)
            firstY = y
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y)
                firstY = cacheY
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        val c = 16.0 / 48.0
        val iconWidth = ((if (type == Connect || type == Disconnect) 117 else 48) * c).toFloat() + 4F
        val long = textLength + KevinClient.fontManager.font35.getStringWidth("[$title]: ") + iconWidth
        // Draw notification
        val fontColor = when(type) {
            Connect,OK -> Color(0, 255, 160)
            Disconnect,Error -> Color(255, 0, 80)
            Warn -> Color(240, 240, 80)
            Info -> Color(0, 160, 255)
        }
        RenderUtils.drawRect(8 + long - x, -y, -x, -20F - y, Color(0, 0, 0, 155).rgb)
        RenderUtils.drawShadow(-x, -20F-y,  8F + long, 20F)
        KevinClient.fontManager.font35.drawString("[$title]: ", iconWidth + 4 - x, -14F-y, Color(240, 240, 240).rgb)
        KevinClient.fontManager.font35.drawString(message, iconWidth + 4 + KevinClient.fontManager.font35.getStringWidth("[$title]: ") - x, -14F-y, fontColor.rgb)
        if (fadeState != IN) {
            var pec = (System.currentTimeMillis() - timer).toDouble() / 3000
            if (pec > 1.0) pec = 1.0
            RenderUtils.drawRect(((8 + long)*pec - x).toFloat(), -y, -x, -2F - y, Color(255, 144, 71, 255).rgb)
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        when(type) { // Draw icon
            Connect -> {
                when (fadeState) {
                    IN -> {
                        RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                        RenderUtils.drawIcon(20 - x, -20F - y, 23F, 16F, "DisconnectedRight")
                    }
                    STAY -> {
                        val pec = (System.currentTimeMillis() - timer).toFloat() / 600F
                        if (pec > 1F) {
                            RenderUtils.drawIcon(8.5F - x, -20F - y, 16F, 16F, "ConnectedLeft")
                            RenderUtils.drawIcon(15.5F - x, -20F - y, 23F, 16F, "ConnectedRight")
                        } else {
                            val value = 4.5F * pec
                            RenderUtils.drawIcon(4 + value - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                            RenderUtils.drawIcon(20 - value - x, -20F - y, 23F, 16F, "DisconnectedRight")
                        }
                    }
                    OUT,END -> {
                        RenderUtils.drawIcon(8.5F - x, -20F - y, 16F, 16F, "ConnectedLeft")
                        RenderUtils.drawIcon(15.5F - x, -20F - y, 23F, 16F, "ConnectedRight")
                    }
                }
            }
            Disconnect -> {
                when (fadeState) {
                    IN -> {
                        RenderUtils.drawIcon(8.5F - x, -20F - y, 16F, 16F, "ConnectedLeft")
                        RenderUtils.drawIcon(15.5F - x, -20F - y, 23F, 16F, "ConnectedRight")
                    }
                    STAY -> {
                        val pec = (System.currentTimeMillis() - timer).toFloat() / 600F
                        if (pec > 1F) {
                            RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                            RenderUtils.drawIcon(20 - x, -20F - y, 23F, 16F, "DisconnectedRight")
                        } else {
                            val value = 4.5F - 4.5F * pec
                            RenderUtils.drawIcon(4 + value - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                            RenderUtils.drawIcon(20 - value - x, -20F - y, 23F, 16F, "DisconnectedRight")
                        }
                    }
                    OUT,END -> {
                        RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                        RenderUtils.drawIcon(20 - x, -20F - y, 23F, 16F, "DisconnectedRight")
                    }
                }
            }
            OK -> RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "Done")
            Warn -> RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "Warn")
            Info -> RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "Info")
            Error -> RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "Error")
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = long + 8F
        when (fadeState) {
            IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = STAY
                    timer = System.currentTimeMillis()
                    x = width
                    fadeStep = width
                }
            }
            STAY -> {
                if (System.currentTimeMillis() - timer >= 3000)
                    fadeState = OUT
            }
            OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else fadeState = END
            END -> KevinClient.hud.removeNotification(this)
        }
    }

    fun drawMilkNewNotification(animationY: Float, renderX: Double, renderY: Double) {
        var y = animationY
        if (firstY == 1919.0F)
            firstY = y
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y)
                firstY = cacheY
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        val c = 16.0 / 48.0
        val long = textLength
        // Draw notification

        //绘制Bloom & Blur
        GL11.glTranslated(-renderX, -renderY, 0.0)
        StencilUtil.initStencilToWrite()
        RenderUtils.drawRect(8 + long - x + 10 + 5 + renderX,
            -y + 7 + renderY, -x - 30 + renderX, -19F - y - 7 + renderY, Color(0, 0, 0, 255).rgb)
        StencilUtil.readStencilBuffer(1)
        GaussianBlur.renderBlur(10F)
        StencilUtil.uninitStencilBuffer()
        GL11.glTranslated(renderX, renderY, 0.0)

//        RenderUtils.drawShadow(8 + long - x + 10 + 5, -y + 7, -x - 30 - (8 + long - x + 10 + 5) , -19F - y - 7 - (-y + 7));
//        RenderUtils.drawRect(8 + long - x + 10 + 5, -y + 7, -x - 30, -19F - y - 7, Color(0, 0, 0, 150).rgb)
        KevinClient.fontManager.notiFont.drawString("b", -x - 20 + 3, -19F - y - 15 + 17,
            Color(255, 255, 255, 255).rgb)
        KevinClient.fontManager.font35.drawString(message, 4 - x, -5F-y, Color(220, 255,255, 200).rgb)
        KevinClient.fontManager.fontNovo40.drawString(title, 4 - x, -18F-y, Color(220, 160, 70, 255).rgb)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = long + 8F
        when (fadeState) {
            IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = STAY
                    timer = System.currentTimeMillis()
                    x = width
                    fadeStep = width
                }
            }
            STAY -> {
                if (System.currentTimeMillis() - timer >= 3000)
                    fadeState = OUT
            }
            OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else fadeState = END
            END -> KevinClient.hud.removeNotification(this)
        }
    }
    fun drawNormalNotification(animationY: Float) {  // connect base
        var y = animationY
        if (firstY == 1919.0F)
            firstY = y
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y)
                firstY = cacheY
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        val c = 16.0 / 48.0
        val long = textLength + KevinClient.fontManager.font35.getStringWidth("[$title] ")
        // Draw notification
        RenderUtils.drawRect(8 + long - x, -y, -x, -20F - y, Color(0, 0, 0, 155).rgb)
        RenderUtils.drawShadow(-x, -20F-y,  8F + long, 20F)
        KevinClient.fontManager.font35.drawString("[$title] ", 4 - x, -14F-y, Color(240, 240, 240).rgb)
        KevinClient.fontManager.font35.drawString(message, 4 + KevinClient.fontManager.font35.getStringWidth("[$title]: ") - x, -14F-y, Color(255, 255, 255, 255).rgb)
        if (fadeState != IN) {
            var pec = (System.currentTimeMillis() - timer).toDouble() / 3000
            if (pec > 1.0) pec = 1.0
            RenderUtils.drawRect(((8 + long)*pec - x).toFloat(), -y, -x, -2F - y, if (message.contains("Enable", true)) {Color(0, 255, 40, 255).rgb} else if (message.contains("Disable", true)) {Color(255, 20, 12, 255).rgb} else {Color(255, 144, 71, 255).rgb})
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = long + 8F
        when (fadeState) {
            IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = STAY
                    timer = System.currentTimeMillis()
                    x = width
                    fadeStep = width
                }
            }
            STAY -> {
                if (System.currentTimeMillis() - timer >= 3000)
                    fadeState = OUT
            }
            OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else fadeState = END
            END -> KevinClient.hud.removeNotification(this)
        }
    }
    fun drawSimpleNotification(animationY: Float) {  // normal base
        var y = animationY
        if (firstY == 1919.0F)
            firstY = y
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y)
                firstY = cacheY
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        val c = 16.0 / 48.0
        val long = textLength
        // Draw notification
        RenderUtils.drawRect(8 + long - x, -y, -x, -19F - y, Color(0, 0, 0, 173).rgb)
        RenderUtils.drawShadow(-x, -19F-y,  8F + long, 19F)
        KevinClient.fontManager.font35.drawString(message, 4 - x, -14F-y, Color(255, 255, 255, 255).rgb)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = long + 8F
        when (fadeState) {
            IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = STAY
                    timer = System.currentTimeMillis()
                    x = width
                    fadeStep = width
                }
            }
            STAY -> {
                if (System.currentTimeMillis() - timer >= 3000)
                    fadeState = OUT
            }
            OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else fadeState = END
            END -> KevinClient.hud.removeNotification(this)
        }
    }
}