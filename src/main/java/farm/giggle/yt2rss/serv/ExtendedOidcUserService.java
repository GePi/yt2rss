package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.model.Auth2Provider;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.repo.UserRepo;
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
        String userId = oidcUser.getName();
        User user = userRepo.findUserByAuth2ProviderAndAndAuth2Id(Auth2Provider.GOOGLE, userId);
        if (user == null) {
            user = new User();
            user.setName(userId);
            user.setAuth2Provider(Auth2Provider.GOOGLE);
            user.setAuth2Id(userId);
            userRepo.save(user);
        }

        return new ExtendedOidcUser(userRequest, oidcUser, user);
    }
}
