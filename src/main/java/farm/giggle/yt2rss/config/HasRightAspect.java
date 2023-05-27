package farm.giggle.yt2rss.config;

import farm.giggle.yt2rss.exceptions.ResourceAccessDeniedException;
import farm.giggle.yt2rss.model.Channel;
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

@Aspect
@Component
@AllArgsConstructor()
public class HasRightAspect {
    private ChannelServiceImpl channelService;

    @Before(value = "@annotation(right)", argNames = "joinPoint,right")
    public void HasRightToChannelAnnotationHandler(JoinPoint joinPoint, HasRightToChannel right) throws ResourceAccessDeniedException {
        Long channelId = getChannelIdParameterValue(joinPoint, right);

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

    private static Long getChannelIdParameterValue(JoinPoint joinPoint, HasRightToChannel right) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        int i = Arrays.asList(parameterNames).indexOf(right.paramNameChannelNumber());
        if (i < 0) {
            throw new IllegalArgumentException("Missing parameter " + right.paramNameChannelNumber());
        }
        Object[] args = joinPoint.getArgs();
        Long channelId = (Long) args[i];
        return channelId;
    }
}
