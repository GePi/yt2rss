package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.config.ApplicationConfig;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.model.repo.ChannelRepo;
import farm.giggle.yt2rss.model.repo.FileRepo;
import farm.giggle.yt2rss.youtube.RssFile;
import farm.giggle.yt2rss.youtube.Y2Rss;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class ChannelServiceImpl {
    private ChannelRepo channelRepo;
    private FileRepo fileRepo;

    public ChannelServiceImpl(ChannelRepo channelRepo, FileRepo fileRepo) {
        this.channelRepo = channelRepo;
        this.fileRepo = fileRepo;
    }

    public Channel getChannel(Long id) {
        return channelRepo.findById(id).orElseGet(() -> null);
    }

    @Transactional
    public Channel addChannel(String channelUrl, String title, User user, List<File> fileList) {
        Channel channel = new Channel(channelUrl, title, "", user);
        fileList.forEach(channel::addFile);
        channelRepo.save(channel);
        return channel;
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

    public List<Channel> getAll() {
        return channelRepo.findAll();
    }

    public List<File> getFileList(Channel channel) {
        return fileRepo.findFilesByChannel(channel);
    }

    public List<File> getFileListDownloadedAfter(Channel channel, OffsetDateTime downloadedAt) {
        return fileRepo.findByChannelIdAndDowloadedAtAfter(channel.getId(), downloadedAt).stream().toList();
    }

    public List<File> getFileListDownloadedAfter(OffsetDateTime downloadedAt) {
        return fileRepo.findByDowloadedAtAfter(downloadedAt).stream().toList();
    }

    public List<File> getNotDownloadedFileList(Channel channel) {
        return fileRepo.findFilesByChannelAndDownloadedFileUrl1IsNull(channel);
    }

    public File getFile(Long fileId) {
        return fileRepo.findFileById(fileId).orElse(null);
    }

    public void refreshFileList(Channel channel) {
        Y2Rss rss = Y2Rss.fromUrl(channel.getUrl());
        List<RssFile> rssFiles = (rss != null) ? rss.getFileList() : new ArrayList<>();
        List<File> dbFiles = channel.getFileList();
        dbFiles.sort(Comparator.comparing(File::getGuid));

        for (var rssFile : rssFiles) {
            File dbFile = new File();
            dbFile.setGuid(rssFile.getVideoId());
            var foundIndex = Collections.binarySearch(dbFiles, dbFile, Comparator.comparing(File::getGuid));

            if (foundIndex < 0) {
                channel.addFile(dbFile);
            } else {
                dbFile = dbFiles.get(foundIndex);
            }

            if (!Objects.equals(dbFile.getPublishedAt(), rssFile.getPublishedAt()) ||
                    !Objects.equals(dbFile.getUpdatedAt(), rssFile.getUpdatedAt())) {
                mapRssFileToDbFile(rssFile, dbFile);
            }
        }
        channelRepo.save(channel);
    }

    public void updateFile(File file) {
        fileRepo.save(file);
    }

    private void mapRssFileToDbFile(RssFile rssFile, File dbFile) {
        dbFile.setTitle(rssFile.getTitle());
        dbFile.setGuid(rssFile.getVideoId());
        dbFile.setOriginalUrl(rssFile.getVideoUrl());
        dbFile.setPublishedAt(rssFile.getPublishedAt());
        dbFile.setUpdatedAt(rssFile.getUpdatedAt());
        //TODO удалить в проде
        dbFile.setDowloadedAt(rssFile.getPublishedAt());
        dbFile.setDownloadedFileUrl1("http://Он_таки_скачен");
    }
}
