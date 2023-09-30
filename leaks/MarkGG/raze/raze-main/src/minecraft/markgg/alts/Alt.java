package markgg.alts;

public final class Alt {

	private String mask = "";
	private final String username;

	public Alt(String username) {
		this(username,"");
	}

	public Alt(String username, String mask) {
		this.username = username;
		this.mask = mask;
	}

	public String getMask() {
		return mask;
	}

	public String getUsername() {
		return username;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

}
