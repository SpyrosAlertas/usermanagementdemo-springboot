package gr.spyrosalertas.usermanagementdemo.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ErrorResponse {

	private int status;
	private String code;
	private String message;
	private Instant timestamp;

	public ErrorResponse() {
		this.timestamp = Instant.now();
	}

	public ErrorResponse(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
		this.timestamp = Instant.now();
	}

	public static ErrorResponse createErrorResponse(ErrorCode errorCode, String message) {
		// Create an error response for exception handler
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(errorCode.getStatus());
		errorResponse.setCode(errorCode.getCode());
		if (message == null) {
			errorResponse.setMessage(errorCode.getMessage());
		} else {
			errorResponse.setMessage(message);
		}
		return errorResponse;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	@JsonIgnore
	public HttpStatus getHttpStatus() {
		return HttpStatus.valueOf(status);
	}

	@Override
	public String toString() {
		return "{ \"status\": " + status + ", \"code\": \"" + code + "\", \"message\": \"" + message
				+ "\", \"timestamp\": \"" + timestamp + "\" }";
	}

}
