package gr.spyrosalertas.usermanagementdemo.exception;

public class UsernameTakenException extends RuntimeException {

	private static final long serialVersionUID = 6231931166913207271L;

	public UsernameTakenException() {
		super();
	}

	public UsernameTakenException(String message) {
		super(message);
	}

}
