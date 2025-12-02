package space.byeoruk.b.domain.member.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.dto.SignDto
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.exception.MemberPasswordMismatchException
import space.byeoruk.b.domain.member.repository.MemberRepository
import space.byeoruk.b.security.exception.TokenValidationException
import space.byeoruk.b.security.model.TokenType
import space.byeoruk.b.security.provider.TokenProvider

@Service
class SignService(
    private val memberRepository: MemberRepository,
    private val tokenProvider: TokenProvider,
    private val passwordEncoder: PasswordEncoder
) {
    /**
     * 사용자 ID 검증
     *
     * @param request 요청 정보 (계정 ID)
     * @return 사용자 ID 에 대한 상세 정보
     */
    fun signId(request: SignDto.IdRequest): SignDto.IdDetails {
        val member = memberRepository.findByIdOrEmail(request.value, request.value)
            .orElseThrow{ MemberNotFoundException() }

        val claims = mapOf(
            Pair("uid", member.uid.toString()),
            Pair("id", member.id),
            Pair("name", if(member.name == null || member.name!!.isBlank()) member.id else member.name!!),
        )
        val token = tokenProvider.createToken(claims, TokenType.SIGN)

        return SignDto.IdDetails.build(token, claims)
    }

    /**
     * 사용자 로그인
     *
     * @param request 요청 정보 (비밀번호)
     * @param authentication 로그인 토큰 문자열
     * @return 로그인 정보 (접근 토큰, 리프레시 토큰, 사용자 기본 정보)
     */
    fun sign(request: SignDto.Request, authentication: String): SignDto.Details {
        val token = tokenProvider.getBearerToken(authentication)
        if(!tokenProvider.isValidToken(token))
            throw TokenValidationException("잘못된 토큰입니다.")

        if(tokenProvider.getTokenType(token) != TokenType.SIGN)
            throw TokenValidationException("토큰 유형이 올바르지 않습니다.")

        val payload = tokenProvider.getTokenPayload(token)

        val memberUid = payload.get("uid", String::class.java).toLong()
        val member = memberRepository.findById(memberUid)
            .orElseThrow { MemberNotFoundException() }

        if(!passwordEncoder.matches(request.password, member.password))
            throw MemberPasswordMismatchException()

        val accessToken = issueAccessTokenByMember(member)
        val refreshToken = issueRefreshTokenByMember(member)

        return SignDto.Details(accessToken, refreshToken, MemberDto.Details.fromEntity(member))
    }

    /**
     * 리프레시 토큰으로 접근 토큰 및 리프레시 토큰 재발급
     *
     * @param authentication 리프레시 토큰 문자열
     * @return 로그인 정보 (접근 토큰, 리프레시 토큰, 사용자 기본 정보)
     */
    fun refresh(authentication: String): SignDto.Details {
        val token = tokenProvider.getBearerToken(authentication)
        if(!tokenProvider.isValidToken(token))
            throw TokenValidationException("잘못된 토큰입니다.")

        if(tokenProvider.getTokenType(token) != TokenType.REFRESH)
            throw TokenValidationException("토큰 유형이 올바르지 않습니다.")

        val payload = tokenProvider.getTokenPayload(token)

        val memberUid = payload.get("uid", String::class.java).toLong()
        val member = memberRepository.findById(memberUid)
            .orElseThrow { MemberNotFoundException() }

        val accessToken = issueAccessTokenByMember(member)
        val refreshToken = issueRefreshTokenByMember(member)

        return SignDto.Details(accessToken, refreshToken, MemberDto.Details.fromEntity(member))
    }

    /**
     * 사용자 Entity 로 접근 토큰 발급
     *
     * @param member 사용자 Entity
     * @return 접근 토큰 문자열
     */
    private fun issueAccessTokenByMember(member: Member): String {
        //  접근 토큰 Claims 맵
        val accessClaims = mapOf(
            Pair("uid", member.uid.toString()),
            Pair("id", member.id),
            Pair("authorities", member.authorities.map { authority -> authority.authority.toString() }.toList().joinToString(separator = ","))
        )

        return tokenProvider.createToken(accessClaims, TokenType.ACCESS)
    }

    /**
     * 사용자 Entity 로 리프레시 토큰 발급
     *
     * @param member 사용자 Entity
     * @return 리프레시 토큰 문자열
     */
    private fun issueRefreshTokenByMember(member: Member): String {
        //  리프레시 토큰 Claims 맵
        val refreshClaims = mapOf(Pair("uid", member.uid.toString()),)
        return tokenProvider.createToken(refreshClaims, TokenType.REFRESH)
    }
}
