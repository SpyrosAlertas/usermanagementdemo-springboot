package gr.spyrosalertas.usermanagementdemo.dao;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import gr.spyrosalertas.usermanagementdemo.entity.User;

// We could use only one method (findAll and findAllFiltered) but i think it's better for
// performance to have a simpler query when no filter is applied

public interface UserRepository extends JpaRepository<User, Long> {

	// -- Create

	// -- Read

	// List all users (read)
	Page<User> findAll(Pageable pageable);

	// Get user by username (read)
	User findUserByUsername(String username);

	// Get user by email (read)
	User findUserByEmail(String email);

	// Search/Filter user results
	@Query(value = "SELECT u FROM User u WHERE " + "u.username LIKE %:username% "
			+ "AND (:isEnabled IS NULL OR u.isEnabled = :isEnabled) "
			+ "AND (:isNonLocked IS NULL OR u.isNonLocked = :isNonLocked) " + "AND u.role LIKE :role "
			+ "AND ((:startDate IS NULL AND :endDate IS NULL) OR (u.joinDate Between :startDate AND :endDate))")
	Page<User> findAllFiltered(String username, Boolean isEnabled, Boolean isNonLocked, String role, Instant startDate,
			Instant endDate, Pageable pageable);

	// -- Update

	// Activate users account (update)
	@Modifying
	@Query("UPDATE User u SET u.isEnabled = true WHERE u.username = ?1")
	int enableUserByUsername(String username);

	// -- Delete

	// Delete user (delete)
	int deleteUserByUsername(String username);

}
