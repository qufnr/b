package space.byeoruk.b.domain.member.logging

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import space.byeoruk.b.domain.member.annotation.MemberAction
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.dto.MemberHistoryDto
import space.byeoruk.b.domain.member.dto.SignDto
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.model.MemberHistoryType
import space.byeoruk.b.domain.member.repository.MemberRepository
import space.byeoruk.b.domain.member.service.MemberHistoryRecorder

@Aspect
@Component
class MemberHistoryAspect(
    private val memberRepository: MemberRepository,
    private val memberHistoryRecorder: MemberHistoryRecorder
) {
    @Around("@annotation(memberAction)")
    @Transactional(readOnly = true)
    fun aroundMemberAction(joinPoint: ProceedingJoinPoint, memberAction: MemberAction): Any? {
        val member = resolveMemberFromContextOrArguments(joinPoint)

        //  이전 계정 디테일 스냅샷
        val beforeMap: Map<String, Any?>? = getMemberSnapshots(member, memberAction)

        //  실제 비즈니스 로직 실행 후 반환하는 응답
        val result = joinPoint.proceed()

        //  이후 계정 디테일 스냅샷
        val resolvedMember = resolveMemberFromProceedResult(result)
        val afterMap: Map<String, Any?>? = getMemberSnapshots(resolvedMember, memberAction)

        if(resolvedMember != null) {
            //  이전, 이후 값 저장하려는 유형
            val beforeAfterSaveTypes = arrayOf(
                MemberHistoryType.ACCOUNT_UPDATED,
                MemberHistoryType.ACCOUNT_RESOURCE_UPDATED
            )

            val record: MemberHistoryDto.RecordMap =
                if(beforeAfterSaveTypes.contains(memberAction.type))
                    MemberHistoryDto.RecordMap(memberAction.type, beforeMap, afterMap, getHistoryMessage(memberAction))
                else
                    MemberHistoryDto.RecordMap(memberAction.type, getHistoryMessage(memberAction))

            memberHistoryRecorder.record(resolvedMember, record)
        }

        return result
    }

    /**
     * 계정 Entity 를 SecurityContextHolder 나 JoinPoint 의 매개변수에서 가져온다.
     *
     * @param joinPoint ProceedingJoinPoint
     * @return 가져오는 데 성공했다면 계정 Entity 를, 실패했다면 null 반환
     */
    private fun resolveMemberFromContextOrArguments(joinPoint: ProceedingJoinPoint): Member? {
        var member: Member? = null
        val authentication = SecurityContextHolder.getContext().authentication
        if(authentication != null && authentication.isAuthenticated)
            member = memberRepository.findById(authentication.name).orElse(null)

        if(member != null)
            return member

        joinPoint.args.forEach { argument ->
            when(argument) {
                is Member -> return argument
                is MemberDetails -> return memberRepository.findById(argument.username).orElse(null)
                is MemberDto.Details -> return memberRepository.findById(argument.uid).orElse(null)
            }
        }

        return null
    }

    /**
     * 계정 Entity 를 Proceed 결과로 부터 가져온다.
     *
     * @param result JoinPoint Proceed
     * @return 가져오는 데 성공했다면 계정 Entity 를, 실패했다면 null 반환
     */
    private fun resolveMemberFromProceedResult(result: Any?): Member? = when(result) {
        is Member -> result
        is MemberDto.Details -> memberRepository.findById(result.uid).orElse(null)
        is SignDto.IdDetails -> memberRepository.findById(result.id).orElse(null)
        is SignDto.Details -> memberRepository.findById(result.member.uid).orElse(null)
        else -> null
    }

    /**
     * 계정 디테일 스냅샷 반환
     *
     * @param member 계정 Entity
     * @param memberAction MemberAction 어노테이션 정보
     * @return 계정 디테일 스냅샷
     */
    private fun getMemberSnapshots(member: Member?, memberAction: MemberAction): Map<String, Any?>? =
        if(member != null && memberAction.trackUpdates)
            MemberDto.Details.snapshots(member)
        else
            null

    /**
     * 계정 히스토리에 저장할 메시지 문구
     *
     * @param memberAction MemberAction 어노테이션 정보
     * @return 메시지
     */
    private fun getHistoryMessage(memberAction: MemberAction): String =
        memberAction.message.ifBlank { memberAction.type.description }
}