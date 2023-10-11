package farm.giggle.yt2rss.config.security;

import farm.giggle.yt2rss.model.User;

public interface MixUserManagement {
    User getUser();
    void setUser(User user);
}
