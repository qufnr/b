package space.byeoruk.b.domain.member.service

import org.springframework.stereotype.Service
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.repository.MemberRepository

@Service
class MemberService(private val memberRepository: MemberRepository) {
    /**
     * 계정 UID 로 정보 조회
     *
     * @param uid 계정 UID
     * @return 계정 상세 정보
     */
    fun read(uid: Long): MemberDto.Details {
        val member = memberRepository.findById(uid)
            .orElseThrow { MemberNotFoundException() }

        return MemberDto.Details.fromEntity(member)
    }
}