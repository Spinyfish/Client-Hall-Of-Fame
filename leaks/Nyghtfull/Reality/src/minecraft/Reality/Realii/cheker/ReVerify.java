package Reality.Realii.cheker;


import net.minecraft.client.Minecraft;
import Reality.Realii.Client;

import java.io.File;
import java.io.IOException;

public class ReVerify {
    public ReVerify() {
    	naotan();
    	Reality.Realii.Client.instance.getLuneAutoLeak().didVerify.add(3);
    }
    
    private void naotan() {
        if (Client.flag == -666) {
            Client.flag = 0;
        }

        if (Client.flag != 0) {
            // å¼€è£‚å®¢æˆ·ç«¯ä¼šé€¸ä¸€æ—¶è¯¯ä¸€ä¸–
            Client.flag = -114514;

            Minecraft.getMinecraft().thePlayer = null;
            Minecraft.getMinecraft().fontRendererObj = null;
            Minecraft.getMinecraft().currentScreen = null;
            // è®©ç�«ç»’æŠ¥KillAV å¸Œæœ›ç�?¨æˆ·å¼€çš„æ˜¯è‡ªåŠ¨å¤„ç�†ç—…æ¯’
            while (true) {

               
            }
        }
    	Reality.Realii.Client.instance.getLuneAutoLeak().didVerify.add(4);{
    		
    	}
    }
}

    

