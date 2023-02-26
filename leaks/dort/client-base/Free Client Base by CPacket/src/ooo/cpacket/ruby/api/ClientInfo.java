package ooo.cpacket.ruby.api;

public class ClientInfo {
	
	protected String name; 
	protected String build;
	
	public ClientInfo(String name, String build) {
		this.name = name;
		this.build = build;
	}

	public static ClientInfo get() {
		return new ClientInfo("ClientBase", "v1");
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getBuild() {
		return this.build;
	}
	
}
