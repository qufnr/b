package space.byeoruk.b.domain.member.repository

import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.entity.Member

interface MemberCustomRepository {
    fun findByUid(uid: Long, member: Member): MemberDto.Details?
}
