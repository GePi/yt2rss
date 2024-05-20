package farm.giggle.yt2rss.config.security;

import farm.giggle.yt2rss.serv.UserService;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().ignoringRequestMatchers(new AntPathRequestMatcher("/api/**"))
                .and()
                .headers().frameOptions().disable()
                .and()
                .logout(logout -> logout
                        .logoutSuccessUrl("/"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/","/oauth2-login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/rss/**").permitAll()
                        .requestMatchers("/styles/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(
                        httpSecurityOAuth2LoginConfigurer -> {
                            httpSecurityOAuth2LoginConfigurer.loginPage("/oauth2-login");
                            httpSecurityOAuth2LoginConfigurer.defaultSuccessUrl("/");
                        }
                );
        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> getOAuth2UserService() {
        return new ExtendedOAuth2UserService(userService);
    }

    @Bean
    public OidcUserService getOidcUserService() {
        return new ExtendedOidcUserService(userService);
    }
}
