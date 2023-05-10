package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.repo.ChannelRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelServiceImpl {
    private ChannelRepo channelRepo;

    public ChannelServiceImpl(ChannelRepo channelRepo) {
        this.channelRepo = channelRepo;
    }

    public Channel addChannel(String channelUrl, String title, User user) {
        Channel channel = new Channel();
        channel.setUser(user);
        channel.setUrl(channelUrl);
        channel.setTitle(title);
        channelRepo.save(channel);
        return channel;
    }

    public List<Channel> getChannelList(User user) {
        return channelRepo.findChannelsByUserId(user.getId());
    }
}
