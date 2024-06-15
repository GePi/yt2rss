package farm.giggle.yt2rss.controllers;

import farm.giggle.yt2rss.config.ApplicationConfig;
import farm.giggle.yt2rss.config.HasRightToChannel;
import farm.giggle.yt2rss.config.HasRightToUser;
import farm.giggle.yt2rss.config.security.userservice.MixUserManagement;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.services.ChannelService;
import farm.giggle.yt2rss.services.UserService;
import farm.giggle.yt2rss.youtube.services.YTEpisodeToDBFileConverter;
import farm.giggle.yt2rss.youtube.services.YoutubeParser;
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
    private final ChannelService channelService;
    private final UserService userService;
    private final ApplicationConfig applicationConfig;

    public ChannelController(ChannelService channelService, ApplicationConfig applicationConfig, UserService userService) {
        this.applicationConfig = applicationConfig;
        this.channelService = channelService;
        this.userService = userService;
    }

    @GetMapping
    @HasRightToUser
    public String channelsPage(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "sort", required = false) ApplicationConfig.SortOrder sort,
            @AuthenticationPrincipal MixUserManagement principal,
            Model model) {

        User userOwnerResource = userId != null ? userService.getUserById(userId) : principal.getUser();

        Integer pageNum = Objects.requireNonNullElse(page, 1) - 1;
        ApplicationConfig.SortOrder pageSort = Objects.requireNonNullElse(sort, ApplicationConfig.SortOrder.UPDATED);

        Page<Channel> channelPage =
                channelService.getChannelPage(userOwnerResource.getId(),
                        pageSort,
                        pageNum,
                        applicationConfig.getChannelPage().getNumberEntriesOnPage());

        model.addAttribute("channels", channelPage.get().collect(Collectors.toList()));
        model.addAttribute("totalPages", Math.max(channelPage.getTotalPages(), 1));
        UrlParams urlParams = UrlParams.of(UrlParam.of("userId", userId));
        model.addAttribute("sessionParamsString", urlParams.getParamString());
        model.addAttribute("sessionParams", urlParams);
        return "channels";
    }

    @GetMapping("/files")
    @HasRightToChannel
    public String filesPage(@RequestParam("channelId") Long channelId,
                            @RequestParam(value = "userId", required = false) Long userId,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "sort", required = false) ApplicationConfig.SortOrder sort,
                            Model model) {

        Integer pageNum = Objects.requireNonNullElse(page, 1) - 1;
        Page<File> filesPage = channelService.getFilePage(channelId, sort, pageNum, applicationConfig.getChannelPage().getNumberEntriesOnPage());
        Channel channel = channelService.getChannel(channelId);
        if (channel == null)
            return "redirect:/channels" + UrlParams.of(UrlParam.of("userId", userId)).getParamStringSeparated();

        model.addAttribute("files", filesPage.getContent());
        model.addAttribute("totalPages", Math.max(filesPage.getTotalPages(), 1));
        model.addAttribute("channel", channel);
        model.addAttribute("sessionParamsString", UrlParams.of(UrlParam.of("userId", userId)).getParamString());
        return "files";
    }

    @GetMapping("/add-channel")
    public String addChannelPage(@RequestParam(value = "userId", required = false) Long userId,
                                 Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("sessionParams", UrlParams.of(UrlParam.of("userId", userId)));
        return "addchannel";
    }

    @PostMapping("/add-channel")
    @HasRightToUser
    public String addChannelPostHandler(@RequestParam("url") String url,
                                        @RequestParam(value = "userId", required = false) Long userId,
                                        @AuthenticationPrincipal MixUserManagement principal) {
        YoutubeParser ytParser = YoutubeParser.createFromUrl(url.trim());
        channelService.addChannel(
                ytParser.getUrl(),
                ytParser.getTitle(),
                () -> userService.getUserById(userId != null ? userId : principal.getUser().getId()),
                ytParser.getEpisodes().stream().map(YTEpisodeToDBFileConverter::createFileByYTEpisode).toList());
        return "redirect:/channels" + UrlParams.of(UrlParam.of("userId", userId)).getParamStringSeparated();
    }

    @PostMapping("/del-channel")
    @HasRightToChannel
    public String delChannelPostHandler(@RequestParam Long channelId,
                                        @RequestParam(value = "userId", required = false) Long userId) {
        channelService.delChannel(channelId);
        return "redirect:/channels" + UrlParams.of(UrlParam.of("userId", userId)).getParamStringSeparated();
    }

    @GetMapping("/update")
    public String updateChannel(@RequestParam(value = "userId", required = false) Long userId) {
        channelService.refreshFileList();
        return "redirect:/channels" + UrlParams.of(UrlParam.of("userId", userId)).getParamStringSeparated();
    }
}
