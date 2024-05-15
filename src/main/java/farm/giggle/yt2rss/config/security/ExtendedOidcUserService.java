package farm.giggle.yt2rss.config.security;

import farm.giggle.yt2rss.model.repo.UserRepo;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class ExtendedOidcUserService extends OidcUserService {
    private final UserRepo userRepo;

    public ExtendedOidcUserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        return dbProcess(oidcUser, userRequest);
    }

    private OidcUser dbProcess(OidcUser oidcUser, OidcUserRequest userRequest) {
        UserFactory userFactory = UserFactory.create(userRequest, oidcUser, userRepo);
        return (OidcUser) userFactory.createAuth2User();
    }
}
