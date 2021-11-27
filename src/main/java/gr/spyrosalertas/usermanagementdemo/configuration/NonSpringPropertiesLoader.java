package gr.spyrosalertas.usermanagementdemo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Helper class to load values from property file for classes not managed by Spring Boot Container
@Component
public class NonSpringPropertiesLoader {

	private static long accountUnlockTime;

	NonSpringPropertiesLoader(@Value("${security-constants.account-unlock-time}") long accountUnlockTimeValue) {
		accountUnlockTime = accountUnlockTimeValue;
	}

	public static long getAccountUnlockTime() {
		return accountUnlockTime;
	}

}
