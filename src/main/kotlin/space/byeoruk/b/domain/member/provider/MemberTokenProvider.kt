package space.byeoruk.b.domain.member.provider

import io.jsonwebtoken.Claims
import org.springframework.stereotype.Component
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.dto.SignDto
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.security.exception.TokenValidationException
import space.byeoruk.b.security.model.TokenType
import space.byeoruk.b.security.provider.TokenProvider

@Component
class MemberTokenProvider(
    private val tokenProvider: TokenProvider
) {
    /**
     * 계정 ID 검증 토큰 발급
     *
     * @param member 계정 Entity
     * @param type 토큰 유형 (기본 값: SIGN)
     * @return 계정 ID 정보 및 ID 검증 토큰
     */
    fun issueSignToken(member: Member, type: TokenType = TokenType.SIGN): SignDto.IdDetails {
        val claims = mapOf(
            "uid" to member.uid.toString(),
            "id" to member.id,
            "name" to if(member.name == null || member.name!!.isBlank()) member.id else member.name!!,
        )
        val token = tokenProvider.createToken(claims, type)
        val expiration = tokenProvider.getTokenPayload(token).expiration.time

        return SignDto.IdDetails(token, expiration, claims)
    }

    /**
     * 접근 토큰, 리프레시 토큰 발급
     *
     * @param member 사용자 Entity
     * @return 로그인 디테일
     */
    fun issueTokens(member: Member): SignDto.Details {
        val accessClaims = mapOf(
            "uid" to member.uid.toString(),
            "id" to member.id,
            "authorities" to member.authorities.map { it.authority.toString() }.toList().joinToString(separator = ",")
        )

        val refreshClaims = mapOf(
            "uid" to member.uid.toString(),
        )

        val accessToken = tokenProvider.createToken(accessClaims, TokenType.ACCESS)
        val accessExpiration = tokenProvider.getTokenPayload(accessToken).expiration.time

        val refreshToken = tokenProvider.createToken(refreshClaims, TokenType.REFRESH)
        val refreshExpiration = tokenProvider.getTokenPayload(refreshToken).expiration.time

        return SignDto.Details(accessToken, accessExpiration, refreshToken, refreshExpiration, MemberDto.Details.fromEntity(member))
    }

    /**
     * 토큰 검증
     *
     * @param authorization Bearer 토큰
     * @param type 토큰 유형
     * @return 토큰 페이로드
     * @throws TokenValidationException 토큰이 잘못되면 던짐
     */
    fun validateToken(authorization: String, type: TokenType): Claims {
        val token = tokenProvider.getBearerToken(authorization)
        if(!tokenProvider.isValidToken(token))
            throw TokenValidationException("잘못된 토큰입니다.")

        if(tokenProvider.getTokenType(token) != type)
            throw TokenValidationException("토큰 유형이 올바르지 않습니다.")

        return tokenProvider.getTokenPayload(token)
    }
}