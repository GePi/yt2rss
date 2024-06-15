package farm.giggle.yt2rss.services;

import farm.giggle.yt2rss.config.ApplicationConfig;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.model.repo.ChannelRepo;
import farm.giggle.yt2rss.model.repo.FileRepo;
import farm.giggle.yt2rss.services.dto.LatestChannelUpdateDatesDTO;
import farm.giggle.yt2rss.youtube.dto.YoutubeEpisodeDTO;
import farm.giggle.yt2rss.youtube.services.YTEpisodeToDBFileConverter;
import farm.giggle.yt2rss.youtube.services.YoutubeParser;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ChannelService {
    private final ChannelRepo channelRepo;
    private final FileRepo fileRepo;
    private final FileJournalService fileJournal;
    private ApplicationConfig appConfig;

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
        channel = channelRepo.save(channel);
        fileJournal.add(channel.getFileList());
        log.debug("add channel " + channel.getId() + " (" + channel.getTitle() + ")");
        channel.getFileList().forEach(file ->
                log.debug("add file " + file.getId() + " (" + file.getTitle() + "), set updatedAt = " + file.getUpdatedAt()));
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
        return channelRepo.findChannelsByUserId(userId, PageRequest.of(pageNum, entriesOnPage, getChannelPageSort(pageSort)));
    }

    private Sort getChannelPageSort(ApplicationConfig.SortOrder pageSort) {
        return Sort.by("title");
    }

    private Sort getFilePageSort(ApplicationConfig.SortOrder pageSort) {
        if (pageSort == null) {
            return Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        return switch (pageSort) {
            case TITLE -> Sort.by("title");
            case UPDATED -> Sort.by(Sort.Direction.DESC, "updatedAt");
            case CREATED -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    public Page<File> getFilePage(Long channelId, ApplicationConfig.SortOrder pageSort, Integer pageNum, Integer entriesOnPage) {
        return fileRepo.findFilesByChannelId(channelId, PageRequest.of(pageNum, entriesOnPage, getFilePageSort(pageSort)));
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


    public void updateChannelFiles(@NonNull Long channelId, @NonNull List<YoutubeEpisodeDTO> youtubeEpisodes) {
        log.debug("call updateChannelFiles(), channel = " + channelId);
        Channel channel;
        if (youtubeEpisodes.isEmpty() || (channel = getChannel(channelId)) == null) {
            return;
        }

        Map<String, File> filesByVideoIdMap = fileRepo.findFilesByVideoIdsList(
                        channelId,
                        youtubeEpisodes.stream()
                                .map(YoutubeEpisodeDTO::getVideoId)
                                .toList())
                .stream()
                .collect(Collectors.toMap(File::getVideoId, Function.identity()));

        for (YoutubeEpisodeDTO youtubeEpisodeDTO : youtubeEpisodes) {
            File file = filesByVideoIdMap.get(youtubeEpisodeDTO.getVideoId());
            if (file == null) {
                file = YTEpisodeToDBFileConverter.createFileByYTEpisode(youtubeEpisodeDTO, channel);
                file = fileRepo.save(file);
                fileJournal.add(file);
                log.debug("add file " + file.getId() + " (" + file.getTitle() + "), set updatedAt = " + file.getUpdatedAt());
            } else {
                LocalDateTime updatedAt = file.getUpdatedAt();
                YTEpisodeToDBFileConverter.setFIleDateTime(youtubeEpisodeDTO, file);
                if (updatedAt.isBefore(file.getUpdatedAt())) {
                    file = fileRepo.save(file);
                    fileJournal.add(file);
                    log.debug("update file " + file.getId() + " (" + file.getTitle() + "), set updatedAt = " + file.getUpdatedAt());
                }
            }
        }
    }

    synchronized public void refreshFileList() {
        List<LatestChannelUpdateDatesDTO> channels = getAllChannelsLastUpdate();
        for (var channel : channels) {
            log.debug("channel = " + channel.getId() + "(" + channel.getTitle() + ")" + " last update at = " + channel.getUpdatedAt());
            YoutubeParser youtubeParser = YoutubeParser.createFromUrl(channel.getUrl());

            updateChannelFiles(
                    channel.getId(),
                    youtubeParser.getEpisodes().stream()
                            .filter(youtubeEpisodeDTO -> channel.getUpdatedAt().isBefore(File.max(youtubeEpisodeDTO.getPublishedAt(), youtubeEpisodeDTO.getUpdatedAt())))
                            .toList());
        }
    }

    public List<LatestChannelUpdateDatesDTO> getAllChannelsLastUpdate() {
        return fileRepo.findChannelsWithLatestFileDates();
    }

}
