package space.byeoruk.b.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.entity.MemberFollow

interface MemberFollowRepository: JpaRepository<MemberFollow, Long> {
    /**
     * 특정 사용자가 다른 사용자를 팔로우 중인지 여부 확인
     *
     * @param follower 팔로우 사용자 엔티티
     * @param followee 팔로잉 사용자 엔티티
     * @return 팔로우 여부
     */
    fun existsByFollowerAndFollowee(follower: Member, followee: Member): Boolean

    /**
     * 언팔로우
     *
     * @param follower 팔로우 사용자 엔티티
     * @param followee 팔로잉 사용자 엔티티
     */
    fun deleteByFollowerAndFollowee(follower: Member, followee: Member)

    /**
     * 팔로잉 수 (내가 팔로우 하는 사용자 수)
     *
     * @param follower 대상 엔티티
     * @return 팔로잉 수
     */
    fun countByFollower(follower: Member): Long

    /**
     * 팔로워 수 (나를 팔로우 하는 사용자 수)
     *
     * @param followee 대상 엔티티
     * @return 팔로워 수
     */
    fun countByFollowee(followee: Member): Long
}