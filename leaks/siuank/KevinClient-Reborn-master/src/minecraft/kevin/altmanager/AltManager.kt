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
package kevin.altmanager

import com.google.gson.*
import com.mojang.authlib.Agent
import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import com.thealtening.AltService
import kevin.command.commands.LoginUtils
import kevin.file.FileManager
import kevin.main.KevinClient
import kevin.font.RainbowFontShader
import kevin.utils.MSTimer
import kevin.utils.RenderUtils
import kevin.utils.StringUtils
import kevin.utils.UserUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.Session
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import java.net.Proxy
import java.util.concurrent.CopyOnWriteArrayList

object AltManager : GuiScreen() {
    private val loginButton: GuiButton by lazy {
        GuiButton(4,mc.currentScreen.width - 75,115,70,20,"Login")
    }
    private val removeButton: GuiButton by lazy {
        GuiButton(3,mc.currentScreen.width - 75,90,70,20,"Remove")
    }
    private val changeButton: GuiButton by lazy {
        GuiButton(2,mc.currentScreen.width - 75,65,70,20,"Change")
    }
    private val altList = CopyOnWriteArrayList<Alt>()
    private var chose:Int? = null
    private val file = KevinClient.fileManager.altsFile
    private var guiMainMenu:GuiMainMenu? = null
    private var stateMessage = "Idle..."
    private var startY = 0F
    private var canRoll = false
    private var canRoll2 = false
    private var kevinPrefix = false
    private val altService = AltService()
    private var lastClick:Alt? = null
    private val clickTimer = MSTimer()
    fun altManager(guiMainMenu: GuiMainMenu): AltManager{
        this.guiMainMenu = guiMainMenu
        return this
    }
    override fun initGui() {
        load()
        buttonList.add(GuiButton(0,mc.currentScreen.width - 75,mc.currentScreen.height - 25,70,20,"Back"))
        buttonList.add(GuiButton(1,mc.currentScreen.width - 75,40,70,20,"Add"))
        buttonList.add(GuiButton(5,mc.currentScreen.width - 75,140,70,20,"Reload"))
        buttonList.add(GuiButton(6,mc.currentScreen.width - 75,165,70,20,"RandomName"))
        buttonList.add(GuiButton(7, mc.currentScreen.width - 75,190,70,20, "§${if(kevinPrefix) "2" else "c"}RandomKevinPrefix"))
        buttonList.add(removeButton)
        buttonList.add(changeButton)
        buttonList.add(loginButton)
        removeButton.enabled = false
        changeButton.enabled = false
        loginButton.enabled = false
        chose = null
        startY = 0F
    }
    override fun doesGuiPauseGame(): Boolean = false
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        if (mouseY.toFloat() !in 37.5F..this.height-37.5F) return
        var yO = 1F - startY
        var c = 0
        altList.forEach{
            val y = yO
            yO += it.getHeight()
            if (it.isClick(mouseX,mouseY,this.width/4F,37.5F+y,this.width/4*3F,37.5F+yO)){
                chose = c
                if (lastClick == null) {
                    lastClick = it
                    clickTimer.reset()
                }else if (lastClick == it) stateMessage = login(it)
                return@forEach
            }
            c += 1
        }
    }
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (clickTimer.hasTimePassed(1000L)){
            lastClick = null
        }
        drawDefaultBackground()
        RenderUtils.drawRect(0F,37.5F,this.width.toFloat(),this.height-37.5F,Color(0,0,0,125).rgb)
        RainbowFontShader.begin(true,-0.00314514F,0.00314514F,System.currentTimeMillis() % 10000 / 10000F).use {
            KevinClient.fontManager.font40.drawString("AltManager",this.width/2F- KevinClient.fontManager.font40.getStringWidth("AltManager")/2F,5F,0)
        }
        var stateX = this.width/2F- KevinClient.fontManager.font35.getStringWidth(stateMessage)/2F
        if (stateMessage.startsWith("§")) stateX += KevinClient.fontManager.font35.getStringWidth("§c")/2F
        KevinClient.fontManager.font35.drawString(stateMessage,stateX,10F+ KevinClient.fontManager.font40.fontHeight,Color.lightGray.rgb)
        var yO = 1F - startY
        var c = 0
        glEnable(GL_SCISSOR_TEST)
        glScissor(0,(mc.displayHeight*(37.5F/this.height)).toInt(),mc.displayWidth,(mc.displayHeight*((this.height-(37.5F*2))/this.height)).toInt())
        altList.forEach{
            yO += it.drawAlt(this.width/4F,this.width/4*3F,37.5F+yO,c==chose)
            c += 1
        }
        glDisable(GL_SCISSOR_TEST)
        canRoll = mouseY.toFloat() in 37.5F..this.height-37.5F
        canRoll2 = yO+37.5F > this.height-37.5F
        if (chose != null){
            removeButton.enabled = true
            changeButton.enabled = true
            loginButton.enabled = true
        } else {
            removeButton.enabled = false
            changeButton.enabled = false
            loginButton.enabled = false
        }
        removeButton.xPosition = mc.currentScreen.width - 75
        changeButton.xPosition = mc.currentScreen.width - 75
        loginButton.xPosition = mc.currentScreen.width - 75
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        if (!canRoll) return
        when{
            Mouse.getEventDWheel() > 0 -> {
                if (startY>0) startY -= 5F
            }
            Mouse.getEventDWheel() < 0 -> {
                if (!canRoll2) return
                startY += 5F
            }
        }
    }
    override fun onGuiClosed() {
        save()
        buttonList.clear()
        stateMessage = "Idle..."
    }
    override fun actionPerformed(button: GuiButton) {
        when(button.id){
            0 -> mc.displayGuiScreen(guiMainMenu)
            1 -> mc.displayGuiScreen(GuiAddAlt(title = "Add"))
            2 -> mc.displayGuiScreen(GuiAddAlt(altList[chose!!].name,altList[chose!!].password,"Change"))
            3 -> {
                altList.removeAt(chose!!)
                chose = null
                save()
            }
            4 -> stateMessage = login(altList[chose!!])
            5 -> load()
            6 -> {
                stateMessage = "§aYour name is now §f§l${randomOffline(9)}§a."
            }
            7 -> {
                kevinPrefix = !kevinPrefix
                button.displayString = "§${if (kevinPrefix) "2" else "c"}RandomKevinPrefix"
            }
        }
    }
    fun login(alt: Alt): String {
        if (altService.currentService != AltService.EnumAltService.MOJANG) {
            try {
                altService.switchService(AltService.EnumAltService.MOJANG)
            } catch (e: NoSuchFieldException) {
                Minecraft.logger.error("Something went wrong while trying to switch alt service.", e)
            } catch (e: IllegalAccessException) {
                Minecraft.logger.error("Something went wrong while trying to switch alt service.", e)
            }
        }
        if (alt.password.isEmpty()) {
            Minecraft.getMinecraft().session = Session(alt.name, UserUtils.getUUID(alt.name), "-", "legacy")
            return "§aYour name is now §8${alt.name}§a."
        }
        val result: LoginUtils.LoginResult = LoginUtils.login(alt.name, alt.password)
        if (result === LoginUtils.LoginResult.LOGGED) {
            val userName = mc.session.username
            alt.inGameName = userName
            save()
            return "§aYour name is now §f§l$userName§a."
        }
        return when(result){
            LoginUtils.LoginResult.WRONG_PASSWORD -> "§cWrong password."
            LoginUtils.LoginResult.NO_CONTACT -> "§cCannot contact authentication server."
            LoginUtils.LoginResult.INVALID_ACCOUNT_DATA -> "§cInvalid username or password."
            LoginUtils.LoginResult.MIGRATED -> "§cAccount migrated."
            else -> ""
        }
    }
    private fun load(){
        if (!file.exists()) return
        altList.clear()
        val jsonElement = JsonParser().parse(BufferedReader(FileReader(file)))
        if (jsonElement is JsonNull) return
        for (accountElement in jsonElement.asJsonArray) {
            val accountObject = accountElement.asJsonObject
            val name = accountObject["name"]
            val password = accountObject["password"]
            val inGameName = accountObject["InGameName"]
            if (inGameName == null || inGameName.isJsonNull) addAccount(
                name.asString,
                password.asString
            ) else if (inGameName.isJsonNull && password.isJsonNull) addAccount(
                name.asString
            ) else addAccount(
                name.asString,
                password.asString,
                inGameName.asString
            )
        }
    }
    private fun addAccount(name: String,password: String = "",inGameName: String = name){
        altList.add(Alt(name, password, inGameName))
    }
    private fun save(){
        val jsonArray = JsonArray()
        altList.forEach{
            val name = it.name
            val password = it.password
            val inGameName = it.inGameName
            val jsonObject = JsonObject()
            jsonObject.addProperty("name",name)
            jsonObject.addProperty("password",password)
            jsonObject.addProperty("InGameName",inGameName)
            jsonArray.add(jsonObject)
        }
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonArray))
        printWriter.close()
    }

    @JvmStatic
    fun randomOffline(baseLen: Int): String {
        val userName = "${if (kevinPrefix) "KEVIN" else ""}${StringUtils.randomString(baseLen)}"
        Minecraft.getMinecraft().session = Session(userName, UserUtils.getUUID(userName), "-", "legacy")
        return userName
    }
    data class Alt(var name:String,var password:String,var inGameName: String){

        fun getHeight() = 1 + KevinClient.fontManager.font35.fontHeight*.8F + KevinClient.fontManager.font35.fontHeight*.6F + 2F

        fun drawAlt(x: Float,x2: Float,y: Float,chose: Boolean): Float{
            var y2 = y + 1
            glPushMatrix()
            glScaled(.8,.8,.8)
            val fx1 = x + (x2-x)/2F - KevinClient.fontManager.font35.getStringWidth(inGameName)*.8F/2F
            KevinClient.fontManager.font35.drawString(inGameName,fx1/.8F,y2/.8F,if (chose) Color(0,111,255).rgb else Color.white.rgb)
            glPopMatrix()
            y2 += KevinClient.fontManager.font35.fontHeight*.8F
            glPushMatrix()
            glScaled(.6,.6,.6)
            val nameAndPassword = if (password.isEmpty()) name else "$name:******${password.removeRange(0..2)}"
            val fx2 = x + (x2-x)/2F - KevinClient.fontManager.font35.getStringWidth(nameAndPassword)*.6F/2F
            KevinClient.fontManager.font35.drawString(nameAndPassword,fx2/.6F,y2/.6F,Color.lightGray.rgb)
            glPopMatrix()
            y2 += KevinClient.fontManager.font35.fontHeight*.6F
            if (chose){
                RenderUtils.drawBorder(x,y,x2,y2,2F,Color.white.rgb)
            }else{
                RenderUtils.drawBorder(x,y,x2,y2,2F,Color(255,255,255,150).rgb)
            }
            y2 += 2F
            return y2-y
        }
        fun isClick(mouseX: Int,mouseY: Int,x: Float,y: Float,x2: Float,y2: Float):Boolean = mouseX.toFloat() in x..x2 && mouseY.toFloat() in y..y2
    }
    class GuiAddAlt(private val name: String? = null,private val password: String? = null,private val title: String) : GuiScreen(){
        private val nameText:GuiTextField by lazy {
            GuiTextField(1,mc.fontRendererObj,this.width/2-75,60,150,20)
        }
        private val passwordText:GuiTextField by lazy {
            GuiTextField(2,mc.fontRendererObj,this.width/2-75,85,150,20)
        }
        private val nameAndPasswordText:GuiTextField by lazy {
            GuiTextField(3,mc.fontRendererObj,this.width/2-75,125,150,20)
        }
        private val buttonDone:GuiButton by lazy {
            GuiButton(1,this.width/2-75,this.height/4*3-25,150,20,"Done")
        }
        private var message = "Idle..."
        override fun initGui() {
            nameText.text = name ?: ""
            nameText.isFocused = true
            nameText.maxStringLength = Int.MAX_VALUE
            passwordText.text = password ?: ""
            passwordText.isFocused = false
            passwordText.maxStringLength = Int.MAX_VALUE
            nameAndPasswordText.text = ""
            nameAndPasswordText.isFocused = false
            nameAndPasswordText.maxStringLength = Int.MAX_VALUE
            this.buttonList.add(GuiButton(0,this.width/2-75,this.height/4*3,150,20,"Back"))
            this.buttonList.add(buttonDone)
        }
        override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
            drawDefaultBackground()
            RainbowFontShader.begin(true,-0.001F,-0.001F,System.currentTimeMillis() % 10000 / 10000F).use {
                KevinClient.fontManager.font40.drawString(title,this.width/2- KevinClient.fontManager.font40.getStringWidth(title)/2F,8F,0)
            }
            nameText.drawTextBox()
            passwordText.drawTextBox()
            nameAndPasswordText.drawTextBox()
            val nameAndPassword = "Use Name And Password"
            KevinClient.fontManager.font35.drawString(nameAndPassword,this.width/2- KevinClient.fontManager.font35.getStringWidth(nameAndPassword)/2F,60F- KevinClient.fontManager.font35.fontHeight-4F,Color(0,255,0).rgb)
            val namePassword = "Use Name:Password"
            KevinClient.fontManager.font35.drawString(namePassword,this.width/2- KevinClient.fontManager.font35.getStringWidth(namePassword)/2F,125F- KevinClient.fontManager.font35.fontHeight-4F,Color(0,255,0).rgb)
            val nameStr = "Name:"
            val passwordStr = "Password:"
            KevinClient.fontManager.font40.drawString(nameStr,this.width/2- KevinClient.fontManager.font40.getStringWidth(nameStr)-80F,60F+(10- KevinClient.fontManager.font40.fontHeight/2),Color(0,111,255).rgb)
            KevinClient.fontManager.font40.drawString(passwordStr,this.width/2- KevinClient.fontManager.font40.getStringWidth(passwordStr)-80F,85F+(10- KevinClient.fontManager.font40.fontHeight/2),Color(0,111,255).rgb)
            nameText.xPosition = this.width/2-75
            passwordText.xPosition = this.width/2-75
            nameAndPasswordText.xPosition = this.width/2-75
            buttonDone.xPosition = this.width/2-75
            buttonDone.yPosition = this.height/4*3-25
            var messageX = this.width/2- KevinClient.fontManager.font35.getStringWidth(message)/2F
            if (message.startsWith("§")) messageX += +KevinClient.fontManager.font35.getStringWidth("§c")/2F
            KevinClient.fontManager.font35.drawString(message,messageX,this.height/4F*3F+21F+ KevinClient.fontManager.font35.fontHeight,Color.lightGray.rgb)
            buttonDone.enabled = (nameText.text!=""||(nameAndPasswordText.text.contains(":")&&!nameAndPasswordText.text.startsWith(":")))&&message!="Checking..."
            super.drawScreen(mouseX, mouseY, partialTicks)
        }
        override fun updateScreen() {
            nameText.updateCursorCounter()
            passwordText.updateCursorCounter()
            nameAndPasswordText.updateCursorCounter()
        }
        override fun keyTyped(typedChar: Char, keyCode: Int) {
            if (nameText.isFocused){
                nameText.textboxKeyTyped(typedChar,keyCode)
            }
            if (passwordText.isFocused){
                passwordText.textboxKeyTyped(typedChar,keyCode)
            }
            if (nameAndPasswordText.isFocused){
                nameAndPasswordText.textboxKeyTyped(typedChar,keyCode)
            }
            super.keyTyped(typedChar, keyCode)
        }
        override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
            super.mouseClicked(mouseX, mouseY, mouseButton)
            nameText.mouseClicked(mouseX,mouseY,mouseButton)
            passwordText.mouseClicked(mouseX,mouseY,mouseButton)
            nameAndPasswordText.mouseClicked(mouseX,mouseY,mouseButton)
        }
        override fun actionPerformed(button: GuiButton) {
            when(button.id){
                0 -> {
                    mc.displayGuiScreen(AltManager)
                }
                1 -> {
                    val newName = if(nameAndPasswordText.text.contains(":")&&!nameAndPasswordText.text.startsWith(":")) nameAndPasswordText.text.split(":")[0] else nameText.text
                    val newPassword = if(nameAndPasswordText.text.contains(":")&&!nameAndPasswordText.text.startsWith(":")) nameAndPasswordText.text.split(":")[1] else passwordText.text
                    if (altList.any { it.name==newName&&it.password==newPassword }){
                        message = "§cAccount already added."
                        return
                    }
                    buttonDone.enabled = false
                    val alt = Alt(newName,newPassword,newName)
                    Thread({
                        if (alt.password.isNotEmpty()){
                            message = "Checking..."
                            try {
                                val oldService: AltService.EnumAltService = altService.currentService
                                if (oldService != AltService.EnumAltService.MOJANG) {
                                    altService.switchService(AltService.EnumAltService.MOJANG)
                                }
                                val userAuthentication = YggdrasilAuthenticationService(Proxy.NO_PROXY, "")
                                    .createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication
                                userAuthentication.setUsername(alt.name)
                                userAuthentication.setPassword(alt.password)
                                userAuthentication.logIn()
                                alt.inGameName = userAuthentication.selectedProfile.name
                                if (oldService == AltService.EnumAltService.THEALTENING) altService.switchService(
                                    AltService.EnumAltService.THEALTENING
                                )
                            } catch (e: NullPointerException) {
                                message = "§cAccount is not working."
                                buttonDone.enabled = true
                                return@Thread
                            } catch (e: AuthenticationException) {
                                message = "§cAccount is not working."
                                buttonDone.enabled = true
                                return@Thread
                            } catch (e: NoSuchFieldException) {
                                message = "§cAccount is not working."
                                buttonDone.enabled = true
                                return@Thread
                            } catch (e: IllegalAccessException) {
                                message = "§cAccount is not working."
                                buttonDone.enabled = true
                                return@Thread
                            }
                        }
                        if (name == null) altList.add(alt) else {
                            altList.forEach {
                                if(it.name==name&&it.password==(password?:"")){
                                    val index = altList.indexOf(it)
                                    altList.removeAt(index)
                                    altList.add(index,alt)
                                }
                            }
                        }
                        save()
                        message = if (name==null) "§aAccount added." else "§aAccount changed."
                        stateMessage = message
                        mc.displayGuiScreen(AltManager)
                    },"AltCheck").start()
                }
            }
        }
    }
}