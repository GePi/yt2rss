package farm.giggle.yt2rss.config;


import farm.giggle.yt2rss.model.repo.UserRepo;
import farm.giggle.yt2rss.serv.ChannelService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DBInitializer {
    private final ChannelService channelService;
    private final UserRepo userRepo;

    public DBInitializer(ChannelService channelService, UserRepo userRepo) {
        this.channelService = channelService;
        this.userRepo = userRepo;
    }

    @PostConstruct
    @Transactional
    public void insertData() {
/*
        User user = new User("SK", Auth2ProviderEnum.GITHUB, "13550159", UUID.randomUUID());
        user.addRole(Role.from(USER));
        user.addRole(Role.from(ADMIN));
        userRepo.save(user);

        ArrayList<Y2Rss> rssList = new ArrayList<>();
        rssList.add(Y2Rss.fromUrl("https://www.youtube.com/channel/UCmJc7tVBDG6R8x21sudricA"));
        rssList.add(Y2Rss.fromUrl("https://www.youtube.com/feeds/videos.xml?channel_id=UCTVk323gzizpujtn2T_BL7w"));
        rssList.add(Y2Rss.fromUrl("https://www.youtube.com/feeds/videos.xml?channel_id=UCYrGYT7BswsJGkmG7-IAF8g"));
        rssList.add(Y2Rss.fromUrl("https://www.youtube.com/feeds/videos.xml?channel_id=UCbM79SGgdcRk1om91Ji-3Ag"));
        rssList.add(Y2Rss.fromUrl("https://www.youtube.com/feeds/videos.xml?channel_id=UCYp3rk70ACGXQ4gFAiMr1SQ"));
        rssList.add(Y2Rss.fromUrl("https://www.youtube.com/feeds/videos.xml?channel_id=UCVbz7l0COUdLupcY4YtYH0w"));
        rssList.add(Y2Rss.fromUrl("https://www.youtube.com/feeds/videos.xml?channel_id=UCklWL_zSjeesmnx81aYUCbQ"));
        rssList.add(Y2Rss.fromUrl("https://www.youtube.com/feeds/videos.xml?channel_id=UC8butISFwT-Wl7EV0hUK0BQ"));
        rssList.add(Y2Rss.fromUrl("https://www.youtube.com/feeds/videos.xml?channel_id=UC8butISFwT-Wl7EV0hUK0BQ"));

        rssList.forEach(rss ->
                channelService.addChannel(
                        rss.getUrl(),
                        rss.getTitle(),
                        user,
                        rss.getFileList().stream().map(RssToDBConverter::rssFile2DBFile).toList()));
*/
    }
}
