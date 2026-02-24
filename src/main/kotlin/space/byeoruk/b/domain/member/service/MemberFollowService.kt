package space.byeoruk.b.domain.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.dto.MemberFollowDto
import space.byeoruk.b.domain.member.entity.MemberFollow
import space.byeoruk.b.domain.member.exception.MemberFolloweeDeniedException
import space.byeoruk.b.domain.member.exception.MemberFolloweeMyselfException
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.model.FollowAcceptType
import space.byeoruk.b.domain.member.model.FollowStatus
import space.byeoruk.b.domain.member.repository.MemberFollowRepository
import space.byeoruk.b.domain.member.repository.MemberRepository
import space.byeoruk.b.domain.notification.model.NotificationType
import space.byeoruk.b.domain.notification.service.NotificationService

@Service
class MemberFollowService(
    private val memberFollowRepository: MemberFollowRepository,
    private val memberRepository: MemberRepository,

    private val notificationService: NotificationService
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
        var followStatus: FollowStatus

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
            followStatus = FollowStatus.UNFOLLOW
            memberFollowRepository.deleteByFollowerAndFollowee(member, followee)
        }
        //  팔로우를 안하고 있으면 팔로우 처리
        else {
            //  상대방 설정의 팔로우 수락 설정을 확인합니다.
            followStatus = when(followee.privacy.followAccept) {
                //  상대방이 기본 설정이면 바로 팔로우 처리
                FollowAcceptType.DEFAULT -> FollowStatus.FOLLOW
                //  상대방이 직접 수락으로 설정했으면 팔로우 대기
                FollowAcceptType.MANUAL -> FollowStatus.FOLLOW_PENDING
                //  상대방이 팔로우를 받지 않으면 예외 터뜨리고 종료
                FollowAcceptType.DENIED -> throw MemberFolloweeDeniedException()
            }

            //  팔로우 처리
            val follow = MemberFollow(follower = member, followee = followee, status = followStatus)
            memberFollowRepository.save(follow)

            if(followStatus == FollowStatus.FOLLOW_PENDING)
                //  팔로우 상태가 대기일 경우 팔로우 요청 왔다고 상대에게 알림 보내기
                notificationService.send("Follow Request", NotificationType.FOLLOW_REQUEST, followee, member)
            else
                notificationService.send("Following Success", NotificationType.FOLLOW, followee, member)
        }

        return MemberFollowDto.Response(followStatus, MemberDto.Details.fromEntity(followee))
    }
}