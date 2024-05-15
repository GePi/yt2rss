package farm.giggle.yt2rss.config.security;

import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.model.repo.UserRepo;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

public abstract class UserFactory {
    protected final UserRepo userRepo;
    protected Auth2ProviderEnum auth2Provider;

    static public UserFactoryOAuth2 create(OAuth2UserRequest userRequest, OAuth2User oAuth2User, UserRepo userRepo) {
        return new UserFactoryOAuth2(userRequest, oAuth2User, userRepo);
    }

    static public UserFactoryOidc create(OidcUserRequest userRequest, OidcUser oidcUser, UserRepo userRepo) {
        return new UserFactoryOidc(userRequest, oidcUser, userRepo);
    }

    public UserFactory(UserRepo userRepo) {

        this.userRepo = userRepo;

    }

    public abstract OAuth2User createAuth2User();

    protected User getDBUser(String userId) {
        return userRepo.findUserByAuth2ProviderAndAuth2Id(auth2Provider, userId);
    }

    protected User createDBUser(String userName, String userId) {
        return userRepo.save(new User(userName, auth2Provider, userId, UUID.randomUUID()));
    }

}


