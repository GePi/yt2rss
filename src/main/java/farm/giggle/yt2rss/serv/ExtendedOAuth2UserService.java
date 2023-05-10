package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.model.Auth2Provider;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.repo.UserRepo;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class ExtendedOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepo userRepo;

    public ExtendedOAuth2UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public ExtendedOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User user = delegate.loadUser(userRequest);
        return dbProcess(user, userRequest);
    }

    private ExtendedOAuth2User dbProcess(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        String userId = oAuth2User.getName();
        User user = userRepo.findUserByAuth2ProviderAndAndAuth2Id(Auth2Provider.GITHUB, userId);
        if (user == null) {
            user = new User();
            user.setName(userId);
            user.setAuth2Provider(Auth2Provider.GITHUB);
            user.setAuth2Id(userId);
            userRepo.save(user);
        }
        return new ExtendedOAuth2User(userRequest, oAuth2User, user);
        /*if ("github".equals(userRequest.getClientRegistration().getRegistrationId())) */
    }
}
