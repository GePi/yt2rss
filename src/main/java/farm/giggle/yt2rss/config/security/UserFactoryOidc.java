package farm.giggle.yt2rss.config.security;

import farm.giggle.yt2rss.exceptions.Auth2ProviderNotSupportedException;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.model.repo.UserRepo;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class UserFactoryOidc extends UserFactory {
    private final OidcUserRequest userRequest;
    private final OidcUser oidcUser;

    public UserFactoryOidc(OidcUserRequest userRequest, OidcUser oidcUser, UserRepo userRepo) {
        super(userRepo);
        this.oidcUser = oidcUser;
        this.userRequest = userRequest;

        switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "google" -> this.auth2Provider = Auth2ProviderEnum.GOOGLE;
            default ->
                    throw new Auth2ProviderNotSupportedException(userRequest.getClientRegistration().getRegistrationId());
        }
    }

    @Override
    public OAuth2User createAuth2User() {
        User user = getDBUser(oidcUser.getName());

        if (user == null) {
            String uName = oidcUser.getFullName();
            if (uName == null || uName.isBlank()) {
                uName = oidcUser.getEmail();
            }
            user = createDBUser(uName, oidcUser.getName());
        }

        return new ExtendedOidcUser(userRequest, oidcUser, user);
    }
}
