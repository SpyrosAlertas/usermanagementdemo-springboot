package gr.spyrosalertas.usermanagementdemo.auth;

import static java.util.Arrays.stream;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.JWTVerifier;

import gr.spyrosalertas.usermanagementdemo.configuration.UserPrincipal;

@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.issuer}")
	private String issuer;

	@Value("${jwt.audience}")
	private String audience;

	@Value("${jwt.expiration-time}")
	private long expirationTime;

	// Create a new Json Web Token for current UserPrincipal
	public String createToken(UserPrincipal userPrincipal) {
		// Get role(s) from the UserPrincipal Object
		String role = getRoleFromUser(userPrincipal);
		// Set the algorithm that will be used for creating the jwts with our servers
		// secret key
		Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
		// Set issuedAt and expiresAt Dates
		Date issuedAt = new Date();
		Date expiresAt = new Date(issuedAt.getTime() + expirationTime);
		// Create the jwt
		String token = "Bearer "
				+ JWT.create().withIssuer(issuer).withAudience(audience).withSubject(userPrincipal.getUsername())
						.withClaim("role", role).withIssuedAt(issuedAt).withExpiresAt(expiresAt).sign(algorithm);
		return token;
	}

	// Check if given token is valid - invalid or expired tokens return false
	boolean isTokenValid(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
		try {
			JWTVerifier jwtVerifier = JWT.require(algorithm).withIssuer(issuer).build();
			jwtVerifier.verify(token);
		} catch (TokenExpiredException e) {
			// If token has expired - consider token invalid
			return false;
		} catch (JWTVerificationException e) {
			// If token is invalid for any other reason (except expired tokens)
			return false;
		}
		// Else jwt is valid
		return true;
	}

	// Get Subject from token
	String getSubject(String token) {
		return JWT.decode(token).getSubject();
	}

	List<GrantedAuthority> getAuthorities(String token) {
		String[] claims = { JWT.decode(token).getClaim("role").asString() };
		return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	Authentication getAuthentication(String username, List<GrantedAuthority> authorities) {
		UsernamePasswordAuthenticationToken usenamerPasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				username, null, authorities);
		usenamerPasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource());
		return usenamerPasswordAuthenticationToken;
	}

	// -- Private Helper Methods

	private String getRoleFromUser(UserPrincipal userPrincipal) {
		return userPrincipal.getAuthorities().iterator().next().getAuthority();
	}

}
