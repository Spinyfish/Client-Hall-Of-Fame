package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.player.ServerUtil;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.value.impl.StringValue;
import util.time.StopWatch;

@Rise
@ModuleInfo(name = "module.other.spammer.name", description = "module.other.spammer.description", category = Category.OTHER)
public final class  Spammer extends Module {

    private final StringValue message = new StringValue("Message", this, "Buy Rise at riseclient.com!");
    private final NumberValue delay = new NumberValue("Delay", this, 3000, 0, 20000, 1);

    private final StopWatch stopWatch = new StopWatch();

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (ServerUtil.isOnServer("loyisa.cn")) {
            ChatUtil.display("Upon a request from Loyisa we have blacklisted Loyisa's Test Server from Spammer.");
            this.toggle();
            return;
        }

        if (this.stopWatch.finished(delay.getValue().longValue())) {
            mc.thePlayer.sendChatMessage(message.getValue());
            this.stopWatch.reset();
        }
    };
}
