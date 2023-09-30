/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  tv.twitch.AuthToken
 *  tv.twitch.Core
 *  tv.twitch.CoreAPI
 *  tv.twitch.ErrorCode
 *  tv.twitch.StandardCoreAPI
 *  tv.twitch.chat.Chat
 *  tv.twitch.chat.ChatAPI
 *  tv.twitch.chat.ChatBadgeData
 *  tv.twitch.chat.ChatChannelInfo
 *  tv.twitch.chat.ChatEmoticonData
 *  tv.twitch.chat.ChatEvent
 *  tv.twitch.chat.ChatRawMessage
 *  tv.twitch.chat.ChatTokenizationOption
 *  tv.twitch.chat.ChatTokenizedMessage
 *  tv.twitch.chat.ChatUserInfo
 *  tv.twitch.chat.IChatAPIListener
 *  tv.twitch.chat.IChatChannelListener
 *  tv.twitch.chat.StandardChatAPI
 */
package net.minecraft.client.stream;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.client.stream.TwitchStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.CoreAPI;
import tv.twitch.ErrorCode;
import tv.twitch.StandardCoreAPI;
import tv.twitch.chat.Chat;
import tv.twitch.chat.ChatAPI;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatChannelInfo;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.ChatEvent;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizationOption;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;
import tv.twitch.chat.StandardChatAPI;

public class ChatController {
    private static final Logger LOGGER = LogManager.getLogger();
    protected ChatListener field_153003_a = null;
    protected String field_153004_b = "";
    protected String field_153006_d = "";
    protected String field_153007_e = "";
    protected Core field_175992_e = null;
    protected Chat field_153008_f = null;
    protected ChatState field_153011_i = ChatState.Uninitialized;
    protected AuthToken field_153012_j = new AuthToken();
    protected HashMap<String, ChatChannelListener> field_175998_i = new HashMap();
    protected int field_153015_m = 128;
    protected EnumEmoticonMode field_175997_k = EnumEmoticonMode.None;
    protected EnumEmoticonMode field_175995_l = EnumEmoticonMode.None;
    protected ChatEmoticonData field_175996_m = null;
    protected int field_175993_n = 500;
    protected int field_175994_o = 2000;
    protected IChatAPIListener field_175999_p = new IChatAPIListener(){

        public void chatInitializationCallback(ErrorCode p_chatInitializationCallback_1_) {
            if (ErrorCode.succeeded((ErrorCode)p_chatInitializationCallback_1_)) {
                ChatController.this.field_153008_f.setMessageFlushInterval(ChatController.this.field_175993_n);
                ChatController.this.field_153008_f.setUserChangeEventInterval(ChatController.this.field_175994_o);
                ChatController.this.func_153001_r();
                ChatController.this.func_175985_a(ChatState.Initialized);
            } else {
                ChatController.this.func_175985_a(ChatState.Uninitialized);
            }
            try {
                if (ChatController.this.field_153003_a != null) {
                    ChatController.this.field_153003_a.func_176023_d(p_chatInitializationCallback_1_);
                }
            }
            catch (Exception exception) {
                ChatController.this.func_152995_h(exception.toString());
            }
        }

        public void chatShutdownCallback(ErrorCode p_chatShutdownCallback_1_) {
            if (ErrorCode.succeeded((ErrorCode)p_chatShutdownCallback_1_)) {
                ErrorCode errorcode = ChatController.this.field_175992_e.shutdown();
                if (ErrorCode.failed((ErrorCode)errorcode)) {
                    String s = ErrorCode.getString((ErrorCode)errorcode);
                    ChatController.this.func_152995_h(String.format("Error shutting down the Twitch sdk: %s", s));
                }
                ChatController.this.func_175985_a(ChatState.Uninitialized);
            } else {
                ChatController.this.func_175985_a(ChatState.Initialized);
                ChatController.this.func_152995_h(String.format("Error shutting down Twith chat: %s", p_chatShutdownCallback_1_));
            }
            try {
                if (ChatController.this.field_153003_a != null) {
                    ChatController.this.field_153003_a.func_176022_e(p_chatShutdownCallback_1_);
                }
            }
            catch (Exception exception) {
                ChatController.this.func_152995_h(exception.toString());
            }
        }

        public void chatEmoticonDataDownloadCallback(ErrorCode p_chatEmoticonDataDownloadCallback_1_) {
            if (ErrorCode.succeeded((ErrorCode)p_chatEmoticonDataDownloadCallback_1_)) {
                ChatController.this.func_152988_s();
            }
        }
    };

