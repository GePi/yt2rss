package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "guid")
    private String guid;
    @Column(name = "title")
    private String title;

    @Column(name = "original_url")
    private String originalUrl;
    @Column(name = "downloaded_1")
    private String downloadedFileUrl1;
    @Column(name = "content_type_1")
    private String downloadedContentType1;

    @Column(name = "published_at")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date publishedTime;
    @Column(name = "downloaded_at")
    @Temporal(TemporalType.DATE)
    private java.util.Date dowloadedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Channel channel;
}
