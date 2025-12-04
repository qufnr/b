package space.byeoruk.b.domain.member.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.repository.MemberRepository

@Component
class CurrentMemberService(
    private val memberRepository: MemberRepository
) {
    /**
     * 현재 시큐리티 콘텍스트 내에 존재하는 계정 조회
     *
     * @return 시큐리티 콘텍스트 내에 계정이 존재하면 계정 Entity 반환, 없으면 null
     */
    @Transactional(readOnly = true)
    fun readMemberOrNull(): Member? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null

        if(!authentication.isAuthenticated)
            return null

        val name = authentication.name ?: return null

        return memberRepository.findById(name).orElse(null)
    }

    /**
     * 현재 시큐리티 콘텍스트 내에 존재하는 계정 조회
     *
     * @throws MemberNotFoundException 시큐리티 콘텍스트 내에 계정이 존재하지 않으면 던짐
     */
    @Transactional(readOnly = true)
    fun readMemberOrThrow(): Member {
        return readMemberOrNull() ?: throw MemberNotFoundException()
    }
}