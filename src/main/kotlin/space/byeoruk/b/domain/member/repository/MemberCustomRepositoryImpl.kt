package space.byeoruk.b.domain.member.repository

import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.JPQLSubQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.entity.QMember
import space.byeoruk.b.domain.member.entity.QMemberFollow
import space.byeoruk.b.domain.member.exception.MemberNotFoundException

@Repository
class MemberCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): MemberCustomRepository {
    override fun findByUid(uid: Long): MemberDto.Details {
        val member = QMember.member

        //  팔로우 수 확인용
        val followCountExpr1 = QMemberFollow("mf1")
        val followCountExpr2 = QMemberFollow("mf2")

        val item: Tuple = queryFactory.select(
            member,
            countFollower(member, followCountExpr1),
            countFollowing(member, followCountExpr2),
        )
            .from(member)
            .leftJoin(member.privacy).fetchJoin()
            .leftJoin(member.authorities).fetchJoin()
            .where(member.uid.eq(uid))
            .fetchOne() ?: throw MemberNotFoundException()

        val details = MemberDto.Details.fromEntity(item.get(member)!!)
        details.followStatus.followerCount = item.get(1, Long::class.javaObjectType)?.toInt() ?: 0
        details.followStatus.followingCount = item.get(2, Long::class.javaObjectType)?.toInt() ?: 0

        details.privacyField()

        return details
    }

    /**
     * 계정 UID로 사용자 조회. 조회하는 계정 기준으로 응답 가공
     *
     * @param uid 조회할 계정 UID
     * @param member 조회하는 계정
     * @return 사용자 정보 DTO
     */
    override fun findByUid(uid: Long, member: Member): MemberDto.Details {
        val opponent = QMember.member

        //  팔로우 여부 확인용
        val followingExpr1 = QMemberFollow("mf1")
        val followingExpr2 = QMemberFollow("mf2")

        //  팔로우 수 확인용
        val followCountExpr1 = QMemberFollow("mf3")
        val followCountExpr2 = QMemberFollow("mf4")

        //  대상이 나를 팔로우 하고 있는지?
        val expressionIsFollowingMe: BooleanExpression = JPAExpressions.selectOne().from(followingExpr1)
            .where(followingExpr1.follower.uid.eq(opponent.uid), followingExpr1.followee.uid.eq(member.uid))
            .exists()

        //  내가 대상을 팔로우 하고 있는지?
        val expressionAmIFollowing: BooleanExpression = JPAExpressions.selectOne().from(followingExpr2)
            .where(followingExpr2.follower.uid.eq(member.uid), followingExpr2.followee.uid.eq(opponent.uid))
            .exists()

        val item: Tuple = queryFactory.select(
            opponent,
            expressionIsFollowingMe,
            expressionAmIFollowing,
            countFollower(opponent, followCountExpr1),
            countFollowing(opponent, followCountExpr2),
        )
            .from(opponent)
            .leftJoin(opponent.privacy).fetchJoin()
            .leftJoin(opponent.authorities).fetchJoin()
            .where(opponent.uid.eq(uid))
            .fetchOne() ?: throw MemberNotFoundException()

        val details = MemberDto.Details.fromEntity(item.get(opponent)!!)
        details.followStatus.isFollowingMe = item.get(1, Boolean::class.javaObjectType) ?: false
        details.followStatus.amIFollowing = item.get(2, Boolean::class.javaObjectType) ?: false
        details.followStatus.followerCount = item.get(3, Long::class.javaObjectType)?.toInt() ?: 0
        details.followStatus.followingCount = item.get(4, Long::class.javaObjectType)?.toInt() ?: 0

        details.privacyField()

        return details
    }

    /**
     * 팔로워 수 카운트
     *
     * @param member 대상 계정 QEntity
     * @param memberFollow 대상 계정 팔로우 정보 QEntity
     * @return JPQL 서브쿼리 결과
     */
    private fun countFollower(member: QMember, memberFollow: QMemberFollow): JPQLSubQuery<Long> =
        JPAExpressions.select(memberFollow.count())
            .from(memberFollow)
            .where(memberFollow.followee.uid.eq(member.uid))

    /**
     * 팔로잉 수 카운트
     *
     * @param member 대상 계정 QEntity
     * @param memberFollow 대상 계정 팔로우 정보 QEntity
     * @return JPQL 서브쿼리 결과
     */
    private fun countFollowing(member: QMember, memberFollow: QMemberFollow): JPQLSubQuery<Long> =
        JPAExpressions.select(memberFollow.count())
            .from(memberFollow)
            .where(memberFollow.follower.uid.eq(member.uid))
}