package gr.spyrosalertas.usermanagementdemo.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

// Global Exception Handler
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

	@Value("${spring.servlet.multipart.max-file-size}")
	private String fileSizeLimit;

	// AccessDenied Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.UNAUTHORIZED,
				ErrorCode.UNAUTHORIZED.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// HttpRequestMethodNotSupported Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.PAGE_NOT_FOUND,
				ErrorCode.PAGE_NOT_FOUND.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// NoHandlerFound Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.PAGE_NOT_FOUND,
				ErrorCode.PAGE_NOT_FOUND.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// BadRequest Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.BAD_REQUEST, e.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// NoFile Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleNoFileException(NoFileException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.NO_FILE, e.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// MaxUploadSizeExceeded Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		String message = "Max file size exceeded the " + fileSizeLimit.toLowerCase() + " limit";
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.FILE_SIZE_LIMIT, message);
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// DataIntegrityViolation Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.BAD_REQUEST,
				ErrorCode.BAD_REQUEST.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// HttpMediaTypeNotSupported Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
			HttpMediaTypeNotSupportedException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.BAD_REQUEST,
				ErrorCode.BAD_REQUEST.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// HttpMessageNotReadable Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.BAD_REQUEST,
				ErrorCode.BAD_REQUEST.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// MethodNotImplemented Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleMethodNotImplementedException(MethodNotImplementedException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.METHOD_NOT_IMPLEMENTED,
				e.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// In production environment we wouldn't return localized message in uncaught
	// exceptions, because we don't want to expose server details to end users

	// Remaining Uncaught Runtime Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleUncaughtRuntimeException(RuntimeException e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.UNKNOWN_ERROR,
				ErrorCode.UNKNOWN_ERROR.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	// Remaining Uncaught Exception Handler
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleUncaughtException(Exception e) {
		ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.UNKNOWN_ERROR,
				ErrorCode.UNKNOWN_ERROR.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

}
