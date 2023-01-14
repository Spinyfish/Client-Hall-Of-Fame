package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

public class ChatStyle {
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
   private static final ChatStyle rootStyle = new ChatStyle() {
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
      public ChatStyle setColor(EnumChatFormatting color) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setBold(Boolean boldIn) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setItalic(Boolean italic) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setStrikethrough(Boolean strikethrough) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setUnderlined(Boolean underlined) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setObfuscated(Boolean obfuscated) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setChatClickEvent(ClickEvent event) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setChatHoverEvent(HoverEvent event) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setParentStyle(ChatStyle parent) {
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

   public EnumChatFormatting getColor() {
      return this.color == null ? this.getParent().getColor() : this.color;
   }

   public boolean getBold() {
      return this.bold == null ? this.getParent().getBold() : this.bold;
   }

   public boolean getItalic() {
      return this.italic == null ? this.getParent().getItalic() : this.italic;
   }

   public boolean getStrikethrough() {
      return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough;
   }

   public boolean getUnderlined() {
      return this.underlined == null ? this.getParent().getUnderlined() : this.underlined;
   }

   public boolean getObfuscated() {
      return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated;
   }

   public boolean isEmpty() {
      return this.bold == null
         && this.italic == null
         && this.strikethrough == null
         && this.underlined == null
         && this.obfuscated == null
         && this.color == null
         && this.chatClickEvent == null
         && this.chatHoverEvent == null;
   }

   public ClickEvent getChatClickEvent() {
      return this.chatClickEvent == null ? this.getParent().getChatClickEvent() : this.chatClickEvent;
   }

   public HoverEvent getChatHoverEvent() {
      return this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
   }

   public String getInsertion() {
      return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
   }

   public ChatStyle setColor(EnumChatFormatting color) {
      this.color = color;
      return this;
   }

   public void setBold(Boolean boldIn) {
      this.bold = boldIn;
   }

   public void setItalic(Boolean italic) {
      this.italic = italic;
   }

   public void setStrikethrough(Boolean strikethrough) {
      this.strikethrough = strikethrough;
   }

   public void setUnderlined(Boolean underlined) {
      this.underlined = underlined;
   }

   public void setObfuscated(Boolean obfuscated) {
      this.obfuscated = obfuscated;
   }

   public ChatStyle setChatClickEvent(ClickEvent event) {
      this.chatClickEvent = event;
      return this;
   }

   public void setChatHoverEvent(HoverEvent event) {
      this.chatHoverEvent = event;
   }

   public void setInsertion(String insertion) {
      this.insertion = insertion;
   }

   public void setParentStyle(ChatStyle parent) {
      this.parentStyle = parent;
   }

   public String getFormattingCode() {
      if (this.isEmpty()) {
         return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
      } else {
         StringBuilder stringbuilder = new StringBuilder();
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
   }

   private ChatStyle getParent() {
      return this.parentStyle == null ? rootStyle : this.parentStyle;
   }

   @Override
   public String toString() {
      return "Style{hasParent="
         + (this.parentStyle != null)
         + ", color="
         + this.color
         + ", bold="
         + this.bold
         + ", italic="
         + this.italic
         + ", underlined="
         + this.underlined
         + ", obfuscated="
         + this.obfuscated
         + ", clickEvent="
         + this.getChatClickEvent()
         + ", hoverEvent="
         + this.getChatHoverEvent()
         + ", insertion="
         + this.getInsertion()
         + '}';
   }

   @Override
   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ChatStyle)) {
         return false;
      } else {
         ChatStyle chatstyle = (ChatStyle)p_equals_1_;
         if (this.getBold() == chatstyle.getBold()
            && this.getColor() == chatstyle.getColor()
            && this.getItalic() == chatstyle.getItalic()
            && this.getObfuscated() == chatstyle.getObfuscated()
            && this.getStrikethrough() == chatstyle.getStrikethrough()
            && this.getUnderlined() == chatstyle.getUnderlined()
            && (this.getChatClickEvent() != null ? this.getChatClickEvent().equals(chatstyle.getChatClickEvent()) : chatstyle.getChatClickEvent() == null)
            && (this.getChatHoverEvent() != null ? this.getChatHoverEvent().equals(chatstyle.getChatHoverEvent()) : chatstyle.getChatHoverEvent() == null)
            && (this.getInsertion() != null ? this.getInsertion().equals(chatstyle.getInsertion()) : chatstyle.getInsertion() == null)) {
            boolean flag = true;
            return true;
         } else {
            boolean flag = false;
            return false;
         }
      }
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
      return 31 * i + this.insertion.hashCode();
   }

