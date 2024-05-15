package farm.giggle.yt2rss.config.security;

import farm.giggle.yt2rss.exceptions.Auth2ProviderNotSupportedException;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.model.repo.UserRepo;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Objects;

public class UserFactoryOAuth2 extends UserFactory {
    private final OAuth2UserRequest userRequest;
    private final OAuth2User oAuth2User;

    public UserFactoryOAuth2(OAuth2UserRequest userRequest, OAuth2User oAuth2User, UserRepo userRepo) {
        super(userRepo);

        this.oAuth2User = oAuth2User;
        this.userRequest = userRequest;

        switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "github" -> this.auth2Provider = Auth2ProviderEnum.GITHUB;
            default ->
                    throw new Auth2ProviderNotSupportedException(userRequest.getClientRegistration().getRegistrationId());
        }
    }

    @Override
    public ExtendedOAuth2User createAuth2User() {
        User user = getDBUser(oAuth2User.getName());
        if (user == null) {
            user = createDBUser(
                    Objects.requireNonNull(oAuth2User.getAttribute("login")).toString(),
                    oAuth2User.getName());
        }
        return new ExtendedOAuth2User(userRequest, oAuth2User, user);
    }

}
