package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.model.User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class ExtendedOAuth2User extends DefaultOAuth2User implements MixUserManagement {
    private User user;

    public ExtendedOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User, User user) {
        super(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