   public ChatStyle createShallowCopy() {
      ChatStyle chatstyle = new ChatStyle();
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
      ChatStyle chatstyle = new ChatStyle();
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

   public static class Serializer implements JsonDeserializer<ChatStyle>, JsonSerializer<ChatStyle> {
      public ChatStyle deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonObject()) {
            ChatStyle chatstyle = new ChatStyle();
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            if (jsonobject == null) {
               return null;
            } else {
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
                  chatstyle.color = (EnumChatFormatting)p_deserialize_3_.deserialize(jsonobject.get("color"), EnumChatFormatting.class);
               }

               if (jsonobject.has("insertion")) {
                  chatstyle.insertion = jsonobject.get("insertion").getAsString();
               }

               if (jsonobject.has("clickEvent")) {
                  JsonObject jsonobject1 = jsonobject.getAsJsonObject("clickEvent");
                  if (jsonobject1 != null) {
                     JsonPrimitive jsonprimitive = jsonobject1.getAsJsonPrimitive("action");
                     ClickEvent.Action clickevent$action = jsonprimitive == null
                        ? null
                        : ClickEvent.Action.getValueByCanonicalName(jsonprimitive.getAsString());
                     JsonPrimitive jsonprimitive1 = jsonobject1.getAsJsonPrimitive("value");
                     String s = jsonprimitive1 == null ? null : jsonprimitive1.getAsString();
                     if (clickevent$action != null && s != null && clickevent$action.shouldAllowInChat()) {
                        chatstyle.chatClickEvent = new ClickEvent(clickevent$action, s);
                     }
                  }
               }

               if (jsonobject.has("hoverEvent")) {
                  JsonObject jsonobject2 = jsonobject.getAsJsonObject("hoverEvent");
                  if (jsonobject2 != null) {
                     JsonPrimitive jsonprimitive2 = jsonobject2.getAsJsonPrimitive("action");
                     HoverEvent.Action hoverevent$action = jsonprimitive2 == null
                        ? null
                        : HoverEvent.Action.getValueByCanonicalName(jsonprimitive2.getAsString());
                     IChatComponent ichatcomponent = (IChatComponent)p_deserialize_3_.deserialize(jsonobject2.get("value"), IChatComponent.class);
                     if (hoverevent$action != null && ichatcomponent != null && hoverevent$action.shouldAllowInChat()) {
                        chatstyle.chatHoverEvent = new HoverEvent(hoverevent$action, ichatcomponent);
                     }
                  }
               }

               return chatstyle;
            }
         } else {
            return null;
         }
      }

      public JsonElement serialize(ChatStyle p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         if (p_serialize_1_.isEmpty()) {
            return null;
         } else {
            JsonObject jsonobject = new JsonObject();
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
               jsonobject.add("color", p_serialize_3_.serialize(p_serialize_1_.color));
            }

            if (p_serialize_1_.insertion != null) {
               jsonobject.add("insertion", p_serialize_3_.serialize(p_serialize_1_.insertion));
            }

            if (p_serialize_1_.chatClickEvent != null) {
               JsonObject jsonobject1 = new JsonObject();
               jsonobject1.addProperty("action", p_serialize_1_.chatClickEvent.getAction().getCanonicalName());
               jsonobject1.addProperty("value", p_serialize_1_.chatClickEvent.getValue());
               jsonobject.add("clickEvent", jsonobject1);
            }

            if (p_serialize_1_.chatHoverEvent != null) {
               JsonObject jsonobject2 = new JsonObject();
               jsonobject2.addProperty("action", p_serialize_1_.chatHoverEvent.getAction().getCanonicalName());
               jsonobject2.add("value", p_serialize_3_.serialize(p_serialize_1_.chatHoverEvent.getValue()));
               jsonobject.add("hoverEvent", jsonobject2);
            }

            return jsonobject;
         }
      }
   }
}
