package farm.giggle.yt2rss.config;

import farm.giggle.yt2rss.config.security.userservice.MixUserManagement;
import farm.giggle.yt2rss.exceptions.ResourceAccessDeniedException;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.services.ChannelService;
import farm.giggle.yt2rss.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HasRightAspectHelper {

    final private ChannelService channelService;
    final private UserService userService;

    public HasRightAspectHelper(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    @Transactional
    public void hasRightToChannel(Long channelId) throws ResourceAccessDeniedException {
        User authorizedUser = getAutorizedUser();
        if (authorizedUser == null) {
            throw new ResourceAccessDeniedException("The user is not logged in");
        }

        Channel channel = channelService.getChannel(channelId);
        if (channel == null) {
            throw new ResourceAccessDeniedException(new IllegalArgumentException("Channel id=" + channelId + " not exist"));
        }

        if (channel.getUser().getId().equals(authorizedUser.getId())) {
            return;
        }

        if (userService.hasAdminRole(authorizedUser)) {
            return;
        }

        throw new ResourceAccessDeniedException("User " + authorizedUser.getId() + " access is denied, access is not to your resource (channelId=" + channelId + ") ");
    }

    @Transactional
    public void hasRightToUsers() throws ResourceAccessDeniedException {
        User authorizedUser = getAutorizedUser();
        if (authorizedUser == null) {
            throw new ResourceAccessDeniedException("The user is not logged in");
        }
        if (userService.hasAdminRole(authorizedUser)) {
            return;
        }

        throw new ResourceAccessDeniedException("User does not have administrative authority");
    }

    @Transactional
    public void hasRightToUser(Long userId) throws ResourceAccessDeniedException {
        User authorizedUser = getAutorizedUser();
        if (authorizedUser == null) {
            throw new ResourceAccessDeniedException("The user is not logged in");
        }

        if (userId.equals(authorizedUser.getId())) {
            return;
        }

        if (userService.hasAdminRole(authorizedUser)) {
            return;
        }

        throw new ResourceAccessDeniedException("User " + userId + " access is denied, access is not to your resource (all channels)");
    }

    private static User getAutorizedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication.isAuthenticated()) {
            return ((MixUserManagement) context.getAuthentication().getPrincipal()).getUser();
        }
        return null;
    }
}
