package gr.spyrosalertas.usermanagementdemo.exception;

public class PageNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6084728617286895151L;

	public PageNotFoundException() {
		super();
	}

	public PageNotFoundException(String message) {
		super(message);
	}

}
