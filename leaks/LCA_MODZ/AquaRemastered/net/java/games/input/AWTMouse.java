package net.java.games.input;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class AWTMouse extends Mouse implements AWTEventListener {
   private static final int EVENT_X = 1;
   private static final int EVENT_Y = 2;
   private static final int EVENT_BUTTON = 4;
   private final List awt_events = new ArrayList();
   private final List processed_awt_events = new ArrayList();
   private int event_state = 1;

   protected AWTMouse() {
      super("AWTMouse", createComponents(), new Controller[0], new Rumbler[0]);
      Toolkit.getDefaultToolkit().addAWTEventListener(this, 131120L);
   }

   private static final Component[] createComponents() {
      return new Component[]{
         new AWTMouse.Axis(Component.Identifier.Axis.X),
         new AWTMouse.Axis(Component.Identifier.Axis.Y),
         new AWTMouse.Axis(Component.Identifier.Axis.Z),
         new AWTMouse.Button(Component.Identifier.Button.LEFT),
         new AWTMouse.Button(Component.Identifier.Button.MIDDLE),
         new AWTMouse.Button(Component.Identifier.Button.RIGHT)
      };
   }

   private final void processButtons(int button_enum, float value) {
      AWTMouse.Button button = this.getButton(button_enum);
      if (button != null) {
         button.setValue(value);
      }
   }

   private final AWTMouse.Button getButton(int button_enum) {
      switch(button_enum) {
         case 0:
         default:
            return null;
         case 1:
            return (AWTMouse.Button)this.getLeft();
         case 2:
            return (AWTMouse.Button)this.getMiddle();
         case 3:
            return (AWTMouse.Button)this.getRight();
      }
   }

   private final void processEvent(AWTEvent event) throws IOException {
      if (event instanceof MouseWheelEvent) {
         MouseWheelEvent mwe = (MouseWheelEvent)event;
         AWTMouse.Axis wheel = (AWTMouse.Axis)this.getWheel();
         wheel.setValue(wheel.poll() + (float)mwe.getWheelRotation());
      } else if (event instanceof MouseEvent) {
         MouseEvent me = (MouseEvent)event;
         AWTMouse.Axis x = (AWTMouse.Axis)this.getX();
         AWTMouse.Axis y = (AWTMouse.Axis)this.getY();
         x.setValue((float)me.getX());
         y.setValue((float)me.getY());
         switch(me.getID()) {
            case 501:
               this.processButtons(me.getButton(), 1.0F);
               break;
            case 502:
               this.processButtons(me.getButton(), 0.0F);
         }
      }
   }

   public final synchronized void pollDevice() throws IOException {
      AWTMouse.Axis wheel = (AWTMouse.Axis)this.getWheel();
      wheel.setValue(0.0F);

      for(int i = 0; i < this.awt_events.size(); ++i) {
         AWTEvent event = (AWTEvent)this.awt_events.get(i);
         this.processEvent(event);
         this.processed_awt_events.add(event);
      }

      this.awt_events.clear();
   }

   protected final synchronized boolean getNextDeviceEvent(Event event) throws IOException {
      while(!this.processed_awt_events.isEmpty()) {
         AWTEvent awt_event = (AWTEvent)this.processed_awt_events.get(0);
         if (awt_event instanceof MouseWheelEvent) {
            MouseWheelEvent awt_wheel_event = (MouseWheelEvent)awt_event;
            long nanos = awt_wheel_event.getWhen() * 1000000L;
            event.set(this.getWheel(), (float)awt_wheel_event.getWheelRotation(), nanos);
            this.processed_awt_events.remove(0);
         } else if (awt_event instanceof MouseEvent) {
            MouseEvent mouse_event = (MouseEvent)awt_event;
            long nanos = mouse_event.getWhen() * 1000000L;
            switch(this.event_state) {
               case 1:
                  this.event_state = 2;
                  event.set(this.getX(), (float)mouse_event.getX(), nanos);
                  return true;
               case 2:
                  this.event_state = 4;
                  event.set(this.getY(), (float)mouse_event.getY(), nanos);
                  return true;
               case 3:
               default:
                  throw new RuntimeException("Unknown event state: " + this.event_state);
               case 4:
                  this.processed_awt_events.remove(0);
                  this.event_state = 1;
                  AWTMouse.Button button = this.getButton(mouse_event.getButton());
                  if (button != null) {
                     switch(mouse_event.getID()) {
                        case 501:
                           event.set(button, 1.0F, nanos);
                           return true;
                        case 502:
                           event.set(button, 0.0F, nanos);
                           return true;
                     }
                  }
            }
         }
      }

      return false;
   }

   public final synchronized void eventDispatched(AWTEvent event) {
      this.awt_events.add(event);
   }

   static final class Axis extends AbstractComponent {
      private float value;

      public Axis(Component.Identifier.Axis axis_id) {
         super(axis_id.getName(), axis_id);
      }

      public final boolean isRelative() {
         return false;
      }

      public final boolean isAnalog() {
         return true;
      }

      protected final void setValue(float value) {
         this.value = value;
      }

      protected final float poll() throws IOException {
         return this.value;
      }
   }

   static final class Button extends AbstractComponent {
      private float value;

      public Button(Component.Identifier.Button button_id) {
         super(button_id.getName(), button_id);
      }

      protected final void setValue(float value) {
         this.value = value;
      }

      protected final float poll() throws IOException {
         return this.value;
      }

      public final boolean isAnalog() {
         return false;
      }

      public final boolean isRelative() {
         return false;
      }
   }
}
