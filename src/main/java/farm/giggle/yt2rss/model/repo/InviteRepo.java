package farm.giggle.yt2rss.model.repo;

import farm.giggle.yt2rss.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepo extends JpaRepository<Invite, Long> {
    @Query(value = "SELECT * from invite as i where i.code = :invite_code for update", nativeQuery = true)
    Invite selectInvite(String invite_code);

    void deleteInviteByCode(String code);
}
