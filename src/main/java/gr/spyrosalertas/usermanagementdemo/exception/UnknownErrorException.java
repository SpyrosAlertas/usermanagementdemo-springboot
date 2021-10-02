package gr.spyrosalertas.usermanagementdemo.exception;

public class UnknownErrorException extends RuntimeException {

	private static final long serialVersionUID = 4661306815684523330L;

	public UnknownErrorException() {
		super();
	}

	public UnknownErrorException(String message) {
		super(message);
	}

}
