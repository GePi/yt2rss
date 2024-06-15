package farm.giggle.yt2rss.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class ExceptionConfig {
    @Bean(name = "loggerExceptionResolver")
    public HandlerExceptionResolver getLoggerExceptionResolver() {
        return new OrderedHandlerExceptionResolver() {
            @Override
            public int getOrder() {
                return Integer.MIN_VALUE;
            }

            @Override
            public ModelAndView resolveException(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, Object handler, @NotNull Exception ex) {
                LoggerFactory.getLogger(ex.getClass()).error("Exception occurred", ex);
                return null;
            }
        };
    }
}
