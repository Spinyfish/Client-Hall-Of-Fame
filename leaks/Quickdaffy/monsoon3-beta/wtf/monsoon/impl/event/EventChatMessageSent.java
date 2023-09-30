/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.event;

import lombok.NonNull;
import wtf.monsoon.api.event.Event;

public class EventChatMessageSent
extends Event {
    @NonNull
    private String content;

    public EventChatMessageSent(@NonNull String content) {
        if (content == null) {
            throw new NullPointerException("content is marked non-null but is null");
        }
        this.content = content;
    }

    @NonNull
    public String getContent() {
        return this.content;
    }

    public void setContent(@NonNull String content) {
        if (content == null) {
            throw new NullPointerException("content is marked non-null but is null");
        }
        this.content = content;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventChatMessageSent)) {
            return false;
        }
        EventChatMessageSent other = (EventChatMessageSent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$content = this.getContent();
        String other$content = other.getContent();
        return !(this$content == null ? other$content != null : !this$content.equals(other$content));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventChatMessageSent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : $content.hashCode());
        return result;
    }

    public String toString() {
        return "EventChatMessageSent(content=" + this.getContent() + ")";
    }
}

