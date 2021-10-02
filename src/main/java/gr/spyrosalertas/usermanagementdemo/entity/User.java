package gr.spyrosalertas.usermanagementdemo.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@JsonIgnoreProperties(value = { "enabled", "nonLocked" })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	@JsonView(UserViews.Hidden.class)
	private long userId;

	@NotNull
	@Pattern(regexp = "^[a-zA-Z0-9,.&\\-\']{2,50}$")
	@Column(name = "username", unique = true)
	@JsonView(UserViews.Brief.class)
	private String username;
	@NotNull
	@Column(name = "password")
	@JsonView(UserViews.Hidden.class)
	private String password;

	@NotNull
	@Column(name = "is_enabled")
	@JsonView(UserViews.Brief.class)
	private boolean isEnabled;
	@NotNull
	@Column(name = "is_non_locked")
	@JsonView(UserViews.Brief.class)
	private boolean isNonLocked;

	@NotNull
	@Pattern(regexp = "^(USER|ADMIN)$")
	@Column(name = "role")
	@JsonView(UserViews.Brief.class)
	private String role; // Available App Roles: ROLE_USER, ROLE_ADMIN.

	@NotNull
	@Pattern(regexp = "^[a-zA-Zα-ωΑ-Ω.,\\-\' ]{2,50}$")
	@Column(name = "first_name")
	@JsonView(UserViews.Detailed.class)
	private String firstName;
	@NotNull
	@Pattern(regexp = "^[a-zA-Zα-ωΑ-Ω.,\\-\' ]{2,50}$")
	@Column(name = "last_name")
	@JsonView(UserViews.Detailed.class)
	private String lastName;

	@NotNull
	@Email
	@Column(name = "email", unique = true)
	@Size(max = 50)
	@JsonView(UserViews.Detailed.class)
	private String email;

	@Pattern(regexp = "^[+ 0-9]{10,15}$")
	@Column(name = "phone", columnDefinition = "char")
	@JsonView(UserViews.Detailed.class)
	private String phone;

	@Pattern(regexp = "^[a-zA-Zα-ωΑ-Ω.,&()\\-\' ]{3,100}$")
	@Column(name = "country")
	@JsonView(UserViews.Detailed.class)
	private String country;
	@Pattern(regexp = "^[a-zA-Zα-ωΑ-Ω.,&()\\-\' ]{3,50}$")
	@Column(name = "city")
	@JsonView(UserViews.Detailed.class)
	private String city;
	@Pattern(regexp = "^[a-zA-Zα-ωΑ-Ω0-9.,&()\\-\' ]{3,50}$")
	@Column(name = "address")
	@JsonView(UserViews.Detailed.class)
	private String address;

	@NotNull
	@Column(name = "join_date")
	@JsonView(UserViews.Brief.class)
	private Instant joinDate;
	@Column(name = "last_login_date")
	@JsonView(UserViews.Detailed.class)
	private Instant lastLoginDate;
	@NotNull
	@Column(name = "last_login_attempt_date")
	@JsonView(UserViews.Hidden.class)
	private Instant lastLoginAttemptDate;

	@NotNull
	@Column(name = "failed_login_attempts")
	@JsonView(UserViews.Hidden.class)
	private int failedLoginAttempts;

	public User() {
	}

	public User(long userId, String username, String password, boolean isEnabled, boolean isNonLocked, String role,
			String firstName, String lastName, String email, String phone, String country, String city, String address,
			Instant joinDate, Instant lastLoginDate, Instant lastLoginAttemptDate, int failedLoginAttempts) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.isEnabled = isEnabled;
		this.isNonLocked = isNonLocked;
		this.role = role;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.country = country;
		this.city = city;
		this.address = address;
		this.joinDate = joinDate;
		this.lastLoginDate = lastLoginDate;
		this.lastLoginAttemptDate = lastLoginAttemptDate;
		this.failedLoginAttempts = failedLoginAttempts;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isNonLocked() {
		return isNonLocked;
	}

	public void setNonLocked(boolean isNonLocked) {
		this.isNonLocked = isNonLocked;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Instant getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Instant joinDate) {
		this.joinDate = joinDate;
	}

	public Instant getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Instant lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Instant getLastLoginAttemptDate() {
		return lastLoginAttemptDate;
	}

	public void setLastLoginAttemptDate(Instant lastLoginAttemptDate) {
		this.lastLoginAttemptDate = lastLoginAttemptDate;
	}

	public int getFailedLoginAttempts() {
		return failedLoginAttempts;
	}

	public void setFailedLoginAttempts(int failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", password=" + password + ", isEnabled="
				+ isEnabled + ", isNonLocked=" + isNonLocked + ", role=" + role + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", phone=" + phone + ", country=" + country
				+ ", city=" + city + ", address=" + address + ", joinDate=" + joinDate + ", lastLoginDate="
				+ lastLoginDate + ", lastLoginAttemptDate=" + lastLoginAttemptDate + ", failedLoginAttempts="
				+ failedLoginAttempts + "]";
	}

}
