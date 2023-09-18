package farm.giggle.yt2rss.model.repo;

import farm.giggle.yt2rss.model.Auth2ProviderEnum;
import farm.giggle.yt2rss.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findUserByAuth2ProviderAndAndAuth2Id(Auth2ProviderEnum auth2ProviderEnum, String auth2Id);
    User findByUuid(UUID userUUID);
}
