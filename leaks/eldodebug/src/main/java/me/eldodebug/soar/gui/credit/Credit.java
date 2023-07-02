package me.eldodebug.soar.gui.credit;

public class Credit {

	private String name;
	private String description;
	private String url;
	private String icon;
	
	public Credit(String name, String description, String url, String icon) {
		this.name = name;
		this.description = description;
		this.url = url;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public String getIcon() {
		return icon;
	}
}
