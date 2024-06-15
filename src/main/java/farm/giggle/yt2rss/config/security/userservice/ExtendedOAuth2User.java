package farm.giggle.yt2rss.config.security.userservice;

import farm.giggle.yt2rss.model.Role;
import farm.giggle.yt2rss.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ExtendedOAuth2User extends DefaultOAuth2User implements MixUserManagement {
    private User user;
    protected Set<GrantedAuthority> authorities = new HashSet<>();

    public ExtendedOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User, User user) {
        super(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
        this.user = user;
        this.authorities.addAll(user.getUserRoles().stream().map(userRole -> getAuthority(userRole.getRole())).toList());
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
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
