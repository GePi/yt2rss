package farm.giggle.yt2rss.config.security;

import farm.giggle.yt2rss.model.repo.UserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepo userRepo;

    public SecurityConfig(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().ignoringRequestMatchers(toH2Console(), new AntPathRequestMatcher("/api/**"))
                .and()
                .headers().frameOptions().disable()
                .and()
                .logout(logout -> logout
                        .logoutSuccessUrl("/"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/rss/**").permitAll()
                        .requestMatchers(toH2Console()).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login();
        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> getOAuth2UserService() {
        return new ExtendedOAuth2UserService(userRepo);
    }

    @Bean
    public OidcUserService getOidcUserService() {
        return new ExtendedOidcUserService(userRepo);
    }
}
