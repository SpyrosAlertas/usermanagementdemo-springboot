package gr.spyrosalertas.usermanagementdemo.exception;

// We use this exception in methods we haven't implemented their logic yet (optional)
public class MethodNotImplementedException extends RuntimeException {

	private static final long serialVersionUID = 2475645290757204716L;

	public MethodNotImplementedException() {
		super();
	}

	public MethodNotImplementedException(String message) {
		super(message);
	}

}
