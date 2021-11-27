package gr.spyrosalertas.usermanagementdemo.service.impl;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gr.spyrosalertas.usermanagementdemo.configuration.UserPrincipal;
import gr.spyrosalertas.usermanagementdemo.dao.UserRepository;
import gr.spyrosalertas.usermanagementdemo.entity.User;
import gr.spyrosalertas.usermanagementdemo.entity.UserRoles;
import gr.spyrosalertas.usermanagementdemo.exception.BadRequestException;
import gr.spyrosalertas.usermanagementdemo.exception.EmailTakenException;
import gr.spyrosalertas.usermanagementdemo.exception.UserNotFoundException;
import gr.spyrosalertas.usermanagementdemo.exception.UsernameTakenException;
import gr.spyrosalertas.usermanagementdemo.service.UserService;

@Service
@Transactional
@Qualifier("userDetailsService")
class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageStorageService imageStorageService;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@Value("${file.supported-img-extensions}")
	private List<String> supportedFileExtensions;

	@Value("${security-constants.account-unlock-time}")
	private long accountUnlockTime;

	// -- Create --

	// Register/Create new user (create)
	@Override
	public void createUser(User newUser) {

		if (newUser.getUsername() == null || newUser.getPassword() == null || newUser.getPassword().trim().length() == 0
				|| newUser.getFirstName() == null || newUser.getLastName() == null || newUser.getEmail() == null
				|| newUser.getEmail().trim().length() == 0)
			throw new BadRequestException();

		newUser.setUsername(newUser.getUsername().trim());
		// Encode password before storing it in database, never store raw passwords/
		// also before encoding the password we could perform any strength validation
		// passwords we may want because after it's hashed we can't validate it in
		// entity
		newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
		newUser.setEnabled(false);
		newUser.setNonLocked(true);
		newUser.setRole(UserRoles.USER.getRole());
		newUser.setFirstName(newUser.getFirstName().trim());
		newUser.setLastName(newUser.getLastName().trim());
		newUser.setEmail(newUser.getEmail().toLowerCase().trim());
		if (newUser.getPhone() != null && newUser.getPhone().trim().length() == 0) {
			newUser.setPhone(null);
		}
		if (newUser.getCountry() != null) {
			if (newUser.getCountry().trim().length() == 0) {
				// We set null value when we receive zero length String so that JPA doesn't
				// perform the validation on the string which will fail. So that we achieve
				// similiar result to how node server app handles this case. Same in all
				// optional fields
				newUser.setCountry(null);
			} else {
				newUser.setCountry(newUser.getCountry().trim());
			}
		}
		if (newUser.getCity() != null) {
			if (newUser.getCity().trim().length() == 0) {
				newUser.setCity(null);
			} else {
				newUser.setCity(newUser.getCity().trim());
			}
		}
		if (newUser.getAddress() != null) {
			if (newUser.getAddress().trim().length() == 0) {
				newUser.setAddress(null);
			} else {
				newUser.setAddress(newUser.getAddress().trim());
			}
		}
		newUser.setJoinDate(Instant.now());
		newUser.setLastLoginAttemptDate(Instant.now());
		newUser.setFailedLoginAttempts(0);

		try {
			userRepository.save(newUser);
		} catch (DataIntegrityViolationException e) {
			// Kind of exception we get when we try to insert existing username/email
			// records in database
			if (e.getMessage().contains("user.username_UNIQUE")) {
				throw new UsernameTakenException(
						"Username " + newUser.getUsername() + " already used by another user.");
			} else if (e.getMessage().contains("user.email_UNIQUE")) {
				throw new EmailTakenException("Email " + newUser.getEmail() + " already used by another user.");
			}
		} catch (ConstraintViolationException e) {
			// Kind of error we get when the data we try to store in database violate our
			// constraints, for example email is not valid email address, number not a
			// number, etc or if any of our custom pattern validations failed
			throw new BadRequestException();
		}

	}

	// -- Read --

	// List all users (read)
	@Override
	public Page<User> findAllUsers(int page, int pageSize, String sort, String order,
			Map<String, String> filterOptions) {

		Pageable pageable;
		if (order.equalsIgnoreCase("DESC")) {
			if (sort.compareTo("username") == 0) {
				pageable = PageRequest.of(page, pageSize, Sort.by(sort).descending());
			} else {
				pageable = PageRequest.of(page, pageSize,
						Sort.by(sort).descending().and(Sort.by("username").ascending()));
			}
		} else {
			pageable = PageRequest.of(page, pageSize, Sort.by(sort, "username").ascending());
		}
		Page<User> results = null;
		if (filterOptions == null) {
			results = userRepository.findAll(pageable);
		} else {
			// Parse search/filter options
			String username = filterOptions.get("username") != null ? filterOptions.get("username") : "";
			Boolean isEnabled = null;
			Boolean isNonLocked = null;
			String role = "%";
			Instant startDate = null;
			Instant endDate = null;

			String isEnabledAsString = filterOptions.get("isEnabled");
			if (isEnabledAsString != null) {
				if (isEnabledAsString != null && isEnabledAsString.compareTo("true") == 0
						|| isEnabledAsString.compareTo("false") == 0) {
					isEnabled = Boolean.parseBoolean(isEnabledAsString);
				}
			}

			String isNonLockedAsString = filterOptions.get("isNonLocked");
			if (isNonLockedAsString != null) {
				if (isNonLockedAsString.compareTo("true") == 0 || isNonLockedAsString.compareTo("false") == 0) {
					isNonLocked = Boolean.parseBoolean(isNonLockedAsString);
				}
			}

			if (filterOptions.get("role") != null) {
				if (UserRoles.ADMIN.getRole().compareTo(filterOptions.get("role").toUpperCase()) == 0
						|| UserRoles.USER.getRole().compareTo(filterOptions.get("role").toUpperCase()) == 0) {
					role = filterOptions.get("role").toUpperCase();
				}
			}

			try {
				startDate = new SimpleDateFormat("yyyy-MM-dd").parse(filterOptions.get("startDate")).toInstant();
			} catch (Exception e) {
				// Nothing to do - there was no start date
			}

			if (startDate != null) {
				try {
					// End date will be at the end of the day
					endDate = new SimpleDateFormat("yyyy-MM-dd").parse(filterOptions.get("endDate")).toInstant().plus(1,
							ChronoUnit.DAYS);
					// If there is end date
				} catch (Exception e) {
					// If there was no end date - add 1 day to start date
					endDate = startDate.plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.MILLIS);
				}
			}

			results = userRepository.findAllFiltered(username, isEnabled, isNonLocked, role, startDate, endDate,
					pageable);

		}

		results.forEach(user -> {
			long timePassed = Instant.now().toEpochMilli() - user.getLastLoginAttemptDate().toEpochMilli();
			if (!user.isNonLocked() && timePassed > accountUnlockTime)
				// If accounts locked cooldown period has passed - show it's unlocked
				user.setNonLocked(true);
		});

		return results;

	}

	// Get user by username (read)
	@Override
	public User findUserByUsername(String username) {
		User user = userRepository.findUserByUsername(username);
		if (user == null)
			throw new UserNotFoundException("No User with username " + username + " was found");
		else {
			long timePassed = Instant.now().toEpochMilli() - user.getLastLoginAttemptDate().toEpochMilli();
			if (!user.isNonLocked() && timePassed > accountUnlockTime)
				// If accounts locked cooldown period has passed - show it's unlocked
				user.setNonLocked(true);
			return user;
		}
	}

	// -- Update --

	// Update user (update)
	@Override
	public User updateUser(User updatedUser, String username) {

		User user = userRepository.findUserByUsername(username);
		if (user == null) {
			// User not found - can't update - this may happen only if a user has a valid
			// token, deletes his account and then tries to update his profile with the
			// token he had before account deletion
			throw new UserNotFoundException("No User with username '" + username + "' was found to update");
		}

		if (updatedUser.getPassword() != null) {
			// If password is not null - update password
			user.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
		}

		if (updatedUser.getFirstName() != null) {
			// If first name is not null - update first name
			user.setFirstName(updatedUser.getFirstName());
		}

		if (updatedUser.getLastName() != null) {
			// If last name is not null - update last name
			user.setLastName(updatedUser.getLastName());
		}

		if (updatedUser.getEmail() != null) {
			// Check if email has already been used
			User tmpUser = userRepository.findUserByEmail(updatedUser.getEmail());
			if (tmpUser != null && tmpUser.getUsername().compareTo(username) != 0) {
				// If updated email is used by someone else, stop updating user details and send
				// error response
				throw new EmailTakenException("Email " + updatedUser.getEmail() + " already used by another user.");
			}
			// If email isn't used by someone else, update it
			user.setEmail(updatedUser.getEmail());
		}

		// the value of the below fields become null if user sent no value
		user.setPhone(updatedUser.getPhone());
		user.setCountry(updatedUser.getCountry());
		user.setCity(updatedUser.getCity());
		user.setAddress(updatedUser.getAddress());

		// Update user in database and get updated user values from database to respond
		// to the server (just to make sure everything is really updated as we wanted in
		// the database)
		user = userRepository.save(user);

		// Return updated user
		return user;

	}

	// Activate users account (update)
	@Override
	public void enableUser(String username) {
		int rows = userRepository.enableUserByUsername(username);
		if (rows == 0)
			throw new UserNotFoundException("No User with username '" + username + "' was found to activate");
	}

	// -- Delete --

	// Delete user (delete)
	@Override
	public void deleteUserByUsername(String username) {
		int rows = userRepository.deleteUserByUsername(username);
		if (rows == 0)
			throw new UserNotFoundException("No User with username '" + username + "' was found to delete");
		else
			// If there was a user with that username, delete his profile image as well (if
			// he has one without throwing errors if there is no image)
			imageStorageService.deleteUserProfileImageIfExists(username);
	}

	// -- Handle profile image actions (download/upload/delete) --

	// Get/Download profile image (download)
	@Override
	public Resource downloadUserProfileImage(String username) {
		// Try to get users profile image
		return imageStorageService.downloadUserProfileImage(username);
	}

	// Update/Upload users profile image (upload)
	@Override
	public void uploadUserProfileImage(MultipartFile file, String username) {
		// Try to update users profile image
		imageStorageService.uploadUserProfileImage(file, username);
	}

	// Delete users profile image (delete)
	@Override
	public void deleteUserProfileImage(String username) {
		// Try to delete users profile image
		imageStorageService.deleteUserProfileImage(username);
	}

	// -- Other methods

	// Authentication Login Success/Failure listeners

	// Authentication success listener
	@EventListener
	void onAuthenticationSuccess(AuthenticationSuccessEvent event) {

		// In case of successfull login - perform successfull login logic

		// Get user from UserPrincipal Object
		User user = ((UserPrincipal) event.getAuthentication().getPrincipal()).getUser();
		user.setLastLoginDate(Instant.now());
		user.setFailedLoginAttempts(0);
		user.setNonLocked(true);

		// Update user in database
		userRepository.save(user);

	}

	// Authentication failure listener
	@EventListener
	void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
		if (event.getException() instanceof UsernameNotFoundException) {
			// Throw a generic exception message, we don't want users to know whether
			// it was the username or the password that was wrong
			throw new BadCredentialsException("Bad Credentials");
		} else if (event.getException() instanceof BadCredentialsException) {
			// If there is a user with that username and password was wrong - perform failed
			// login attempt logic
			String username = event.getAuthentication().getPrincipal().toString();
			String password = event.getAuthentication().getCredentials().toString();
			boolean waslocked = failedLoginAttempt(username, password);
			if (waslocked) {
				// If account got locked after this failed login attempt
				throw new LockedException("Your account is locked");
			}
			throw new BadCredentialsException("Bad Credentials");
		} else if (event.getException() instanceof LockedException) {
			// If users account has already been locked due to many failed log in attempts
			String username = event.getAuthentication().getPrincipal().toString();
			String password = event.getAuthentication().getCredentials().toString();
			failedLoginAttempt(username, password);
			// If credentials were correct but account was locked and cooldown/lock period
			// hasn't passed yet
			throw new LockedException("Your account is locked");
			// When account is locked - we don't always throw LockedException
			// If locked period has passed, we unlock his account and thus
			// user gets successfully loged in
		} else if (event.getException() instanceof DisabledException) {
			throw new DisabledException("Your account is not enabled");
		}

	}

	// -- Private Helper Methods

	@Value("${security-constants.failed-attempts-period}")
	private long failedAttemptsPeriod;

	@Value("${security-constants.failed-attempts-allowed}")
	private long failedAttemptsAllowed;

	// Returns true if account got locked or false if account was already locked or
	// not locked yet. In other words only when account lock status changes to
	// locked
	private boolean failedLoginAttempt(String username, String password) {

		boolean gotLocked = false;

		User user = userRepository.findUserByUsername(username);
		if (user == null) // Normally we would never get a null user here
			return gotLocked;

		// Time that has passed since the last failed login attempt
		long timePassed = Instant.now().toEpochMilli() - user.getLastLoginAttemptDate().toEpochMilli();

		// Account unlocked - wrong password -> password variable is null
		// Account locked - we don't know if password is correct or wrong so we check it
		// here -> password variable not null
		if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
			// If password was wrong
			if (timePassed < failedAttemptsPeriod) {
				user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
				if (user.getFailedLoginAttempts() >= this.failedAttemptsAllowed) {
					// If allowed failed login attempts in a short period have been exceeded, lock
					// users account
					user.setNonLocked(false);
					gotLocked = true;
				}
			} else {
				// Reset failed login attempts - time period has passed
				user.setFailedLoginAttempts(1);
				user.setNonLocked(true);
			}
			user.setLastLoginAttemptDate(Instant.now());
		}

		userRepository.save(user);

		return gotLocked;

	}

}
