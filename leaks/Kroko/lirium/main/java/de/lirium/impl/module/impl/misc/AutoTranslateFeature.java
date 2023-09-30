package de.lirium.impl.module.impl.misc;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.misc.TranslatorUtil;
import lombok.AllArgsConstructor;
import net.minecraft.network.play.client.CPacketChatMessage;

@ModuleFeature.Info(name = "Auto Translate", category = ModuleFeature.Category.MISCELLANEOUS, description = "Translate your messages")
public class AutoTranslateFeature extends ModuleFeature {

    @Value(name = "Language")
    private final ComboBox<String> language = new ComboBox<>("English", new String[]{"Afrikaans", "Albanian", "Amharic", "Arabic", "Armenian", "Assamese", "Aymara", "Azerbaijani", "Bambara", "Basque", "Belarusian", "Bengali",
            "Bhojpuri", "Bosnian", "Bulgarian", "Catalan", "Cebuano", "Chinese - Simplified", "Chinese", "Corsican", "Croatian", "Czech", "Danish", "Dhivehi", "Dogri", "Dutch", "Esperanto", "Estonian", "Ewe", "Filipino", "Finnish",
            "French", "Frisian", "Galician", "Georgian", "German", "Greek", "Guarani", "Gujarati", "Haitian Creole", "Hausa", "Hawaiian", "Hebrew", "Hindi", "Hmong", "Hungarian", "Icelandic", "Igbo", "Ilocano", "Indonesian", "Irish",
            "Italian", "Japanese", "Javanese", "Kannada", "Kazakh", "Khmer", "Kinyarwanda", "Konkani", "Korean", "Krio", "Kurdish", "Kurdish - Sorani", "Kyrgyz", "Lao", "Latin", "Latvian", "Lingala", "Lithuanian", "Luganda",
            "Luxembourgish", "Macedonian", "Maithili", "Malagasy", "Malay", "Malayalam", "Maltese", "Maori", "Marathi", "Meiteilon", "Mizo", "Mongolian", "Myanmar", "Nepali", "Norwegian", "Nyanja", "Odia", "Oromo", "Pashto",
            "Persian", "Polish", "Portuguese", "Punjabi", "Quechua", "Romanian", "Russian", "Samoan", "Sanskrit", "Scots Gaelic", "Sepedi", "Serbian", "Sesotho", "Shona", "Sindhi", "Sinhala", "Slovak", "Slovenian", "Somali",
            "Spanish", "Sundanese", "Swahili", "Swedish", "Tagalog", "Tajik", "Tamil", "Tatar", "Telugu", "Thai", "Tigrinya", "Tsonga", "Turkish", "Turkmen", "Twi", "Ukrainian", "Urdu", "Uyghur", "Uzbek", "Vietnamese", "Welsh",
            "Xhosa", "Yiddish", "Yoruba", "Zulu"});

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = e -> {
        if(e.state == PacketEvent.State.RECEIVE) return;
        if (e.packet instanceof CPacketChatMessage) {
            final CPacketChatMessage messagePacket = (CPacketChatMessage) e.packet;
            e.setCancelled(true);
            new Thread() {
                @Override
                public void run() {
                    final String formatted = language.getValue().replace(" - ", "_").replace(" ", "_");
                    final String langCode = Languages.valueOf(formatted.toUpperCase()).langCode;
                    String translated = TranslatorUtil.getTranslatedText(messagePacket.message, langCode);
                    if (translated.length() > 256) {
                        translated = translated.substring(0, 256);
                    }
                    System.out.println(langCode);
                    sendMessage("Translated to " + translated);
                    sendPacketUnlogged(new CPacketChatMessage(translated));
                    super.run();
                }
            }.start();
        }
    };

    @AllArgsConstructor
    private enum Languages {
        AFRIKAANS("af"), ALBANIAN("sq"), AMHARIC("am"), ARABIC("ar"), ARMENIAN("hy"), ASSAMESE("as"), AYMARA("ay"), AZERBAIJANI("az"), BAMBARA("bm"), BASQUE("eu"), BELARUSIAN("be"),
        BENGALI("bn"), BHOJPURI("bho"), BOSNIAN("bs"), BULGARIAN("bg"), CATALAN("ca"), CEBUANO("ceb"), CHINESE_SIMPLIFIED("zh"), CHINESE("zh-TW"), CORSICAN("co"), CROATIAN("hr"),
        CZECH("cs"), DANISH("da"), DHIVEHI("dv"), DOGRI("doi"), DUTCH("nl"), ENGLISH("en"), ESPERANTO("eo"), ESTONIAN("et"), EWE("ee"), FILIPINO("fil"),
        FINNISH("fi"), FRENCH("fr"), FRISIAN("fy"), GALICIAN("gl"), GEORGIAN("ka"), GERMAN("de"), GREEK("el"), GUARANI("gn"), GUJARATI("gu"), HAITIAN_CREOLE("ht"), HAUSA("ha"), HAWAIIAN("haw"),
        HEBREW("he"), HINDI("hi"), HMONG("hmn"), HUNGARIAN("hu"), ICELANDIC("is"), IGBO("ig"), ILOCANO("ilo"), INDONESIAN("id"), IRISH("ga"), ITALIAN("it"),
        JAPANESE("ja"), JAVANESE("jv"), KANNADA("kn"), KAZAKH("kk"), KHMER("km"), KINYARWANDA("rw"), KONKANI("gom"), KOREAN("ko"), KRIO("kri"), KURDISH("ku"), KURDISH_SORANI("ckb"),
        KYRGYZ("ky"), LAO("lo"), LATIN("la"), LATVIAN("lv"), LINGALA("ln"), LITHUANIAN("lt"), LUGANDA("lg"), LUXEMBOURGISH("lb"), MACEDONIAN("mk"), MAITHILI("mai"),
        MALAGASY("mg"), MALAY("ms"), MALAYALAM("ml"), MALTESE("mt"), MAORI("mi"), MARATHI("mr"), MEITEILON("mni-Mtei"), MIZO("lus"), MONGOLIAN("mn"),
        MYANMAR("my"), NEPALI("ne"), NORWEGIAN("no"), NYANJA("ny"), ODIA("or"), OROMO("om"), PASHTO("ps"), PERSIAN("fa"), POLISH("pl"), PORTUGUESE("pt"),
        PUNJABI("pa"), QUECHUA("qu"), ROMANIAN("ro"), RUSSIAN("ru"), SAMOAN("sm"), SANSKRIT("sa"), SCOTS_GAELIC("gd"), SEPEDI("nso"), SERBIAN("sr"), SESOTHO("st"),
        SHONA("sn"), SINDHI("sd"), SINHALA("si"), SLOVAK("sk"), SLOVENIAN("sl"), SOMALI("so"), SPANISH("es"), SUNDANESE("su"), SWAHILI("sw"), SWEDISH("sv"),
        TAGALOG("tl"), TAJIK("tg"), TAMIL("ta"), TATAR("tt"), TELUGU("te"), THAI("th"), TIGRINYA("ti"), TSONGA("ts"), TURKISH("tr"), TURKMEN("tk"),
        TWI("ak"), UKRAINIAN("uk"), URDU("ur"), UYGHUR("ug"), UZBEK("uz"), VIETNAMESE("vi"), WELSH("cy"), XHOSA("xh"), YIDDISH("yi"), YORUBA("yo"), ZULU("zu");
        public final String langCode;
    }
}