    public void func_152990_a(ChatListener p_152990_1_) {
        this.field_153003_a = p_152990_1_;
    }

    public void func_152994_a(AuthToken p_152994_1_) {
        this.field_153012_j = p_152994_1_;
    }

    public void func_152984_a(String p_152984_1_) {
        this.field_153006_d = p_152984_1_;
    }

    public void func_152998_c(String p_152998_1_) {
        this.field_153004_b = p_152998_1_;
    }

    public ChatState func_153000_j() {
        return this.field_153011_i;
    }

    public boolean func_175990_d(String p_175990_1_) {
        if (!this.field_175998_i.containsKey(p_175990_1_)) {
            return false;
        }
        ChatChannelListener chatcontroller$chatchannellistener = this.field_175998_i.get(p_175990_1_);
        return chatcontroller$chatchannellistener.func_176040_a() == EnumChannelState.Connected;
    }

    public EnumChannelState func_175989_e(String p_175989_1_) {
        if (!this.field_175998_i.containsKey(p_175989_1_)) {
            return EnumChannelState.Disconnected;
        }
        ChatChannelListener chatcontroller$chatchannellistener = this.field_175998_i.get(p_175989_1_);
        return chatcontroller$chatchannellistener.func_176040_a();
    }

    public ChatController() {
        this.field_175992_e = Core.getInstance();
        if (this.field_175992_e == null) {
            this.field_175992_e = new Core((CoreAPI)new StandardCoreAPI());
        }
        this.field_153008_f = new Chat((ChatAPI)new StandardChatAPI());
    }

