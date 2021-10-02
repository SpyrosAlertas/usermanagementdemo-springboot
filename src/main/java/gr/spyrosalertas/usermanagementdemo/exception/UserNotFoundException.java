package gr.spyrosalertas.usermanagementdemo.exception;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1591420809260879910L;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}

}
