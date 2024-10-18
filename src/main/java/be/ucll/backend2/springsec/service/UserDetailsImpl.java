package be.ucll.backend2.springsec.service;

import be.ucll.backend2.springsec.entity.Role;
import be.ucll.backend2.springsec.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record UserDetailsImpl(User user) implements UserDetails {
    @Override
    public String getUsername() {
        return user.getEmailAddress();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> authorities;

        if (user.getRole() == Role.Admin) {
            authorities = List.of("ROLE_ADMIN", "ROLE_USER");
        } else {
            authorities = List.of("ROLE_USER");
        }

        return AuthorityUtils.createAuthorityList(authorities);
    }
}
