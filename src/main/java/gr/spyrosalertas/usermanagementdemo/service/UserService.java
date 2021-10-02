package gr.spyrosalertas.usermanagementdemo.service;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import gr.spyrosalertas.usermanagementdemo.entity.User;

public interface UserService {

	// -- Create --

	// Register/Create new user (create)
	void createUser(User newUser);

	// -- Read --

	// List all users (read)
	Page<User> findAllUsers(int page, int pageSize, String sort, String order, Map<String, String> filterOptions);

	// Get user by username (read)
	User findUserByUsername(String username);

	// -- Update --

	// Update user (update)
	User updateUser(User updatedUser, String username);

	// Activate users account (update)
	void enableUser(String username);

	// -- Delete --

	// Delete user (delete)
	void deleteUserByUsername(String username);

	// -- Handle profile image actions (download/upload/delete) --

	// Get/Download profile image (download)
	Resource downloadUserProfileImage(String username);

	// Update/Upload users profile image (upload)
	void uploadUserProfileImage(MultipartFile file, String username);

	// Delete users profile image (delete)
	void deleteUserProfileImage(String username);

}
