package gr.spyrosalertas.usermanagementdemo.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

import gr.spyrosalertas.usermanagementdemo.auth.JwtProvider;
import gr.spyrosalertas.usermanagementdemo.configuration.UserPrincipal;
import gr.spyrosalertas.usermanagementdemo.entity.User;
import gr.spyrosalertas.usermanagementdemo.entity.UserViews;
import gr.spyrosalertas.usermanagementdemo.exception.BadRequestException;
import gr.spyrosalertas.usermanagementdemo.service.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtProvider jwtProvider;

	@Value("${pagination.page-default-size}")
	private int defaultPageSize;

	@Value("${pagination.default-sort-field}")
	private String defaultSort;

	@Value("${pagination.default-order}")
	private String defaultOrder;

	@Value("${jwt.token-header}")
	private String jwtHeader;

	// Register new user route (create)
	// Anyone can register
	@PostMapping
	@PreAuthorize("permitAll()")
	ResponseEntity<Void> createUser(@RequestBody User user) {
		// Call service method to create new user
		userService.createUser(user);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	// List all users route (read) (supports sorting & filtering of user results)
	// Only admins can get a list of all users
	@GetMapping
	@JsonView(value = UserViews.Brief.class)
	@PreAuthorize("hasRole('ADMIN')")
	ResponseEntity<Page<User>> getUsers(@RequestParam(name = "page") Optional<String> pageReqParam,
			@RequestParam(name = "pagesize") Optional<String> pagesizeReqParam,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String order) {

		// Read request parameters if sent or else use the default values loaded from
		// properties files
		int page = 0;
		int pagesize = defaultPageSize;

		try {
			page = Integer.parseInt(pageReqParam.get());
			if (page < 0)
				page = 0;
		} catch (NumberFormatException | NoSuchElementException e) {
			// We don't really need to do anything here
			// we just don't want this to throw exception anywhere as we use the default
			// values in case of bad values given in query parameters
		}
		try {
			pagesize = Integer.parseInt(pagesizeReqParam.get());
			if (pagesize < 0)
				pagesize = defaultPageSize;
		} catch (NumberFormatException | NoSuchElementException e) {
			// We don't really need to do anything here
			// we just don't want this to throw exception anywhere as we use the default
			// values in case of bad values given in query parameters
		}

		String[] fields = { "username", "isEnabled", "isNonLocked", "role", "joinDate" };
		if (sort == null || !Arrays.stream(fields).anyMatch(sort::equals)) {
			// If no or invalid sorting field was given, use the default one
			sort = defaultSort;
		}

		if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
			// If no order value is given, or it's not valid value (ASC/DESC)
			order = defaultOrder;
		}

		return new ResponseEntity<>(userService.findAllUsers(page, pagesize, sort, order, null), HttpStatus.OK);

	}

	// List all users route (read) (supports sorting & filtering of user results)
	// Only admins can get a list of all users
	@PostMapping("/search")
	@JsonView(value = UserViews.Brief.class)
	@PreAuthorize("hasRole('ADMIN')")
	ResponseEntity<Page<User>> getUsersFiltered(@RequestParam(name = "page") Optional<String> pageReqParam,
			@RequestParam(name = "pagesize") Optional<String> pagesizeReqParam,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String order,
			@RequestBody Optional<Map<String, String>> filterOptionsParam) {

		// Read request parameters if sent or else use the default values loaded from
		// properties files
		int page = 0;
		int pagesize = defaultPageSize;

		Map<String, String> filterOptions = null;

		try {
			page = Integer.parseInt(pageReqParam.get());
			if (page < 0)
				page = 0;
		} catch (NumberFormatException | NoSuchElementException e) {
			// We don't really need to do anything here
			// we just don't want this to throw exception anywhere as we use the default
			// values in case of bad values given in query parameters
		}
		try {
			pagesize = Integer.parseInt(pagesizeReqParam.get());
			if (pagesize < 0)
				pagesize = defaultPageSize;
		} catch (NumberFormatException | NoSuchElementException e) {
			// We don't really need to do anything here
			// we just don't want this to throw exception anywhere as we use the default
			// values in case of bad values given in query parameters
		}

		String[] fields = { "username", "isEnabled", "isNonLocked", "role", "joinDate" };
		if (sort == null || !Arrays.stream(fields).anyMatch(sort::equals)) {
			// If no or invalid sorting field was given, use the default one
			sort = defaultSort;
		}

		if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
			// If no order value is given, or it's not valid value (ASC/DESC)
			order = defaultOrder;
		}

		try {
			filterOptions = filterOptionsParam.get();
		} catch (NoSuchElementException e) {
			// Nothing to do here - if we receive no filter options - we apply no filter
			// options
		}

		return new ResponseEntity<>(userService.findAllUsers(page, pagesize, sort, order, filterOptions),
				HttpStatus.OK);

	}

	// Login user route (update)
	// Anyone can try to log in
	@PostMapping("/login")
	@JsonView(value = UserViews.Detailed.class)
	@PreAuthorize("permitAll()")
	public ResponseEntity<User> login(@RequestBody User user) {

		if (user.getUsername() == null || user.getUsername().length() == 0 || user.getPassword() == null
				|| user.getPassword().length() == 0) {
			// If user didn't provide username/password
			throw new BadRequestException();
		}

		// Try to authenticate user
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

		// Beyond this point - either an exception has been thrown and code won't reach
		// this point, or user is successfully authenticated

		// User object to return to client as response
		User fullUser = ((UserPrincipal) authentication.getPrincipal()).getUser();

		// Generate jwt and store it in Http Header
		HttpHeaders headers = new HttpHeaders();
		headers.add(jwtHeader, jwtProvider.createToken((UserPrincipal) authentication.getPrincipal()));

		return new ResponseEntity<>(fullUser, headers, HttpStatus.OK);

	}

	// Get user by username route (read)
	// Admins can view any users profile and users can view their own profiles only
	@GetMapping("{username}")
	@JsonView(value = UserViews.Detailed.class)
	@PreAuthorize("hasRole('ADMIN') or @userPermissions.isSelf(#username)")
	ResponseEntity<User> getUser(@PathVariable String username) {
		// If a user with that username was found
		return new ResponseEntity<>(userService.findUserByUsername(username), HttpStatus.OK);
	}

	// Update user (update)
	// A user can update only his own data
	@PutMapping("{username}")
	@PreAuthorize("@userPermissions.isSelf(#username)")
	ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable String username) {
		// Call service method to create new user
		return new ResponseEntity<>(userService.updateUser(user, username), HttpStatus.OK);
	}

	// Delete user (delete)
	// A user can delete his own profile or an admin can delete anyones profile
	@DeleteMapping("{username}")
	@PreAuthorize("hasRole('ADMIN') or @userPermissions.isSelf(#username)")
	ResponseEntity<Void> deleteUser(@PathVariable String username) {
		// Call service method to delete user
		userService.deleteUserByUsername(username);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	// Activate user account (update)
	// Admins can activate user accounts
	@PutMapping("{username}/activate")
	@PreAuthorize("hasRole('ADMIN')")
	ResponseEntity<Void> activateUser(@PathVariable String username) {
		// Call service method to activate/enable users account
		userService.enableUser(username);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	// handle profile image actions (upload/download/delete)

	// Download users profile image
	// Anyone can download anyones profile image (download) as long as they are
	// logged in
	@GetMapping(value = "{username}/profileImage", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<Resource> userProfileImageDownload(@PathVariable String username) {
		Resource resource = userService.downloadUserProfileImage(username);
		if (resource == null) {
			// If there is no profile image
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		// If there was a profile image
		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

	// Update/Upload users profile image (upload)
	// A user can update only his own profile image
	@PutMapping("{username}/profileImage")
	@PreAuthorize("@userPermissions.isSelf(#username)")
	ResponseEntity<Void> userProfileImageUpload(@PathVariable String username,
			@RequestParam(value = "profileImage", required = false) MultipartFile img) {
		userService.uploadUserProfileImage(img, username);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	// Delete users profile image (delete)
	// A user can delete only his own profile image but an admin can delete anyones
	// profile image (in case it's inappropriate)
	@DeleteMapping("{username}/profileImage")
	@PreAuthorize("hasRole('ADMIN') or @userPermissions.isSelf(#username)")
	ResponseEntity<Void> userProfileImageDelete(@PathVariable String username) {
		userService.deleteUserProfileImage(username);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
