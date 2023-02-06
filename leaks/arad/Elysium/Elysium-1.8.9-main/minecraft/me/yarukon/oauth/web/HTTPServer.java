package me.yarukon.oauth.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {
	private boolean shutdown = false;
	private boolean created = false;
	public String token = "";
	public int port;

	public HTTPServer(int port) {
		this.port = port;
	}

	public void await() {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
			created = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		Socket socket;
		InputStream input;
		OutputStream output;
		while (!shutdown && created) {
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				Request request = new Request(input);
				request.parse();

				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResource();
				
				if(request.getUri() != null && request.getUri().contains("login")) {
					this.token = request.getUri().replace("/login?code=", "");
					System.out.println("Token obtained! Shutting down server...");
					this.shutdown = true;
				}
			} catch (Exception ignored) {}
		}

		if(serverSocket != null) {
			try {
				serverSocket.close();
			} catch (Exception ignored) {}
		}
	}

}
