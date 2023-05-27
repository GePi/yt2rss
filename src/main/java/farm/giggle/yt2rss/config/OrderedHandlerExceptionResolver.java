package farm.giggle.yt2rss.config;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;

public interface OrderedHandlerExceptionResolver extends HandlerExceptionResolver, Ordered {
}
