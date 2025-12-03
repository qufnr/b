package space.byeoruk.b.security.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import space.byeoruk.b.security.exception.AccessDeniedHandlerImpl
import space.byeoruk.b.security.exception.AuthenticationEntryPointImpl
import space.byeoruk.b.security.filter.TokenAuthenticationFilter

@EnableWebSecurity
@Configuration
class SecurityConfiguration(
    private val tokenAuthenticationFilter: TokenAuthenticationFilter,
    private val authenticationEntryPointImpl: AuthenticationEntryPointImpl,
    private val accessDeniedHandlerImpl: AccessDeniedHandlerImpl,

    @Value($$"${bserver.app-url}")
    private val appUrl: String
) {
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
                .requestMatchers(HttpMethod.POST, "/api/member-management/signs").permitAll()               //  로그인
                .requestMatchers(HttpMethod.POST, "/api/member-management/signs/id").permitAll()            //  로그인 전 ID 검증
                .requestMatchers(HttpMethod.GET, "/api/member-management/members").permitAll()              //  계정 찾기
                .requestMatchers(HttpMethod.GET, "/api/member-management/members/can-use").permitAll()      //  계정 ID 또는 이메일 사용 가능 여부 확인
                .requestMatchers(HttpMethod.POST, "/api/member-management/members").permitAll()             //  계정 생성
                .requestMatchers(HttpMethod.PUT, "/api/member-management/members").authenticated()          //  계정 수정
                .requestMatchers(HttpMethod.PUT, "/api/member-management/members/resource").authenticated() //  계정 리소스 수정
                .anyRequest().denyAll()
            }
            .exceptionHandling { configurer -> configurer
                .authenticationEntryPoint(authenticationEntryPointImpl)
                .accessDeniedHandler(accessDeniedHandlerImpl)
            }
            .addFilterBefore(tokenAuthenticationFilter, BasicAuthenticationFilter::class.java)
            .build()
    }

    /**
     * CORS 설정
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.addAllowedOrigin(appUrl)
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