package net.java.games.input;

import java.io.IOException;

class OSXComponent extends AbstractComponent {
   private final OSXHIDElement element;

   public OSXComponent(Component.Identifier id, OSXHIDElement element) {
      super(id.getName(), id);
      this.element = element;
   }

   public final boolean isRelative() {
      return this.element.isRelative();
   }

   public boolean isAnalog() {
      return this.element.isAnalog();
   }

   public final OSXHIDElement getElement() {
      return this.element;
   }

   protected float poll() throws IOException {
      return OSXControllers.poll(this.element);
   }
}
