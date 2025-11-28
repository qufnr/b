package space.byeoruk.b.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import space.byeoruk.b.domain.member.entity.Member
import java.util.Optional

@Repository
interface MemberRepository: JpaRepository<Member, Long> {
    fun findById(id: String): Optional<Member>
}