package farm.giggle.yt2rss.web;

import farm.giggle.yt2rss.config.ApplicationConfig;
import farm.giggle.yt2rss.config.HasRightToChannel;
import farm.giggle.yt2rss.config.HasRightToUser;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.serv.ChannelServiceImpl;
import farm.giggle.yt2rss.serv.MixUserManagement;
import farm.giggle.yt2rss.youtube.RssToDBConverter;
import farm.giggle.yt2rss.youtube.Y2Rss;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelServiceImpl channelService;
    private final ApplicationConfig applicationConfig;

    public ChannelController(ChannelServiceImpl channelService, ApplicationConfig applicationConfig) {
        this.channelService = channelService;
        this.applicationConfig = applicationConfig;
    }

    @GetMapping
    @HasRightToUser
    public String channelsPage(
            @RequestParam(value = "userid", required = false) Long userid,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "sort", required = false) ApplicationConfig.SortOrder sort,
            @AuthenticationPrincipal MixUserManagement principal,
            Model model) {
        if (userid == null && principal.getUser() == null) {
            throw new IllegalArgumentException("User need");
        }

        Integer pageNum = Objects.requireNonNullElse(page, 1) - 1;
        ApplicationConfig.SortOrder pageSort = Objects.requireNonNullElse(sort, ApplicationConfig.SortOrder.TITLE);

        Page<Channel> channelPage =
                channelService.getChannelPage((userid != null) ? userid : principal.getUser().getId(),
                        pageSort,
                        pageNum,
                        applicationConfig.getChannelPage().getNumberEntriesOnPage());

        model.addAttribute("channels", channelPage.get().collect(Collectors.toList()));
        model.addAttribute("totalPages", channelPage.getTotalPages());
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
            channelService.addChannel(
                    rss.getUrl(),
                    rss.getTitle(),
                    principal.getUser(),
                    rss.getFileList().stream().map(RssToDBConverter::RssFile2DBFile).toList());
            // channelService.refreshFileList(channel);
        }
        return "redirect:/channels";
    }

    @PostMapping("/del-channel")
    @HasRightToChannel(paramNameChannelNumber = "channelId")
    public String delChannelPage(@RequestParam Long channelId) {
        channelService.delChannel(channelId);
        return "redirect:/channels";
    }

    @GetMapping("/files")
    @HasRightToChannel(paramNameChannelNumber = "channelId")
    public String channelFilesPage(@RequestParam("channelId") Long channelId,
                                   Model model) {
        log.debug("channelFilesPage channelId=" + channelId);

        Channel channel = channelService.getChannel(channelId);
        if (channel == null) {
            return "redirect:/channels";
        }
        model.addAttribute("files", channelService.getFileList(channel));
        model.addAttribute("channel", channel);
        return "files";
    }
}
