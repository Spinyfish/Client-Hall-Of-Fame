package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent {
   protected final List<IChatComponent> siblings = Lists.newArrayList();
   private ChatStyle style;

   @Override
   public IChatComponent appendSibling(IChatComponent component) {
      component.getChatStyle().setParentStyle(this.getChatStyle());
      this.siblings.add(component);
      return this;
   }

   @Override
   public List<IChatComponent> getSiblings() {
      return this.siblings;
   }

   @Override
   public IChatComponent appendText(String text) {
      return this.appendSibling(new ChatComponentText(text));
   }

   @Override
   public void setChatStyle(ChatStyle style) {
      this.style = style;

      for(IChatComponent ichatcomponent : this.siblings) {
         ichatcomponent.getChatStyle().setParentStyle(this.getChatStyle());
      }
   }

   @Override
   public ChatStyle getChatStyle() {
      if (this.style == null) {
         this.style = new ChatStyle();

         for(IChatComponent ichatcomponent : this.siblings) {
            ichatcomponent.getChatStyle().setParentStyle(this.style);
         }
      }

      return this.style;
   }

   @Override
   public Iterator<IChatComponent> iterator() {
      return Iterators.concat(Iterators.forArray(new ChatComponentStyle[]{this}), createDeepCopyIterator(this.siblings));
   }

   @Override
   public final String getUnformattedText() {
      StringBuilder stringbuilder = new StringBuilder();

      for(IChatComponent ichatcomponent : this) {
         stringbuilder.append(ichatcomponent.getUnformattedTextForChat());
      }

      return stringbuilder.toString();
   }

   @Override
   public final String getFormattedText() {
      StringBuilder stringbuilder = new StringBuilder();

      for(IChatComponent ichatcomponent : this) {
         stringbuilder.append(ichatcomponent.getChatStyle().getFormattingCode());
         stringbuilder.append(ichatcomponent.getUnformattedTextForChat());
         stringbuilder.append(EnumChatFormatting.RESET);
      }

      return stringbuilder.toString();
   }

   public static Iterator<IChatComponent> createDeepCopyIterator(Iterable<IChatComponent> components) {
      Iterator<IChatComponent> iterator = Iterators.concat(Iterators.transform(components.iterator(), Iterable::iterator));
      return Iterators.transform(iterator, p_apply_1_ -> {
         IChatComponent ichatcomponent = p_apply_1_.createCopy();
         ichatcomponent.setChatStyle(ichatcomponent.getChatStyle().createDeepCopy());
         return ichatcomponent;
      });
   }

   @Override
   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ChatComponentStyle)) {
         return false;
      } else {
         ChatComponentStyle chatcomponentstyle = (ChatComponentStyle)p_equals_1_;
         return this.siblings.equals(chatcomponentstyle.siblings) && this.getChatStyle().equals(chatcomponentstyle.getChatStyle());
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.style.hashCode() + this.siblings.hashCode();
   }

   @Override
   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }
}
