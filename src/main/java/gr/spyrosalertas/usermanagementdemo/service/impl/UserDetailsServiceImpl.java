package gr.spyrosalertas.usermanagementdemo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import gr.spyrosalertas.usermanagementdemo.configuration.UserPrincipal;
import gr.spyrosalertas.usermanagementdemo.dao.UserRepository;
import gr.spyrosalertas.usermanagementdemo.entity.User;

@Service
@Qualifier("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findUserByUsername(username);

		if (user == null) {
			// If there is no such user in our database throw error
			throw new UsernameNotFoundException("No user with username " + username + " was found x");
		}

		// Here we only know whether the user exists, we don't know if provided password
		// is correct/wrong

		return new UserPrincipal(user);

	}

}
