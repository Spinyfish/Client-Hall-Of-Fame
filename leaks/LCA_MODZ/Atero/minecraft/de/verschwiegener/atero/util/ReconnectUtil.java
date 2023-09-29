package de.verschwiegener.atero.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class ReconnectUtil {
    
    static String xmlbody;
    static String http_data;
    

    public static String sendReconnect() {
	 xmlbody = String.join("\r\n", "<?xml version=\"1.0\" encoding=\"utf-8\"?>",
		    "<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">",
		    "  <s:Body>",
		    "    <u:ForceTermination xmlns:u=\"urn:schemas-upnp-org:service:WANIPConnection:1\" />",
		    "  </s:Body>",
		    "</s:Envelope>"
		    );
	    http_data = String.join("\r\n","POST /igdupnp/control/WANIPConn1 HTTP/1.1",
		        "Host: fritz.box:49000",
		        "SoapAction: urn:schemas-upnp-org:service:WANIPConnection:1#ForceTermination",
		        "Content-Type: text/xml; charset=\"utf-8\"",
		        "Content-Length: " + xmlbody.length(),"", xmlbody);
	
	try {
	    Socket socket = new Socket("192.168.178.1", 49000);
	    BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
	    System.out.println("Send: " + http_data);
	    wr.write(http_data);
	    wr.flush();
	    wr.close();
	    socket.close();
	    return "Reconnecting";
	} catch (Exception e) {
	    e.printStackTrace();
	    return "Failed";
	}
    }
    
    private static void execute() throws IOException {
	URL url = new URL("http://192.168.178.1:49000/igdupnp/control/WANIPConn1");
	URLConnection connection = url.openConnection();
	connection.setDoOutput(true);
        connection.connect();
        PrintWriter writer = new PrintWriter(connection.getOutputStream());
        writer.print(http_data);
        writer.flush();
        System.out.println("writen");
        writer.close();
    }

}
