package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.repo.ChannelRepo;
import farm.giggle.yt2rss.repo.FileRepo;
import farm.giggle.yt2rss.web.ApplicationConfiguration;
import farm.giggle.yt2rss.youtube.RssFile;
import farm.giggle.yt2rss.youtube.Y2Rss;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public Channel addChannel(String channelUrl, String title, User user) {
        Channel channel = new Channel();
        channel.setUser(user);
        channel.setUrl(channelUrl);
        channel.setTitle(title);
        channelRepo.save(channel);
        return channel;
    }

    public void delChannel(Long channelId) {
        channelRepo.deleteById(channelId);
    }

    public List<Channel> getChannelList(Long userId) {
        return channelRepo.findChannelsByUserId(userId);
    }

    public Page<Channel> getChannelPage(Long userId, ApplicationConfiguration.SortOrder pageSort, Integer pageNum, Integer entriesOnPage) {
        if (pageSort == ApplicationConfiguration.SortOrder.TITLE) {
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

    public List<File> getNotDownloadedFileList(Channel channel) {
        return fileRepo.findFilesByChannelAndDownloadedFileUrl1IsNull(channel);
    }

    public File getFile(Long fileId) {
        return fileRepo.findFileById(fileId).orElse(null);
    }

    public void refreshFileList(Channel channel) {
        //TODO синхронизация? (для одного канала...)

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
