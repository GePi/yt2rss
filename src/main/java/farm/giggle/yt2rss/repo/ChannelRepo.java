package farm.giggle.yt2rss.repo;

import farm.giggle.yt2rss.model.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepo extends JpaRepository<Channel, Long> {
    List<Channel> findChannelsByUserId(Long userId);

    Page<Channel> findChannelsByUserId(Long userId, Pageable pageable);
}
