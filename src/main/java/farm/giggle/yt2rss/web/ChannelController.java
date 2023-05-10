package farm.giggle.yt2rss.web;

import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.serv.ChannelServiceImpl;
import farm.giggle.yt2rss.serv.MixUserManagement;
import farm.giggle.yt2rss.youtube.Y2Rss;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelServiceImpl channelService;

    public ChannelController(ChannelServiceImpl channelService) {
        this.channelService = channelService;
    }

    @ModelAttribute(name = "channels")
    public List<Channel> getChannelList(@AuthenticationPrincipal MixUserManagement principal) {
        return channelService.getChannelList(principal.getUser());
    }

    @GetMapping
    public String channelsPage() {
        return "channels";
    }

    @GetMapping("/add-channel")
    public String addChannelPage() {
        return "addchannel";
    }

    @PostMapping("/add-channel")
    public String addChannelPostPage(@RequestParam("url") String urlInput,
                                     @AuthenticationPrincipal MixUserManagement principal) {
        Y2Rss rss = Y2Rss.fromUrl(urlInput.trim());
        if (rss != null) {
            channelService.addChannel(rss.getUrl(), rss.getTitle() ,principal.getUser());
        }
        return "redirect:/channels";
    }
}
