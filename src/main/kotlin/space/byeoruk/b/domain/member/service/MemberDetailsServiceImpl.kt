package space.byeoruk.b.domain.member.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.repository.MemberRepository

@Service
class MemberDetailsServiceImpl(private val memberRepository: MemberRepository): UserDetailsService {
    @Transactional(readOnly = true)
    override fun loadUserByUsername(name: String): UserDetails {
        val member = memberRepository.findById(name)
            .orElseThrow { UsernameNotFoundException("계정 정보가 존재하지 않습니다.") }

        val memberDetails = MemberDto.Details.fromEntity(member)
        val password = member.password

        return MemberDetails(memberDetails, password)
    }
}