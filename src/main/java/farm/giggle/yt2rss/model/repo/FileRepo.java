package farm.giggle.yt2rss.model.repo;

import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.services.dto.LatestChannelUpdateDatesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FileRepo extends JpaRepository<File, Long> {
    Page<File> findFilesByChannelId(Long channelId, Pageable pageable);

    List<File> findFilesByDownloadedFileUrlIsNull(Pageable pageable);

    Optional<File> findFileById(Long fileId);

    List<File> findByChannelIdAndDowloadedAtAfter(Long channelId, LocalDateTime localDateTime);

    @Query(value = "SELECT f FROM File f WHERE f.channel.id = :channelId AND f.videoId IN :videoIds")
    List<File> findFilesByVideoIdsList(@Param("channelId") Long channelId, @Param("videoIds") Collection<String> videoIds);

    @Query(value = "SELECT new farm.giggle.yt2rss.services.dto.LatestChannelUpdateDatesDTO(c.id, c.url, c.title, max(f.publishedAt), max(f.updatedAt)) FROM Channel c INNER JOIN File f ON c.id = f.channel.id GROUP BY c.id, c.url")
    List<LatestChannelUpdateDatesDTO> findChannelsWithLatestFileDates();
}
