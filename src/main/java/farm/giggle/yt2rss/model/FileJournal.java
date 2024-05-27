package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@NoArgsConstructor
@Entity
@Table(name = "FILE_JOURNAL")
public class FileJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    @Column(nullable = false)
    private String url;

    @Column
    private java.time.LocalDateTime publicationTime;

    @Column(name = "USER_UUID")
    private UUID userUUID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "FILE_ID")
    private File file;

    public FileJournal(@NonNull File file) {
        this.url = file.getOriginalUrl();
        this.publicationTime = file.getPublishedAt();
        this.userUUID = file.getChannel().getUser().getUuid();
        this.file = file;
    }
}
