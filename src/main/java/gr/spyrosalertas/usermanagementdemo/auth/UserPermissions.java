package gr.spyrosalertas.usermanagementdemo.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userPermissions")
public class UserPermissions {

	// Check if given username (in parameter) is the same as the one in Springs
	// SecurityContextHolder
	public boolean isSelf(String username) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication.getName().compareTo(username) == 0)
			return true;
		else
			return false;

	}

}
