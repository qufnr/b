package space.byeoruk.b.security.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import space.byeoruk.b.security.filter.TokenAuthenticationFilter

@EnableWebSecurity
@Configuration
class SecurityConfiguration(private val tokenAuthenticationFilter: TokenAuthenticationFilter) {
    //  허용 해더 목록
    private final val allowedHeaders: Array<String> = arrayOf("Authorization")
    //  허용 메소드 목록
    private final val allowedMethods: Array<String> = arrayOf("GET", "POST", "PUT", "DELETE")

    /**
     * Security 설정
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity, corsConfigurationSource: CorsConfigurationSource): SecurityFilterChain {
        return http.httpBasic{ configurer -> configurer.disable() }
            .csrf { configurer -> configurer.disable() }
            .formLogin { configurer -> configurer.disable() }
            .cors { configurer -> configurer.configurationSource(corsConfigurationSource) }
            .sessionManagement { configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { registry -> registry.requestMatchers("/", "/error", "/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/sign-management/signs").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/member-management/members").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/member-management/members").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/member-management/members").authenticated()
                .anyRequest().denyAll()
            }
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    /**
     * CORS 설정
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.addAllowedOrigin("http://localhost:5000")
        configuration.allowCredentials = true
        allowedHeaders.forEach { header -> configuration.addAllowedHeader(header) }
        allowedMethods.forEach { method -> configuration.addAllowedMethod(method) }

        val configurationSource = UrlBasedCorsConfigurationSource()
        configurationSource.registerCorsConfiguration("/**", configuration)

        return configurationSource
    }

    /**
     * 비밀번호 인코더 Bean
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}