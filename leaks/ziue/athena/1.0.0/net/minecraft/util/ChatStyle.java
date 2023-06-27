package net.minecraft.util;

import net.minecraft.event.*;
import java.lang.reflect.*;
import com.google.gson.*;

public class ChatStyle
{
    private ChatStyle parentStyle;
    private EnumChatFormatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private ClickEvent chatClickEvent;
    private HoverEvent chatHoverEvent;
    private String insertion;
    private static final ChatStyle rootStyle;
    
    public EnumChatFormatting getColor() {
        return (this.color == null) ? this.getParent().getColor() : this.color;
    }
    
    public boolean getBold() {
        return (this.bold == null) ? this.getParent().getBold() : this.bold;
    }
    
    public boolean getItalic() {
        return (this.italic == null) ? this.getParent().getItalic() : this.italic;
    }
    
    public boolean getStrikethrough() {
        return (this.strikethrough == null) ? this.getParent().getStrikethrough() : this.strikethrough;
    }
    
    public boolean getUnderlined() {
        return (this.underlined == null) ? this.getParent().getUnderlined() : this.underlined;
    }
    
    public boolean getObfuscated() {
        return (this.obfuscated == null) ? this.getParent().getObfuscated() : this.obfuscated;
    }
    
    public boolean isEmpty() {
        return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.chatClickEvent == null && this.chatHoverEvent == null;
    }
    
    public ClickEvent getChatClickEvent() {
        return (this.chatClickEvent == null) ? this.getParent().getChatClickEvent() : this.chatClickEvent;
    }
    
    public HoverEvent getChatHoverEvent() {
        return (this.chatHoverEvent == null) ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
    }
    
    public String getInsertion() {
        return (this.insertion == null) ? this.getParent().getInsertion() : this.insertion;
    }
    
    public ChatStyle setColor(final EnumChatFormatting color) {
        this.color = color;
        return this;
    }
    
    public ChatStyle setBold(final Boolean boldIn) {
        this.bold = boldIn;
        return this;
    }
    
    public ChatStyle setItalic(final Boolean italic) {
        this.italic = italic;
        return this;
    }
    
    public ChatStyle setStrikethrough(final Boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }
    
    public ChatStyle setUnderlined(final Boolean underlined) {
        this.underlined = underlined;
        return this;
    }
    
    public ChatStyle setObfuscated(final Boolean obfuscated) {
        this.obfuscated = obfuscated;
        return this;
    }
    
    public ChatStyle setChatClickEvent(final ClickEvent event) {
        this.chatClickEvent = event;
        return this;
    }
    
    public ChatStyle setChatHoverEvent(final HoverEvent event) {
        this.chatHoverEvent = event;
        return this;
    }
    
    public ChatStyle setInsertion(final String insertion) {
        this.insertion = insertion;
        return this;
    }
    
    public ChatStyle setParentStyle(final ChatStyle parent) {
        this.parentStyle = parent;
        return this;
    }
    
    public String getFormattingCode() {
        if (this.isEmpty()) {
            return (this.parentStyle != null) ? this.parentStyle.getFormattingCode() : "";
        }
        final StringBuilder stringbuilder = new StringBuilder();
        if (this.getColor() != null) {
            stringbuilder.append(this.getColor());
        }
        if (this.getBold()) {
            stringbuilder.append(EnumChatFormatting.BOLD);
        }
        if (this.getItalic()) {
            stringbuilder.append(EnumChatFormatting.ITALIC);
        }
        if (this.getUnderlined()) {
            stringbuilder.append(EnumChatFormatting.UNDERLINE);
        }
        if (this.getObfuscated()) {
            stringbuilder.append(EnumChatFormatting.OBFUSCATED);
        }
        if (this.getStrikethrough()) {
            stringbuilder.append(EnumChatFormatting.STRIKETHROUGH);
        }
        return stringbuilder.toString();
    }
    
    private ChatStyle getParent() {
        return (this.parentStyle == null) ? ChatStyle.rootStyle : this.parentStyle;
    }
    
