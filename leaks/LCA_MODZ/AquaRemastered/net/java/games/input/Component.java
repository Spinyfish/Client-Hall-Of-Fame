package net.java.games.input;

public interface Component {
   Component.Identifier getIdentifier();

   boolean isRelative();

   boolean isAnalog();

   float getDeadZone();

   float getPollData();

   String getName();

   public static class Identifier {
      private final String name;

      protected Identifier(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public static class Axis extends Component.Identifier {
         public static final Component.Identifier.Axis X = new Component.Identifier.Axis("x");
         public static final Component.Identifier.Axis Y = new Component.Identifier.Axis("y");
         public static final Component.Identifier.Axis Z = new Component.Identifier.Axis("z");
         public static final Component.Identifier.Axis RX = new Component.Identifier.Axis("rx");
         public static final Component.Identifier.Axis RY = new Component.Identifier.Axis("ry");
         public static final Component.Identifier.Axis RZ = new Component.Identifier.Axis("rz");
         public static final Component.Identifier.Axis SLIDER = new Component.Identifier.Axis("slider");
         public static final Component.Identifier.Axis SLIDER_ACCELERATION = new Component.Identifier.Axis("slider-acceleration");
         public static final Component.Identifier.Axis SLIDER_FORCE = new Component.Identifier.Axis("slider-force");
         public static final Component.Identifier.Axis SLIDER_VELOCITY = new Component.Identifier.Axis("slider-velocity");
         public static final Component.Identifier.Axis X_ACCELERATION = new Component.Identifier.Axis("x-acceleration");
         public static final Component.Identifier.Axis X_FORCE = new Component.Identifier.Axis("x-force");
         public static final Component.Identifier.Axis X_VELOCITY = new Component.Identifier.Axis("x-velocity");
         public static final Component.Identifier.Axis Y_ACCELERATION = new Component.Identifier.Axis("y-acceleration");
         public static final Component.Identifier.Axis Y_FORCE = new Component.Identifier.Axis("y-force");
         public static final Component.Identifier.Axis Y_VELOCITY = new Component.Identifier.Axis("y-velocity");
         public static final Component.Identifier.Axis Z_ACCELERATION = new Component.Identifier.Axis("z-acceleration");
         public static final Component.Identifier.Axis Z_FORCE = new Component.Identifier.Axis("z-force");
         public static final Component.Identifier.Axis Z_VELOCITY = new Component.Identifier.Axis("z-velocity");
         public static final Component.Identifier.Axis RX_ACCELERATION = new Component.Identifier.Axis("rx-acceleration");
         public static final Component.Identifier.Axis RX_FORCE = new Component.Identifier.Axis("rx-force");
         public static final Component.Identifier.Axis RX_VELOCITY = new Component.Identifier.Axis("rx-velocity");
         public static final Component.Identifier.Axis RY_ACCELERATION = new Component.Identifier.Axis("ry-acceleration");
         public static final Component.Identifier.Axis RY_FORCE = new Component.Identifier.Axis("ry-force");
         public static final Component.Identifier.Axis RY_VELOCITY = new Component.Identifier.Axis("ry-velocity");
         public static final Component.Identifier.Axis RZ_ACCELERATION = new Component.Identifier.Axis("rz-acceleration");
         public static final Component.Identifier.Axis RZ_FORCE = new Component.Identifier.Axis("rz-force");
         public static final Component.Identifier.Axis RZ_VELOCITY = new Component.Identifier.Axis("rz-velocity");
         public static final Component.Identifier.Axis POV = new Component.Identifier.Axis("pov");
         public static final Component.Identifier.Axis UNKNOWN = new Component.Identifier.Axis("unknown");

         protected Axis(String name) {
            super(name);
         }
      }

      public static class Button extends Component.Identifier {
         public static final Component.Identifier.Button _0 = new Component.Identifier.Button("0");
         public static final Component.Identifier.Button _1 = new Component.Identifier.Button("1");
         public static final Component.Identifier.Button _2 = new Component.Identifier.Button("2");
         public static final Component.Identifier.Button _3 = new Component.Identifier.Button("3");
         public static final Component.Identifier.Button _4 = new Component.Identifier.Button("4");
         public static final Component.Identifier.Button _5 = new Component.Identifier.Button("5");
         public static final Component.Identifier.Button _6 = new Component.Identifier.Button("6");
         public static final Component.Identifier.Button _7 = new Component.Identifier.Button("7");
         public static final Component.Identifier.Button _8 = new Component.Identifier.Button("8");
         public static final Component.Identifier.Button _9 = new Component.Identifier.Button("9");
         public static final Component.Identifier.Button _10 = new Component.Identifier.Button("10");
         public static final Component.Identifier.Button _11 = new Component.Identifier.Button("11");
         public static final Component.Identifier.Button _12 = new Component.Identifier.Button("12");
         public static final Component.Identifier.Button _13 = new Component.Identifier.Button("13");
         public static final Component.Identifier.Button _14 = new Component.Identifier.Button("14");
         public static final Component.Identifier.Button _15 = new Component.Identifier.Button("15");
         public static final Component.Identifier.Button _16 = new Component.Identifier.Button("16");
         public static final Component.Identifier.Button _17 = new Component.Identifier.Button("17");
         public static final Component.Identifier.Button _18 = new Component.Identifier.Button("18");
         public static final Component.Identifier.Button _19 = new Component.Identifier.Button("19");
         public static final Component.Identifier.Button _20 = new Component.Identifier.Button("20");
         public static final Component.Identifier.Button _21 = new Component.Identifier.Button("21");
         public static final Component.Identifier.Button _22 = new Component.Identifier.Button("22");
         public static final Component.Identifier.Button _23 = new Component.Identifier.Button("23");
         public static final Component.Identifier.Button _24 = new Component.Identifier.Button("24");
         public static final Component.Identifier.Button _25 = new Component.Identifier.Button("25");
         public static final Component.Identifier.Button _26 = new Component.Identifier.Button("26");
         public static final Component.Identifier.Button _27 = new Component.Identifier.Button("27");
         public static final Component.Identifier.Button _28 = new Component.Identifier.Button("28");
         public static final Component.Identifier.Button _29 = new Component.Identifier.Button("29");
         public static final Component.Identifier.Button _30 = new Component.Identifier.Button("30");
         public static final Component.Identifier.Button _31 = new Component.Identifier.Button("31");
         public static final Component.Identifier.Button TRIGGER = new Component.Identifier.Button("Trigger");
         public static final Component.Identifier.Button THUMB = new Component.Identifier.Button("Thumb");
         public static final Component.Identifier.Button THUMB2 = new Component.Identifier.Button("Thumb 2");
         public static final Component.Identifier.Button TOP = new Component.Identifier.Button("Top");
         public static final Component.Identifier.Button TOP2 = new Component.Identifier.Button("Top 2");
         public static final Component.Identifier.Button PINKIE = new Component.Identifier.Button("Pinkie");
         public static final Component.Identifier.Button BASE = new Component.Identifier.Button("Base");
         public static final Component.Identifier.Button BASE2 = new Component.Identifier.Button("Base 2");
         public static final Component.Identifier.Button BASE3 = new Component.Identifier.Button("Base 3");
         public static final Component.Identifier.Button BASE4 = new Component.Identifier.Button("Base 4");
         public static final Component.Identifier.Button BASE5 = new Component.Identifier.Button("Base 5");
         public static final Component.Identifier.Button BASE6 = new Component.Identifier.Button("Base 6");
         public static final Component.Identifier.Button DEAD = new Component.Identifier.Button("Dead");
         public static final Component.Identifier.Button A = new Component.Identifier.Button("A");
         public static final Component.Identifier.Button B = new Component.Identifier.Button("B");
         public static final Component.Identifier.Button C = new Component.Identifier.Button("C");
         public static final Component.Identifier.Button X = new Component.Identifier.Button("X");
         public static final Component.Identifier.Button Y = new Component.Identifier.Button("Y");
         public static final Component.Identifier.Button Z = new Component.Identifier.Button("Z");
         public static final Component.Identifier.Button LEFT_THUMB = new Component.Identifier.Button("Left Thumb");
         public static final Component.Identifier.Button RIGHT_THUMB = new Component.Identifier.Button("Right Thumb");
         public static final Component.Identifier.Button LEFT_THUMB2 = new Component.Identifier.Button("Left Thumb 2");
         public static final Component.Identifier.Button RIGHT_THUMB2 = new Component.Identifier.Button("Right Thumb 2");
         public static final Component.Identifier.Button SELECT = new Component.Identifier.Button("Select");
         public static final Component.Identifier.Button MODE = new Component.Identifier.Button("Mode");
         public static final Component.Identifier.Button LEFT_THUMB3 = new Component.Identifier.Button("Left Thumb 3");
         public static final Component.Identifier.Button RIGHT_THUMB3 = new Component.Identifier.Button("Right Thumb 3");
         public static final Component.Identifier.Button TOOL_PEN = new Component.Identifier.Button("Pen");
         public static final Component.Identifier.Button TOOL_RUBBER = new Component.Identifier.Button("Rubber");
         public static final Component.Identifier.Button TOOL_BRUSH = new Component.Identifier.Button("Brush");
         public static final Component.Identifier.Button TOOL_PENCIL = new Component.Identifier.Button("Pencil");
         public static final Component.Identifier.Button TOOL_AIRBRUSH = new Component.Identifier.Button("Airbrush");
         public static final Component.Identifier.Button TOOL_FINGER = new Component.Identifier.Button("Finger");
         public static final Component.Identifier.Button TOOL_MOUSE = new Component.Identifier.Button("Mouse");
         public static final Component.Identifier.Button TOOL_LENS = new Component.Identifier.Button("Lens");
         public static final Component.Identifier.Button TOUCH = new Component.Identifier.Button("Touch");
         public static final Component.Identifier.Button STYLUS = new Component.Identifier.Button("Stylus");
         public static final Component.Identifier.Button STYLUS2 = new Component.Identifier.Button("Stylus 2");
         public static final Component.Identifier.Button UNKNOWN = new Component.Identifier.Button("Unknown");
         public static final Component.Identifier.Button BACK = new Component.Identifier.Button("Back");
         public static final Component.Identifier.Button EXTRA = new Component.Identifier.Button("Extra");
         public static final Component.Identifier.Button FORWARD = new Component.Identifier.Button("Forward");
         public static final Component.Identifier.Button LEFT = new Component.Identifier.Button("Left");
         public static final Component.Identifier.Button MIDDLE = new Component.Identifier.Button("Middle");
         public static final Component.Identifier.Button RIGHT = new Component.Identifier.Button("Right");
         public static final Component.Identifier.Button SIDE = new Component.Identifier.Button("Side");

         public Button(String name) {
            super(name);
         }
      }

      public static class Key extends Component.Identifier {
         public static final Component.Identifier.Key VOID = new Component.Identifier.Key("Void");
         public static final Component.Identifier.Key ESCAPE = new Component.Identifier.Key("Escape");
         public static final Component.Identifier.Key _1 = new Component.Identifier.Key("1");
         public static final Component.Identifier.Key _2 = new Component.Identifier.Key("2");
         public static final Component.Identifier.Key _3 = new Component.Identifier.Key("3");
         public static final Component.Identifier.Key _4 = new Component.Identifier.Key("4");
         public static final Component.Identifier.Key _5 = new Component.Identifier.Key("5");
         public static final Component.Identifier.Key _6 = new Component.Identifier.Key("6");
         public static final Component.Identifier.Key _7 = new Component.Identifier.Key("7");
         public static final Component.Identifier.Key _8 = new Component.Identifier.Key("8");
         public static final Component.Identifier.Key _9 = new Component.Identifier.Key("9");
         public static final Component.Identifier.Key _0 = new Component.Identifier.Key("0");
         public static final Component.Identifier.Key MINUS = new Component.Identifier.Key("-");
         public static final Component.Identifier.Key EQUALS = new Component.Identifier.Key("=");
         public static final Component.Identifier.Key BACK = new Component.Identifier.Key("Back");
         public static final Component.Identifier.Key TAB = new Component.Identifier.Key("Tab");
         public static final Component.Identifier.Key Q = new Component.Identifier.Key("Q");
         public static final Component.Identifier.Key W = new Component.Identifier.Key("W");
         public static final Component.Identifier.Key E = new Component.Identifier.Key("E");
         public static final Component.Identifier.Key R = new Component.Identifier.Key("R");
         public static final Component.Identifier.Key T = new Component.Identifier.Key("T");
         public static final Component.Identifier.Key Y = new Component.Identifier.Key("Y");
         public static final Component.Identifier.Key U = new Component.Identifier.Key("U");
         public static final Component.Identifier.Key I = new Component.Identifier.Key("I");
         public static final Component.Identifier.Key O = new Component.Identifier.Key("O");
         public static final Component.Identifier.Key P = new Component.Identifier.Key("P");
         public static final Component.Identifier.Key LBRACKET = new Component.Identifier.Key("[");
         public static final Component.Identifier.Key RBRACKET = new Component.Identifier.Key("]");
         public static final Component.Identifier.Key RETURN = new Component.Identifier.Key("Return");
         public static final Component.Identifier.Key LCONTROL = new Component.Identifier.Key("Left Control");
         public static final Component.Identifier.Key A = new Component.Identifier.Key("A");
         public static final Component.Identifier.Key S = new Component.Identifier.Key("S");
         public static final Component.Identifier.Key D = new Component.Identifier.Key("D");
         public static final Component.Identifier.Key F = new Component.Identifier.Key("F");
         public static final Component.Identifier.Key G = new Component.Identifier.Key("G");
         public static final Component.Identifier.Key H = new Component.Identifier.Key("H");
         public static final Component.Identifier.Key J = new Component.Identifier.Key("J");
         public static final Component.Identifier.Key K = new Component.Identifier.Key("K");
         public static final Component.Identifier.Key L = new Component.Identifier.Key("L");
         public static final Component.Identifier.Key SEMICOLON = new Component.Identifier.Key(";");
         public static final Component.Identifier.Key APOSTROPHE = new Component.Identifier.Key("'");
         public static final Component.Identifier.Key GRAVE = new Component.Identifier.Key("~");
         public static final Component.Identifier.Key LSHIFT = new Component.Identifier.Key("Left Shift");
         public static final Component.Identifier.Key BACKSLASH = new Component.Identifier.Key("\\");
         public static final Component.Identifier.Key Z = new Component.Identifier.Key("Z");
         public static final Component.Identifier.Key X = new Component.Identifier.Key("X");
         public static final Component.Identifier.Key C = new Component.Identifier.Key("C");
         public static final Component.Identifier.Key V = new Component.Identifier.Key("V");
         public static final Component.Identifier.Key B = new Component.Identifier.Key("B");
         public static final Component.Identifier.Key N = new Component.Identifier.Key("N");
         public static final Component.Identifier.Key M = new Component.Identifier.Key("M");
         public static final Component.Identifier.Key COMMA = new Component.Identifier.Key(",");
         public static final Component.Identifier.Key PERIOD = new Component.Identifier.Key(".");
         public static final Component.Identifier.Key SLASH = new Component.Identifier.Key("/");
         public static final Component.Identifier.Key RSHIFT = new Component.Identifier.Key("Right Shift");
         public static final Component.Identifier.Key MULTIPLY = new Component.Identifier.Key("Multiply");
         public static final Component.Identifier.Key LALT = new Component.Identifier.Key("Left Alt");
         public static final Component.Identifier.Key SPACE = new Component.Identifier.Key(" ");
         public static final Component.Identifier.Key CAPITAL = new Component.Identifier.Key("Caps Lock");
         public static final Component.Identifier.Key F1 = new Component.Identifier.Key("F1");
         public static final Component.Identifier.Key F2 = new Component.Identifier.Key("F2");
         public static final Component.Identifier.Key F3 = new Component.Identifier.Key("F3");
         public static final Component.Identifier.Key F4 = new Component.Identifier.Key("F4");
         public static final Component.Identifier.Key F5 = new Component.Identifier.Key("F5");
         public static final Component.Identifier.Key F6 = new Component.Identifier.Key("F6");
         public static final Component.Identifier.Key F7 = new Component.Identifier.Key("F7");
         public static final Component.Identifier.Key F8 = new Component.Identifier.Key("F8");
         public static final Component.Identifier.Key F9 = new Component.Identifier.Key("F9");
         public static final Component.Identifier.Key F10 = new Component.Identifier.Key("F10");
         public static final Component.Identifier.Key NUMLOCK = new Component.Identifier.Key("Num Lock");
         public static final Component.Identifier.Key SCROLL = new Component.Identifier.Key("Scroll Lock");
         public static final Component.Identifier.Key NUMPAD7 = new Component.Identifier.Key("Num 7");
         public static final Component.Identifier.Key NUMPAD8 = new Component.Identifier.Key("Num 8");
         public static final Component.Identifier.Key NUMPAD9 = new Component.Identifier.Key("Num 9");
         public static final Component.Identifier.Key SUBTRACT = new Component.Identifier.Key("Num -");
         public static final Component.Identifier.Key NUMPAD4 = new Component.Identifier.Key("Num 4");
         public static final Component.Identifier.Key NUMPAD5 = new Component.Identifier.Key("Num 5");
         public static final Component.Identifier.Key NUMPAD6 = new Component.Identifier.Key("Num 6");
         public static final Component.Identifier.Key ADD = new Component.Identifier.Key("Num +");
         public static final Component.Identifier.Key NUMPAD1 = new Component.Identifier.Key("Num 1");
         public static final Component.Identifier.Key NUMPAD2 = new Component.Identifier.Key("Num 2");
         public static final Component.Identifier.Key NUMPAD3 = new Component.Identifier.Key("Num 3");
         public static final Component.Identifier.Key NUMPAD0 = new Component.Identifier.Key("Num 0");
         public static final Component.Identifier.Key DECIMAL = new Component.Identifier.Key("Num .");
         public static final Component.Identifier.Key F11 = new Component.Identifier.Key("F11");
         public static final Component.Identifier.Key F12 = new Component.Identifier.Key("F12");
         public static final Component.Identifier.Key F13 = new Component.Identifier.Key("F13");
         public static final Component.Identifier.Key F14 = new Component.Identifier.Key("F14");
         public static final Component.Identifier.Key F15 = new Component.Identifier.Key("F15");
         public static final Component.Identifier.Key KANA = new Component.Identifier.Key("Kana");
         public static final Component.Identifier.Key CONVERT = new Component.Identifier.Key("Convert");
         public static final Component.Identifier.Key NOCONVERT = new Component.Identifier.Key("Noconvert");
         public static final Component.Identifier.Key YEN = new Component.Identifier.Key("Yen");
         public static final Component.Identifier.Key NUMPADEQUAL = new Component.Identifier.Key("Num =");
         public static final Component.Identifier.Key CIRCUMFLEX = new Component.Identifier.Key("Circumflex");
         public static final Component.Identifier.Key AT = new Component.Identifier.Key("At");
         public static final Component.Identifier.Key COLON = new Component.Identifier.Key("Colon");
         public static final Component.Identifier.Key UNDERLINE = new Component.Identifier.Key("Underline");
         public static final Component.Identifier.Key KANJI = new Component.Identifier.Key("Kanji");
         public static final Component.Identifier.Key STOP = new Component.Identifier.Key("Stop");
         public static final Component.Identifier.Key AX = new Component.Identifier.Key("Ax");
         public static final Component.Identifier.Key UNLABELED = new Component.Identifier.Key("Unlabeled");
         public static final Component.Identifier.Key NUMPADENTER = new Component.Identifier.Key("Num Enter");
         public static final Component.Identifier.Key RCONTROL = new Component.Identifier.Key("Right Control");
         public static final Component.Identifier.Key NUMPADCOMMA = new Component.Identifier.Key("Num ,");
         public static final Component.Identifier.Key DIVIDE = new Component.Identifier.Key("Num /");
         public static final Component.Identifier.Key SYSRQ = new Component.Identifier.Key("SysRq");
         public static final Component.Identifier.Key RALT = new Component.Identifier.Key("Right Alt");
         public static final Component.Identifier.Key PAUSE = new Component.Identifier.Key("Pause");
         public static final Component.Identifier.Key HOME = new Component.Identifier.Key("Home");
         public static final Component.Identifier.Key UP = new Component.Identifier.Key("Up");
         public static final Component.Identifier.Key PAGEUP = new Component.Identifier.Key("Pg Up");
         public static final Component.Identifier.Key LEFT = new Component.Identifier.Key("Left");
         public static final Component.Identifier.Key RIGHT = new Component.Identifier.Key("Right");
         public static final Component.Identifier.Key END = new Component.Identifier.Key("End");
         public static final Component.Identifier.Key DOWN = new Component.Identifier.Key("Down");
         public static final Component.Identifier.Key PAGEDOWN = new Component.Identifier.Key("Pg Down");
         public static final Component.Identifier.Key INSERT = new Component.Identifier.Key("Insert");
         public static final Component.Identifier.Key DELETE = new Component.Identifier.Key("Delete");
         public static final Component.Identifier.Key LWIN = new Component.Identifier.Key("Left Windows");
         public static final Component.Identifier.Key RWIN = new Component.Identifier.Key("Right Windows");
         public static final Component.Identifier.Key APPS = new Component.Identifier.Key("Apps");
         public static final Component.Identifier.Key POWER = new Component.Identifier.Key("Power");
         public static final Component.Identifier.Key SLEEP = new Component.Identifier.Key("Sleep");
         public static final Component.Identifier.Key UNKNOWN = new Component.Identifier.Key("Unknown");

         protected Key(String name) {
            super(name);
         }
      }
   }

   public static class POV {
      public static final float OFF = 0.0F;
      public static final float CENTER = 0.0F;
      public static final float UP_LEFT = 0.125F;
      public static final float UP = 0.25F;
      public static final float UP_RIGHT = 0.375F;
      public static final float RIGHT = 0.5F;
      public static final float DOWN_RIGHT = 0.625F;
      public static final float DOWN = 0.75F;
      public static final float DOWN_LEFT = 0.875F;
      public static final float LEFT = 1.0F;
   }
}
