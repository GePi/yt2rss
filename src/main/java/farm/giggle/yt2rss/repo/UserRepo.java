package farm.giggle.yt2rss.repo;

import farm.giggle.yt2rss.model.Auth2Provider;
import farm.giggle.yt2rss.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findUserByAuth2ProviderAndAndAuth2Id(Auth2Provider auth2Provider, String auth2Id);
}
