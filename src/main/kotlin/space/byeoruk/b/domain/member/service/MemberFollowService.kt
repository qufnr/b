package space.byeoruk.b.domain.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.dto.MemberFollowDto
import space.byeoruk.b.domain.member.entity.MemberFollow
import space.byeoruk.b.domain.member.exception.MemberFolloweeMyselfException
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.model.FollowState
import space.byeoruk.b.domain.member.repository.MemberFollowRepository
import space.byeoruk.b.domain.member.repository.MemberRepository

@Service
class MemberFollowService(
    private val memberFollowRepository: MemberFollowRepository,
    private val memberRepository: MemberRepository
) {
    /**
     * 팔로우 토글
     *
     * @param followeeMemberUid 팔로우할 대상 UID
     * @param memberDetails 사용자 인증 객체
     * @return 팔로우 상태
     */
    @Transactional
    fun toggle(followeeMemberUid: Long, memberDetails: MemberDetails): MemberFollowDto.Response {
        var followState: FollowState

        val member = memberRepository.findById(memberDetails.username)
            .orElseThrow { MemberNotFoundException() }

        val followee = memberRepository.findById(followeeMemberUid)
            .orElseThrow { MemberNotFoundException() }

        //  자신 팔로우 막기
        if(followee.uid == member.uid)
            throw MemberFolloweeMyselfException()

        val isFollowing = memberFollowRepository.existsByFollowerAndFollowee(member, followee)

        //  팔로우 중일 경우 언팔로우 처리
        if(isFollowing) {
            memberFollowRepository.deleteByFollowerAndFollowee(member, followee)
            followState = FollowState.UNFOLLOW
        }
        //  팔로우를 안하고 있으면 팔로우 처리
        else {
            val follow = MemberFollow(follower = member, followee = followee)
            memberFollowRepository.save(follow)
            followState = FollowState.FOLLOW
        }

        return MemberFollowDto.Response(followState, MemberDto.Details.fromEntity(followee))
    }
}