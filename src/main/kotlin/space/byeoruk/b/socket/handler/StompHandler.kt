package space.byeoruk.b.socket.handler

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.service.MemberDetailsServiceImpl
import space.byeoruk.b.domain.member.service.MemberService
import space.byeoruk.b.security.exception.TokenValidationException
import space.byeoruk.b.security.provider.TokenProvider
import space.byeoruk.b.socket.exception.DestinationNotFoundException
import space.byeoruk.b.socket.exception.QueueAccessDeniedException
import space.byeoruk.b.socket.exception.SubscribeAccessorInvalidException
import java.security.Principal

@Component
//  Spring Security 필터보다 우선순위를 높게 설정
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
class StompHandler(
    private val tokenProvider: TokenProvider,
    private val memberDetailsService: MemberDetailsServiceImpl
): ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if(accessor != null) {
            when(accessor.command) {
                //  연결 시에만 JWT 검증
                StompCommand.CONNECT -> {
                    //  JWT 읽기
                    val authorization = accessor.getFirstNativeHeader("Authorization")
                        ?: throw TokenValidationException("error.token.missing")
                    val token = tokenProvider.getBearerToken(authorization)

                    //  JWT 검증
                    if(!tokenProvider.isValidToken(token))
                        throw TokenValidationException("error.token.invalid")

                    //  JWT 페이로드 읽기
                    val payload = tokenProvider.getTokenPayload(token)
                    val memberId = payload["id"] as String

                    //  사용자 인증 객체 생성
                    val userDetails = memberDetailsService.loadUserByUsername(memberId)

                    //  Accessor에 사용자 정보 주입
                    accessor.user = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                }

                //  구독 시 권한 검사
                StompCommand.SUBSCRIBE -> {
                    val member = accessor.user ?: throw SubscribeAccessorInvalidException()
                    val destination = accessor.destination ?: throw DestinationNotFoundException()

                    validateSubscription(member, destination)
                }

                else -> {}
            }
        }

        return message
    }

    /**
     * 구독 권한 검증
     *
     * @param member 사용자 인증 객체
     * @param destination 구독 경로
     */
    private fun validateSubscription(member: Principal, destination: String) {
        val authentication = member as UsernamePasswordAuthenticationToken
        val memberDetails = authentication.principal as MemberDetails

        //  1:1 구독 검증
        if(destination.startsWith("/queue/member/")) {
            val pathSegments = destination.split("/")
            val uid = pathSegments.getOrNull(3)?.toLongOrNull()

            if(uid == null || uid != memberDetails.getIdentifier())
                throw QueueAccessDeniedException()
        }
    }
}