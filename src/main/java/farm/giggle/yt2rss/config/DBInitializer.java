package farm.giggle.yt2rss.config;


import farm.giggle.yt2rss.model.Auth2ProviderEnum;
import farm.giggle.yt2rss.model.Role;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.repo.UserRepo;
import farm.giggle.yt2rss.serv.ChannelServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import static farm.giggle.yt2rss.model.Role.RoleEnum.ADMIN;
import static farm.giggle.yt2rss.model.Role.RoleEnum.USER;

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
        user.addRole(Role.from(USER));
        user.addRole(Role.from(ADMIN));
        userRepo.save(user);
        channelService.addChannel("https://www.youtube.com/feeds/videos.xml?channel_id=UCTVk323gzizpujtn2T_BL7w", "Тестовый канал", user);
        channelService.addChannel("https://www.youtube.com/feeds/videos.xml?channel_id=UCYrGYT7BswsJGkmG7-IAF8g", "JPoint, Joker и JUG ru", user);
        channelService.addChannel("https://www.youtube.com/feeds/videos.xml?channel_id=UCbM79SGgdcRk1om91Ji-3Ag", "Mad Brains", user);
        channelService.addChannel("https://www.youtube.com/feeds/videos.xml?channel_id=UCYp3rk70ACGXQ4gFAiMr1SQ", "Rammstein Official", user);
        channelService.addChannel("https://www.youtube.com/feeds/videos.xml?channel_id=UCVbz7l0COUdLupcY4YtYH0w", "Sergey Nemchinskiy", user);
        channelService.addChannel("https://www.youtube.com/feeds/videos.xml?channel_id=UCklWL_zSjeesmnx81aYUCbQ", "Java coding interview", user);
        channelService.addChannel("https://www.youtube.com/feeds/videos.xml?channel_id=UC8butISFwT-Wl7EV0hUK0BQ", "freeCodeCamp.org", user);
    }
}
