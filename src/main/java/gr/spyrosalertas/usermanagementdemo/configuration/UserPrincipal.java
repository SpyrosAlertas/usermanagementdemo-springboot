package gr.spyrosalertas.usermanagementdemo.configuration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import gr.spyrosalertas.usermanagementdemo.entity.User;

public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = 5866037761901124508L;

	private User user;

	private static final long accountUnlockTime = NonSpringPropertiesLoader.getAccountUnlockTime();

	public UserPrincipal(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(user.getRole()));
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // Not used - always true
	}

	@Override
	public boolean isAccountNonLocked() {
		// Account is considered unlocked when it's non locked or if cooldown/lock
		// period has passed
		long timePassed = Instant.now().toEpochMilli() - user.getLastLoginAttemptDate().toEpochMilli();
		return user.isNonLocked() || timePassed > accountUnlockTime;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // Not used - always true
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}

	public User getUser() {
		return user;
	}

}