    @Override
    public String toString() {
        return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getChatClickEvent() + ", hoverEvent=" + this.getChatHoverEvent() + ", insertion=" + this.getInsertion() + '}';
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof ChatStyle)) {
            return false;
        }
        final ChatStyle chatstyle = (ChatStyle)p_equals_1_;
        if (this.getBold() == chatstyle.getBold() && this.getColor() == chatstyle.getColor() && this.getItalic() == chatstyle.getItalic() && this.getObfuscated() == chatstyle.getObfuscated() && this.getStrikethrough() == chatstyle.getStrikethrough() && this.getUnderlined() == chatstyle.getUnderlined()) {
            if (this.getChatClickEvent() != null) {
                if (!this.getChatClickEvent().equals(chatstyle.getChatClickEvent())) {
                    return false;
                }
            }
            else if (chatstyle.getChatClickEvent() != null) {
                return false;
            }
            if (this.getChatHoverEvent() != null) {
                if (!this.getChatHoverEvent().equals(chatstyle.getChatHoverEvent())) {
                    return false;
                }
            }
            else if (chatstyle.getChatHoverEvent() != null) {
                return false;
            }
            if (this.getInsertion() != null) {
                if (!this.getInsertion().equals(chatstyle.getInsertion())) {
                    return false;
                }
            }
            else if (chatstyle.getInsertion() != null) {
                return false;
            }
            final boolean flag = true;
            return flag;
        }
        final boolean flag = false;
        return flag;
    }
    
    @Override
    public int hashCode() {
        int i = this.color.hashCode();
        i = 31 * i + this.bold.hashCode();
        i = 31 * i + this.italic.hashCode();
        i = 31 * i + this.underlined.hashCode();
        i = 31 * i + this.strikethrough.hashCode();
        i = 31 * i + this.obfuscated.hashCode();
        i = 31 * i + this.chatClickEvent.hashCode();
        i = 31 * i + this.chatHoverEvent.hashCode();
        i = 31 * i + this.insertion.hashCode();
        return i;
    }
    
    public ChatStyle createShallowCopy() {
        final ChatStyle chatstyle = new ChatStyle();
        chatstyle.bold = this.bold;
        chatstyle.italic = this.italic;
        chatstyle.strikethrough = this.strikethrough;
        chatstyle.underlined = this.underlined;
        chatstyle.obfuscated = this.obfuscated;
        chatstyle.color = this.color;
        chatstyle.chatClickEvent = this.chatClickEvent;
        chatstyle.chatHoverEvent = this.chatHoverEvent;
        chatstyle.parentStyle = this.parentStyle;
        chatstyle.insertion = this.insertion;
        return chatstyle;
    }
    
    public ChatStyle createDeepCopy() {
        final ChatStyle chatstyle = new ChatStyle();
        chatstyle.setBold(this.getBold());
        chatstyle.setItalic(this.getItalic());
        chatstyle.setStrikethrough(this.getStrikethrough());
        chatstyle.setUnderlined(this.getUnderlined());
        chatstyle.setObfuscated(this.getObfuscated());
        chatstyle.setColor(this.getColor());
        chatstyle.setChatClickEvent(this.getChatClickEvent());
        chatstyle.setChatHoverEvent(this.getChatHoverEvent());
        chatstyle.setInsertion(this.getInsertion());
        return chatstyle;
    }
    
    static {
        rootStyle = new ChatStyle() {
            @Override
            public EnumChatFormatting getColor() {
                return null;
            }
            
            @Override
            public boolean getBold() {
                return false;
            }
            
            @Override
            public boolean getItalic() {
                return false;
            }
            
            @Override
            public boolean getStrikethrough() {
                return false;
            }
            
            @Override
            public boolean getUnderlined() {
                return false;
            }
            
            @Override
            public boolean getObfuscated() {
                return false;
            }
            
            @Override
            public ClickEvent getChatClickEvent() {
                return null;
            }
            
            @Override
            public HoverEvent getChatHoverEvent() {
                return null;
            }
            
            @Override
            public String getInsertion() {
                return null;
            }
            
            @Override
            public ChatStyle setColor(final EnumChatFormatting color) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public ChatStyle setBold(final Boolean boldIn) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public ChatStyle setItalic(final Boolean italic) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public ChatStyle setStrikethrough(final Boolean strikethrough) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public ChatStyle setUnderlined(final Boolean underlined) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public ChatStyle setObfuscated(final Boolean obfuscated) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public ChatStyle setChatClickEvent(final ClickEvent event) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public ChatStyle setChatHoverEvent(final HoverEvent event) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public ChatStyle setParentStyle(final ChatStyle parent) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public String toString() {
                return "Style.ROOT";
            }
            
            @Override
            public ChatStyle createShallowCopy() {
                return this;
            }
            
            @Override
            public ChatStyle createDeepCopy() {
                return this;
            }
            
            @Override
            public String getFormattingCode() {
                return "";
            }
        };
    }
    
    public static class Serializer implements JsonDeserializer<ChatStyle>, JsonSerializer<ChatStyle>
    {
        public ChatStyle deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (!p_deserialize_1_.isJsonObject()) {
                return null;
            }
            final ChatStyle chatstyle = new ChatStyle();
            final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            if (jsonobject == null) {
                return null;
            }
            if (jsonobject.has("bold")) {
                chatstyle.bold = jsonobject.get("bold").getAsBoolean();
            }
            if (jsonobject.has("italic")) {
                chatstyle.italic = jsonobject.get("italic").getAsBoolean();
            }
            if (jsonobject.has("underlined")) {
                chatstyle.underlined = jsonobject.get("underlined").getAsBoolean();
            }
            if (jsonobject.has("strikethrough")) {
                chatstyle.strikethrough = jsonobject.get("strikethrough").getAsBoolean();
            }
            if (jsonobject.has("obfuscated")) {
                chatstyle.obfuscated = jsonobject.get("obfuscated").getAsBoolean();
            }
            if (jsonobject.has("color")) {
                chatstyle.color = (EnumChatFormatting)p_deserialize_3_.deserialize(jsonobject.get("color"), (Type)EnumChatFormatting.class);
            }
            if (jsonobject.has("insertion")) {
                chatstyle.insertion = jsonobject.get("insertion").getAsString();
            }
            if (jsonobject.has("clickEvent")) {
                final JsonObject jsonobject2 = jsonobject.getAsJsonObject("clickEvent");
                if (jsonobject2 != null) {
                    final JsonPrimitive jsonprimitive = jsonobject2.getAsJsonPrimitive("action");
                    final ClickEvent.Action clickevent$action = (jsonprimitive == null) ? null : ClickEvent.Action.getValueByCanonicalName(jsonprimitive.getAsString());
                    final JsonPrimitive jsonprimitive2 = jsonobject2.getAsJsonPrimitive("value");
                    final String s = (jsonprimitive2 == null) ? null : jsonprimitive2.getAsString();
                    if (clickevent$action != null && s != null && clickevent$action.shouldAllowInChat()) {
                        chatstyle.chatClickEvent = new ClickEvent(clickevent$action, s);
                    }
                }
            }
            if (jsonobject.has("hoverEvent")) {
                final JsonObject jsonobject3 = jsonobject.getAsJsonObject("hoverEvent");
                if (jsonobject3 != null) {
                    final JsonPrimitive jsonprimitive3 = jsonobject3.getAsJsonPrimitive("action");
                    final HoverEvent.Action hoverevent$action = (jsonprimitive3 == null) ? null : HoverEvent.Action.getValueByCanonicalName(jsonprimitive3.getAsString());
                    final IChatComponent ichatcomponent = (IChatComponent)p_deserialize_3_.deserialize(jsonobject3.get("value"), (Type)IChatComponent.class);
                    if (hoverevent$action != null && ichatcomponent != null && hoverevent$action.shouldAllowInChat()) {
                        chatstyle.chatHoverEvent = new HoverEvent(hoverevent$action, ichatcomponent);
                    }
                }
            }
            return chatstyle;
        }
        
        public JsonElement serialize(final ChatStyle p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
            if (p_serialize_1_.isEmpty()) {
                return null;
            }
            final JsonObject jsonobject = new JsonObject();
            if (p_serialize_1_.bold != null) {
                jsonobject.addProperty("bold", p_serialize_1_.bold);
            }
            if (p_serialize_1_.italic != null) {
                jsonobject.addProperty("italic", p_serialize_1_.italic);
            }
            if (p_serialize_1_.underlined != null) {
                jsonobject.addProperty("underlined", p_serialize_1_.underlined);
            }
            if (p_serialize_1_.strikethrough != null) {
                jsonobject.addProperty("strikethrough", p_serialize_1_.strikethrough);
            }
            if (p_serialize_1_.obfuscated != null) {
                jsonobject.addProperty("obfuscated", p_serialize_1_.obfuscated);
            }
            if (p_serialize_1_.color != null) {
                jsonobject.add("color", p_serialize_3_.serialize((Object)p_serialize_1_.color));
            }
            if (p_serialize_1_.insertion != null) {
                jsonobject.add("insertion", p_serialize_3_.serialize((Object)p_serialize_1_.insertion));
            }
            if (p_serialize_1_.chatClickEvent != null) {
                final JsonObject jsonobject2 = new JsonObject();
                jsonobject2.addProperty("action", p_serialize_1_.chatClickEvent.getAction().getCanonicalName());
                jsonobject2.addProperty("value", p_serialize_1_.chatClickEvent.getValue());
                jsonobject.add("clickEvent", (JsonElement)jsonobject2);
            }
            if (p_serialize_1_.chatHoverEvent != null) {
                final JsonObject jsonobject3 = new JsonObject();
                jsonobject3.addProperty("action", p_serialize_1_.chatHoverEvent.getAction().getCanonicalName());
                jsonobject3.add("value", p_serialize_3_.serialize((Object)p_serialize_1_.chatHoverEvent.getValue()));
                jsonobject.add("hoverEvent", (JsonElement)jsonobject3);
            }
            return (JsonElement)jsonobject;
        }
    }
}
