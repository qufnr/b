package space.byeoruk.b.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.entity.MemberVerification
import space.byeoruk.b.domain.member.model.MemberVerifyType
import java.time.LocalDateTime

interface MemberVerificationRepository: JpaRepository<MemberVerification, Long> {
    @Query("""
            SELECT mv FROM MemberVerification mv
            WHERE mv.type = :type AND mv.member = :member AND mv.expiredAt > :now AND mv.usedAt IS null
            ORDER BY mv.uid
        """)
    fun findValidKeys(member: Member, type: MemberVerifyType, now: LocalDateTime = LocalDateTime.now()): List<MemberVerification>
}