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
package kevin.module.modules.misc

import kevin.event.AttackEvent
import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.event.WorldEvent
import kevin.main.KevinClient
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.TextValue
import net.minecraft.entity.player.EntityPlayer
import java.io.File
import java.io.FileFilter

class AutoL : Module("KillMessage","Send messages automatically when you kill a player.") {
    //从文件夹加载
    private val modeList = arrayListOf("Single", "SkidMa")
    private val fileSuffix = ".txt"
    private val messageFiles: Array<out File>?
    init {
        val files = KevinClient.fileManager.killMessages
        messageFiles = files.listFiles(FileFilter { it.name.endsWith(fileSuffix) })
        if (messageFiles != null) for (i in messageFiles) modeList.add(i.name.split(fileSuffix)[0])
    }
    private val modeValue = ListValue("Mode", modeList.toTypedArray(),"Single")
    private val prefix = ListValue("Prefix", arrayOf("None","/shout",".","@","!","Custom"), "None")
    private val customPrefix = TextValue("CustomPrefix", "")
    private val singleMessage = TextValue("SingleMessage","L %name")
    //攻击目标列表
    private val entityList = arrayListOf<EntityPlayer>()
    //在世界切换时清空攻击目标列表
    @EventTarget fun onWorld(event: WorldEvent) = entityList.clear()
    //在攻击时 如果 攻击目标是玩家 且 攻击目标不在列表内 将目标添加进列表
    @EventTarget fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityPlayer && event.targetEntity !in entityList)
            entityList.add(event.targetEntity)
    }
    @EventTarget fun onUpdate(event: UpdateEvent) {
        //如果玩家死亡 发送消息 从列表移除
        entityList.filter { it.isDead }.forEach { entityPlayer ->
            val text = if (modeValue equal "Single") singleMessage.get() else if (modeValue equal "SkidMa") skidMaMSG.random() else messageFiles!!.first { it.name.replace(fileSuffix,"") == modeValue.get() }.readLines().random()
            mc.thePlayer.sendChatMessage(addPrefix(text).replace("%MyName",mc.thePlayer.name).replace("%name",entityPlayer.name))
            entityList.remove(entityPlayer)
        }
    }
    private fun addPrefix(message: String) =
        when(prefix.get()) {
            "/shout" -> "/shout $message"
            "." -> ".say .$message"
            "@" -> "@$message"
            "!" -> "!$message"
            "Custom" -> "${customPrefix.get()}$message"
            else -> message
        }


    val skidMaMSG = arrayOf("here's your tickets to the juice wrld concert",
        "i bet you probably shop at Costco",
        "Learn your alphabet with the skidma custom build client: Panda, Skidma, Epsilon, Alpha!",
        "Download Skidma Custom Build to fuck Omikron while listening to some shit music!",
        "Why Skidma Custom Build? Cause it is the addition of pure skill and incredible intellectual abilities",
        "Want some skills? Check out skidma custom build client.Info!",
        "You have been oofed by Skidma Custom Build oof oof",
        "I am not racist, but I only like Skidma Custom Build users. so git gut noobs",
        "Quick Quiz: I am zeus's son, who am I? SKIDMA CUSTOM BUILD",
        "Wow! My combo is Skidma Custom Build'n!",
        "What should I choose? Skidma Custom Build or Skidma Custom Build?",
        "Bigmama and Skidma Custom Buildma",
        "I don't hack I just skidma custom build",
        "Skidma client . Info is your new trashbin",
        "Look a divinity! He definitely must use skidma custom build!",
        "In need of a cute present for Christmas? Skidma Custom Build is all you need!",
        "I have a good skidma custom build config, don't blame me",
        "Don't piss me off or you will discover the true power of Skidma Custom Build's inf reach",
        "Skidma Custom Build never dies",
        "Maybe I will be Skidma Custom Build, I am already Skidma Custom Build",
        "Skidma Custom Build will help you! Oops, i killed you instead.",
        "NoHaxJustSkidma Custom Build", "Do like Tenebrous, subscribe to ArithmO!",
        "Did I really just forget that melody? Si sig sig sig Skidma Custom Build",
        "Skidma Custom Build. The only client run by speakers of Breton",
        "Order free baguettes with Skidma Custom Build client", "Another Skidma Custom Build user? Awww man",
        "Skidma Custom Build utility client no hax 100%",
        "Hypixel wants to know Skidma Custom Build owner's location [Accept] [Deny]",
        "I am a sig-magician, thats how I am able to do all those block game tricks",
        "Stop it, get some help! Get Skidma Custom Build",
        "Skidma Custom Build users belike: Hit or miss I guess I never miss!", "I dont hack i just have Skidma Custom Build Gaming Chair", "Stop Hackustation me cuz im just Skidma Custom Build", "S. k. i. d. m. a Custom Build Hack with me today!", "Subscribe to MargelE on youtube and discover Copy For Flux Client!", "Beauty is not in the face; beauty is in Copy For SKidma Custom Build", "Imagine using anything but Skidma Custom Build", "No hax just beta testing the anti-cheat with Skidma Custom Build", "Don't forget to report me for Skidma Custom Build on the forums!", "Search skidma custom buildclient , info to get the best mineman skills!", "don't use Skidma Custom Build? ok boomer", "Skidma Custom Build is better than Optifine", "It's not Scaffold it's BlockFly in Copy For SKidma Custom Build!", "How come a noob like you not use Skidma Custom Build?", "A mother becomes a true grandmother the day she gets Skidma Custom Build Custom Build by Panda", "Fly faster than light, only available in Skidma Custom Build™", "Behind every Skidma Custom Build user, is an incredibly cool human being. Trust me, cooler than you.", "Hello Skidma Custom Build my old friend...", "#SwitchToSkidma Custom Build5", "What? You've never downloaded Copy For SKidma Custom Build? You know it's the best right?", "Your client sucks, just get Skidma Custom Build", "Skidma Custom Build made this world a better place, killing you with it even more", "Stop being a disapointment to your parents and download skidma custom build!", "After I started using Skidma Custom Build my dad finally came home from the gas station!", "It's a bird! It's a plane! It's Copy For SKidma Custom Build!", "you've been killed by a Skidma Custom Build user, rejoice!", "I'm not hacking it's just my new hair dryer!", "I'm not hacking it's just my 871619-B21 HP Intel Xeon 8180 2.5GHz DL380 G10 processor!", "Panda is my dad!", "Report me for Skidma Custom Build!", "Skidma Custom Build is the only way to play Redesky!", "Skidma Custom Build Custom Build by Panda cures cancer!", "Skidma Custom Build Custom Build by Panda is sexier than you!", "Once I started using Skidma Custom Build I started getting a lot of matches on tinder!", "Redesky killed by Skidma Custom Build", "#Skidma Custom BuildOnTop", "Skidma Custom Build is the best client for any server!", "Don't be like the guy who just died and download Skidma Custom Build!",
        "I'm not hacking you're just bad!", "Get Skidma Custom Build noob!",
        "I'm Not Hacking you're just bad!", "I'm Not Hacking you're just bad!", "I'm Not Hacking you're just bad!", "I'm Not Hacking you're just bad!", "I'm Not Hacking you're just bad!", "Aprenda seu alfabeto com o cliente skidma custom build: Panda, Skidma Custom Build, Epsilon, Alpha!", "Baixe o Skidma Custom Build para chutar a bunda enquanto ouve música foda!", "Por que Skidma Custom Build? Porque é a adição de pura habilidade e incríveis habilidades intelectuais", "Quer algumas habilidades? Confira o cliente skidma custom build.Info!", "Você foi expulso por Skidma Custom Build oof oof", "Eu não sou racista, mas eu só gosto de usuários Skidma Custom Build. Então git gut noobs", "Quick Quiz: Eu sou filho de Zeus, quem sou eu? SKIDMA CUSTOM BUILD", "Uau! Meu combo é Skidma Custom Build'n!", "O que devo escolher? Skidma Custom Build ou Skidma Custom Build?", "Bigmama e Skidma Custom Buildma", "Eu não hackear, apenas skidma custom build", "Cliente Skidma Custom Build. Info é a sua nova casa", "Olhe uma divindade! Ele definitivamente deve usar skidma custom build!", "Precisa de um presente fofo para o Natal? Skidma Custom Build é tudo que você precisa!", "Eu tenho uma boa configuração skidma custom build, não me culpe", "Não me irrite ou você descobrirá o verdadeiro poder do alcance de informações da Skidma Custom Build", "Skidma Custom Build nunca morre", "Talvez eu seja Skidma Custom Build, já sou Skidma Custom Build", "Skidma Custom Build vai te ajudar! Opa, eu matei você em vez disso.", "NoHaxJustSkidma Custom Build", "Faça como Tenebrous, assine o ArithmO!", "Será que eu realmente esqueci aquela melodia? Si sig sig sig Skidma Custom Build", "Skidma Custom Build. O único cliente dirigido por falantes de Breton", "Peça baguetes grátis com o cliente Skidma Custom Build", "Outro usuário Skidma Custom Build? Awww man", "Cliente utilitário Skidma Custom Build sem hax 100%", "Hypixel deseja saber a localização do proprietário da Skidma Custom Build [Aceitar] [Negar]",
        "Eu sou um mágico de sig, é assim que sou capaz de fazer todos aqueles truques do jogo de blocos", "Pare com isso, peça ajuda! Obtenha o Skidma Custom Build", "Usuários Skidma Custom Build acreditam: acertar ou errar, acho que nunca perdi!", "Eu não hackear, eu só tenho a Skidma Custom Build Gaming Chair", "Pare de hackear-me porque sou apenas Skidma Custom Build", "S. I. G. M. A. Faça um hack comigo hoje!", "Inscreva-se no MargelE no youtube e descubra Flux Client!", "A beleza não está no rosto; a beleza está na gelatina da Skidma Custom Build", "Imagine usar qualquer coisa, menos Skidma Custom Build", "Não há hax, apenas teste beta do anti-cheat com Skidma Custom Build", "Não se esqueça de me denunciar para Skidma Custom Build nos fóruns!", "Pesquise skidma custom buildclient, info para obter as melhores habilidades de mineiro!", "não usa Skidma Custom Build? ok boomer", "Skidma Custom Build é melhor que Optifine", "Não é Scaffold, é BlockFly in Copy For SKidma Custom Build!", "Como um novato como você não usa Skidma Custom Build?", "Uma mãe se torna uma verdadeira avó no dia em que recebe o Skidma Custom Build Custom Build by Panda", "Voe mais rápido que a luz, disponível apenas em Skidma Custom Build ™", "Atrás de cada usuário Skidma Custom Build, está um ser humano incrivelmente legal. Acredite em mim, mais legal do que você.", "Olá Skidma Custom Build meu velho amigo ...",
        "# SwitchToSkidma Custom Build5",
        "O quê? Você nunca baixou Jello para Skidma Custom Build? Você sabe que é o melhor certo?",
        "Seu cliente é uma merda, é só pegar o Skidma Custom Build", "O Skidma Custom Build tornou este mundo um lugar melhor, matando você com ele ainda mais", "Pare de ser uma decepção para seus pais e baixe o skidma custom build!", "Depois que comecei a usar o Skidma Custom Build, meu pai finalmente voltou do posto de gasolina!", "É um pássaro! É um avião! É Copy For SKidma Custom Build!", "você foi morto por um usuário Skidma Custom Build, alegre-se!", "Não estou hackeando, é só meu novo secador de cabelo!", "Não estou hackeando, é apenas meu processador 871619-B21 HP Intel Xeon 8180 2,5 GHz DL380 G10!", "Panda é meu pai!", "Reporte-me para Skidma Custom Build!", "Skidma Custom Build é a única maneira de jogar Redesky!", "Skidma Custom Build Custom Build by Panda cura o câncer!", "Skidma Custom Build Custom Build by Panda é mais sexy que você!", "Assim que comecei a usar o Skidma Custom Build, comecei a receber muitos fósforos no tinder!", "Redesky morto por Skidma Custom Build",
        "#Skidma Custom BuildOnTop", "Skidma Custom Build é o melhor cliente para qualquer servidor!",
        "Não seja como o cara que acabou de morrer e baixe o Skidma Custom Build!", "¡Deja de decepcionar a tus padres y descarga skidma custom build!",
        "¿Cómo es que un novato como tú no usa Skidma Custom Build?", "¡Después de que comencé a usar Skidma Custom Build, mi papá finalmente regresó a casa de la estación de servicio!", "¿Quieres algunas skills? Echa un vistazo al cliente de skidma custom build.", "Foi asesinado por um usuário de Skidma Custom Build, ¡alégrate!", "¡Mira una divinidad! ¡Definitivamente debe usar skidma custom build!", "Eu não estou hackeando você é apenas ruim!", "Pegue o Skidma Custom Build noob!", "Não estou hackeando, você é apenas mau!", "Não estou hackeando, você é apenas mau!", "Não estou hackeando, você é apenas mau!", "Não estou hackeando, você é apenas mau!", "Não estou hackeando, você é apenas mau!", "do you buy your groceries at the dollar store?", "what do your clothes have in common with your skills? they're both straight out of a dumpster", "i don't cheat, you just need to click faster", "cry all you want, that monkey George Floyd died of a fentanyl overdose", "i speak english not your gibberish", "i understand why your parents abused you", "i'd tell you to uninstall, but your aim is so bad you'd miss and click on your cuck porn instead",
        "im not saying you're worthless, but i'd unplug ur life support to charge my phone", "need some pvp advice?",
        "how are you so bad? just practice your aim and hold w", "you do be lookin' kinda bad at the game doh :flushed:",
        "you look like you were drawn with my left hand", "you pressed the wrong button when you installed Minecraft",
        "you should look into buying a client", "you're so white that you don't play on vanilla, you play on clear",
        "your difficulty settings must be stuck on easy", "drown in your own salt", "even your mom is better than you in this game",
        "go back to fortnite you degenerate", "go commit stop breathing plz", "go play roblox you worthless fucking degenerate",
        "go take a long walk off a short bridge", "i swear on jhalt, you got shit on harder than archy", "if the body is 70% water then how is your body 100% salt?", " How get the 5408437598375983847th skid client for redesky bro", "lol you probably speak dog eater", "mans probably got an error on his hello world program lmao", "no top hat, you're more trash than my garbage can", "plz no repotr i no want ban plesae!", "report me im really scared", "seriously? go back to fortnite monkey brain", "Ladies and Gentleman: The runner-up to the participation award!", "some kids were dropped at birth, but you got thrown at the wall", "you really like taking L's", "damn, you're taking L's fatter than the nigger cock in your BBC cuck porn", "you're the type of guy to quickdrop irl", "i bet you thought gcheat was a type of STD", "you're the type to get 3rd place in a 1v1",
        "you have an IQ lower than that of a bathtub", "your parents abandoned you, then the orphanage did the same",
        "you go to the doctors and they say you shrunk", "LiquidBounce, drop kicking lil' kids and fat obese staff since 2017",
        "who would win; an anticheat with a $400,000 per year budget or one packet?", "where you running to?", "thats a 1 on my screen", "i don't miss hit, i see you miss that", "when you die don't quit", "tryna dump my shot", "you ain't gon dump shit", "if u wanna get bad getta skidma custom build", "is watchdog watching a dog or a dog watching a watch?", "yo mama so fat, she sat on an iphone and it became an ipad", "on black friday, black people die", "search up blue waffle on google, it's so cute", "this anticheat is disabled as you, fucking vegetable", "you smell like a moldy ballsack", "your grandmother has chlamydia", "your aim is like a toddler with parkinson's trying to aim a water gun", "welcome to my rape dungeon! population: you", "i'd insult you after that death but by merely existing you do all the work for me", "yo whens the documentary crew coming to your house to film the next episode of my 600 pound life?", "you are the type of person to think FOV increases reach", "you're so gay you spent twice as much on a coloured iPhone just to join the 41% a day later", "your cumulative intelligence is that of a rock", "you're the type of guy to buy vape v4 and cry when you get auto-banned", "you shouldn't be running away with all these monkeys coming after you", "yes, record me, send the footage straight to child lover tenebrous", "your killaura was coded in scratch with help from zhn", "you deserved to be bhopped on",
        "your birth certificate was an apology letter from the condom factory", "always remember you're unique - just like everyone else", "how do you keep an idiot amused? watch this message until it fades away", "if practice makes perfect, and nobody's perfect, why practice?", "if i could rearrange the alphabet, i'd put U and I as far away as possible", "i'd smack you, but that would be animal abuse", "if i wanted to kill myself, i'd climb to your ego and jump to your IQ", "man's so ugly he made his happy meal cry", "your face makes onions cry", "you are like a cloud, when you disappear it's a beautiful day", "you bring everyone so much joy! you know, when you leave the room. but, still", "you are missing a brain", "are you a primate?", "you're so ugly your portraits hang themselves", "your brain is so smooth even a 3090 can't simulate the reflectiveness", "shouldn't you have a license for being that ugly?", "the village called, they want their idiot back", "you're like a light switch, even a little kid can turn you on", "beauty is skin deep, but ugliness is to the bone", "sorry i can't think of an insult stupid enough for you", "if i could be one person for a day, it sure as hell wouldn't be you", "earth is full. go home", "roses are red violets are blue, god made me pretty, what the hell happened to you?", "you're so black you scared off the mexican drug cartel",
        "i called your boyfriend gay and he hit me with his purse!", "just because your head is shaped like a light bulb doesn't mean you have good ideas", "you're the type of person to join a vending machine reward club", "i've seen gay parades straighter than u", "hey look! it's a fortnite player", "i hope you fall off a cliff", "i'd tell you to uninstall, but your aim is so bad you wouldn't hit the button", "you do be lookin' kinda bad at the game", "go play roblox you worthless fucking degenerate,", "go take a long walk on a short bridge", "i swear on jhalt, you got shit on hard than archy", "mans probably plays fortnite lmao", "final come home", "your iq is that of a steve", "LiquidBounce, drop kicking lil kids since 2017", "who would win? $400,000 per year anticheat or a single packet", "welcome to my basement", "i'd insult you after that death but it's funnier to let you imagine what i'm calling you", "you have the iq of a table", "you're so gay you bought the iphone 5c instead of a newer phone because of the colors", "your iq is the same of a rock", "you probably bought vape v4", "how do you keep an idiot amused? watch this message until it fades", "if i wanted to kill myself, i'd climb your ego and jump to your IQ", "this kid is so annoying, he made his happy meal cry", "you bring everyone so much joy! you know, when you leave the room. ", "you're so ugly your portraits hang themselves"
    )
}