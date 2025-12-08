package space.byeoruk.b.domain.member.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import space.byeoruk.b.domain.member.annotation.MemberAction
import space.byeoruk.b.domain.member.dto.SignDto
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.exception.MemberPasswordMismatchException
import space.byeoruk.b.domain.member.model.MemberHistoryType
import space.byeoruk.b.domain.member.provider.MemberTokenProvider
import space.byeoruk.b.domain.member.repository.MemberRepository
import space.byeoruk.b.security.model.TokenType

@Service
class SignService(
    private val memberRepository: MemberRepository,
    private val memberTokenProvider: MemberTokenProvider,
    private val passwordEncoder: PasswordEncoder
) {
    /**
     * 사용자 ID 검증
     *
     * @param request 요청 정보 (계정 ID)
     * @return 사용자 ID 에 대한 상세 정보
     */
    @MemberAction(MemberHistoryType.ID_SIGN)
    fun signId(request: SignDto.IdRequest): SignDto.IdDetails {
        val member = memberRepository.findByIdOrEmail(request.value, request.value)
            .orElseThrow{ MemberNotFoundException() }

        return memberTokenProvider.issueSignToken(member)
    }

    /**
     * 사용자 로그인
     *
     * @param request 요청 정보 (비밀번호)
     * @param authorization 로그인 토큰 문자열
     * @return 로그인 정보 (접근 토큰, 리프레시 토큰, 사용자 기본 정보)
     */
    @MemberAction(MemberHistoryType.SIGN)
    fun sign(request: SignDto.Request, authorization: String): SignDto.Details {
        //  Sign ID 토큰 검증
        val payload = memberTokenProvider.validateToken(authorization, TokenType.SIGN)

        val memberUid = payload.get("uid", String::class.java).toLong()
        val member = memberRepository.findById(memberUid)
            .orElseThrow { MemberNotFoundException() }

        if(!passwordEncoder.matches(request.password, member.password))
            throw MemberPasswordMismatchException()

        return memberTokenProvider.issueTokens(member)
    }

    /**
     * 리프레시 토큰으로 접근 토큰 및 리프레시 토큰 재발급
     *
     * @param authorization 리프레시 토큰 문자열
     * @return 로그인 정보 (접근 토큰, 리프레시 토큰, 사용자 기본 정보)
     */
    fun refresh(authorization: String): SignDto.Details {
        //  리프레시 토큰 검증
        val payload = memberTokenProvider.validateToken(authorization, TokenType.REFRESH)

        val memberUid = payload.get("uid", String::class.java).toLong()
        val member = memberRepository.findById(memberUid)
            .orElseThrow { MemberNotFoundException() }

        return memberTokenProvider.issueTokens(member)
    }
}
