package farm.giggle.yt2rss.api;

import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.serv.ChannelService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ExternalDownloaderController {
    private final ChannelService channelService;

    public ExternalDownloaderController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("/whatdownload")
    public ExchangeFileFormat[] getFilesForDownload() {
        List<ExchangeFileFormat> exchangeFileFormats = new ArrayList<>();
        for (var file : channelService.getNotDownloadedFileList()) {
            exchangeFileFormats.add(new ExchangeFileFormat(file.getId(), file.getOriginalUrl()));
        }
        return exchangeFileFormats.toArray(ExchangeFileFormat[]::new);
    }

    @PutMapping("/downloaded")
    @Transactional
    public void setDownloadedUrl(@RequestBody ExchangeFileFormat[] exchangeFileFormats) {
        //TODO можно ли как то массово внести
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        for (var downloadedFile : exchangeFileFormats) {
            File file = channelService.getFile(downloadedFile.getId());
            if (file != null) {
                file.setDownloadedFileUrl(downloadedFile.getDownloadedUrl());
                file.setDowloadedAt(now);
            }
        }
    }
}