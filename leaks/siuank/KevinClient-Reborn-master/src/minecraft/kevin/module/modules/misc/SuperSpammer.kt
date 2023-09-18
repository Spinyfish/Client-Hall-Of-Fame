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

import kevin.event.EventTarget
import kevin.event.UpdateEvent

//import kevin.event.UpdateState

import kevin.main.KevinClient
import kevin.module.*
import kevin.utils.MSTimer
import kevin.utils.RandomUtils
import kevin.utils.TimeUtils
import java.io.File
import java.io.FileFilter
import java.util.*
import kotlin.collections.ArrayList

class SuperSpammer : Module("SuperSpammer","Spams the chat with given messages.", category = ModuleCategory.MISC) {
    private val modeList = arrayListOf(
        "Single",
        "Switch",
        "华强买瓜",
        "精通人性的女讲师",
        "杰哥不要",
        "你看到的我",
        "AlieZ",
        "说句实话",
        "Invincible"
    )
    private val fileSuffix = ".txt"
    private var modeListArray = modeList.toTypedArray()
    init {
        val files = KevinClient.fileManager.spammerDir
        val spammerFiles = files.listFiles(FileFilter { it.name.endsWith(fileSuffix) })
        if (spammerFiles != null) for (i in spammerFiles) modeList.add(i.name.split(".txt")[0])
        modeListArray = modeList.toTypedArray()
    }
    private val modeValue = ListValue("Mode", modeListArray,"Single")

    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 1000, 0, 5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelayValueObject = minDelayValue.get()
            if (minDelayValueObject > newValue) set(minDelayValueObject)
            delay = TimeUtils.randomDelay(minDelayValue.get(), get())
        }
    }

    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 500, 0, 5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelayValueObject = maxDelayValue.get()
            if (maxDelayValueObject < newValue) set(maxDelayValueObject)
            delay = TimeUtils.randomDelay(get(), maxDelayValue.get())
        }
    }

    private val messageValue = TextValue("SingleMessage", KevinClient.name + " Client | Jiege")
    private val switchMessage1 = TextValue("SwitchMessageFirst","https://space.bilibili.com/1372772553 Liquid_Bounce-杰哥")
    private val switchMessage2 = TextValue("SwitchMessageSecond","Liquid_Bounce-杰哥 https://space.bilibili.com/1372772553")

    private val randomCharacterAtFirst = BooleanValue("RandomCharacterAtFirst",true)
    private val randomCharacterAtLast = BooleanValue("RandomCharacterAtLast",true)


    private val firstMaxLength:IntegerValue = object : IntegerValue("RandomCharacterAtFirstMaxLength",3,1,7){
        override fun onChange(oldValue: Int, newValue: Int) {
            val min = firstMinLength.get()
            if (min > newValue) set(min)
            firstLength = RandomUtils.nextInt(firstMinLength.get(),get())
            super.onChange(oldValue, newValue)
        }
    }
    private val firstMinLength:IntegerValue = object : IntegerValue("RandomCharacterAtFirstMinLength",1,1,7){
        override fun onChange(oldValue: Int, newValue: Int) {
            val max = firstMaxLength.get()
            if (max < newValue) set(max)
            firstLength = RandomUtils.nextInt(get(),firstMaxLength.get())
            super.onChange(oldValue, newValue)
        }
    }
    private var firstLength = RandomUtils.nextInt(firstMinLength.get(),firstMaxLength.get())


    private val lastMaxLength:IntegerValue = object : IntegerValue("RandomCharacterAtLastMaxLength",3,1,7){
        override fun onChange(oldValue: Int, newValue: Int) {
            val min = lastMinLength.get()
            if (min > newValue) set(min)
            lastLength = RandomUtils.nextInt(lastMinLength.get(),get())
            super.onChange(oldValue, newValue)
        }
    }
    private val lastMinLength:IntegerValue = object : IntegerValue("RandomCharacterAtLastMinLength",1,1,7){
        override fun onChange(oldValue: Int, newValue: Int) {
            val max = lastMaxLength.get()
            if (max < newValue) set(max)
            lastLength = RandomUtils.nextInt(get(),lastMaxLength.get())
            super.onChange(oldValue, newValue)
        }
    }
    private var lastLength = RandomUtils.nextInt(lastMinLength.get(),lastMaxLength.get())

    private val startMode = ListValue("Prefix", arrayOf("None","/shout",".","@","!","Custom"),"None")
    private val customPrefix = TextValue("CustomPrefix", "")
    private val firstLeft = TextValue("RandomCharacterAtFirstLeft","[")
    private val firstRight = TextValue("RandomCharacterAtFirstRight","]")
    private val lastLeft = TextValue("RandomCharacterAtLastLeft","[")
    private val lastRight = TextValue("RandomCharacterAtLastRight","]")
    private val customNoRandomV = BooleanValue("CustomNoRandom",true)
    private val autoDisableV = BooleanValue("AutoDisable",false)

    private val msTimer = MSTimer()
    private var delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
    private var sentencesNumber = 0
    private var switchState = 1
    private var lastMode:String? = null

    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        //if (event.eventState == UpdateState.OnUpdate) return

        val mode = modeValue.get()
        val start = when(startMode.get()) {
            "/shout" -> "/shout "
            "." -> ".say ."
            "@" -> "@"
            "!" -> "!"
            "Custom" -> customPrefix.get()
            else -> ""
        }
        val first = if (randomCharacterAtFirst.get()) "$start${firstLeft.get()}${RandomUtils.randomString(firstLength)}${firstRight.get()}" else start
        val last = if (randomCharacterAtLast.get())"${lastLeft.get()}${RandomUtils.randomString(lastLength)}${lastRight.get()}" else ""
        if (msTimer.hasTimePassed(delay)) {
            if (mode.equals("Switch",true)) {
                switchState = if (switchState == 1) {
                    val text = "$first${switchMessage1.get()}$last"
                    mc.thePlayer.sendChatMessage(text)
                    2
                } else {
                    val text = "$first${switchMessage2.get()}$last"
                    mc.thePlayer.sendChatMessage(text)
                    1
                }
            } else if (mode.equals("Single",true)) {
                val text = "$first${messageValue.get()}$last"
                mc.thePlayer.sendChatMessage(text)
            } else {
                var spammerList = arrayListOf("")
                when (mode.lowercase(Locale.getDefault())){
                    "华强买瓜" -> spammerList = huaQiangList
                    "精通人性的女讲师" -> spammerList = nvJiangShiList
                    "杰哥不要" -> spammerList = jieGeList
                    "你看到的我" -> spammerList = niKanDaoDeWoList
                    "aliez" -> spammerList = aliezList
                    "说句实话" -> spammerList = shuoJuShiHuaList
                    "invincible" -> spammerList = invincibleList
                    else -> {
                        val files = KevinClient.fileManager.spammerDir.listFiles()!!
                        var file: File? = null
                        for (i in files){
                            if (mode == i.name.split(".txt")[0]) file = i
                        }
                        if (file != null) spammerList = ArrayList(file.readLines())
                    }
                }
                if (mode != lastMode){
                    sentencesNumber = 0
                    lastMode = mode
                }
                if (spammerList.isNotEmpty()) mc.thePlayer.sendChatMessage(if (customNoRandomV.get()) "$start${spammerList[sentencesNumber]}" else "$first${spammerList[sentencesNumber]}$last")
                if (sentencesNumber < spammerList.size - 1) sentencesNumber += 1 else if (autoDisableV.get()) KevinClient.moduleManager.getModuleByName(this.name)?.toggle() else sentencesNumber = 0
            }
            msTimer.reset()
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            firstLength = RandomUtils.nextInt(firstMinLength.get(),firstMaxLength.get())
            lastLength = RandomUtils.nextInt(lastMinLength.get(),lastMaxLength.get())
        }
    }
    override fun onEnable() {
        lastMode = modeValue.get()
        msTimer.reset()
        sentencesNumber = 0
    }
    override fun onDisable() {
        lastMode = null
        sentencesNumber = 0
    }
    override val tag: String
        get() = modeValue.get() + "  " + sentencesNumber.toString()
    private val nvJiangShiList = arrayListOf(
        "<三句话，让男人为我花了十八万>",
        "我是一个很善于让男人为我花钱的精通人性的女讲师",
        "前两天呢我与一个男性的朋友吃饭",
        "当我坐下来的时候我直接问了一句：哇塞我今天好漂亮，给你个机会夸夸我",
        "他哈哈大笑，一时半会儿呢都没有回过神来",
        "这种呢就是典型的直男",
        "然后我坐下来继续问：我们玩个问答游戏吧",
        "他说：你问我答",
        "我说 ：你知道在我眼里你什么时候最帅吗",
        "他说我不知道，所以直男很无趣",
        "普通女人呢这时候会说你为我买单的时候最帅",
        "但是我说什么呢 你为我拿餐具 点菜 夹菜 买单 提包的时候最帅",
        "他又是一份意想不到的狂喜，接下来的全程呢我是什么也不用干，他还屁颠屁颠的为我服务",
        "吃到最后我说来你给我来一只龙虾奖励我这么有眼光跟天下第一帅的绅士男吃饭，好开心啊",
        "最后，他非常开心的就把单给买了",
        "这一餐饭我们花了十五万八千六",
        "回到家的时候我打开手机以看这个男人给我转了一个一万八千八的红包，说了一句和你在一起真开心",
        "一个女人说话有趣很重要，会调戏男人更重要",
        "先敬于礼乐野人也，后敬于礼乐君子也"
    )
    private val huaQiangList = arrayListOf(
        "<有一个人前来买瓜>",
        "生异形吗你们哥几个，哥俩",
        "哥们，你这瓜多少钱一斤啊",
        "两块钱一斤",
        "What's Up 这瓜皮子是金子做的还是瓜粒子是金子做的",
        "你瞧瞧这现在哪儿有瓜呀，这都是大棚的瓜，你嫌贵我还嫌贵呢",
        "给我挑一个",
        "行",
        "这个怎么样",
        "你这瓜保熟吗",
        "我开水果摊的，能卖给你生瓜蛋子",
        "我问你这瓜保熟吗",
        "你是故意找茬是不是啊，你要不要吧",
        "这瓜要熟我肯定要啊，那他要是不熟怎么办啊",
        "哎，要是不熟，我自己吃了它，满意了吧",
        "十五斤，三十块",
        "你这哪够十五斤啊你这称有问题啊",
        "你TM故意找茬是不是啊，你要不要吧，你要不要",
        "吸铁石，另外你说的这瓜要是生的你自己吞进去啊",
        "（劈瓜）！",
        "你TM劈我瓜是吧",
        "我，chi↘！（劈人）",
        "哎，杀人啦，杀人啦",
        "哎，华强，华强！（回头一笑）"
    )
    private val jieGeList = arrayListOf(
        "《杰哥不要》",
        "阿伟：弱欸，拜托你很弱欸，现在知道谁是老大了哦，哈！",
        "阿嫲：阿伟你又在打电动哦，休息一下吧，看个书好不好？",
        "阿伟：烦哎。",
        "阿嫲：我在和你说话你有没有听到？",
        "阿伟：你不要烦好不好！",
        "阿嫲：我才说你两句你就说我烦！",
        "阿嫲：我只希望你能够好好读书，整天看到你在那边打电动！",
        "阿伟：啊，死了啦，都你害的啦！拜托！",
        "阿伟：那天，我只是因为受不了阿嫲啰嗦，就冲出去，谁知道竟然...",
        "阿伟：他超废的欸！",
        "彬彬：我知道啊，之前就干掉他过",
        "阿伟：二班那个啊，他每次都被我洗战绩欸。",
        "彬彬：他就嫩呗",
        "阿伟：哎快点那",
        "彬彬：看到看到",
        "阿伟：左边左边",
        "彬彬：好嘞",
        "阿伟：上面上面上面上面上面",
        "彬彬：放了放了",
        "阿伟：左边左边",
        "彬彬：哦你不会放哦。快点快点，我死了！",
        "阿伟：复活复活",
        "彬彬：来救我快点快点",
        "阿伟：来了来了来了来了",
        "彬彬：啊我死了我不要玩了",
        "阿伟：哎",
        "彬彬：干嘛啦",
        "阿伟：不要每次都这样好不好",
        "彬彬：不想玩了啦",
        "阿伟：哎你还有没有钱啊我肚子好饿",
        "彬彬：没了啦今天都花光了",
        "阿伟：我刚刚不是让你多带一点吗？",
        "彬彬：你干嘛自己不多带",
        "阿伟：你不要每次都这样好不好",
        "彬彬：好了时间也差不多了我们该回家了",
        "阿伟：不要我要再玩一下",
        "阿伟：哎彬彬",
        "彬彬：干嘛",
        "阿伟：我好饿哦，我们两个都没钱了，你要干嘛啊",
        "彬彬：没有钱我们就只能回家",
        "阿伟：我才不要回家欸我阿嫲超凶的，去住你家啦",
        "彬彬：不行啦",
        "阿伟：为什么不行",
        "彬彬：我自己都自身难保了",
        "阿伟：哪有",
        "彬彬：而且我爸会揍我",
        "阿伟：真的假的",
        "杰哥：欸不好意思，我刚刚听到你们两个说肚子饿，我这刚好有个面包",
        "杰哥：我还不饿，来请你们吃",
        "阿伟：先吃先吃",
        "杰哥：对了，我叫阿杰，我也常来这里玩，他们都叫我杰哥",
        "阿伟，彬彬：杰哥好",
        "阿伟：先吃啦先吃啦",
        "杰哥：你们好，我一个人住，我的房子还蛮大的",
        "杰哥：欢迎你们到我家玩，玩累了就直接睡觉，没问题的",
        "阿伟：你觉得呢",
        "彬彬：我觉得怪怪的",
        "阿伟：就是一个很奇怪的人啊，不要理他",
        "彬彬：不要去不要去",
        "杰哥：我常常帮助一些翘家的人，如果你们不要来的话，也没有关系",
        "杰哥：如果要来的话，我等会可以带你们去超商，买一些好吃的哦",
        "阿伟：有东西可以吃哎，要不要去啊",
        "彬彬：好了不要去",
        "阿伟：去一下好了啦",
        "彬彬：好好",
        "阿伟：杰哥那我跟我朋友今天就住你家",
        "杰哥：好啊没问题啊，那走啊，我们现在就去超商买一些吃的",
        "阿伟：好啊",
        "杰哥：走走走走走",
        "阿伟：你去那边你去那边",
        "彬彬：泡面",
        "阿伟：小泡芙欸",
        "杰哥：都可以拿",
        "阿伟：谢谢杰哥",
        "彬彬：好多饮料哦",
        "阿伟：有酒哎",
        "彬彬：不要看酒啦先看饮料",
        "彬彬：什么都可以拿吗",
        "杰哥：都可以拿",
        "彬彬：真的假的",
        "杰哥：随便拿，你们随便拿",
        "阿伟：真的可以吗",
        "杰哥：可以拿，都拿",
        "阿伟：谢谢杰哥",
        "彬彬：啊再喝，再喝，啊再喝再喝",
        "杰哥：你看，你看那个彬彬，才喝几罐就醉了，真的太逊了",
        "阿伟：这个彬彬就是逊啦",
        "杰哥：听你那么说，你很勇哦",
        "阿伟：开玩笑，我超勇的好不好，我超会喝的啦",
        "杰哥：超会喝，很勇嘛，身材不错哦，蛮结实的啊",
        "彬彬：杰哥你干嘛啊",
        "杰哥：都几岁了，还那么害羞，我看你完全是不懂哦",
        "阿伟：懂什么啊",
        "杰哥：你想懂，我房里有一些好康的",
        "阿伟：好康，是新游戏哦",
        "杰哥：什么新游戏，比游戏还刺激，还可以教你登dua郎哦",
        "阿伟：登dua郎？",
        "杰哥：对了来看你就知道了",
        "阿伟：杰哥酒",
        "杰哥：拿拿拿来来来",
        "阿伟：干嘛啦",
        "阿伟：杰哥你有好多A片哦",
        "杰哥：那没什么来看这个好康的",
        "阿伟：杰哥，这是什么啊",
        "杰哥：哎呦，你脸红啦，来，让我看看",
        "阿伟：不要啦",
        "杰哥：让我看看",
        "阿伟：不要啦，杰哥，你干嘛啊",
        "杰哥：让我看看你发育正不正常啊",
        "阿伟：杰哥，不要啦",
        "杰哥：听话，让我看看！！",
        "阿伟：不要",
        "阿伟：杰哥不要啊，杰哥不要，杰哥",
        "杰哥：这件事是我们两个人之间的秘密，你最好不要给我告诉任何人",
        "杰哥：如果你要说出去，就给我小心一点",
        "杰哥：我知道你学校在哪，也知道你都那一班",
        "杰哥：你最好给我好好记住，懂吗",
        "完"
    )
    private val niKanDaoDeWoList = arrayListOf(
        "<你看到的我>",
        "背起了行囊",
        "离开家的那一刻",
        "我知道现实生活",
        "有太多特别的特",
        "假如你看到了我",
        "也不要太过冷漠",
        "我多愁善感",
        "但也热情奔放洒脱",

        "匆忙的世界",
        "来不及问为什么",
        "我知道漂泊的人",
        "心中都有一团火",
        "假如你又看到我",
        "请给我一个拥抱",
        "我偶尔沉默但也勇敢执着",
        "你看到的我你看到的我",
        "是哪一种颜色悲伤或快乐",
        "也许老了一点",
        "眼神变得不再那么清澈",
        "热血依然沸腾着我的脉搏",
        "你看到的我",
        "你看到的我",
        "是哪一种性格",
        "开朗或慢热",
        "像勇敢的雄鹰",
        "不怕风雨不怕困难挫折",
        "如果要飞就飞出天空海阔",
        "我就是我",

        "匆忙的世界来不及问为什么",
        "我知道漂泊的人",
        "心中都有一团火",
        "假如你又看到我",
        "请给我一个拥抱",
        "我偶尔沉默但也勇敢执着",
        "你看到的我你看到的我",
        "是哪一种颜色悲伤或快乐",
        "也许老了一点",
        "眼神变得不再那么清澈",
        "热血依然沸腾着我的脉搏",
        "你看到的我你看到的我",
        "是哪一种性格",
        "开朗或慢热",
        "像勇敢的雄鹰",
        "不怕风雨不怕困难挫折",
        "如果要飞就飞出天空海阔",
        "我就是我",

        "你看到的我你看到的我",
        "是哪一种颜色悲伤或快乐",
        "也许老了一点",
        "眼神变得不再那么清澈",
        "热血依然沸腾着我的脉搏",
        "你看到的我你看到的我",
        "是哪一种性格",
        "开朗或慢热",
        "像勇敢的雄鹰",
        "不怕风雨不怕困难挫折",
        "如果要飞就飞出天空海阔",
        "我就是我"
    )
    private val aliezList = arrayListOf(
        "<AlieZ>",
        "決めつけばかり",
        "自惚れを着たチープな hokori で",
        "音荒げても",
        "棚に隠した哀れな",
        "恥に濡れた鏡の中",
        "都合の傷だけひけらかして",
        "手軽な強さで勝取る術を",
        "どれだけ磨いでも気はやつれる",
        "ふらついた思想通りだ",
        "愛-same-CRIER 愛撫-save-LIAR",
        "Eid-聖-Rising HELL",
        "愛してる game 世界の day",
        "Don't-生-War Lie-兵士-War-World",
        "Eyes-Hate-War",
        "A-Z Looser-Krankheit-Was IS das?",
        "受け売り盾に見下してても",
        "そこには地面しかない事さえ",
        "気付かぬままに壊れた",
        "過去に負けた鏡の奥",
        "どこまで叫べば位置を知れる",
        "とどめもないまま息が切れる",
        "堂々さらした罪の群れと",
        "後ろ向きにあらがう",
        "愛-same-CRIER 愛撫-save-LIAR",
        "Aid-聖-Rising HELL",
        "I'll-ness Reset-Endじゃない Burst",
        "Don't-生-War Lie-兵士-War-World",
        "Eyes-Hate-War",
        "A-Z 想像High-de-Siehst YOU das?",
        "偽の態度な臆病loud voice",
        "気高さを勘違いした心臓音",
        "狙い通りの幻見ても",
        "満たせない何度も目を開けても",
        "どこまで叫べば位置を知れる",
        "とどめもないまま息が切れる",
        "堂々さらした罪の群れと",
        "後ろ向きにあらがう",
        "愛-same-CRIER 愛撫-save-LIAR",
        "Eid-聖-Rising HELL",
        "愛してる Game世界のDay",
        "Don't-生-War Lie-兵士-War-World",
        "Eyes-Hate-War",
        "A-Z Looser-Krankheit-Was IS das?",
        "Leben was ist das?",
        "Signal siehst du das?",
        "Rade die du nicht weisst",
        "Aus eigenem willen",
        "Leben was ist das?",
        "Signal siehst du das?",
        "Rade die du nicht weisst",
        "Sieh mit deinen augen"
    )
    private val shuoJuShiHuaList = arrayListOf(
        "<说句实话>",
        "我还是习惯性的对你好奇",
        "窥探你的生活",
        "但放下所有一切关于你的不再争夺",
        "我无所谓去接受你的 runaway",
        "反正你现在的他没我 OK",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "当你开始对我失望之后",
        "我的创作都靠酒精",
        "让暴雨把我淋的湿透",
        "他们说我开始变的走心",
        "U know girl 现在的我滴酒不沾",
        "当我彻底的不在乎才发现",
        "身边的女孩都比你好看",
        "当初发过誓一定把你追回来",
        "我承认后来被雷劈了",
        "曾经能背下电话里面的对白",
        "现在连号码都不记得",
        "但我想邀请你看一下我的现在",
        "房间里随便坐别再见外",
        "一起养过的狗依然健在",
        "我承认我曾经就像个变态",
        "Who can do it like me what will I be",
        "太多想不出答案的问题",
        "连我自己都开始怀疑",
        "Who can do it like me",
        "就像胎记",
        "像是与生俱来的一直存在的",
        "都不借助外力",
        "我还是习惯性的对你好奇",
        "窥探你的生活",
        "但放下所有一切关于你的不再争夺",
        "我无所谓去接受你的 runaway",
        "反正你现在的他没我 OK",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "在非洲 run ２ 长颈马",
        "让暴雨淋到了圣诞节",
        "在逃命前抱好你黄金吧",
        "Jet 式病毒都不用蔓延",
        "这首歌只是表达我牛逼",
        "你有什么意见呢",
        "一脚把你踹飞滚下楼梯",
        "Peace and love",
        "我这么狂野没有谦虚从不 be humble",
        "Make sh*t 脚踏实地 绝不是充气的",
        "U wanna do it like me 秘诀是 dgu",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me",
        "说句实话",
        "Who can do it like me",
        "Who can do it like me"
    )
    private val invincibleList = arrayListOf(
        "<Invincible>",
        "I feel like a super woman in your eyes tonight",
        "And you make me feel like I am bulletproof inside",
        "'Cause I'll fight for you give my life for you",
        "And I got you by my side",
        "There's no barricade we can't tear away",
        "When it comes to you and I",
        "'Cause even if we break even if we fall",
        "Baby you know we can have it all",
        "And if they knock us down like a wrecking ball",
        "We'll get up and walk right through these walls",
        "'Cause we are we are invincible invincible",
        "We are we are invincible invincible",
        "We are we are invincible invincible",
        "We are we are invincible invincible",
        "You make me feel not afraid of anything",
        "And nothing in the universe will come between",
        "'Cause I'll fight for you give my life for you",
        "And I got you by my side",
        "There's no barricade we can't tear away",
        "When it comes to you and I",
        "'Cause even if we break even if we fall",
        "Baby you know we can have it all",
        "And if they knock us down like a wrecking ball",
        "We'll get up and walk right through these walls",
        "'Cause we are we are invincible invincible",
        "We are we are invincible invincible",
        "We are we are invincible invincible",
        "We are we are invincible invincible",
        "I feel like a super woman in your eyes tonight",
        "And you make me feel like I am bulletproof inside",
        "'Cause I'll fight for you give my life for you",
        "And I got you by my side",
        "There's no barricade we can't tear away",
        "When it comes to you and I",
        "'Cause even if we break even if we fall",
        "Baby you know we can have it all",
        "And if they knock us down like a wrecking ball",
        "We'll get up and walk right through these walls",
        "'Cause we are we are invincible invincible",
        "We are we are invincible invincible",
        "We are we are invincible invincible",
        "We are we are invincible invincible"
    )
}