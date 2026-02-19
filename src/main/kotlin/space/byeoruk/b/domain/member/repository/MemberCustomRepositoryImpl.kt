package space.byeoruk.b.domain.member.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.entity.Member

@Repository
class MemberCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): MemberCustomRepository {
    /**
     * 계정 UID로 사용자 조회. 조회하는 계정 기준으로 응답 가공
     *
     * @param uid 조회할 계정 UID
     * @param member 조회하는 계정
     * @return 사용자 정보 DTO
     */
    override fun findByUid(uid: Long, member: Member): MemberDto.Details? {
        //  TODO :: 조회하는 계정(`member` 매개변수) 기준으로 쿼리 짜기
        return null
    }
}