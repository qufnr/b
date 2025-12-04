package space.byeoruk.b.security.filter

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import space.byeoruk.b.domain.member.service.MemberDetailsServiceImpl
import space.byeoruk.b.security.model.TokenType
import space.byeoruk.b.security.provider.TokenProvider

private val log = KotlinLogging.logger {}

@Component
class TokenAuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val memberDetailsService: MemberDetailsServiceImpl
): OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            val bearerToken = request.getHeader("Authorization")

            //  Header 의 Authentication 에 Bearer 토큰이 있을 때만 검증 수행
            if(bearerToken != null && bearerToken.isNotBlank()) {
                val token = tokenProvider.getBearerToken(bearerToken)
                if(tokenProvider.isValidToken(token) && tokenProvider.getTokenType(token) == TokenType.ACCESS) {
                    val payload = tokenProvider.getTokenPayload(token)
                    val memberDetails = memberDetailsService.loadUserByUsername(payload["id"] as String)

                    if(!memberDetails.isAccountNonLocked)
                        throw BadCredentialsException("차단된 계정입니다. 관리자에게 문의해 주세요.")

                    if(!memberDetails.isEnabled)
                        throw BadCredentialsException("비활성화 상태인 계정입니다. 활성화 하려면 계정을 다시 인증해 주세요.")

                    val authentication = UsernamePasswordAuthenticationToken(memberDetails, "", memberDetails.authorities)

                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }
        catch(e: Exception) {
            log.error(e) { "필터 예외 발생" }
            request.setAttribute("exception", e)
        }

        filterChain.doFilter(request, response)
    }
}