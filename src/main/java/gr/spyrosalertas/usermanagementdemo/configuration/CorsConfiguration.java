package gr.spyrosalertas.usermanagementdemo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {

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
				String[] allowedMethods = { "GET", "POST", "PUT", "DELETE", "OPTIONS" };
				// A list of the headers we want to be allowed to the client
				String[] exposedHeaders = { "Authorization" };

				registry.addMapping(mappingPattern).allowedMethods(allowedMethods).allowedOrigins(origins)
						.exposedHeaders(exposedHeaders);

			}
		};
	}

}
