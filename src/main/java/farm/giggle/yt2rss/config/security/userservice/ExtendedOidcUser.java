package farm.giggle.yt2rss.config.security.userservice;

import farm.giggle.yt2rss.model.Role;
import farm.giggle.yt2rss.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ExtendedOidcUser extends DefaultOidcUser implements MixUserManagement {
    User user;
    OidcUserRequest userRequest;
    protected Set<GrantedAuthority> authorities = new HashSet<>();

    public ExtendedOidcUser(OidcUserRequest userRequest, OidcUser oidcUser, User user) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken());
        this.user = user;
        this.userRequest = userRequest;
        this.authorities.addAll(user.getUserRoles().stream().map(userRole -> getAuthority(userRole.getRole())).toList());
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }

    public GrantedAuthority getAuthority(Role role) {
        return new GrantedAuthority() {
            final String authority = role.getRoleName().getRoleName();

            @Override
            public String getAuthority() {
                return authority;
            }
        };
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.addAll(authorities);
        grantedAuthorities.addAll(super.getAuthorities());
        return grantedAuthorities;
    }
}
