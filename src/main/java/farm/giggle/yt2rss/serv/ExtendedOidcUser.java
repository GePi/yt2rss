package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.model.User;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class ExtendedOidcUser extends DefaultOidcUser implements MixUserManagement {
    User user;

    public ExtendedOidcUser(OidcUserRequest userRequest, OidcUser oidcUser, User user) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken());
        this.user = user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }
}
