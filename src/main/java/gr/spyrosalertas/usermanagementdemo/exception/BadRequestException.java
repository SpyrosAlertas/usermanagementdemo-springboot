package gr.spyrosalertas.usermanagementdemo.exception;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = -4571647533687917038L;

	public BadRequestException() {
		super();
	}

	public BadRequestException(String message) {
		super(message);
	}

}