    public boolean func_175984_n() {
        if (this.field_153011_i != ChatState.Uninitialized) {
            return false;
        }
        this.func_175985_a(ChatState.Initializing);
        ErrorCode errorcode = this.field_175992_e.initialize(this.field_153006_d, (String)null);
        if (ErrorCode.failed((ErrorCode)errorcode)) {
            this.func_175985_a(ChatState.Uninitialized);
            String s1 = ErrorCode.getString((ErrorCode)errorcode);
            this.func_152995_h(String.format("Error initializing Twitch sdk: %s", s1));
            return false;
        }
        this.field_175995_l = this.field_175997_k;
        HashSet<ChatTokenizationOption> hashset = new HashSet<ChatTokenizationOption>();
        switch (this.field_175997_k) {
            case None: {
                hashset.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE);
                break;
            }
            case Url: {
                hashset.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS);
                break;
            }
            case TextureAtlas: {
                hashset.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES);
            }
        }
        errorcode = this.field_153008_f.initialize(hashset, this.field_175999_p);
        if (ErrorCode.failed((ErrorCode)errorcode)) {
            this.field_175992_e.shutdown();
            this.func_175985_a(ChatState.Uninitialized);
            String s = ErrorCode.getString((ErrorCode)errorcode);
            this.func_152995_h(String.format("Error initializing Twitch chat: %s", s));
            return false;
        }
        this.func_175985_a(ChatState.Initialized);
        return true;
    }

    public boolean func_152986_d(String p_152986_1_) {
        return this.func_175987_a(p_152986_1_, false);
    }

    protected boolean func_175987_a(String p_175987_1_, boolean p_175987_2_) {
        if (this.field_153011_i != ChatState.Initialized) {
            return false;
        }
        if (this.field_175998_i.containsKey(p_175987_1_)) {
            this.func_152995_h("Already in channel: " + p_175987_1_);
            return false;
        }
        if (p_175987_1_ != null && !p_175987_1_.equals("")) {
            ChatChannelListener chatcontroller$chatchannellistener = new ChatChannelListener(p_175987_1_);
            this.field_175998_i.put(p_175987_1_, chatcontroller$chatchannellistener);
            boolean flag = chatcontroller$chatchannellistener.func_176038_a(p_175987_2_);
            if (!flag) {
                this.field_175998_i.remove(p_175987_1_);
            }
            return flag;
        }
        return false;
    }

    public boolean func_175991_l(String p_175991_1_) {
        if (this.field_153011_i != ChatState.Initialized) {
            return false;
        }
        if (!this.field_175998_i.containsKey(p_175991_1_)) {
            this.func_152995_h("Not in channel: " + p_175991_1_);
            return false;
        }
        ChatChannelListener chatcontroller$chatchannellistener = this.field_175998_i.get(p_175991_1_);
        return chatcontroller$chatchannellistener.func_176034_g();
    }

    public boolean func_152993_m() {
        if (this.field_153011_i != ChatState.Initialized) {
            return false;
        }
        ErrorCode errorcode = this.field_153008_f.shutdown();
        if (ErrorCode.failed((ErrorCode)errorcode)) {
            String s = ErrorCode.getString((ErrorCode)errorcode);
            this.func_152995_h(String.format("Error shutting down chat: %s", s));
            return false;
        }
        this.func_152996_t();
        this.func_175985_a(ChatState.ShuttingDown);
        return true;
    }

    public void func_175988_p() {
        if (this.func_153000_j() != ChatState.Uninitialized) {
            this.func_152993_m();
            if (this.func_153000_j() == ChatState.ShuttingDown) {
                while (this.func_153000_j() != ChatState.Uninitialized) {
                    try {
                        Thread.sleep(200L);
                        this.func_152997_n();
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
        }
    }

    public void func_152997_n() {
        ErrorCode errorcode;
        if (this.field_153011_i != ChatState.Uninitialized && ErrorCode.failed((ErrorCode)(errorcode = this.field_153008_f.flushEvents()))) {
            String s = ErrorCode.getString((ErrorCode)errorcode);
            this.func_152995_h(String.format("Error flushing chat events: %s", s));
        }
    }

    public boolean func_175986_a(String p_175986_1_, String p_175986_2_) {
        if (this.field_153011_i != ChatState.Initialized) {
            return false;
        }
        if (!this.field_175998_i.containsKey(p_175986_1_)) {
            this.func_152995_h("Not in channel: " + p_175986_1_);
            return false;
        }
        ChatChannelListener chatcontroller$chatchannellistener = this.field_175998_i.get(p_175986_1_);
        return chatcontroller$chatchannellistener.func_176037_b(p_175986_2_);
    }

    protected void func_175985_a(ChatState p_175985_1_) {
        if (p_175985_1_ != this.field_153011_i) {
            this.field_153011_i = p_175985_1_;
            try {
                if (this.field_153003_a != null) {
                    this.field_153003_a.func_176017_a(p_175985_1_);
                }
            }
            catch (Exception exception) {
                this.func_152995_h(exception.toString());
            }
        }
    }

    protected void func_153001_r() {
        ErrorCode errorcode;
        if (this.field_175995_l != EnumEmoticonMode.None && this.field_175996_m == null && ErrorCode.failed((ErrorCode)(errorcode = this.field_153008_f.downloadEmoticonData()))) {
            String s = ErrorCode.getString((ErrorCode)errorcode);
            this.func_152995_h(String.format("Error trying to download emoticon data: %s", s));
        }
    }

    protected void func_152988_s() {
        if (this.field_175996_m == null) {
            this.field_175996_m = new ChatEmoticonData();
            ErrorCode errorcode = this.field_153008_f.getEmoticonData(this.field_175996_m);
            if (ErrorCode.succeeded((ErrorCode)errorcode)) {
                try {
                    if (this.field_153003_a != null) {
                        this.field_153003_a.func_176021_d();
                    }
                }
                catch (Exception exception) {
                    this.func_152995_h(exception.toString());
                }
            } else {
                this.func_152995_h("Error preparing emoticon data: " + ErrorCode.getString((ErrorCode)errorcode));
            }
        }
    }

    protected void func_152996_t() {
        if (this.field_175996_m != null) {
            ErrorCode errorcode = this.field_153008_f.clearEmoticonData();
            if (ErrorCode.succeeded((ErrorCode)errorcode)) {
                this.field_175996_m = null;
                try {
                    if (this.field_153003_a != null) {
                        this.field_153003_a.func_176024_e();
                    }
                }
                catch (Exception exception) {
                    this.func_152995_h(exception.toString());
                }
            } else {
                this.func_152995_h("Error clearing emoticon data: " + ErrorCode.getString((ErrorCode)errorcode));
            }
        }
    }

    protected void func_152995_h(String p_152995_1_) {
        LOGGER.error(TwitchStream.STREAM_MARKER, "[Chat controller] {}", new Object[]{p_152995_1_});
    }

    public static enum EnumEmoticonMode {
        None,
        Url,
        TextureAtlas;

    }

    public static enum EnumChannelState {
        Created,
        Connecting,
        Connected,
        Disconnecting,
        Disconnected;

    }

    public static enum ChatState {
        Uninitialized,
        Initializing,
        Initialized,
        ShuttingDown;

    }

    public static interface ChatListener {
        public void func_176023_d(ErrorCode var1);

        public void func_176022_e(ErrorCode var1);

        public void func_176021_d();

        public void func_176024_e();

        public void func_176017_a(ChatState var1);

        public void func_176025_a(String var1, ChatTokenizedMessage[] var2);

        public void func_180605_a(String var1, ChatRawMessage[] var2);

        public void func_176018_a(String var1, ChatUserInfo[] var2, ChatUserInfo[] var3, ChatUserInfo[] var4);

        public void func_180606_a(String var1);

        public void func_180607_b(String var1);

        public void func_176019_a(String var1, String var2);

        public void func_176016_c(String var1);

        public void func_176020_d(String var1);
    }

    public class ChatChannelListener
    implements IChatChannelListener {
        protected String field_176048_a = null;
        protected boolean field_176046_b = false;
        protected EnumChannelState field_176047_c = EnumChannelState.Created;
        protected List<ChatUserInfo> field_176044_d = Lists.newArrayList();
        protected LinkedList<ChatRawMessage> field_176045_e = new LinkedList();
        protected LinkedList<ChatTokenizedMessage> field_176042_f = new LinkedList();
        protected ChatBadgeData field_176043_g = null;

        public ChatChannelListener(String p_i46061_2_) {
            this.field_176048_a = p_i46061_2_;
        }

        public EnumChannelState func_176040_a() {
            return this.field_176047_c;
        }

        public boolean func_176038_a(boolean p_176038_1_) {
            this.field_176046_b = p_176038_1_;
            ErrorCode errorcode = ErrorCode.TTV_EC_SUCCESS;
            errorcode = p_176038_1_ ? ChatController.this.field_153008_f.connectAnonymous(this.field_176048_a, (IChatChannelListener)this) : ChatController.this.field_153008_f.connect(this.field_176048_a, ChatController.this.field_153004_b, ChatController.this.field_153012_j.data, (IChatChannelListener)this);
            if (ErrorCode.failed((ErrorCode)errorcode)) {
                String s = ErrorCode.getString((ErrorCode)errorcode);
                ChatController.this.func_152995_h(String.format("Error connecting: %s", s));
                this.func_176036_d(this.field_176048_a);
                return false;
            }
            this.func_176035_a(EnumChannelState.Connecting);
            this.func_176041_h();
            return true;
        }

        public boolean func_176034_g() {
            switch (this.field_176047_c) {
                case Connected: 
                case Connecting: {
                    ErrorCode errorcode = ChatController.this.field_153008_f.disconnect(this.field_176048_a);
                    if (ErrorCode.failed((ErrorCode)errorcode)) {
                        String s = ErrorCode.getString((ErrorCode)errorcode);
                        ChatController.this.func_152995_h(String.format("Error disconnecting: %s", s));
                        return false;
                    }
                    this.func_176035_a(EnumChannelState.Disconnecting);
                    return true;
                }
            }
            return false;
        }

        protected void func_176035_a(EnumChannelState p_176035_1_) {
            if (p_176035_1_ != this.field_176047_c) {
                this.field_176047_c = p_176035_1_;
            }
        }

        public void func_176032_a(String p_176032_1_) {
            if (ChatController.this.field_175995_l == EnumEmoticonMode.None) {
                this.field_176045_e.clear();
                this.field_176042_f.clear();
            } else {
                if (this.field_176045_e.size() > 0) {
                    ListIterator listiterator = this.field_176045_e.listIterator();
                    while (listiterator.hasNext()) {
                        ChatRawMessage chatrawmessage = (ChatRawMessage)listiterator.next();
                        if (!chatrawmessage.userName.equals(p_176032_1_)) continue;
                        listiterator.remove();
                    }
                }
                if (this.field_176042_f.size() > 0) {
                    ListIterator listiterator1 = this.field_176042_f.listIterator();
                    while (listiterator1.hasNext()) {
                        ChatTokenizedMessage chattokenizedmessage = (ChatTokenizedMessage)listiterator1.next();
                        if (!chattokenizedmessage.displayName.equals(p_176032_1_)) continue;
                        listiterator1.remove();
                    }
                }
            }
            try {
                if (ChatController.this.field_153003_a != null) {
                    ChatController.this.field_153003_a.func_176019_a(this.field_176048_a, p_176032_1_);
                }
            }
            catch (Exception exception) {
                ChatController.this.func_152995_h(exception.toString());
            }
        }

        public boolean func_176037_b(String p_176037_1_) {
            if (this.field_176047_c != EnumChannelState.Connected) {
                return false;
            }
            ErrorCode errorcode = ChatController.this.field_153008_f.sendMessage(this.field_176048_a, p_176037_1_);
            if (ErrorCode.failed((ErrorCode)errorcode)) {
                String s = ErrorCode.getString((ErrorCode)errorcode);
                ChatController.this.func_152995_h(String.format("Error sending chat message: %s", s));
                return false;
            }
            return true;
        }

        protected void func_176041_h() {
            ErrorCode errorcode;
            if (ChatController.this.field_175995_l != EnumEmoticonMode.None && this.field_176043_g == null && ErrorCode.failed((ErrorCode)(errorcode = ChatController.this.field_153008_f.downloadBadgeData(this.field_176048_a)))) {
                String s = ErrorCode.getString((ErrorCode)errorcode);
                ChatController.this.func_152995_h(String.format("Error trying to download badge data: %s", s));
            }
        }

        protected void func_176039_i() {
            if (this.field_176043_g == null) {
                this.field_176043_g = new ChatBadgeData();
                ErrorCode errorcode = ChatController.this.field_153008_f.getBadgeData(this.field_176048_a, this.field_176043_g);
                if (ErrorCode.succeeded((ErrorCode)errorcode)) {
                    try {
                        if (ChatController.this.field_153003_a != null) {
                            ChatController.this.field_153003_a.func_176016_c(this.field_176048_a);
                        }
                    }
                    catch (Exception exception) {
                        ChatController.this.func_152995_h(exception.toString());
                    }
                } else {
                    ChatController.this.func_152995_h("Error preparing badge data: " + ErrorCode.getString((ErrorCode)errorcode));
                }
            }
        }

        protected void func_176033_j() {
            if (this.field_176043_g != null) {
                ErrorCode errorcode = ChatController.this.field_153008_f.clearBadgeData(this.field_176048_a);
                if (ErrorCode.succeeded((ErrorCode)errorcode)) {
                    this.field_176043_g = null;
                    try {
                        if (ChatController.this.field_153003_a != null) {
                            ChatController.this.field_153003_a.func_176020_d(this.field_176048_a);
                        }
                    }
                    catch (Exception exception) {
                        ChatController.this.func_152995_h(exception.toString());
                    }
                } else {
                    ChatController.this.func_152995_h("Error releasing badge data: " + ErrorCode.getString((ErrorCode)errorcode));
                }
            }
        }

        protected void func_176031_c(String p_176031_1_) {
            try {
                if (ChatController.this.field_153003_a != null) {
                    ChatController.this.field_153003_a.func_180606_a(p_176031_1_);
                }
            }
            catch (Exception exception) {
                ChatController.this.func_152995_h(exception.toString());
            }
        }

        protected void func_176036_d(String p_176036_1_) {
            try {
                if (ChatController.this.field_153003_a != null) {
                    ChatController.this.field_153003_a.func_180607_b(p_176036_1_);
                }
            }
            catch (Exception exception) {
                ChatController.this.func_152995_h(exception.toString());
            }
        }

        private void func_176030_k() {
            if (this.field_176047_c != EnumChannelState.Disconnected) {
                this.func_176035_a(EnumChannelState.Disconnected);
                this.func_176036_d(this.field_176048_a);
                this.func_176033_j();
            }
        }

        public void chatStatusCallback(String p_chatStatusCallback_1_, ErrorCode p_chatStatusCallback_2_) {
            if (!ErrorCode.succeeded((ErrorCode)p_chatStatusCallback_2_)) {
                ChatController.this.field_175998_i.remove(p_chatStatusCallback_1_);
                this.func_176030_k();
            }
        }

        public void chatChannelMembershipCallback(String p_chatChannelMembershipCallback_1_, ChatEvent p_chatChannelMembershipCallback_2_, ChatChannelInfo p_chatChannelMembershipCallback_3_) {
            switch (p_chatChannelMembershipCallback_2_) {
                case TTV_CHAT_JOINED_CHANNEL: {
                    this.func_176035_a(EnumChannelState.Connected);
                    this.func_176031_c(p_chatChannelMembershipCallback_1_);
                    break;
                }
                case TTV_CHAT_LEFT_CHANNEL: {
                    this.func_176030_k();
                }
            }
        }

        public void chatChannelUserChangeCallback(String p_chatChannelUserChangeCallback_1_, ChatUserInfo[] p_chatChannelUserChangeCallback_2_, ChatUserInfo[] p_chatChannelUserChangeCallback_3_, ChatUserInfo[] p_chatChannelUserChangeCallback_4_) {
            for (int i = 0; i < p_chatChannelUserChangeCallback_3_.length; ++i) {
                int j = this.field_176044_d.indexOf(p_chatChannelUserChangeCallback_3_[i]);
                if (j < 0) continue;
                this.field_176044_d.remove(j);
            }
            for (int k = 0; k < p_chatChannelUserChangeCallback_4_.length; ++k) {
                int i1 = this.field_176044_d.indexOf(p_chatChannelUserChangeCallback_4_[k]);
                if (i1 >= 0) {
                    this.field_176044_d.remove(i1);
                }
                this.field_176044_d.add(p_chatChannelUserChangeCallback_4_[k]);
            }
            for (int l = 0; l < p_chatChannelUserChangeCallback_2_.length; ++l) {
                this.field_176044_d.add(p_chatChannelUserChangeCallback_2_[l]);
            }
            try {
                if (ChatController.this.field_153003_a != null) {
                    ChatController.this.field_153003_a.func_176018_a(this.field_176048_a, p_chatChannelUserChangeCallback_2_, p_chatChannelUserChangeCallback_3_, p_chatChannelUserChangeCallback_4_);
                }
            }
            catch (Exception exception) {
                ChatController.this.func_152995_h(exception.toString());
            }
        }

        public void chatChannelRawMessageCallback(String p_chatChannelRawMessageCallback_1_, ChatRawMessage[] p_chatChannelRawMessageCallback_2_) {
            for (int i = 0; i < p_chatChannelRawMessageCallback_2_.length; ++i) {
                this.field_176045_e.addLast(p_chatChannelRawMessageCallback_2_[i]);
            }
            try {
                if (ChatController.this.field_153003_a != null) {
                    ChatController.this.field_153003_a.func_180605_a(this.field_176048_a, p_chatChannelRawMessageCallback_2_);
                }
            }
            catch (Exception exception) {
                ChatController.this.func_152995_h(exception.toString());
            }
            while (this.field_176045_e.size() > ChatController.this.field_153015_m) {
                this.field_176045_e.removeFirst();
            }
        }

        public void chatChannelTokenizedMessageCallback(String p_chatChannelTokenizedMessageCallback_1_, ChatTokenizedMessage[] p_chatChannelTokenizedMessageCallback_2_) {
            for (int i = 0; i < p_chatChannelTokenizedMessageCallback_2_.length; ++i) {
                this.field_176042_f.addLast(p_chatChannelTokenizedMessageCallback_2_[i]);
            }
            try {
                if (ChatController.this.field_153003_a != null) {
                    ChatController.this.field_153003_a.func_176025_a(this.field_176048_a, p_chatChannelTokenizedMessageCallback_2_);
                }
            }
            catch (Exception exception) {
                ChatController.this.func_152995_h(exception.toString());
            }
            while (this.field_176042_f.size() > ChatController.this.field_153015_m) {
                this.field_176042_f.removeFirst();
            }
        }

        public void chatClearCallback(String p_chatClearCallback_1_, String p_chatClearCallback_2_) {
            this.func_176032_a(p_chatClearCallback_2_);
        }

        public void chatBadgeDataDownloadCallback(String p_chatBadgeDataDownloadCallback_1_, ErrorCode p_chatBadgeDataDownloadCallback_2_) {
            if (ErrorCode.succeeded((ErrorCode)p_chatBadgeDataDownloadCallback_2_)) {
                this.func_176039_i();
            }
        }
    }
}

