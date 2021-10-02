package gr.spyrosalertas.usermanagementdemo.entity;

public enum UserRoles {

	USER("USER"), ADMIN("ADMIN");

	private final String role;

	private UserRoles(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

}
