package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.repo.ChannelRepo;
import farm.giggle.yt2rss.repo.FileRepo;
import farm.giggle.yt2rss.youtube.RssFile;
import farm.giggle.yt2rss.youtube.Y2Rss;
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

    public List<Channel> getChannelList(User user) {
        return channelRepo.findChannelsByUserId(user.getId());
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
            File file = new File();
            file.setGuid(rssFile.getVideoId());
            var foundIndex = Collections.binarySearch(dbFiles, file, Comparator.comparing(File::getGuid));
            if ((foundIndex < 0) || (dbFiles.get(foundIndex).getPublishedTime().compareTo(rssFile.getTimeOfLastPublication()) < 0)) {
                file.setTitle(rssFile.getTitle());
                file.setGuid(rssFile.getVideoId());
                file.setOriginalUrl(rssFile.getVideoUrl());
                file.setPublishedTime(rssFile.getTimeOfLastPublication());
                channel.addFile(file);
            }
        }
        channelRepo.save(channel);
    }

    public void updateFile(File file) {
        fileRepo.save(file);
    }
}
