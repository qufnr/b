package space.byeoruk.b.domain.member.service

import org.springframework.stereotype.Service
import space.byeoruk.b.domain.member.dto.SignDto
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.repository.MemberRepository
import space.byeoruk.b.security.model.TokenType
import space.byeoruk.b.security.service.TokenProvider

@Service
class SignService(private val memberRepository: MemberRepository, private val tokenProvider: TokenProvider) {
    fun signId(request: SignDto.IdRequest): SignDto.IdDetails {
        val member = memberRepository.findById(request.id)
            .orElseThrow{ MemberNotFoundException() }

        val claims = mapOf(
            Pair("uid", member.uid.toString()),
            Pair("id", member.id),
            Pair("name", if(member.name == null || member.name!!.isBlank()) member.id else member.name!!),
        )
        val token = tokenProvider.createToken(claims, TokenType.SIGN)

        return SignDto.IdDetails.build(token, claims)
    }
}
