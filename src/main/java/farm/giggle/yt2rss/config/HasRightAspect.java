package farm.giggle.yt2rss.config;

import farm.giggle.yt2rss.exceptions.ResourceAccessDeniedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class HasRightAspect {
    final HasRightAspectHelper hasRightAspectHelper;

    public HasRightAspect(HasRightAspectHelper hasRightAspectHelper) {
        this.hasRightAspectHelper = hasRightAspectHelper;
    }

    @Before(value = "@annotation(right)", argNames = "joinPoint,right")
    public void HasRightToChannelAnnotationHandler(JoinPoint joinPoint, HasRightToChannel right) throws ResourceAccessDeniedException {
        Long channelId = getParameterValue(joinPoint, right.paramNameChannelNumber());
        if (channelId == null) {
            throw new IllegalArgumentException("Missing parameter " + right.paramNameChannelNumber());
        }

        hasRightAspectHelper.hasRightToChannel(channelId);
    }

    @Before(value = "@annotation(userAnnotation)", argNames = "joinPoint,userAnnotation")
    public void HasRightToUserAnnotationHandler(JoinPoint joinPoint, HasRightToUser userAnnotation) throws ResourceAccessDeniedException {
        Long userId = getParameterValue(joinPoint, userAnnotation.paramUserid());
        if (userId == null) {
            return; // The rights are granted to themselves
        }

        hasRightAspectHelper.hasRightToUser(userId);
    }

    @Before(value = "@annotation(HasRightToUsers)")
    public void HasRightToUserAnnotationHandler() throws ResourceAccessDeniedException {
        hasRightAspectHelper.hasRightToUsers();
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
