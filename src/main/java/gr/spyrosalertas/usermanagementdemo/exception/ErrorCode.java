package gr.spyrosalertas.usermanagementdemo.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	// Generic errors
	UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "UNKNOWN_ERROR", "Unknown Internal Server Error"),
	PAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "PAGE_NOT_FOUND", "Page Not Found"),
	BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST", "Bad request"),
	METHOD_NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED.value(), "METHOD_NOT_IMPLEMENTED", "Method not implemented"),
	// Authentication/authorization errors
	UNAUTHENTICATED(HttpStatus.UNAUTHORIZED.value(), "NOT_AUTHENTICATED", "You have to log in first"),
	UNAUTHORIZED(HttpStatus.FORBIDDEN.value(), "NOT_AUTHORIZED", "You don't have permission for this action"),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "EXPIRED_TOKEN", "You have to log in again"),
	INVALID_TOKEN(HttpStatus.BAD_REQUEST.value(), "INVALID_TOKEN", "You have to log in first"),
	ACCOUNT_NOT_ENABLED(HttpStatus.FORBIDDEN.value(), "ACCOUNT_NOT_ENABLED",
			"Your account is pending activation by admins. Try again later"),
	ACCOUNT_LOCKED(HttpStatus.FORBIDDEN.value(), "ACCOUNT_LOCKED",
			"Your account has been locked due to many failed login attempts. Try again later"),
	BAD_CREDENTIALS(HttpStatus.FORBIDDEN.value(), "BAD_CREDENTIALS",
			"User credentials are wrong (username or password)"),
	// Profile image upload errors
	FILE_SIZE_LIMIT(400, "FILE_SIZE_LIMIT", "Max file size limit exceeded"),
	FILE_FORMAT_NOT_SUPPORTED(400, "FILE_FORMAT_NOT_SUPPORTED", "Uploaded file format is not supported"),
	NO_FILE(400, "NO_FILE", "No file was sent to the server"),
	PROFILE_IMAGE_NOT_FOUND(404, "PROFILE_IMAGE_NOT_FOUND", "Profile image not found"),
	// User controller related errors
	USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "USER_NOT_FOUND", "No user was found with that username"),
	USERNAME_TAKEN(HttpStatus.CONFLICT.value(), "USERNAME_TAKEN", "Username is already used by another user"),
	EMAIL_TAKEN(HttpStatus.CONFLICT.value(), "EMAIL_TAKEN", "Email is already used by another user");

	private final int status;
	private final String code;
	private final String message;

	private ErrorCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "{ \"" + status + "\", " + code + "\", \"" + message + "\" }";
	}

}
