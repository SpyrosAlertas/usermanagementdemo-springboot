package gr.spyrosalertas.usermanagementdemo.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.auth0.jwt.exceptions.TokenExpiredException;

import gr.spyrosalertas.usermanagementdemo.exception.ErrorCode;
import gr.spyrosalertas.usermanagementdemo.exception.ErrorResponse;
import gr.spyrosalertas.usermanagementdemo.exception.InvalidTokenException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GenericExceptionHandlerFilter extends GenericFilterBean {

	@Value("${response-content-type}")
	private String responseContentType;

	// Some exceptions can't be caught by RestControllerAdvice - so we have to
	// handle them here and respond to the server
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (RequestRejectedException ex) {
			// 404 Error - Rest end point doesn't exist if we get RequestRejectedException
			// (for example link method)
			ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.PAGE_NOT_FOUND,
					ErrorCode.PAGE_NOT_FOUND.getMessage());
			response.setContentType(responseContentType);
			((HttpServletResponse) response).setStatus(errorResponse.getStatus());
			response.getWriter().write(errorResponse.toString());
		} catch (TokenExpiredException e) {
			// TokenExpired Exception
			ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.EXPIRED_TOKEN,
					ErrorCode.EXPIRED_TOKEN.getMessage());
			response.setContentType(responseContentType);
			((HttpServletResponse) response).setStatus(errorResponse.getStatus());
			response.getWriter().write(errorResponse.toString());
		} catch (InvalidTokenException e) {
			// InvalidToken Exception
			ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.INVALID_TOKEN,
					ErrorCode.INVALID_TOKEN.getMessage());
			response.setContentType(responseContentType);
			((HttpServletResponse) response).setStatus(errorResponse.getStatus());
			response.getWriter().write(errorResponse.toString());
		} catch (Exception e) {
			// In case we have missed some kind of exception that might occur here
			e.printStackTrace();
			ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.UNKNOWN_ERROR,
					ErrorCode.UNKNOWN_ERROR.getMessage());
			response.setContentType(responseContentType);
			((HttpServletResponse) response).setStatus(errorResponse.getStatus());
			response.getWriter().write(errorResponse.toString());
		}

	}

}
