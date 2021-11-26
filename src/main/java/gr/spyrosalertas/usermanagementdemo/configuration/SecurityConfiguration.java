package gr.spyrosalertas.usermanagementdemo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import gr.spyrosalertas.usermanagementdemo.auth.JwtAuthenticationEntryPoint;
import gr.spyrosalertas.usermanagementdemo.auth.JwtAuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtAuthorizationFilter jwtAuthorizationFilter;

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				// Disable Cross-Site Request Forgery
				.csrf().disable()
				// and enable CORS
				.cors().and()
				// Allow Cross Origin Resource Sharing
				// Stateless on the Server Side - No Sessions
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//				.and().authorizeRequests()
				// Public rest end points - anyone can use them
//				.antMatchers(HttpMethod.POST, "/users").permitAll() // Create new user rest api route
//				.antMatchers(HttpMethod.POST, "/users/login").permitAll() // Login user rest api route
				// All other rest end points require users to be authenticated (logged in users)
				// or in other words those who send a valid jwt. Authorization rules are applied
				// in Controller Class(es)
//				.anyRequest().authenticated()
				.and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
				// Run jwtAuthorizationFilter Filter before UsernamePasswordAuthenticationFilter
				.and().addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

		// As is now by default Rest Api Endpoints are public, accessible by anyone
		// without authorization, unless we apply class/method security restrictions.
		// If we want to define here the public rest endpoints and lock the rest Rest
		// Api Endpoints here or using class/method security restrictions we can
		// uncomment the above 4 lines and perform the necessary changes
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

			@Value("${origins}")
			private String[] origins;

			// Enable CORS for desired origins - also we have to expose the Authorization
			// header to the client, else the client won't receive the jwt when he logins
			@Override
			public void addCorsMappings(CorsRegistry registry) {

				// Cors configuration will be applied to every rest end point
				String mappingPattern = "/**";
				// Methods we want to be allowed/exposed for use by our rest api
				String[] allowedMethods = { "GET", "POST", "PUT", "DELETE", "OPTION" };
				// A list of the headers we want to be allowed to the client
				String[] exposedHeaders = { "Authorization" };

				registry.addMapping(mappingPattern).allowedMethods(allowedMethods).allowedOrigins(origins)
						.exposedHeaders(exposedHeaders);

			}
		};
	}

	// Spring Bean that creates the authentication provider
	@Bean
	DaoAuthenticationProvider authProvider() {
		// Spring by default returns a BadCredentials error, thus you can't know if it
		// was the was the username or the password that was wrong. Here we tell Spring
		// Boot to allow UserNotFoundException
		// because when the username exists and the password is wrong we might have to
		// lock users account in case of many failed attempts in a short period
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setHideUserNotFoundExceptions(false); // The reason we needed this
		authProvider.setPasswordEncoder(bCryptPasswordEncoder());
		return authProvider;
	}

	// The Password Encoder our app will use
	@Bean
	PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// The Authentication Manager Bean
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	// This is used so that when we use the hasRole() the ROLE_ prefix is removed
	// (not required)
	@Bean
	GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
	}

}
