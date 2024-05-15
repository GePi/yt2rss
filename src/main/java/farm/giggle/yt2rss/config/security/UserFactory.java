package farm.giggle.yt2rss.config.security;

import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.serv.UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

public abstract class UserFactory {
    protected final UserService userService;
    protected Auth2ProviderEnum auth2Provider;

    static public UserFactoryOAuth2 create(OAuth2UserRequest userRequest, OAuth2User oAuth2User, UserService userService) {
        return new UserFactoryOAuth2(userRequest, oAuth2User, userService);
    }

    static public UserFactoryOidc create(OidcUserRequest userRequest, OidcUser oidcUser, UserService userService) {
        return new UserFactoryOidc(userRequest, oidcUser, userService);
    }

    public UserFactory(UserService userService) {
        this.userService = userService;
    }

    public abstract OAuth2User createAuth2User();

    protected User getDBUser(String userId) {
        return userService.findUserByOAuth2Id(auth2Provider, userId);
    }

    protected User createDBUser(String userName, String userId) {
        return userService.createOrdinaryUser(userName, auth2Provider, userId, UUID.randomUUID());
    }
}


