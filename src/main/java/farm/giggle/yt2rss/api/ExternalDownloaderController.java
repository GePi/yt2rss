package farm.giggle.yt2rss.api;

import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.serv.ChannelServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ExternalDownloaderController {
    private final ChannelServiceImpl channelService;

    public ExternalDownloaderController(ChannelServiceImpl channelService) {
        this.channelService = channelService;
    }

    @GetMapping("/whatdownload")
    public ExchangeFileFormat[] getFilesForDownload() {
        List<ExchangeFileFormat> exchangeFileFormats = new ArrayList<>();
        List<Channel> channels = channelService.getAll();
        for (var channel : channels) {
            channelService.refreshFileList(channel);
            for (var file : channelService.getNotDownloadedFileList(channel)) {
                exchangeFileFormats.add(new ExchangeFileFormat(file.getId(), file.getOriginalUrl()));
            }
        }
        return exchangeFileFormats.toArray(ExchangeFileFormat[]::new);
    }

    @PutMapping("/downloaded")
    public void setDownloadedUrl(@RequestBody ExchangeFileFormat[] exchangeFileFormats) {
        //TODO можно ли как то массово внести
        OffsetDateTime now = OffsetDateTime.now();
        for (var downloadedFile : exchangeFileFormats) {
            File file = channelService.getFile(downloadedFile.getId());
            if (file != null) {
                file.setDownloadedFileUrl1(downloadedFile.getDownloadedUrl());
                file.setDowloadedAt(now);
                channelService.updateFile(file);
            }
        }
    }
}