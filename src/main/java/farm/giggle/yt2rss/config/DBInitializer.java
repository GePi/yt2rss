package farm.giggle.yt2rss.config;


import farm.giggle.yt2rss.model.Auth2ProviderEnum;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.repo.UserRepo;
import farm.giggle.yt2rss.serv.ChannelServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DBInitializer {
    private ChannelServiceImpl channelService;
    private UserRepo userRepo;

    public DBInitializer(ChannelServiceImpl channelService, UserRepo userRepo) {
        this.channelService = channelService;
        this.userRepo = userRepo;
    }

    @PostConstruct
    public void insertData() {
        User user = new User();
        user.setName("Test user");
        user.setAuth2Provider(Auth2ProviderEnum.GITHUB);
        user.setAuth2Id("13550159");
        userRepo.save(user);
        channelService.addChannel("https://www.youtube.com/feeds/videos.xml?channel_id=UCTVk323gzizpujtn2T_BL7w", "Тестовый канал", user);
    }
}
