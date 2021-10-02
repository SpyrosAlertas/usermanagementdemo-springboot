package gr.spyrosalertas.usermanagementdemo.exception;

public class NoFileException extends RuntimeException {

	private static final long serialVersionUID = -5283911295661770627L;

	public NoFileException() {
		super();
	}

	public NoFileException(String message) {
		super(message);
	}

}
