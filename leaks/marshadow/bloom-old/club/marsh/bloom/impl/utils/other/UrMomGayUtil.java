package club.marsh.bloom.impl.utils.other;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class UrMomGayUtil {
    public static String r = "r";
    public static String a = "a";
    public static String i = "i";
    public static String n = "n";
    public static String q = "q";
    public static String e = "e";
    public static String x = "x";
    public static String p = "p";
    public static String y = "y";
    public static String z = "z";
    public static void IILIILLLASIOJCNLINULLBBBCCCC() {

    }
    public static void IILIILLLASIOJCNLINULLBBBCCCB() {

    }
    public static boolean ILLIILLLASIOJCNLINULLBBBCCCC() throws IOException {
        //'http://rain.qexp.xyz:1087/checkauth/?key='..getgenv().Key..'&hwid='..hwid..":"..getgenv().DiscordId
        String urlString = "http://"+r+a+i+n+"."+q+e+x+p+"."+x+y+z+":1087/checkauth/?key=" + UserData.INSTANCE.userID + "&hwid=" + HWID.getHwid() + ":" + UserData.INSTANCE.userID;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        String result = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
        //System.out.println(urlString + " " + result);
        if (!result.contains("dawg im a teapot wtf"))
            return false;
        return !result.contains("-");//development switch
    }
    
    public static boolean ILLIILLLASIOJCNLINULLBBBCCCC(String hwid) throws IOException {
        //'http://rain.qexp.xyz:1087/checkauth/?key='..getgenv().Key..'&hwid='..hwid..":"..getgenv().DiscordId
        String urlString = "http://"+r+a+i+n+"."+q+e+x+p+"."+x+y+z+":1087/checkauth/?key=" + UserData.INSTANCE.userID + "&hwid=" + hwid + ":" + UserData.INSTANCE.userID;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        String result = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
        //System.out.println(urlString + " " + result);
        if (!result.contains("dawg im a teapot wtf"))
            return false;
        return !result.contains("-");//development switch
    }
}
