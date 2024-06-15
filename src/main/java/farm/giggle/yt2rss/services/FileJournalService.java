package farm.giggle.yt2rss.services;

import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.FileJournal;
import farm.giggle.yt2rss.model.repo.FileJournalRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FileJournalService {
    private final FileJournalRepo fileJournalRepo;

    public FileJournalService(FileJournalRepo fileJournalRepo) {
        this.fileJournalRepo = fileJournalRepo;
    }

    public void add(List<File> fileList) {
        fileJournalRepo.saveAll(
                fileList.stream().map(FileJournal::new).toList()
        );
    }
    public void add(File file) {
        fileJournalRepo.save(new FileJournal(file));
    }
}
