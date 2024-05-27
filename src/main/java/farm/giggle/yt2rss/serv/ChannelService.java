package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.config.ApplicationConfig;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.model.repo.ChannelRepo;
import farm.giggle.yt2rss.model.repo.FileRepo;
import farm.giggle.yt2rss.youtube.RssFile;
import farm.giggle.yt2rss.youtube.RssToDBConverter;
import farm.giggle.yt2rss.youtube.Y2Rss;
import jakarta.persistence.Tuple;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@Transactional
public class ChannelService {
    private final ChannelRepo channelRepo;
    private final FileRepo fileRepo;
    private final FileJournalService fileJournal;

    public ChannelService(ChannelRepo channelRepo, FileRepo fileRepo, ApplicationConfig appConfig, FileJournalService fileJournal) {
        this.channelRepo = channelRepo;
        this.fileRepo = fileRepo;
        this.fileJournal = fileJournal;
    }

    public Channel getChannel(Long id) {
        return channelRepo.findById(id).orElse(null);
    }

    public void addChannel(String channelUrl, String title, Supplier<User> userSupplier, List<File> fileList) {
        Channel channel = new Channel(channelUrl, title, userSupplier.get());
        fileList.forEach(channel::addFile);
        channelRepo.save(channel);
        fileJournal.add(channel.getFileList());
    }

    public void delChannel(Long channelId) {
        channelRepo.deleteById(channelId);
    }

    public Channel getChannel(UUID channelUUID) {
        return channelRepo.findByUuid(channelUUID);
    }

    public List<Channel> getChannelList(Long userId) {
        return channelRepo.findChannelsByUserId(userId);
    }

    public Page<Channel> getChannelPage(Long userId, ApplicationConfig.SortOrder pageSort, Integer pageNum, Integer entriesOnPage) {
        if (pageSort == ApplicationConfig.SortOrder.TITLE) {
            return channelRepo.findChannelsByUserId(userId, PageRequest.of(pageNum, entriesOnPage, Sort.by("title")));
        }
        return channelRepo.findChannelsByUserId(userId, PageRequest.of(pageNum, entriesOnPage));
    }

    public Page<File> getFilePage(Long channelId, Integer pageNum, Integer entriesOnPage) {
        return fileRepo.findFilesByChannelId(channelId, PageRequest.of(pageNum, entriesOnPage, Sort.by("title")));
    }

    public List<File> getFileListDownloadedAfter(@NotNull Channel channel, LocalDateTime downloadedAt) {
        return fileRepo.findByChannelIdAndDowloadedAtAfter(channel.getId(), downloadedAt);
    }

    public List<File> getNotDownloadedFileList() {
        return fileRepo.findFilesByDownloadedFileUrlIsNull(Pageable.ofSize(appConfig.getDownloadablePortion()));
    }

    public File getFile(Long fileId) {
        return fileRepo.findFileById(fileId).orElse(null);
    }

    public void refreshFileList() {
        List<Tuple> channelTuples = fileRepo.findChannelsWithLatestFileDates();
        for (Tuple channelTuple : channelTuples) {
            Y2Rss y2Rss = Y2Rss.fromUrl((String) channelTuple.get(1));
            if (y2Rss != null) {

                OffsetDateTime timeLastChannelUpdate =
                        File.max(channelTuple.get(2, java.sql.Timestamp.class), channelTuple.get(3, java.sql.Timestamp.class));

                updateChannelFiles(
                        channelTuple.get(0, Long.class),
                        y2Rss.getFileList().stream()
                                .filter(rssFile -> timeLastChannelUpdate.isBefore(File.max(rssFile.getPublishedAt(), rssFile.getUpdatedAt())))
                                .toList());
            }
        }
    }

    public void updateChannelFiles(@NonNull Long channelId, @NonNull List<RssFile> rssNewFiles) {
        Channel channel;
        if (rssNewFiles.isEmpty() || (channel = getChannel(channelId)) == null) {
            return;
        }

        Map<String, File> filesByVideoIdMap = new HashMap<>();
        fileRepo.findFilesByVideoIdsList(channelId,
                rssNewFiles.stream().map(RssFile::getVideoId).toList()).forEach(file -> filesByVideoIdMap.put(file.getVideoId(), file));

        for (RssFile rssFile : rssNewFiles) {
            File file = filesByVideoIdMap.get(rssFile.getVideoId());
            if (file == null) {
                file = RssToDBConverter.rssFile2DBFile(rssFile, channel);
                fileRepo.save(file);
            } else {
                RssToDBConverter.rssFile2DBFileUpdatedTime(rssFile, file);
            }
        }
    }
}
