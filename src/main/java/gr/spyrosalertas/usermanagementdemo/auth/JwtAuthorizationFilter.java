package gr.spyrosalertas.usermanagementdemo.auth;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtProvider jwtProvider;

	@Value("${jwt.token-prefix}")
	private String tokenPrefix;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		// Get AutorizationHeader - if there is one
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null || !authorizationHeader.startsWith(tokenPrefix)) {
			// If there was no authorization header or it doesn't start with the proper
			// prefix (Bearer)
			filterChain.doFilter(request, response);
			// We got nothing to do here - no user to set in Springs SecurityContextHolder
			return;
		}

		// Get jwt from authorization header, removing the 'Bearer' starting part as
		// it's not part of the jwt itself
		String token = authorizationHeader.substring(tokenPrefix.length());

		if (!jwtProvider.isTokenValid(token)) {
			// If jwt isn't valid - clear SecurityContextHolder as user is not authenticated
			// (since he doesn't have a valid jwt)
			SecurityContextHolder.clearContext();
		} else {
			// If jwt is valid
			// Get Subject/username of user from jwt
			String username = jwtProvider.getSubject(token);
			// Get authorities/roles of user from jwt
			List<GrantedAuthority> authorities = jwtProvider.getAuthorities(token);
			Authentication authentication = jwtProvider.getAuthentication(username, authorities);
			// Store in Springs SecurityContextHolder the Authentication Object we created
			// from the request and the jwt
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		// Continue to the next filter or proceed to the Controller class that this
		// filter intercepted
		filterChain.doFilter(request, response);

	}

}
