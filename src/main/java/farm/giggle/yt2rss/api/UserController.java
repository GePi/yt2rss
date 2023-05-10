package farm.giggle.yt2rss.api;

import farm.giggle.yt2rss.youtube.Y2Serv;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {
    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return (principal == null) ?
                Collections.singletonMap("name", null) :
                Collections.singletonMap("name", principal.getName());
    }

    @GetMapping("/download")
    public String download() {
        Y2Serv.downloadVideo();
        return "Downloaded";
    }
}
