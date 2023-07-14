/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.IChatComponent;

public class ChatComponentSelector
extends ChatComponentStyle {
    private final String selector;

    public ChatComponentSelector(String selectorIn) {
        this.selector = selectorIn;
    }

    public String getSelector() {
        return this.selector;
    }

    @Override
    public String getUnformattedTextForChat() {
        return this.selector;
    }

    @Override
    public ChatComponentSelector createCopy() {
        ChatComponentSelector chatcomponentselector = new ChatComponentSelector(this.selector);
        chatcomponentselector.setChatStyle(this.getChatStyle().createShallowCopy());
        for (IChatComponent ichatcomponent : this.getSiblings()) {
            chatcomponentselector.appendSibling(ichatcomponent.createCopy());
        }
        return chatcomponentselector;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof ChatComponentSelector)) {
            return false;
        }
        ChatComponentSelector chatcomponentselector = (ChatComponentSelector)p_equals_1_;
        return this.selector.equals(chatcomponentselector.selector) && super.equals(p_equals_1_);
    }

    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }
}

