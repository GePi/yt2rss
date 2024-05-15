package farm.giggle.yt2rss.config.security;

import farm.giggle.yt2rss.model.repo.UserRepo;
import farm.giggle.yt2rss.serv.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class ExtendedOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserService userService;

    public ExtendedOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ExtendedOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User user = delegate.loadUser(userRequest);
        return dbProcess(user, userRequest);
    }

    private ExtendedOAuth2User dbProcess(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        UserFactory userFactory = UserFactory.create(userRequest, oAuth2User, userService);
        return (ExtendedOAuth2User) userFactory.createAuth2User();
    }
}
