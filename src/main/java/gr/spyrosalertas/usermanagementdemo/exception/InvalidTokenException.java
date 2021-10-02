package gr.spyrosalertas.usermanagementdemo.exception;

public class InvalidTokenException extends RuntimeException {

	private static final long serialVersionUID = -1375298959072867582L;

	public InvalidTokenException() {
		super();
	}

	public InvalidTokenException(String message) {
		super(message);
	}

}
