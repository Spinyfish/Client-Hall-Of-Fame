/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package crispy.features.script.runtime;

import crispy.Crispy;
import crispy.util.player.SpeedUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;


public class ScriptRuntime {

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }
    public static void onSendChat(String msg) {
        Crispy.addChatMessage(msg);
    }
    public static void setMotion(double speed) {
        SpeedUtils.setMotion(speed);
    }
    public static void hclip(double teleport) {
        SpeedUtils.setPosition(teleport);
    }
    public static double getSpeed() {
        return SpeedUtils.getSpeed();
    }
    public static boolean isOnGround(double distance) { return SpeedUtils.isOnGround(distance); }


}
