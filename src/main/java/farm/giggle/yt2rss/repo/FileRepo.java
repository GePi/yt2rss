package farm.giggle.yt2rss.repo;

import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepo extends JpaRepository<File, Long> {
    List<File> findFilesByChannelOrderByGuid(Channel channel);

    List<File> findFilesByChannel(Channel channel);

    List<File> findFilesByChannelAndDownloadedFileUrl1IsNull(Channel channel);

    Optional<File> findFileById(Long fileId);
}
