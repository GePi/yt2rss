package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "channel")
public class Channel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "url", nullable = false)
    private String url;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String filter;
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<File> fileList = new ArrayList<>();

    public Channel() {
    }

    public Channel(String url, String title, String filter, User user) {
        this.url = url;
        this.title = title;
        this.filter = filter;
        this.user = user;
        this.uuid = UUID.randomUUID();
    }

    public void addFile(File file) {
        fileList.add(file);
        file.setChannel(this);
    }

    public void removeFile(File file) {
        fileList.remove(file);
        file.setChannel(null);
    }
}