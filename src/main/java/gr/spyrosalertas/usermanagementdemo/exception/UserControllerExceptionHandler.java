package gr.spyrosalertas.usermanagementdemo.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import gr.spyrosalertas.usermanagementdemo.controller.UserController;

// UserController Exception Handler that handles all possible server exceptions and returns human friendly errors to client
@RestControllerAdvice(assignableTypes = { UserController.class })
@Order(1)
public class UserControllerExceptionHandler {

	// AccountLocked Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleAccountLockedException(LockedException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.ACCOUNT_LOCKED,
				ErrorCode.ACCOUNT_LOCKED.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// AccountNotEnabled Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleAccountNotEnabledException(DisabledException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.ACCOUNT_NOT_ENABLED,
				ErrorCode.ACCOUNT_NOT_ENABLED.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// BadCredentials Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.BAD_CREDENTIALS,
				ErrorCode.BAD_CREDENTIALS.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// Duplicate Username Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleUsernameTakenException(UsernameTakenException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.USERNAME_TAKEN,
				e.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// Duplicate Email Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleEmailTakenException(EmailTakenException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.EMAIL_TAKEN, e.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// UserNotFound Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.USER_NOT_FOUND,
				e.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// FileFormatNotSupported Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleFileFormatNotSupportedException(FileFormatNotSupportedException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.FILE_FORMAT_NOT_SUPPORTED,
				e.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// MethodArgumentNotValidException Exception Handler - When database constraints
	// are not satisfied
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.BAD_REQUEST,
				ErrorCode.BAD_REQUEST.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// ProfileImageNotFound Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleProfileImageNotFoundException(ProfileImageNotFoundException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.PROFILE_IMAGE_NOT_FOUND,
				e.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

}
