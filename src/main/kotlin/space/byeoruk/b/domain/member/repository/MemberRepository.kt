package space.byeoruk.b.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import space.byeoruk.b.domain.member.entity.Member
import java.util.Optional

@Repository
interface MemberRepository: JpaRepository<Member, Long> {
    /**
     * 계정 ID로 사용자 조회
     *
     * @param id 계정 ID
     * @return 사용자 Entity
     */
    fun findById(id: String): Optional<Member>

    /**
     * 계정 ID 또는 이메일로 사용자 조회
     *
     * @param id 계정 ID
     * @param email 이메일
     * @return 사용자 Entity
     */
    fun findByIdOrEmail(id: String, email: String): Optional<Member>
}