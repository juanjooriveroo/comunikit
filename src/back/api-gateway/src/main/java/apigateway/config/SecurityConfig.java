package apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.fqdn}")
    private String front;

    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtServerAuthenticationConverter jwtServerAuthenticationConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        jwtFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/board/public/**").permitAll()
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                        .pathMatchers("/documentacion", "/documentacion/**").permitAll()
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .pathMatchers("/api-docs", "/api-docs/**").permitAll()
                        .pathMatchers("/webjars/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/board/**")
                        .hasAnyAuthority("ROLE_USUARIO", "ROLE_TUTOR", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/board/**")
                        .hasAnyAuthority("ROLE_TUTOR", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/board/**")
                        .hasAnyAuthority("ROLE_TUTOR", "ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/board/**")
                        .hasAnyAuthority("ROLE_TUTOR", "ROLE_ADMIN")
                        .pathMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyExchange().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(front);
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}