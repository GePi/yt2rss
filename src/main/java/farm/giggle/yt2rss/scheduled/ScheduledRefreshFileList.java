package farm.giggle.yt2rss.scheduled;

import farm.giggle.yt2rss.services.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledRefreshFileList {
    private final ChannelService channelService;

    public ScheduledRefreshFileList(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Scheduled(fixedDelay = 300000)
    public void doRefresh() {
        log.debug("start doRefresh()");
        channelService.refreshFileList();
    }
}
