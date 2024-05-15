package farm.giggle.yt2rss.model.repo;

import farm.giggle.yt2rss.config.security.Auth2ProviderEnum;
import farm.giggle.yt2rss.model.Role;
import farm.giggle.yt2rss.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"userRoles" , "userRoles.role"})
    User findUserByAuth2ProviderAndAuth2Id(Auth2ProviderEnum auth2ProviderEnum, String auth2Id);

    User findByUuid(UUID userUUID);
    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long Id);

    @EntityGraph(attributePaths = {"userRoles"})
    Optional<User> getUserById(Long Id);

    @Query("SELECT r FROM Role r where r.roleName = :roleName")
    Role getRoleByName(@Param("roleName") Role.RoleEnum roleName);

}
