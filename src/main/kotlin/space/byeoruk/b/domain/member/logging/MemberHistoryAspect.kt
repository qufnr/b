package space.byeoruk.b.domain.member.logging

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import space.byeoruk.b.domain.member.annotation.MemberAction
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.dto.MemberHistoryDto
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.service.CurrentMemberService
import space.byeoruk.b.domain.member.service.MemberHistoryRecorder

@Aspect
@Component
class MemberHistoryAspect(
    private val currentMemberService: CurrentMemberService,
    private val memberHistoryRecorder: MemberHistoryRecorder
) {
    @Around("@annotation(memberAction)")
    fun aroundMemberAction(joinPoint: ProceedingJoinPoint, memberAction: MemberAction): Any? {
        val member = currentMemberService.readMemberOrNull()

        val beforeMap: Map<String, Any?>? = getMemberSnapshots(member, memberAction)

        //  실제 비즈니스 로직 실행
        val result = joinPoint.proceed()

        //  member의 영속성이 유지 되는지?
        val afterMap: Map<String, Any?>? = getMemberSnapshots(member, memberAction)

        if(member != null) {
            val record: MemberHistoryDto.RecordMap =
                //  이전, 이후 값 비교
                if(beforeMap != null && afterMap != null && beforeMap != afterMap)
                    MemberHistoryDto.RecordMap(memberAction.type, beforeMap, afterMap, memberAction.type.description)
                else
                    MemberHistoryDto.RecordMap(memberAction.type, memberAction.type.description)

            memberHistoryRecorder.record(member, record)
        }

        return result
    }

    private fun getMemberSnapshots(member: Member?, memberAction: MemberAction): Map<String, Any?>? =
        if(member != null && memberAction.trackUpdates)
            MemberDto.Details.snapshots(member)
        else
            null
}