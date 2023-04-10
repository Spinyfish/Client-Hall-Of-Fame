/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package crispy.features.script;



import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.render.Event3D;
import crispy.features.event.impl.render.EventRenderGui;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.script.runtime.events.ScriptMotionUpdateEvent;
import crispy.features.script.runtime.events.ScriptPacketEvent;


import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;



public class ScriptModule extends Hack {

    ScriptModule(String name, String desc, Category category) {
        super(name, desc, category);
    }

    private ScriptEngine engine;
    public void setScriptEngine(ScriptEngine scriptEngine) {
        engine = scriptEngine;
    }

    public void onEnable() {
        try {
            ((Invocable) engine).invokeFunction("onEnable");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException ignored) {
        }


    }
    public void onDisable() {
        try {
            ((Invocable) engine).invokeFunction("onDisable");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException ignored) {
        }

    }

    public void onEvent(Event e) {
        if(e instanceof EventRenderGui) {
            try {
                ((Invocable) engine).invokeFunction("onRender2D");
            } catch (ScriptException ec) {
                ec.printStackTrace();
            } catch (NoSuchMethodException ignored) {
            }
        } else if(e instanceof Event3D) {
            try {
                ((Invocable) engine).invokeFunction("onRender3D");
            } catch (ScriptException ec) {
                ec.printStackTrace();
            } catch (NoSuchMethodException ignored) {
            }
        } else if(e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            ScriptMotionUpdateEvent ev = new ScriptMotionUpdateEvent(event.isPre(), event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.ground);
            try {
                ((Invocable) engine).invokeFunction("onMotionUpdate", ev);
            } catch (NoSuchMethodException ignored) {
            } catch (Exception ec) {
                ec.printStackTrace();
            }

            ev.apply(event);

        } else if(e instanceof EventPacket) {
            EventPacket event = (EventPacket) e;
            ScriptPacketEvent ev = new ScriptPacketEvent(event.getPacket(), event.getDirection());
            try {
                ((Invocable) engine).invokeFunction("onPacket", ev);
            } catch (NoSuchMethodException ignored) {
            } catch (Exception ec) {
                ec.printStackTrace();
            }
            
            ev.apply(event);
        }
    }
}
