package farm.giggle.yt2rss.config;

import farm.giggle.yt2rss.repo.UserRepo;
import farm.giggle.yt2rss.serv.ExtendedOAuth2UserService;
import farm.giggle.yt2rss.serv.ExtendedOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;

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
                .csrf().ignoringRequestMatchers(toH2Console())
                .and()
                .headers().frameOptions().disable()
                .and()
                .logout(logout -> logout
                        .logoutSuccessUrl("/"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/user").permitAll()
                        .requestMatchers(toH2Console()).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login();
        SecurityFilterChain securityFilterChain = http.build();
        return securityFilterChain;
    }

    @Bean
    public OAuth2UserService getOAuth2UserService() {
        return new ExtendedOAuth2UserService(userRepo);
    }

    @Bean
    public OidcUserService getOidcUserService() {
        return new ExtendedOidcUserService(userRepo);
    }
}
