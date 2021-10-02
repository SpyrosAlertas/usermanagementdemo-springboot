package gr.spyrosalertas.usermanagementdemo.seeder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import gr.spyrosalertas.usermanagementdemo.dao.UserRepository;
import gr.spyrosalertas.usermanagementdemo.entity.User;
import gr.spyrosalertas.usermanagementdemo.entity.UserRoles;

@Component
public class UsermanagementdemoDBSeeder {

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Value("${seed-db}")
	private boolean seedDB;

	// + 1 admins we always create seperately
	private final int numberOfUsers = 100;

	// always creates a few less entries because of validation/constraint errors

	private final double chanceToBeActive = 0.6;
	private final double chanceToHaveLocationDetails = 0.8;
	private final double changeToHavePhone = 0.8;
	private final double chanceToBeAdmin = 0.1;

	@EventListener
	public void seeder(ContextRefreshedEvent event) {
		if (seedDB) {
			System.out.println("DB Seeding initialized");
			this.usermanagementdemoDBSeeder();
			System.out.println("DB Seeding complete");
		}
	}

	private void usermanagementdemoDBSeeder() {

		List<User> users = new ArrayList<>();
		Faker faker = new Faker();
		Random random = new Random();

		for (int i = 0; i < numberOfUsers; i++) {
			User user = new User();

			user.setLastName(faker.name().lastName());
			user.setFirstName(faker.name().firstName());

			user.setUsername(user.getLastName().toLowerCase());
			user.setPassword(bCryptPasswordEncoder.encode(user.getUsername()));
			user.setEnabled(chanceToBeActive > Math.random());
			user.setNonLocked(true);
			user.setRole(chanceToBeAdmin > Math.random() ? UserRoles.ADMIN.getRole() : UserRoles.USER.getRole());
			user.setEmail(faker.internet().emailAddress(user.getFirstName() + user.getLastName()));
			String phone = Integer.toString(random.nextInt(9));
			while (phone.length() < 8) {
				phone += random.nextInt(9);
			}
			user.setPhone(changeToHavePhone > Math.random() ? "69" + phone : null);
			if (chanceToHaveLocationDetails > Math.random()) {
				user.setCountry(faker.address().country());
				user.setCity(faker.address().city());
				user.setAddress(faker.address().streetAddress());
			}
			user.setJoinDate(Instant.now());
			user.setLastLoginAttemptDate(Instant.now());
			user.setFailedLoginAttempts(0);

			users.add(user);

		}

		// Create standard admin testing account
		{ // Anonymous local block
			User user = new User();
			user.setUsername("admin");
			user.setPassword(bCryptPasswordEncoder.encode(user.getUsername()));
			user.setEnabled(true);
			user.setNonLocked(true);
			user.setRole(UserRoles.ADMIN.getRole());
			user.setFirstName("Admin");
			user.setLastName("Admin");
			user.setEmail("admin@examplemail.com");
			user.setCountry("Greece");
			user.setCity("Athens");
			user.setJoinDate(Instant.now());
			user.setLastLoginAttemptDate(Instant.now());
			user.setFailedLoginAttempts(0);

			// Add last admin user in users array
			users.add(user);
		}

		// Save all generated usrs in our database
		for (User user : users) {
			try {
				userRepository.save(user);
			} catch (ConstraintViolationException | DataIntegrityViolationException e) {
				// Ignore these exceptions - normally we might only get Duplicate username
				// exception
			}
		}

	}

}
