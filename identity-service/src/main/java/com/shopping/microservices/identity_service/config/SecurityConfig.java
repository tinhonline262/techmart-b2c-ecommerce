package com.shopping.microservices.identity_service.config;

import com.shopping.microservices.identity_service.filter.JwtBlacklistFilter;
import com.shopping.microservices.identity_service.handler.CustomAuthenticationFailureHandler;
import com.shopping.microservices.identity_service.handler.OAuth2LoginSuccessHandler;
import com.shopping.microservices.identity_service.properties.JwtProperties;
import com.shopping.microservices.identity_service.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final JwtProperties jwtProperties;
    private final JwtBlacklistFilter jwtBlacklistFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/","/api/v1/auth/**",
                                "/actuator/**",
                                "/oauth2/**",
                                "/login/oauth2/code/google",
                                "/api/debug/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtBlacklistFilter, BearerTokenAuthenticationFilter.class)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // mapping oauth2 info to user in db
                        )
                        .successHandler(oAuth2LoginSuccessHandler) // generate token or redirect
                        .failureHandler(customAuthenticationFailureHandler)
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public JwtDecoder customJwtDecoder() {
        SecretKey key = new SecretKeySpec(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }//ROLE_ADMIN

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();

        authConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>(grantedAuthoritiesConverter.convert(jwt));

            Optional.ofNullable(jwt.getClaimAsStringList("permissions"))
                    .ifPresent(permissions -> permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p))));

            return authorities;
        });

        return authConverter;
    }
}
