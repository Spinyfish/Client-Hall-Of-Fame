package Reality.Realii.mainemneiua1;

import Reality.Realii.Client;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Random;

/**
 * @description å��è¡¥ä¸�
 * @author LuneClientTeam
 */
public class AntiPatch {
    //å��è¡¥ä¸�
    private static String VM = "";
    public static boolean patched = false;

    public AntiPatch() {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        for (String s:arguments){
            if(contains_(s,"Xbootclasspath")){
                patched = true;
            	Client.flag = -new Random().nextInt(555);
            }
        }
        if(!"Xboot".contains("X") || !"Xboo".contains("b") || !"Classpath".contains("h")) {
        	patched = true;
        	Client.flag = -new Random().nextInt(555);
        }

        if(patched) {
           	Client.flag = -new Random().nextInt(555);
        }
        
    	Reality.Realii.Client.instance.getLuneAutoLeak().didVerify.add(2);
    }

    public static boolean contains_(String s,String t){
        char[] array1 = s.toCharArray();
        char[] array2 = t.toCharArray();
        boolean status = false;

        if(array2.length < array1.length) {
            for(int i=0; i<array1.length; i++){
                if (array1[i] == array2[0] && i+array2.length-1<array1.length) {
                    int j = 0;
                    while(j<array2.length)
                    {
                        if (array1[i+j] == array2[j])
                        {
                            j++;
                        }
                        else
                            break;
                    }
                    if (j==array2.length)
                    {
                        status = true;
                        break;
                    }
                }

            }
        }
        return status;
    }
}
