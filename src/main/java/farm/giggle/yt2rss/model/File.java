package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.*;

@Getter
@Setter
@Entity
@Table(name = "FILES",
        uniqueConstraints = {@UniqueConstraint(name = "FILE_IN_CHANNEL", columnNames = {"CHANNEL_ID", "VIDEO_ID"})})
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_generator")
    @SequenceGenerator(name="files_generator", sequenceName = "files_SEQ", allocationSize=1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "VIDEO_ID", nullable = false)
    private String videoId;
    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "ORIGINAL_URL", nullable = false)
    private String originalUrl;

    @NotNull
    @Column(nullable = false, name = "PUBLISHED_AT")
    private java.time.LocalDateTime publishedAt;
    @NotNull
    @Column(nullable = false, name = "UPDATED_AT")
    private java.time.LocalDateTime updatedAt;

    @Column(name = "DOWNLOADED_AT")
    private java.time.LocalDateTime dowloadedAt;
    @Column(name = "DOWNLOADED_URL")
    private String downloadedFileUrl;
    @Column(name = "DOWNLOADED_CONTENT_TYPE")
    private String downloadedContentType;
    @Column(name = "DOWNLOADED_SIZE")
    private int downloadedSize;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;

    public File() {
    }

    public File(String videoId, String title, String originalUrl, LocalDateTime publishedAt, LocalDateTime updatedAt, Channel channel) {
        this.videoId = videoId;
        this.title = title;
        this.originalUrl = originalUrl;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        if (this.updatedAt == null) {
            this.updatedAt = publishedAt;
        }
        this.channel = channel;
    }

    public File(String videoId, String title, String originalUrl, LocalDateTime publishedAt, LocalDateTime updatedAt) {
        this(videoId, title, originalUrl, publishedAt, updatedAt, null);
    }

    public static OffsetDateTime timeStampToOffsetDataTime(java.sql.Timestamp timestamp) {
        return timestamp == null ? null : OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneId.of("UTC"));
    }


    public static OffsetDateTime max(OffsetDateTime publishedAt, OffsetDateTime updatedAt) {
        return (updatedAt == null || publishedAt.isAfter(updatedAt)) ? publishedAt : updatedAt;
    }

    public static OffsetDateTime max(java.sql.Timestamp publishedAt, java.sql.Timestamp updatedAt) {
        return (updatedAt == null || publishedAt.after(updatedAt)) ?
                OffsetDateTime.of(publishedAt.toLocalDateTime(), ZoneOffset.UTC) :
                OffsetDateTime.of(updatedAt.toLocalDateTime(), ZoneOffset.UTC);
    }
}
