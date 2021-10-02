package gr.spyrosalertas.usermanagementdemo.exception;

public class EmailTakenException extends RuntimeException {

	private static final long serialVersionUID = 1943531598001020888L;

	public EmailTakenException() {
		super();
	}

	public EmailTakenException(String message) {
		super(message);
	}

}
