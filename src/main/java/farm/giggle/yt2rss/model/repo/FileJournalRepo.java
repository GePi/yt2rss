package farm.giggle.yt2rss.model.repo;

import farm.giggle.yt2rss.model.FileJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileJournalRepo extends JpaRepository<FileJournal, UUID> {

}
