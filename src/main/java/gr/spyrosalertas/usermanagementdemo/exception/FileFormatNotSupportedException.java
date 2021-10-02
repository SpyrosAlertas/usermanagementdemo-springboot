package gr.spyrosalertas.usermanagementdemo.exception;

public class FileFormatNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 2266278851653938230L;

	public FileFormatNotSupportedException() {
		super();
	}

	public FileFormatNotSupportedException(String message) {
		super(message);
	}

}
