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
package kevin.command.commands

import com.mojang.authlib.Agent
import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.exceptions.AuthenticationUnavailableException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import com.thealtening.ReflectionUtility
import kevin.command.ICommand
import kevin.utils.ChatUtils
import kevin.utils.MinecraftInstance
import kevin.utils.ServerUtils
import kevin.utils.UserUtils.getUUID
import net.minecraft.client.Minecraft
import net.minecraft.util.Session
import java.net.MalformedURLException
import java.net.Proxy
import java.net.URL

class LoginCommand : ICommand {
    override fun run(args: Array<out String>?) {
        if (args == null || args.isEmpty()){
            ChatUtils.messageWithStart("§cUsage: .login <Name> <Password>")
            return
        }
        if (args.size == 1){
            Minecraft.getMinecraft().session = Session(args[0], getUUID(args[0]), "-", "legacy")
            ChatUtils.messageWithStart("§c现在你的名字是 §f§l${args[0]}§c。")
        } else
            ChatUtils.messageWithStart(login(args[0],args[1]))
        if (Minecraft.getMinecraft().isIntegratedServerRunning)
            return
        Minecraft.getMinecraft().theWorld.sendQuittingDisconnectingPacket()
        ServerUtils.connectToLastServer()
    }
    private fun login(name: String,password: String):String {
        if (AltService().getCurrentService() != AltService.EnumAltService.MOJANG) {
            try {
                AltService().switchService(AltService.EnumAltService.MOJANG)
            } catch (e: NoSuchFieldException) { }
        }
        val result: LoginUtils.LoginResult = LoginUtils.login(name, password)
        if (result === LoginUtils.LoginResult.LOGGED) {
            val userName: String = Minecraft.getMinecraft().session.username
            return "§c现在你的名字是 §f§l$userName§c。"
        }

        if (result === LoginUtils.LoginResult.WRONG_PASSWORD) return "§c错误的密码。"

        if (result === LoginUtils.LoginResult.NO_CONTACT) return "§c无法连接到登录服务器。"

        if (result === LoginUtils.LoginResult.INVALID_ACCOUNT_DATA) return "§c用户名或密码无效。"

        return if (result === LoginUtils.LoginResult.MIGRATED) "§c账户已迁移。" else ""
    }
}
open class AltService {
    private val userAuthentication: ReflectionUtility =
        ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication")
    private val minecraftSession: ReflectionUtility =
        ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService")
    private var currentService: EnumAltService? = null
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    open fun switchService(enumAltService: EnumAltService) {
        if (currentService == enumAltService) return
        reflectionFields(enumAltService.hostname)
        currentService = enumAltService
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun reflectionFields(authServer: String) {
        val userAuthenticationModifies: HashMap<String?, URL?> = HashMap()
        val useSecureStart = if (authServer.contains("thealtening")) "http" else "https"
        userAuthenticationModifies["ROUTE_AUTHENTICATE"] =
            constantURL("$useSecureStart://authserver.$authServer.com/authenticate")
        userAuthenticationModifies["ROUTE_INVALIDATE"] =
            constantURL(useSecureStart + "://authserver" + authServer + "com/invalidate")
        userAuthenticationModifies["ROUTE_REFRESH"] =
            constantURL("$useSecureStart://authserver.$authServer.com/refresh")
        userAuthenticationModifies["ROUTE_VALIDATE"] =
            constantURL("$useSecureStart://authserver.$authServer.com/validate")
        userAuthenticationModifies["ROUTE_SIGNOUT"] =
            constantURL("$useSecureStart://authserver.$authServer.com/signout")
        userAuthenticationModifies.forEach { (key: String?, value: URL?) ->
            try {
                userAuthentication.setStaticField(key, value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        userAuthentication.setStaticField("BASE_URL", "$useSecureStart://authserver.$authServer.com/")
        minecraftSession.setStaticField(
            "BASE_URL",
            "$useSecureStart://sessionserver.$authServer.com/session/minecraft/"
        )
        minecraftSession.setStaticField(
            "JOIN_URL",
            constantURL("$useSecureStart://sessionserver.$authServer.com/session/minecraft/join")
        )
        minecraftSession.setStaticField(
            "CHECK_URL",
            constantURL("$useSecureStart://sessionserver.$authServer.com/session/minecraft/hasJoined")
        )
        minecraftSession.setStaticField(
            "WHITELISTED_DOMAINS",
            arrayOf(".minecraft.net", ".mojang.com", ".thealtening.com")
        )
    }

    private fun constantURL(url: String): URL {
        return try {
            URL(url)
        } catch (ex: MalformedURLException) {
            throw Error("Couldn't create constant for $url", ex)
        }
    }

    open fun getCurrentService(): EnumAltService? {
        if (currentService == null) currentService = EnumAltService.MOJANG

        return currentService
    }

    enum class EnumAltService(var hostname: String) {
        MOJANG("mojang"), THEALTENING("thealtening");
    }
}
object LoginUtils : MinecraftInstance() {

    @JvmStatic
    fun login(username: String?, password: String?): LoginResult {
        val userAuthentication = YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication

        userAuthentication.setUsername(username)
        userAuthentication.setPassword(password)

        return try {
            userAuthentication.logIn()
            mc.session = Session(userAuthentication.selectedProfile.name,
                userAuthentication.selectedProfile.id.toString(), userAuthentication.authenticatedToken, "mojang")
            LoginResult.LOGGED
        } catch (exception: AuthenticationUnavailableException) {
            LoginResult.NO_CONTACT
        } catch (exception: AuthenticationException) {
            val message = exception.message!!
            when {
                message.contains("invalid username or password.", ignoreCase = true) -> LoginResult.INVALID_ACCOUNT_DATA
                message.contains("account migrated", ignoreCase = true) -> LoginResult.MIGRATED
                else -> LoginResult.NO_CONTACT
            }
        } catch (exception: NullPointerException) {
            LoginResult.WRONG_PASSWORD
        }
    }

    enum class LoginResult {
        WRONG_PASSWORD, NO_CONTACT, INVALID_ACCOUNT_DATA, MIGRATED, LOGGED, FAILED_PARSE_TOKEN
    }
}