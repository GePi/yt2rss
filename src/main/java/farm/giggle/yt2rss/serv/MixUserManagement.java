package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.model.User;

public interface MixUserManagement {
    User getUser();
    void setUser(User user);
}
