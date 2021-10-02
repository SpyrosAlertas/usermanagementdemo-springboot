package gr.spyrosalertas.usermanagementdemo.exception;

public class ProfileImageNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 385037830541467898L;

	public ProfileImageNotFoundException() {
		super();
	}

	public ProfileImageNotFoundException(String message) {
		super(message);
	}

}
