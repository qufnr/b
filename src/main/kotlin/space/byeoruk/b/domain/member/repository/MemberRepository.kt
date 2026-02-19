package space.byeoruk.b.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import space.byeoruk.b.domain.member.entity.Member
import java.util.Optional

interface MemberRepository: JpaRepository<Member, Long>, MemberCustomRepository {
    /**
     * 계정 ID로 사용자 조회
     *
     * @param id 계정 ID
     * @return 사용자 Entity
     */
    fun findById(id: String): Optional<Member>

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return 사용자 Entity
     */
    fun findByEmail(email: String): Optional<Member>

    /**
     * 계정 ID 또는 이메일로 사용자 조회
     *
     * @param id 계정 ID
     * @param email 이메일
     * @return 사용자 Entity
     */
    fun findByIdOrEmail(id: String, email: String): Optional<Member>
}