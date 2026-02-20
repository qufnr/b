package space.byeoruk.b.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import space.byeoruk.b.domain.member.entity.MemberHistory

interface MemberHistoryRepository: JpaRepository<MemberHistory, Long> {

}
