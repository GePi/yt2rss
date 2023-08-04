package farm.giggle.yt2rss.config;

import farm.giggle.yt2rss.exceptions.ResourceAccessDeniedException;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.serv.ChannelServiceImpl;
import farm.giggle.yt2rss.serv.MixUserManagement;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@AllArgsConstructor()
public class HasRightAspect {
    private ChannelServiceImpl channelService;

    @Before(value = "@annotation(right)", argNames = "joinPoint,right")
    public void HasRightToChannelAnnotationHandler(JoinPoint joinPoint, HasRightToChannel right) throws ResourceAccessDeniedException {
        Long channelId = getParameterValue(joinPoint, right.paramNameChannelNumber());
        if (channelId == null) {
            throw new IllegalArgumentException("Missing parameter " + right.paramNameChannelNumber());
        }

        Channel channel = channelService.getChannel(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("Feed id=" + channelId + " not exist");
        }

        SecurityContext context = SecurityContextHolder.getContext();
        Long userId = ((MixUserManagement) context.getAuthentication().getPrincipal()).getUser().getId();
        if (!channel.getUser().getId().equals(userId)) {
            throw new ResourceAccessDeniedException("User " + userId + " access is denied, access is not to your resource (channelId=" + channelId + ") ");
        }
    }

    @Before(value = "@annotation(userAnnotation)", argNames = "joinPoint,userAnnotation")
    public void HasRightToUserAnnotationHandler(JoinPoint joinPoint, HasRightToUser userAnnotation) throws ResourceAccessDeniedException {
        Long userId = getParameterValue(joinPoint, userAnnotation.paramUserid());
        if (userId == null) {
            return;
        }

        SecurityContext context = SecurityContextHolder.getContext();
        User user = ((MixUserManagement) context.getAuthentication().getPrincipal()).getUser();
        if (user == null ||
                !Objects.equals(user.getId(), userId) && !user.isAdmin()) {
            //user.getRoles().stream().noneMatch(role -> role.getRoleName() == Role.RoleEnum.ADMIN)) {
            throw new ResourceAccessDeniedException("User " + userId + " access is denied, access is not to your resource (all channels)");
        }
    }

    private static Long getParameterValue(JoinPoint joinPoint, String paramName) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        int i = Arrays.asList(parameterNames).indexOf(paramName);
        if (i < 0) {
            return null;
        }
        Object[] args = joinPoint.getArgs();
        return (Long) args[i];
    }
}
