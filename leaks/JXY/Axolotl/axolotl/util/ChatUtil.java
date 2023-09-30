package axolotl.util;

public class ChatUtil {

	public static String random(int size) {
		
		String[] characters = ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890").split("");
		String output = "";
	
		for(int i=0;i<size;i++) {
			output = output + characters[(int) Math.floor(Math.random() * characters.length)];
		}
		
		return output;
		
	}
	
	public static String random(int size, String[] characters) {
		
		String output = "";
	
		for(int i=0;i<size;i++) {
			output = output + characters[(int) Math.floor(Math.random() * characters.length)];
		}
		
		return output;
		
	}
	
}
