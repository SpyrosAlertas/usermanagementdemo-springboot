package gr.spyrosalertas.usermanagementdemo.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import gr.spyrosalertas.usermanagementdemo.exception.ErrorCode;
import gr.spyrosalertas.usermanagementdemo.exception.ErrorResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Autowired
	private ApplicationContext applicationContext;

	@Value("${response-content-type}")
	private String responseContentType;

	@Override
	public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e)
			throws IOException {

		// First: We check if request rest end point exists, if not we return a 404
		// error
		RequestMappingHandlerMapping endpoints = applicationContext.getBean(RequestMappingHandlerMapping.class);

		try {
			if (endpoints.getHandler(req) == null) {
				// If there is no handler it means we have a 404 error, rest end point doesn't
				// exist
				throw new Exception();
			}
		} catch (Exception exc) {
			// 404 Error - Rest end point doesn't exist
			ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.PAGE_NOT_FOUND,
					ErrorCode.PAGE_NOT_FOUND.getMessage());
			res.setContentType(responseContentType);
			res.setStatus(errorResponse.getStatus());
			res.getWriter().write(errorResponse.toString());
			// Nothing more to do in this method
			return;

		}

		// Second: If the rest end point exists, we check if user is authenticated, if
		// not he gets Unauthenticated error
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			// If user isn't authenticated - return not authenticated error
			ErrorResponse errorResponse = ErrorResponse.createErrorResponse(ErrorCode.UNAUTHENTICATED,
					ErrorCode.UNAUTHENTICATED.getMessage());
			res.setContentType(responseContentType);
			res.setStatus(errorResponse.getStatus());
			res.getWriter().write(errorResponse.toString());
			// Nothing more to do in this method
			return;
		}

		// If user is logged in but he doesn't have permission to reach a rest end point
		// we get an access denied exception which is handled by our Exception Handlers

	}

}
